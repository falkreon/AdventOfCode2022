package blue.endless.advent;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blue.endless.advent.util.Day;

public class Day21 extends Day {

	@Override
	public void partOne(List<String> input) {
		HashMap<String, Long> values = new HashMap<>();
		Deque<PendingOp> pendingOps = new ArrayDeque<>();
		
		for(int i = 0; i<input.size(); i++) {
			String[] pieces = input.get(i).split("\\s");
			String monkeyName = pieces[0].substring(0, pieces[0].length()-1).trim();
			
			if (pieces.length == 2) {
				long value = Long.parseLong(pieces[1].trim());
				values.put(monkeyName, value);
			} else if (pieces.length == 4) {
				PendingOp op = new PendingOp();
				op.a = pieces[1].trim();
				op.b = pieces[3].trim();
				op.op = pieces[2].trim();
				op.monkeyName = monkeyName;
				
				if (!op.evaluate(values)) pendingOps.addLast(op);
			}
		}
		
		System.out.println("Initial State:");
		System.out.println("  Known Values: "+values);
		System.out.println("  Pending Ops: "+pendingOps);
		System.out.println();
		
		Deque<PendingOp> nextPending = new ArrayDeque<>();
		
		while(!pendingOps.isEmpty()) {
			PendingOp op = pendingOps.removeFirst();
			if (!op.evaluate(values)) nextPending.addLast(op);
			if (pendingOps.isEmpty() && !nextPending.isEmpty()) {
				System.out.println("End of Round:");
				
				pendingOps = nextPending;
				nextPending = new ArrayDeque<>();
				System.out.println("  Known Values: "+values);
				System.out.println("  Pending Ops: "+pendingOps);
				System.out.println();
			}
		}
		
		System.out.println("Simulation Complete:");
		System.out.println("  Known Values: "+values);
		System.out.println("  Pending Ops: (none)");
		System.out.println();
		System.out.println("Value of root: "+values.get("root"));
	}
	
	public static class PendingOp {
		public String a;
		public String b;
		public String op;
		public String monkeyName;
		
		public boolean evaluate(Map<String, Long> values) {
			Long av = values.get(a);
			Long bv = values.get(b);
			if (av==null || bv==null) return false;
			
			values.put(monkeyName, switch(op) {
				case "-" -> av - bv;
				case "+" -> av + bv;
				case "*" -> av * bv;
				case "/" -> av / bv;
				default -> throw new IllegalStateException();
			});
			
			return true;
		}
		
		public boolean reverseEvaluate(long result, Map<String, Long> values, Collection<PendingOp> ops) {
			Long av = values.get(a);
			Long bv = values.get(b);
			if (av!=null && bv!=null) {
				return evaluate(values);
			}
			
			if (av==null && bv==null) return false;
			if (av==null) {
				if (a.equals("humn")) {
					//WE'RE HERE
					switch(this.op) {
						//result = humn / bv -> humn = result * bv
						case "/" -> values.put("humn", result * bv);
						case "*" -> values.put("humn", result / bv);
						case "+" -> values.put("humn", result - bv);
						case "-" -> values.put("humn", result + bv);
						default -> throw new IllegalStateException();
					}
					return true;
				}
				
				PendingOp aMonkey = null;
				for(PendingOp op : ops) if (op.monkeyName.equals(a)) aMonkey = op;
				if (aMonkey==null) {
					System.out.println("Cannot find monkey '"+a+"'");
					return false; //TODO: Maybe throw!
				}
				
				switch(this.op) {
					//result = x / bv -> x = result * bv
					case "/" -> aMonkey.reverseEvaluate(result * bv, values, ops);
					//result = x * bv -> x = result / bv
					case "*" -> aMonkey.reverseEvaluate(result / bv, values, ops);
					//result = x + bv -> x = result - bv
					case "+" -> aMonkey.reverseEvaluate(result - bv, values, ops);
					//result = x - bv -> x = result + bv
					case "-" -> aMonkey.reverseEvaluate(result + bv, values, ops);
					//x = bv = result
					case "=" -> aMonkey.reverseEvaluate(bv, values, ops);
				}
				
				return true;
			} else if (bv==null) {
				if (b.equals("humn")) {
					//WE'RE HERE
					switch(this.op) {
						//result = humn / bv -> humn = result * bv
						case "/" -> values.put("humn", av / result);
						case "*" -> values.put("humn", result / av);
						case "+" -> values.put("humn", result - av);
						case "-" -> values.put("humn", av - result);
						default -> throw new IllegalStateException();
					}
					return true;
				}
				
				PendingOp bMonkey = null;
				for(PendingOp op : ops) if (op.monkeyName.equals(b)) bMonkey = op;
				if (bMonkey==null) {
					System.out.println("Cannot find monkey '"+b+"'");
					return false; //TODO: Maybe throw!
				}
				
				switch(this.op) {
					//result = av / x -> result * x = av -> x = av / result
					case "/" -> bMonkey.reverseEvaluate(av / result, values, ops);
					//result = av * x -> result / av = x
					case "*" -> bMonkey.reverseEvaluate(result / av, values, ops);
					//result = av + x -> x = result - av
					case "+" -> bMonkey.reverseEvaluate(result - av, values, ops);
					//result = av - x -> result + -av = -x -> -result + av = x -> av-result = x
					case "-" -> bMonkey.reverseEvaluate(av - result, values, ops);
					//av = x = result
					case "=" -> bMonkey.reverseEvaluate(av, values, ops);
				}
				
				return true;
			}
			
			return false;
		}
		
		@Override
		public String toString() {
			return monkeyName+": "+a+" "+op+" "+b;
		}
	}

	@Override
	public void partTwo(List<String> input) {
		/*
		 * We're going to go ahead and yank the entire root: and humn: entries into special places, and only act when
		 * everything else has been evaluated
		 */
		
		PendingOp root = new PendingOp();
		root.monkeyName = "root";
		root.op = "=";
		
		PendingOp human = new PendingOp();
		human.monkeyName = "humn";
		human.op = "Solve for Human";
		
		
		HashMap<String, Long> values = new HashMap<>();
		Deque<PendingOp> pendingOps = new ArrayDeque<>();
		
		for(int i = 0; i<input.size(); i++) {
			String[] pieces = input.get(i).split("\\s");
			String monkeyName = pieces[0].substring(0, pieces[0].length()-1).trim();
			if (monkeyName.equals("humn")) continue;
			if (monkeyName.equals("root")) {
				root.a = pieces[1].trim();
				root.b = pieces[3].trim();
				continue;
			}
			
			if (pieces.length == 2) {
				long value = Long.parseLong(pieces[1].trim());
				values.put(monkeyName, value);
			} else if (pieces.length == 4) {
				PendingOp op = new PendingOp();
				op.a = pieces[1].trim();
				op.b = pieces[3].trim();
				op.op = pieces[2].trim();
				op.monkeyName = monkeyName;
				
				if (!op.evaluate(values)) pendingOps.addLast(op);
			}
		}
		
		System.out.println("Initial State:");
		System.out.println("  Known Values: "+values);
		System.out.println("  Pending Ops: "+pendingOps);
		System.out.println();
		
		Deque<PendingOp> nextPending = new ArrayDeque<>();
		
		boolean didAnythingThisRound = false;
		while(!pendingOps.isEmpty()) {
			PendingOp op = pendingOps.removeFirst();
			if (!op.evaluate(values)) {
				nextPending.addLast(op);
			} else {
				didAnythingThisRound = true;
			}
			if (pendingOps.isEmpty() && !nextPending.isEmpty()) {
				System.out.println("End of Round:");
				
				if (didAnythingThisRound) {
					pendingOps = nextPending;
					nextPending = new ArrayDeque<>();
					
					System.out.println("  Known Values: "+values);
					System.out.println("  Pending Ops: "+pendingOps);
					System.out.println();
					didAnythingThisRound = false;
				}
			}
		}
		
		pendingOps = nextPending;
		
		System.out.println("Simulation Complete:");
		System.out.println("  Known Values: "+values);
		System.out.println("  Pending Ops: "+pendingOps);
		System.out.println();
		System.out.println(root);
		
		//Now fill in root
		root.reverseEvaluate(0, values, pendingOps);
		System.out.println("  Known Values: "+values);
		System.out.println("  Pending Ops: "+pendingOps);
		
		System.out.println();
		System.out.println("Human should yell "+values.get("humn"));
	}

	@Override
	protected String getRawSampleData() {
		return """
				root: pppw + sjmn
				dbpl: 5
				cczh: sllz + lgvd
				zczc: 2
				ptdq: humn - dvpt
				dvpt: 3
				lfqf: 4
				humn: 5
				ljgn: 2
				sjmn: drzm * dbpl
				sllz: 4
				pppw: cczh / lfqf
				lgvd: ljgn * ptdq
				drzm: hmdt - zczc
				hmdt: 32
				""";
	}

}
