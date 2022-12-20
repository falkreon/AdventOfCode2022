package blue.endless.advent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

import blue.endless.advent.util.Day;
import static blue.endless.advent.util.Strings.*;

/*
 * Today's problem is going to take some explaining. We haven't had a graph theory problem yet this year so I'm excited!
 * 
 * This is a perfect-information non-random mathematical game. We make choices along the way, and not only do you know
 * the complete effects of your choices once you make them, but because the game has clear rules you know these effects
 * before you make them. This puts this valve game in a class of high-strategy games like Chess and Go.
 * 
 * Now, we won't see anything quite like Go in an Advent of Code because it wasn't solved for nearly fifty years, but
 * extremely advanced chess has been played by computers for a very long time.
 * 
 * The idea behind a chess bot is that since the effects of a proposed move are known before you make it, you can give
 * each move a numerical score based on how much it moves you towards the win condition or, negatively, towards the lose
 * condition. We call this function that evaluates a proposed move the "evaluation function". Often in order to fairly
 * score a move, we need to "look ahead" and consider all possible opponent moves, and then all our moves after the
 * opponent, and so on. All these choices and side effects make up a "decision tree", and we walk the tree to score our
 * move.
 * 
 * This valve game is slightly simpler than chess because we don't have an opponent, and the maximum game length is 30
 * moves. That makes the chess approach a slam dunk because we don't even need to do cycle ("threefold repetition")
 * detection. After all, if we travel in a circle for six moves at the end of a valve game, all the opened valves are
 * still flowing, so it could possibly be an optimal move! (with the same score as standing still for that time.)
 * 
 * So to solve this test we need to do the following:
 * 
 * - Construct the graph of tunnels, valves, and flow rates
 * 
 * - Construct a representation of a path from the start of the game (the root of the decision tree) to the end (a leaf
 *   node at depth 30)
 *   
 *   - Prefix codes (with depth less than 30) will represent choosing passes (standing still) for the rest of the game.
 * 
 * - Create an evaluation function to evaluate depth 30 paths
 * 
 * - Create a recursive evaluation function to traverse the decision tree for codes that are not yet complete.
 * 
 * - Finally, put it all together and simulate every possible outcome to find the optimal strategy for this graph.
 */


/*
 * Update: The decision tree is far, far too big. With a training set of 10 nodes, that's something shy of 12^30
 * strategies to evaluate (12^30 includes invalid strategies like following tunnels between nodes which aren't linked),
 * and it turns out the limits of my hardware if I really push it are about 12^16. So we'll need a new strategy.
 * 
 * 
 * The new strategy will be to evaluate locally optimal moves starting with the beginning of the game.
 * 
 * For each valve, we propose a move starting with the current prefix:
 * 
 * - Pathfind through the graph to find the nearest route to that valve, appending the path to the move proposal
 * 
 * - Append Move.TURN_VALVE to the move proposal
 * 
 * - Assume Move.PASS for the rest of the game
 * 
 * - Evaluate the move's total score
 * 
 * The move with the highest total score will not only be the locally optimal move, it'll be the globally optimal one,
 * because delaying the valve with the greatest overall benefit will cause a less optimal result.
 */

/*
 * Update update: Picking locally optimal solutions PRODUCES A GLOBALLY SUBOPTIMAL RESULT!
 * 
 * 
 * 
 */

public class Day16 extends Day {

	public static class GraphNode {
		public final int flowRate;
		public final String name;
		
		public GraphNode(String name, int flowRate) {
			this.name = name;
			this.flowRate = flowRate;
		}
		
		public String toString() {
			return name;
			//return "{"+name+" flowRate="+flowRate+"}";
		}
	}
	
	public static class Graph {
		Map<String, GraphNode> nodes = new HashMap<>();
		Map<String, List<String>> adjacency = new HashMap<>(); //Adjacency has a *defined* order so that we can reliably visit all nodes in the decision tree
		
		public Collection<GraphNode> nodes() {
			return nodes.values();
		}
		
		public GraphNode getNode(String name) {
			return nodes.get(name);
		}
		
		public List<GraphNode> getAdjacent(String nodeName) {
			List<GraphNode> result = new ArrayList<>();
			
			List<String> list = adjacency.get(nodeName);
			if (list==null) return result;
			for(String s : list) {
				GraphNode node = nodes.get(s);
				if (node!=null) result.add(node);
			}
			
			return result;
		}
		
		public boolean isEdge(String from, String to) {
			List<String> list = adjacency.get(from);
			if (list==null) return false;
			return list.contains(to);
		}
		
		public void addNode(GraphNode node) {
			nodes.put(node.name, node);
		}
		
		public void addEdge(String from, String to) {
			List<String> list = adjacency.get(from);
			if (list==null) {
				list = new ArrayList<>();
				adjacency.put(from, list);
			}
			
			list.add(to);
		}
		
		@Override
		public String toString() {
			return nodes.values().toString();
		}
	}
	
	public Graph unpackTunnels(List<String> lines) {
		Graph result = new Graph();
		for(String s : lines) {
			String[] parts = s.split("\\s");
			String name = parts[1];
			String ratePart = parts[4];
			String[] ratePartParts = ratePart.split("=");
			String numberString = ratePartParts[1].substring(0, ratePartParts[1].length()-1);
			Integer flowRate = Integer.parseInt(numberString);
			
			GraphNode node = new GraphNode(name, flowRate);
			
			int loc = s.indexOf("valve");
			String end = s.substring(loc);
			end = end.substring("valve".length());
			if (end.startsWith("s")) end = end.substring(1);
			end = end.substring(1);
			String[] edgeParts = end.split(",");
			for(String edgePart : edgeParts) {
				result.addEdge(name, edgePart.trim());
			}
			
			result.addNode(node);
		}
		
		return result;
	}
	
	public int score(Graph map, String strategy, boolean loud) {
		Set<GraphNode> openValves = new HashSet<>();
		
		String[] parts = strategy.split("\\s");
		
		int totalFlow = 0;
		GraphNode curNode = map.getNode("AA");
		if (loud) System.out.println("Starting at "+curNode);
		
		for(int i=0; i<30; i++) {
			if (loud) System.out.println("== Minute "+(i+1)+" ==");
			if (openValves.isEmpty()) {
				if (loud) System.out.println("No valves are open.");
			} else {
				int released = 0;
				for(GraphNode node : openValves) released += node.flowRate;
				totalFlow += released;
				
				if (loud) System.out.println("Valves "+openValves+" are open, releasing "+released+" pressure.");
			}
			
			String move = (i<parts.length) ? parts[i].trim() : "..";
			switch(move) {
				case "++" -> {
					openValves.add(curNode);
					if (loud) System.out.println("You open valve "+curNode.name+".");
				}
				case ".." -> {} //Wait
				default -> {
					boolean validEdge = false;
					List<GraphNode> edges = map.getAdjacent(curNode.name);
					for(GraphNode g : edges) {
						if (g.name.equals(move)) {
							curNode = g;
							validEdge = true;
							if (loud) System.out.println("You move to valve "+curNode.name+".");
							break;
						}
					}
					if (!validEdge) {
						System.out.println("Not recognized: '"+move+"'");
					}
				}
			}
		}
		
		return totalFlow;
	}
	
	public String packPartTwo(List<BiMove> strategy) {
		StringBuilder result = new StringBuilder();
		
		for(BiMove move : strategy) {
			result.append(move.a.value());
			result.append(',');
			result.append(move.b.value());
			result.append(' ');
		}
		
		result.deleteCharAt(result.length()-1);
		
		return result.toString();
	}
	
	public List<BiMove> unpackPartTwo(String strategy) {
		String[] parts = strategy.split("\\s");
		List<BiMove> result = new ArrayList<>();
		
		for(String s : parts) {
			if (s.isBlank()) continue;
			String[] moves = s.split(",");
			if (moves.length<2) throw new IllegalArgumentException();
			result.add(new BiMove(moves[0], moves[1]));
		}
		
		while(result.size()<26) result.add(BiMove.BOTH_WAIT);
		
		return result;
	}
	
	public int scorePartTwo(Graph map, List<BiMove> strategy, boolean loud) {
		Set<GraphNode> openValves = new HashSet<>();
		int totalFlow = 0;
		GraphNode aNode = map.getNode("AA"); //Human
		GraphNode bNode = aNode; //Elephant
		
		for(int i=0; i<26; i++) {
			if (loud) System.out.println("== Minute "+(i+1)+" ==");
			if (openValves.isEmpty()) {
				if (loud) System.out.println("No valves are open.");
			} else {
				int released = 0;
				for(GraphNode node : openValves) released += node.flowRate;
				totalFlow += released;
				
				if (loud) {
					if (openValves.size()==1) {
						System.out.println("Valve "+openValves.iterator().next()+" is open, releasing "+released+" pressure.");
					} else {
						System.out.println("Valves "+openValves+" are open, releasing "+released+" pressure.");
					}
				}
			}
			
			BiMove curMove = (i<strategy.size()) ? strategy.get(i) : BiMove.BOTH_WAIT;
			switch(curMove.a().value()) {
				case ".." -> {}
				case "++" -> {
					if (openValves.contains(aNode)) {
						if (loud) System.out.println("You go to turn valve "+aNode.name+" but it is already open.");
					} else {
						if (loud) System.out.println("You open valve "+aNode.name+".");
						openValves.add(aNode);
					}
				}
				default -> {
					if (map.isEdge(aNode.name, curMove.a().value)) {
						aNode = map.getNode(curMove.a().value());
						if (loud) System.out.println("You move to valve "+aNode.name+".");
					} else {
						if (loud) System.out.println("You bump into a wall trying to go to valve "+curMove.a()+".");
					}
				}
			}
			switch(curMove.b().value()) {
				case ".." -> {}
				case "++" -> {
					if (openValves.contains(bNode)) {
						if (loud) System.out.println("The elephant goes to turn valve "+bNode.name+" but it is already open.");
					} else {
						if (loud) System.out.println("The elephant opens valve "+bNode.name+".");
						openValves.add(bNode);
					}
				}
				default -> {
					if (map.isEdge(bNode.name, curMove.b().value)) {
						bNode = map.getNode(curMove.b().value());
						if (loud) System.out.println("The elephant moves to valve "+bNode.name+".");
					} else {
						if (loud) System.out.println("You bump into a wall trying to go to valve "+curMove.b()+".");
					}
				}
			}
			if (loud) System.out.println();
		}
		
		return totalFlow;
	}
	
	public record Move(String value) {
		public static final Move WAIT = new Move("..");
		public static final Move TURN_VALVE = new Move("++");
	}
	
	public record BiMove(Move a, Move b) {		
		public static final BiMove BOTH_WAIT = new BiMove(Move.WAIT, Move.WAIT);
		
		public BiMove(String a, String b) {
			this(new Move(a), new Move(b));
		}
		
		@Override
		public String toString() {
			return a.value+","+b.value;
		}
	}
	
	
	public List<String> pathfind(Graph map, String from, String to) {
		ArrayList<String> result = new ArrayList<>();
		if (from.equals(to)) return result;
		
		Deque<List<String>> queue = new ArrayDeque<>();
		Set<String> traversed = new HashSet<>();
		traversed.add(from);
		List<String> seed = new ArrayList<>();
		seed.add(from);
		queue.add(seed);
		
		while(!queue.isEmpty()) {
			List<String> prefix = queue.removeFirst();
			List<GraphNode> adj = map.getAdjacent(prefix.get(prefix.size()-1));
			for(GraphNode maybeTo : adj) {
				if (maybeTo.name.equals(to)) {
					prefix.add(to);
					prefix.remove(0);
					return prefix;
				} else {
					if (traversed.contains(maybeTo.name)) continue;
					
					List<String> nextPrefix = new ArrayList<String>(prefix);
					nextPrefix.add(maybeTo.name);
					traversed.add(maybeTo.name);
					queue.add(nextPrefix);
				}
			}
		}
		
		throw new IllegalStateException("Cannot pathfind from "+from+" to "+to);
	}
	
	public List<Move> appendBestMove(Graph map, List<Move> strategy) {
		//Figure out where this strat leaves us
		GraphNode from = map.getNode("AA");
		for(Move move : strategy) {
			if (move.equals(Move.TURN_VALVE)) continue;
			if (move.equals(Move.WAIT)) continue;
			
			GraphNode node = map.getNode(move.value());
			if (node!=null) from = node;
		}
		
		int baseScore = score(map, pack(strategy), false);
		
		//Go through each potential destination from "from"
		//List<List<Move>> strategies = new ArrayList<>();
		List<Move> bestStrategy = strategy;
		int bestScore = score(map, pack(strategy), false);
		int progress = 0;
		for(GraphNode to : map.nodes()) {
			if (strategy.size()==0) {
				//This is the top level, let's give progress updates
				progress++;
				String progressBar = "[";
				for(int i=0; i<map.nodes().size(); i++) {
					progressBar += ((i<progress) ? "#" : ".");
				}
				progressBar += "]";
				System.out.println(progressBar+" "+pack(bestStrategy)+" ("+bestScore+" vented)");
			}
			if (to.equals(from)) continue;
			if (to.flowRate==0) continue;
			
			List<String> path = pathfind(map, from.name, to.name);
			//System.out.println("From "+from+" to "+to+": "+path);
			
			List<Move> result = new ArrayList<>(strategy);
			for(String s : path) result.add(new Move(s));
			result.add(Move.TURN_VALVE);
			
			int proposalScore = score(map, pack(result), false);
			if (proposalScore<=baseScore) continue;
			
			if (result.size()>30) continue;
			
			if (result.size()<30) result = appendBestMove(map, result);
			
			//strategies.add(result);
			String packed = pack(result);
			int score = score(map, packed, false);
			//if (result.size()==30) System.out.println("Possible path: "+packed+": "+score);
			//System.out.println("Possible path: "+packed+": "+score);
			if (score>bestScore) {
				//System.out.println("New best score: "+packed+": "+score);
				//System.out.print(".");
				bestStrategy = result;
				bestScore = score;
			}
		}
		
		return bestStrategy;
	}
	
	private String normalizeRedditPartTwo(String input) {
		if (Character.isDigit(input.charAt(0))) {
			int space = input.indexOf(' ');
			input = input.substring(space).trim();
		}
		
		if (input.startsWith("|")) input = input.substring(1);
		
		return input;
	}
	
	public List<BiMove> unpackRedditPartTwo(Graph map, String packed) {
		String[] lines = packed.split("\n");
		if (lines.length!=2) throw new IllegalArgumentException();
		String a = normalizeRedditPartTwo(lines[0]);
		String b = normalizeRedditPartTwo(lines[1]);
		
		String[] aParts = a.split("|");
		String[] bParts = b.split("|");
		
		List<BiMove> unpacked = new ArrayList<>();
		for(int i=0; i<26; i++) {
			String aPart = (i<aParts.length) ? aParts[i] : "..";
			String bPart = (i<bParts.length) ? bParts[i] : "..";
			unpacked.add(new BiMove(aPart, bPart));
		}
		
		return unpacked;
	}
	
	public List<BiMove> fold(List<Move> a, List<Move> b) {
		List<BiMove> result = new ArrayList<>();
		
		for(int i=0; i<26; i++) {
			String aPart = (i<a.size()) ? a.get(i).value : "..";
			String bPart = (i<b.size()) ? b.get(i).value : "..";
			result.add(new BiMove(aPart, bPart));
		}
		
		return result;
	}
	
	public List<Move> formProposal(List<Move> baseStrategy, List<String> path) {
		ArrayList<Move> result = new ArrayList<>(baseStrategy);
		for(String s : path) {
			result.add(new Move(s));
		}
		result.add(Move.TURN_VALVE);
		
		return result;
	}
	
	public record Pair<T>(T left, T right) {}
	
	public <T> List<Pair<T>> cartesianProduct(List<T> ts, boolean duplicates) {
		List<Pair<T>> result = new ArrayList<>();
		for(T t : ts) {
			for(T u : ts) {
				if (!duplicates && t.equals(u)) continue;
				result.add(new Pair<>(t, u));
			}
		}
		return result;
	}
	
	public <T> void withCartesianProduct(List<T> ts, boolean duplicates, BiConsumer<T,T> consumer) {
		for(T t : ts) {
			for(T u : ts) {
				if (!duplicates && t.equals(u)) continue;
				consumer.accept(t, u);
			}
		}
	}
	
	public List<BiMove> appendBestPartTwo(Graph map, List<Move> baseAStrategy, List<Move> baseBStrategy) {
		/**
		 * This is a little different from Part 1 in that there will be a reddit form and an expanded form.
		 */
		
		GraphNode fromA = map.getNode("AA");
		GraphNode fromB = fromA;
		
		//Figure out what valve this prefix leaves each of us at
		for(Move move : baseAStrategy) {
			if (!(move.equals(Move.TURN_VALVE) || move.equals(Move.WAIT))) {
				GraphNode maybe = map.getNode(move.value());
				if (maybe!=null) fromA = maybe;
			}
		}
		
		for(Move move : baseBStrategy) {
			if (!(move.equals(Move.TURN_VALVE) || move.equals(Move.WAIT))) {
				GraphNode maybe = map.getNode(move.value());
				if (maybe!=null) fromB = maybe;
			}
		}
		
		//Now that we know what valves we're coming from, go through each combination of 'to' valves to find the best
		//followup moves
		
		List<BiMove> bestStrategy = fold(baseAStrategy, baseBStrategy);
		int bestScore = scorePartTwo(map, bestStrategy, false);
		int progress = 0;
		int maxProgress = map.nodes.size();
		
		for(GraphNode toA : map.nodes()) {
			if (baseAStrategy.isEmpty() && baseBStrategy.isEmpty()) {
				//This is the top level, let's give progress updates
				progress++;
				String progressBar = "[";
				for(int i=0; i<maxProgress; i++) {
					progressBar += ((i<progress) ? "#" : ".");
				}
				progressBar += "]";
				System.out.println(progressBar+" "+packPartTwo(bestStrategy)+" ("+bestScore+" vented)");
			}
			
			//Reject useless avenues of inquiry
			if (toA.equals(fromA)) continue;
			if (toA.flowRate==0) continue;
			
			//Create a proposal for a new path
			
			List<Move> pathA = formProposal(baseAStrategy, pathfind(map, fromA.name, toA.name));
			if (pathA.size()>26) continue; //This path is unviable in the amount of time we have.
			
			for(GraphNode toB : map.nodes()) {
				if (toB.equals(fromB)) continue;
				if (toB.flowRate==0) continue;
				
				List<Move> pathB = formProposal(baseBStrategy, pathfind(map, fromB.name, toB.name));
				if (pathB.size()>26) continue; //Again, this path is unviable given the remaining time.
				
				List<BiMove> proposal = fold(pathA, pathB);
				int proposalScore = scorePartTwo(map, proposal, false);
				if (proposalScore>bestScore) {
					bestStrategy = proposal;
					bestScore = proposalScore;
				}
				
				
			}
		}
		
		return bestStrategy;
	}
	
	public List<Move> unpackReddit(Graph map, String reddit) {
		if (reddit.startsWith("|")) reddit = reddit.substring(1);
		String[] pieces = reddit.split(Pattern.quote("|"));
		int startAt = (pieces[0].equals("AA")) ? 1 : 0;
		
		List<Move> result = new ArrayList<>();
		String from = "AA";
		for(int i=startAt; i<pieces.length; i++) {
			String to = pieces[i];
			List<String> path = pathfind(map, from, to);
			for(String s : path) result.add(new Move(s));
			result.add(Move.TURN_VALVE);
			from = to;
		}
		
		return result;
	}
	
	public String pack(List<Move> strategy) {
		List<Move> strat = new ArrayList<>(strategy);
		while(strat.size()<30) strat.add(new Move(".."));
		
		StringBuilder b = new StringBuilder();
		for(Move m : strat) {
			b.append(m.value());
			b.append(' ');
		}
		b.deleteCharAt(b.length()-1);
		return b.toString();
	}
	
	public List<Move> unpack(String s) {
		if (s.isBlank()) return new ArrayList<>();
		String[] parts = s.split("\\s");
		List<Move> result = new ArrayList<>();
		for(String t : parts) result.add(new Move(t.trim()));
		return result;
	}
	
	@Override
	public void partOne(List<String> input) {
		Graph map = unpackTunnels(input);
		
		System.out.println(map);
		System.out.println(map.adjacency);
		System.out.println();
		
		List<Move> strat = new ArrayList<>();
		strat = appendBestMove(map, strat);
		
		
		String bestStrat = pack(strat);
		int bestScore = score(map, bestStrat, false);
		System.out.println("Best Strategy: "+bestStrat+" ("+bestScore+" vented)");
		
	}

	@Override
	public void partTwo(List<String> input) {
		Graph map = unpackTunnels(input);
		
		System.out.println(map);
		System.out.println(map.adjacency);
		System.out.println();
		
		List<BiMove> strategy = unpackPartTwo("II,DD JJ,++ ++,EE II,FF AA,GG BB,HH ++,++ CC,GG ++,FF ..,EE ..,++");
		System.out.println(packPartTwo(strategy)+": "+scorePartTwo(map, strategy, false));
	}
	
	
	
	
	

	@Override
	protected String getRawSampleData() {
		//Original example; optimal flow rate 1651 (passed)
		
		return """
				Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
				Valve BB has flow rate=13; tunnels lead to valves CC, AA
				Valve CC has flow rate=2; tunnels lead to valves DD, BB
				Valve DD has flow rate=20; tunnels lead to valves CC, AA, EE
				Valve EE has flow rate=3; tunnels lead to valves FF, DD
				Valve FF has flow rate=0; tunnels lead to valves EE, GG
				Valve GG has flow rate=0; tunnels lead to valves FF, HH
				Valve HH has flow rate=22; tunnel leads to valve GG
				Valve II has flow rate=0; tunnels lead to valves AA, JJ
				Valve JJ has flow rate=21; tunnel leads to valve II
				""";
		
		
		//Reddit example 1; optimal flow rate 2640 (passed)
		/*
		return """
				Valve AA has flow rate=0; tunnels lead to valves BA
				Valve BA has flow rate=2; tunnels lead to valves AA, CA
				Valve CA has flow rate=4; tunnels lead to valves BA, DA
				Valve DA has flow rate=6; tunnels lead to valves CA, EA
				Valve EA has flow rate=8; tunnels lead to valves DA, FA
				Valve FA has flow rate=10; tunnels lead to valves EA, GA
				Valve GA has flow rate=12; tunnels lead to valves FA, HA
				Valve HA has flow rate=14; tunnels lead to valves GA, IA
				Valve IA has flow rate=16; tunnels lead to valves HA, JA
				Valve JA has flow rate=18; tunnels lead to valves IA, KA
				Valve KA has flow rate=20; tunnels lead to valves JA, LA
				Valve LA has flow rate=22; tunnels lead to valves KA, MA
				Valve MA has flow rate=24; tunnels lead to valves LA, NA
				Valve NA has flow rate=26; tunnels lead to valves MA, OA
				Valve OA has flow rate=28; tunnels lead to valves NA, PA
				Valve PA has flow rate=30; tunnels lead to valves OA
				""";*/
		
		//Reddit example 2; optimal flow rate 13468 (passed)
		/*
		return """
				Valve AA has flow rate=0; tunnels lead to valves BA
				Valve BA has flow rate=1; tunnels lead to valves AA, CA
				Valve CA has flow rate=4; tunnels lead to valves BA, DA
				Valve DA has flow rate=9; tunnels lead to valves CA, EA
				Valve EA has flow rate=16; tunnels lead to valves DA, FA
				Valve FA has flow rate=25; tunnels lead to valves EA, GA
				Valve GA has flow rate=36; tunnels lead to valves FA, HA
				Valve HA has flow rate=49; tunnels lead to valves GA, IA
				Valve IA has flow rate=64; tunnels lead to valves HA, JA
				Valve JA has flow rate=81; tunnels lead to valves IA, KA
				Valve KA has flow rate=100; tunnels lead to valves JA, LA
				Valve LA has flow rate=121; tunnels lead to valves KA, MA
				Valve MA has flow rate=144; tunnels lead to valves LA, NA
				Valve NA has flow rate=169; tunnels lead to valves MA, OA
				Valve OA has flow rate=196; tunnels lead to valves NA, PA
				Valve PA has flow rate=225; tunnels lead to valves OA
				""";*/
		
		//Reddit example 3; optimal flow rate 1288 (FAILED)
		//Optimal path is AA|CA|EA|GA|IA|KA|MA|NA|OA|PA|BA 1288
		//Optimal path, expanded:
		//  "BA CA ++ DA EA ++ FA GA ++ HA IA ++ JA KA ++ LA MA ++ NA ++ OA ++ PA ++ AA BA ++ .. .. .."
		//Generated path  AA|BA|CA|EA|GA|IA|KA|MA|OA|NA|PA 1264
		//At great length, bugs in this path were fixed by improvements to pathfinding.
		/*
		return """
				Valve BA has flow rate=2; tunnels lead to valves AA, CA
				Valve CA has flow rate=10; tunnels lead to valves BA, DA
				Valve DA has flow rate=2; tunnels lead to valves CA, EA
				Valve EA has flow rate=10; tunnels lead to valves DA, FA
				Valve FA has flow rate=2; tunnels lead to valves EA, GA
				Valve GA has flow rate=10; tunnels lead to valves FA, HA
				Valve HA has flow rate=2; tunnels lead to valves GA, IA
				Valve IA has flow rate=10; tunnels lead to valves HA, JA
				Valve JA has flow rate=2; tunnels lead to valves IA, KA
				Valve KA has flow rate=10; tunnels lead to valves JA, LA
				Valve LA has flow rate=2; tunnels lead to valves KA, MA
				Valve MA has flow rate=10; tunnels lead to valves LA, NA
				Valve NA has flow rate=2; tunnels lead to valves MA, OA
				Valve OA has flow rate=10; tunnels lead to valves NA, PA
				Valve PA has flow rate=2; tunnels lead to valves OA, AA
				Valve AA has flow rate=0; tunnels lead to valves BA, PA
				""";*/
	}

	@Override
	protected String getDataFileName() {
		return "day16.txt";
	}
	
}
