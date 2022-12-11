package blue.endless.advent;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

public class Day11 {
	public static String SAMPLE_INPUT = """
			Monkey 0:
			  Starting items: 79, 98
			  Operation: new = old * 19
			  Test: divisible by 23
			    If true: throw to monkey 2
			    If false: throw to monkey 3
			
			Monkey 1:
			  Starting items: 54, 65, 75, 74
			  Operation: new = old + 6
			  Test: divisible by 19
			    If true: throw to monkey 2
			    If false: throw to monkey 0
			
			Monkey 2:
			  Starting items: 79, 60, 97
			  Operation: new = old * old
			  Test: divisible by 13
			    If true: throw to monkey 1
			    If false: throw to monkey 3
			
			Monkey 3:
			  Starting items: 74
			  Operation: new = old + 3
			  Test: divisible by 17
			    If true: throw to monkey 0
			    If false: throw to monkey 1
			""";
	
	public void run() {
		//run(AdventOfCode.processSampleInput(SAMPLE_INPUT));
		
		
		try {
			run(Files.readAllLines(Path.of("data", "day11.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<Monkey> getMonkeys(List<String> input) {
		ArrayList<Monkey> monkeys = new ArrayList<>();
		Monkey cur = null;
		
		for(String line : input) {
			if (line.startsWith("Monkey ")) {
				if (cur!=null) monkeys.add(cur);
				
				cur = new Monkey();
			} else if (line.startsWith("  Starting items: ")) {
				String itemList = line.substring("  Starting items: ".length());
				String[] itemParts = itemList.split(",");
				for(String itemString : itemParts) {
					BigInteger itemWorry = BigInteger.valueOf(Long.parseLong(itemString.trim()));
					cur.items.add(itemWorry);
				}
			} else if (line.startsWith("  Operation: new = ")) {
				String opString = line.substring("  Operation: new = ".length());
				String[] opParts = opString.split("\\s");
				if (opParts.length!=3) throw new IllegalArgumentException();
				
				cur.expression = new Expression();
				cur.expression.aToken = (opParts[0].equals("old")) ? Token.OLD_VALUE : Token.of(Integer.parseInt(opParts[0]));
				cur.expression.op = switch(opParts[1]) {
					case "+" -> OP_ADD;
					case "*" -> OP_MUL;
					default -> throw new IllegalArgumentException();
				};
				cur.expression.bToken = (opParts[2].equals("old")) ? Token.OLD_VALUE : Token.of(Integer.parseInt(opParts[2]));
			} else if (line.startsWith("  Test: divisible by ")) {
				String testString = line.substring("  Test: divisible by ".length());
				cur.testDivisibleBy = Integer.parseInt(testString);
			} else if (line.startsWith("    If true: throw to monkey ")) {
				String trueString = line.substring("    If true: throw to monkey ".length());
				cur.trueTarget = Integer.parseInt(trueString);
			} else if (line.startsWith("    If false: throw to monkey ")) {
				String falseString = line.substring("    If false: throw to monkey ".length());
				cur.falseTarget = Integer.parseInt(falseString);
			}
		}
		if (cur!=null) monkeys.add(cur);
		
		return monkeys;
	}
	
	public void run(List<String> input) {
		//Assimilate input
		List<Monkey> monkeys = getMonkeys(input);
		
		//Print out initial state
		for(Monkey m : monkeys) {
			System.out.println(m);
			System.out.println();
		}
		
		//Run 20 rounds
		for(int round = 0; round<20; round++) {
			for(int i=0; i<monkeys.size(); i++) {
				Monkey m = monkeys.get(i);
				//System.out.println("Monkey "+i+":");
				m.throwItems(monkeys);
			}
		}
		
		
		//Print out final state
		for(Monkey m : monkeys) {
			System.out.println(m);
			System.out.println();
		}
		
		//Get problem answer
		Monkey topMonkey = null;
		for(Monkey m : monkeys) {
			if (topMonkey==null || m.inspections.compareTo(topMonkey.inspections)>0) {
				topMonkey = m;
			}
		}
		monkeys.remove(topMonkey);
		
		Monkey secondMonkey = null;
		for(Monkey m : monkeys) {
			if (secondMonkey==null || m.inspections.compareTo(secondMonkey.inspections)>0) {
				secondMonkey = m;
			}
		}
		
		BigInteger topTwo = topMonkey.inspections.multiply(secondMonkey.inspections);
		System.out.println("Top two monkeys multiplied: "+topTwo);
	}
	
	public static class Monkey {
		public ArrayList<BigInteger> items = new ArrayList<>();
		public Expression expression;
		public int testDivisibleBy;
		public int trueTarget;
		public int falseTarget;
		
		public BigInteger inspections = BigInteger.ZERO;
		
		public void throwItems(List<Monkey> targets) {
			for(BigInteger item : items) {
				//System.out.println("  Monkey inspects an item with a worry level of "+item);
				BigInteger inspected = expression.apply(item);
				inspections = inspections.add(BigInteger.ONE);
				//System.out.println("    Worry level becomes "+inspected);
				inspected = inspected.divide(BigInteger.valueOf(3));
				//inspected /= 3;
				//System.out.println("    Monkey gets bored of item. Worry level is divided by 3 to "+inspected);
				
				if (inspected.remainder(BigInteger.valueOf(testDivisibleBy)).equals(BigInteger.ZERO)) {
				//if (inspected % testDivisibleBy == 0) {
					//System.out.println("    Current worry level is divisible by "+testDivisibleBy);
					//System.out.println("    Item with worry level "+inspected+" is thrown to monkey "+trueTarget);
					targets.get(trueTarget).items.add(inspected);
				} else {
					//System.out.println("    Current worry level is NOT divisible by "+testDivisibleBy);
					//System.out.println("    Item with worry level "+inspected+" is thrown to monkey "+falseTarget);
					targets.get(falseTarget).items.add(inspected);
				}
			}
			items.clear();
		}
		
		public void throwItemsAnxious(List<Monkey> targets, BigInteger lcm) {
			for(BigInteger item : items) {
				System.out.println("  Monkey inspects an item with a worry level of "+item);
				BigInteger inspected = expression.apply(item);
				inspections = inspections.add(BigInteger.ONE);
				
				//##### We are no longer dividing by 3. Instead, we can take the remainder of the LCM #####
				//inspected = inspected.divide(BigInteger.valueOf(3));
				inspected = inspected.remainder(lcm);
				
				System.out.println("    Monkey gets bored of item. Worry level is divided by 3 to "+inspected);
				
				if (inspected.remainder(BigInteger.valueOf(testDivisibleBy)).equals(BigInteger.ZERO)) {
				//if (inspected % testDivisibleBy == 0) {
					System.out.println("    Current worry level is divisible by "+testDivisibleBy);
					System.out.println("    Item with worry level "+inspected+" is thrown to monkey "+trueTarget);
					targets.get(trueTarget).items.add(inspected);
				} else {
					System.out.println("    Current worry level is NOT divisible by "+testDivisibleBy);
					System.out.println("    Item with worry level "+inspected+" is thrown to monkey "+falseTarget);
					targets.get(falseTarget).items.add(inspected);
				}
			}
			items.clear();
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			
			builder.append("Items: ");
			builder.append(items);
			builder.append('\n');
			
			builder.append("Operation: new = ");
			builder.append(expression);
			builder.append('\n');
			
			builder.append("Test: divisible by ");
			builder.append(testDivisibleBy);
			builder.append('\n');
			
			builder.append("  If true: throw to monkey ");
			builder.append(trueTarget);
			builder.append('\n');
			
			builder.append("  If false: throw to monkey ");
			builder.append(falseTarget);
			builder.append('\n');
			
			builder.append("Inspected items "+inspections+" times.");
			
			return builder.toString();
		}
	}
	
	public static abstract class Token {
		public static final Token OLD_VALUE = new OldToken();
		public abstract BigInteger getValue(BigInteger oldValue);
		public static IntToken of(int value) { return new IntToken(value); }
	}
	
	public static class OldToken extends Token {
		@Override
		public BigInteger getValue(BigInteger oldValue) {
			return oldValue;
		}
		
		@Override
		public String toString() {
			return "old";
		}
	}
	
	public static class IntToken extends Token {
		private BigInteger value;
		
		public IntToken(int value) {
			this.value = BigInteger.valueOf(value);
		}
		
		public IntToken(BigInteger value) {
			this.value = value;
		}
		
		@Override
		public BigInteger getValue(BigInteger oldValue) {
			return value;
		}
		
		@Override
		public String toString() {
			return ""+value;
		}
	}
	
	public static final BinaryOperator<BigInteger> OP_ADD = (a,b)->a.add(b);
	public static final BinaryOperator<BigInteger> OP_MUL = (a,b)->a.multiply(b);
	
	public static class Expression {
		private Token aToken = Token.of(0);
		private BinaryOperator<BigInteger> op = OP_ADD;
		private Token bToken = Token.of(0);
		
		public BigInteger apply(BigInteger old) {
			return op.apply(aToken.getValue(old), bToken.getValue(old));
		}
		
		@Override
		public String toString() {
			return
					aToken.toString() + " " +
					((op==OP_ADD) ? "+" : "*") + " " +
					bToken.toString();
		}
	}
	
	public void runPartB() {
		//runPartB(AdventOfCode.processSampleInput(SAMPLE_INPUT));
		
		try {
			runPartB(Files.readAllLines(Path.of("data", "day11.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void runPartB(List<String> input) {
		//Assimilate input
		List<Monkey> monkeys = getMonkeys(input);
		
		BigInteger lcm = BigInteger.ONE;
		for(Monkey m : monkeys) {
			lcm = lcm.multiply(BigInteger.valueOf(m.testDivisibleBy));
		}
		
		//Print out initial state
		for(Monkey m : monkeys) {
			System.out.println(m);
			System.out.println();
		}
		
		//Run 10,000 rounds
		for(int round = 0; round<10_000; round++) {
			for(int i=0; i<monkeys.size(); i++) {
				Monkey m = monkeys.get(i);
				System.out.println("Monkey "+i+":");
				m.throwItemsAnxious(monkeys, lcm);
			}
		}
		
		//Print out final state
		for(Monkey m : monkeys) {
			System.out.println(m);
			System.out.println();
		}
		
		//Get problem answer
		Monkey topMonkey = null;
		for(Monkey m : monkeys) {
			if (topMonkey==null || m.inspections.compareTo(topMonkey.inspections)>0) {
				topMonkey = m;
			}
		}
		monkeys.remove(topMonkey);
		
		Monkey secondMonkey = null;
		for(Monkey m : monkeys) {
			if (secondMonkey==null || m.inspections.compareTo(secondMonkey.inspections)>0) {
				secondMonkey = m;
			}
		}
		
		BigInteger topTwo = topMonkey.inspections.multiply(secondMonkey.inspections);
		System.out.println("Top two monkeys multiplied: "+topTwo);
	}
}
