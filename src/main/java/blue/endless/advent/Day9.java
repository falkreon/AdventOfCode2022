package blue.endless.advent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

import blue.endless.advent.util.Vec2i;

public class Day9 {
	public static String SAMPLE_INPUT =
			"""
			R 4
			U 4
			L 3
			D 1
			R 4
			D 1
			L 5
			R 2
			""";
	
	public static String SAMPLE_INPUT_2 =
			"""
			R 5
			U 8
			L 8
			D 3
			R 17
			D 10
			L 25
			U 20
			""";
	
	public void run() {
		//run(AdventOfCode.processSampleInput(SAMPLE_INPUT));
		
		try {
			run(Files.readAllLines(Path.of("data", "day9.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(List<String> input) {
		RopeState rope = new RopeState();
		
		for(String line : input) {
			if (line.isBlank()) continue;
			
			String[] parts = line.split("\\s");
			if (parts.length!=2) {
				System.out.println("Did not understand movement: "+line);
			} else {
				String direction = parts[0];
				int steps = Integer.parseInt(parts[1]);
				
				for(int i=0; i<steps; i++) {
					switch(direction) {
						case "R" -> rope.move( 1,  0);
						case "L" -> rope.move(-1,  0);
						case "U" -> rope.move( 0, -1);
						case "D" -> rope.move( 0,  1);
					}
				}
				System.out.println(rope);
				System.out.println();
			}
		}
		
		System.out.println("Number of visited locations: "+rope.tailVisited.size());
	}
	
	public void runPartB() {
		//runPartB(AdventOfCode.processSampleInput(SAMPLE_INPUT_2));
		
		try {
			runPartB(Files.readAllLines(Path.of("data", "day9.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void runPartB(List<String> input) {
		ExtendedRopeState rope = new ExtendedRopeState();
		
		for(String line : input) {
			if (line.isBlank()) continue;
			
			String[] parts = line.split("\\s");
			if (parts.length!=2) {
				System.out.println("Did not understand movement: "+line);
			} else {
				String direction = parts[0];
				int steps = Integer.parseInt(parts[1]);
				
				for(int i=0; i<steps; i++) {
					switch(direction) {
						case "R" -> rope.move( 1,  0);
						case "L" -> rope.move(-1,  0);
						case "U" -> rope.move( 0, -1);
						case "D" -> rope.move( 0,  1);
					}
				}
				System.out.println(rope);
				System.out.println();
			}
		}
		
		System.out.println("Number of visited locations: "+rope.tailVisited.size());
	}
	
	
	
	public static class RopeState {
		Vec2i head = new Vec2i(0,0);
		Vec2i tail = new Vec2i(0,0);
		HashSet<Vec2i> tailVisited = new HashSet<>();
		
		public String toString() {
			//Find extents
			int minX = Math.min(head.x(), tail.x());
			int minY = Math.min(head.y(), tail.y());
			int maxX = Math.max(head.x(), tail.x());
			int maxY = Math.max(head.y(), tail.y());
			for(Vec2i vec : tailVisited) {
				minX = Math.min(minX, vec.x());
				minY = Math.min(minY, vec.y());
				maxX = Math.max(maxX, vec.x());
				maxY = Math.max(maxY, vec.y());
			}
			
			StringBuilder builder = new StringBuilder();
			builder.append(minX);
			builder.append(", ");
			builder.append(minY);
			builder.append(" .. ");
			builder.append(maxX);
			builder.append(", ");
			builder.append(maxY);
			builder.append('\n');
			
			for(int y=minY; y<=maxY; y++) {
				for(int x=minX; x<=maxX; x++) {
					if (head.x() == x && head.y() == y) {
						if (tail.x() == x && tail.y() == y) {
							builder.append('*');
						} else {
							builder.append('H');
						}
					} else if (tail.x() == x && tail.y() == y) {
						builder.append('T');
					} else if (tailVisited.contains(new Vec2i(x, y))) {
						builder.append('#');
					} else {
						builder.append('.');
					}
				}
				builder.append('\n');
			}
			builder.deleteCharAt(builder.length()-1);
			
			return builder.toString();
		}
		
		public void move(int dx, int dy) {
			head = new Vec2i(head.x() + (int) Math.signum(dx), head.y() + (int) Math.signum(dy));
			
			tailVisited.add(tail);
			
			
			if (Math.abs(head.x() - tail.x()) > 1 || Math.abs(head.y() - tail.y()) > 1) {
				int moveX = (int) Math.signum(head.x() - tail.x());
				int moveY = (int) Math.signum(head.y() - tail.y());
				
				tail = new Vec2i(tail.x() + moveX, tail.y() + moveY);
			}
			
			tailVisited.add(tail);
		}
	}
	
	public static class ExtendedRopeState {
		Vec2i[] knots = new Vec2i[10];
		HashSet<Vec2i> tailVisited = new HashSet<>();
		
		public ExtendedRopeState() {
			for(int i=0; i<knots.length; i++) {
				knots[i] = new Vec2i(0,0);
			}
			tailVisited.add(new Vec2i(0,0));
		}
		
		public void move(int dx, int dy) {
			knots[0] = new Vec2i(knots[0].x() + dx, knots[0].y() + dy);
			for(int i=1; i<10; i++) {
				update(i);
			}
		}
		
		public void update(int i) {
			if (i==0) return;
			
			Vec2i head = knots[i-1];
			Vec2i tail = knots[i];
			
			if (i==knots.length-1) tailVisited.add(knots[i]);
			
			if (Math.abs(head.x() - tail.x()) > 1 || Math.abs(head.y() - tail.y()) > 1) {
				int moveX = (int) Math.signum(head.x() - tail.x());
				int moveY = (int) Math.signum(head.y() - tail.y());
				
				knots[i] = new Vec2i(tail.x() + moveX, tail.y() + moveY);
				
				if (i==knots.length-1) tailVisited.add(knots[i]);
			}
		}
		
		@Override
		public String toString() {
			
			//Find extents
			int minX = knots[0].x();
			int minY = knots[0].y();
			int maxX = knots[0].x();
			int maxY = knots[0].y();
			
			for(int i=1; i<10; i++) {
				minX = Math.min(minX, knots[i].x());
				minY = Math.min(minY, knots[i].y());
				maxX = Math.max(maxX, knots[i].x());
				maxY = Math.max(maxY, knots[i].y());
			}
			
			for(Vec2i vec : tailVisited) {
				minX = Math.min(minX, vec.x());
				minY = Math.min(minY, vec.y());
				maxX = Math.max(maxX, vec.x());
				maxY = Math.max(maxY, vec.y());
			}
			
			
			StringBuilder builder = new StringBuilder();
			builder.append(minX);
			builder.append(", ");
			builder.append(minY);
			builder.append(" .. ");
			builder.append(maxX);
			builder.append(", ");
			builder.append(maxY);
			builder.append('\n');
			
			for(int y=minY; y<=maxY; y++) {
				for(int x=minX; x<=maxX; x++) {
					if (knots[0].x() == x && knots[0].y() == y) {
						builder.append('H');
					} else {
						boolean claimed = false;
						for(int i=1; i<10; i++) {
							if (knots[i].x() == x && knots[i].y() == y) {
								claimed = true;
								builder.append(""+i);
								break;
							}
						}
						
						if (!claimed) {
							if (tailVisited.contains(new Vec2i(x, y))) {
								builder.append('#');
							} else {
								builder.append('.');
							}
						}
					}
				}
				builder.append('\n');
			}
			builder.deleteCharAt(builder.length()-1);
			
			return builder.toString();
		}
	}
}
