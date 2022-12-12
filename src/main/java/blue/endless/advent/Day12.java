package blue.endless.advent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.List;

import blue.endless.advent.util.ArrayGrid;
import blue.endless.advent.util.Vec2i;

public class Day12 {
	public static String SAMPLE_INPUT =
			"""
			Sabqponm
			abcryxxl
			accszExk
			acctuvwj
			abdefghi
			""";
	
	public void run() {
		//run(AdventOfCode.processSampleInput(SAMPLE_INPUT));
		
		try {
			run(Files.readAllLines(Path.of("data", "day12.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(List<String> input) {
		ArrayGrid<Integer> map = new ArrayGrid<>(input.get(0).length(), input.size(), 0);
		map.elementToString(it->""+(char) ('a'+it.intValue()), false);
		
		Vec2i start = new Vec2i(0,0);
		Vec2i end = new Vec2i(0,0);
		
		for(int y=0; y<input.size(); y++) {
			String line = input.get(y);
			for(int x=0; x<line.length(); x++) {
				char cur = line.charAt(x);
				if (cur=='S') {
					start = new Vec2i(x,y);
					cur = 'a';
				} else if (cur=='E') {
					end = new Vec2i(x,y);
					cur = 'z';
				}
				
				map.set(x,y, cur-'a');
			}
		}
		
		System.out.println(map);
		System.out.println();
		
		ArrayGrid<Integer> gradient = createObjectiveMap(map, end);
		
		System.out.println(gradient);
		System.out.println();
		
		ArrayGrid<Character> visualExplanation = new ArrayGrid<>(map.getWidth(), map.getHeight(), '.');
		visualExplanation.elementToString(it->""+it, false);
		
		Vec2i cur = start;
		int steps = 0;
		while(!cur.equals(end)) {
			Vec2i west  = new Vec2i(cur.x()-1, cur.y());
			Vec2i east  = new Vec2i(cur.x()+1, cur.y());
			Vec2i north = new Vec2i(cur.x(), cur.y()-1);
			Vec2i south = new Vec2i(cur.x(), cur.y()+1);
			
			if (shouldMove(cur, west, map, gradient)) {
				visualExplanation.set(cur, '<');
				cur = west;
			} else if (shouldMove(cur, east, map, gradient)) {
				visualExplanation.set(cur, '>');
				cur = east;
			} else if (shouldMove(cur, north, map, gradient)) {
				visualExplanation.set(cur, '^');
				cur = north;
			} else if (shouldMove(cur, south, map, gradient)) {
				visualExplanation.set(cur, 'V');
				cur = south;
			} else {
				System.out.println("Stuck!");
				break;
			}
			
			steps++;
			if (steps>1000) break;
		}
		
		System.out.println(visualExplanation);
		System.out.println("Arrived in "+steps+" steps.");
	}
	
	public ArrayGrid<Integer> createObjectiveMap(ArrayGrid<Integer> terrain, Vec2i objective) {
		ArrayGrid<Integer> result = new ArrayGrid<>(terrain.getWidth(), terrain.getHeight(), Integer.MAX_VALUE);
		
		result.set(objective.x(), objective.y(), 0);
		ArrayDeque<Vec2i> queue = new ArrayDeque<>();
		queue.add(objective);
		while(!queue.isEmpty()) {
			Vec2i cur = queue.removeLast();
			
			Vec2i west  = new Vec2i(cur.x()-1, cur.y());
			Vec2i east  = new Vec2i(cur.x()+1, cur.y());
			Vec2i north = new Vec2i(cur.x(), cur.y()-1);
			Vec2i south = new Vec2i(cur.x(), cur.y()+1);
			
			if (checkAdd(west,  cur, terrain, result)) queue.add(west);
			if (checkAdd(east,  cur, terrain, result)) queue.add(east);
			if (checkAdd(north, cur, terrain, result)) queue.add(north);
			if (checkAdd(south, cur, terrain, result)) queue.add(south);
		}
		
		return result;
	}
	
	public boolean checkAdd(Vec2i from, Vec2i to, ArrayGrid<Integer> terrain, ArrayGrid<Integer> gradient) {
		if (from.x()<0 || from.y()<0 || from.x()>=terrain.getWidth() || from.y()>=terrain.getHeight()) return false;
		
		//Can we travel from 'from' to 'to'?
		int fromHeight = terrain.get(from);
		int toHeight = terrain.get(to);
		
		if (toHeight>fromHeight+1) return false; //Nope
		
		//Get the gradient value we'd be if we went here this way
		int proposedValue = gradient.get(to)+1;
		int existingValue = gradient.get(from);
		
		if (existingValue<=proposedValue) {
			//We already have an equal or better route
			return false;
		} else {
			gradient.set(from.x(), from.y(), gradient.get(to)+1);
			return true;
		}
	}
	
	public boolean shouldMove(Vec2i from, Vec2i to, ArrayGrid<Integer> terrain, ArrayGrid<Integer> gradient) {
		//Can we travel from 'from' to 'to'?
		Integer fromHeight = terrain.get(from);
		Integer toHeight = terrain.get(to);
		if (fromHeight==null || toHeight==null) return false;
		
		if (toHeight>fromHeight+1) return false; //Nope
		
		//SHOULD we travel from 'from' to 'to'?
		int fromGradient = gradient.get(from);
		int toGradient = gradient.get(to);
		
		return toGradient < fromGradient;
	}
	
	public void runPartB() {
		//runPartB(AdventOfCode.processSampleInput(SAMPLE_INPUT));
		
		try {
			runPartB(Files.readAllLines(Path.of("data", "day12.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void runPartB(List<String> input) {
		ArrayGrid<Integer> map = new ArrayGrid<>(input.get(0).length(), input.size(), 0);
		map.elementToString(it->""+(char) ('a'+it.intValue()), false);
		
		Vec2i start = new Vec2i(0,0);
		Vec2i end = new Vec2i(0,0);
		
		for(int y=0; y<input.size(); y++) {
			String line = input.get(y);
			for(int x=0; x<line.length(); x++) {
				char cur = line.charAt(x);
				if (cur=='S') {
					start = new Vec2i(x,y);
					cur = 'a';
				} else if (cur=='E') {
					end = new Vec2i(x,y);
					cur = 'z';
				}
				
				map.set(x,y, cur-'a');
			}
		}
		
		System.out.println(map);
		System.out.println();
		
		ArrayGrid<Integer> gradient = createObjectiveMap(map, end);
		
		System.out.println(gradient);
		System.out.println();
		
		Vec2i best = start;
		int bestSteps = Integer.MAX_VALUE;
		for(int y=0; y<map.getHeight(); y++) {
			for(int x=0; x<map.getWidth(); x++) {
				int elevation = map.get(x, y);
				if (elevation==0) {
					int steps = gradient.get(x, y);
					if (steps < bestSteps) {
						best = new Vec2i(x, y);
						bestSteps = steps;
					}
				}
			}
		}
		
		System.out.println("Best start: "+best+", with "+bestSteps+" steps.");
	}
}
