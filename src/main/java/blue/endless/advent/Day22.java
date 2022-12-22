package blue.endless.advent;

import java.util.ArrayList;
import java.util.List;

import blue.endless.advent.util.ArrayGrid;
import blue.endless.advent.util.Day;
import blue.endless.advent.util.Either;
import blue.endless.advent.util.Grid;
import blue.endless.advent.util.Vec2i;

/**
 * I am unaware of any trickery today. At least as of part 1, this looks like a softball. But I like softball problems
 * I can grid out and simulate, so let's go!
 */
public class Day22 extends Day {

	@Override
	public void partOne(List<String> input) {
		String directions = input.get(input.size()-1);
		
		int height = input.size()-2;
		int width = input.get(0).length();
		for(int i=1; i<height; i++) {
			int curWidth = input.get(i).length();
			width = Math.max(width, curWidth);
		}
		
		ArrayGrid<Character> grid = new ArrayGrid<>(width, height, ' ');
		grid.elementToString(it->it.toString(), false);
		for(int y=0; y<grid.getHeight(); y++) {
			String line = input.get(y);
			for(int x=0; x<grid.getWidth(); x++) {
				if (x<line.length()) {
					grid.set(x, y, line.charAt(x));
				}
			}
		}
		
		System.out.println("Acquired Grid:");
		System.out.println(grid);
		
		List<Either<Turn, Integer>> moves = new ArrayList<>();
		
		while(!directions.isBlank()) {
			char first = directions.charAt(0);
			if (Character.isWhitespace(first)) {
				directions = directions.substring(1);
				continue;
			}
			
			int distance = 0;
			
			if (Character.isDigit(first)) {
				//Capture all the digits
				String numString = "";
				for(int i=0; i<directions.length(); i++) {
					char ci = directions.charAt(0);
					if (Character.isDigit(ci)) {
						numString = numString + ci;
						directions = directions.substring(1);
					} else {
						break;
					}
				}
				distance = Integer.parseInt(numString);
				moves.add(Either.right(distance));
			} else {
				char turnChar = directions.charAt(0);
				Turn turn = switch(turnChar) {
					case 'L' -> Turn.LEFT;
					case 'R' -> Turn.RIGHT;
					default -> throw new IllegalStateException();
				};
				moves.add(Either.left(turn));
				directions = directions.substring(1);
			}
		}
		
		System.out.println(moves);
		
		Facing startingFacing = Facing.RIGHT;
		Vec2i startingPosition = new Vec2i(0,0);
		
		for(int x=0; x<grid.getWidth(); x++) {
			Character ch = grid.get(x, 0);
			if (ch==null) continue;
			if (ch!=' ') {
				startingPosition = new Vec2i(x, 0);
				break;
			}
		}
		
		System.out.println("Starting position: "+startingPosition);
		System.out.println("Starting facing: "+startingFacing);
		
		Facing facing = startingFacing;
		Vec2i pos = startingPosition;
		for(Either<Turn, Integer> step : moves) {
			if (step.isLeft()) {
				facing = step.left().apply(facing);
				System.out.println("Turned "+step.left()+"; now facing "+facing);
			} else {
				for(int i=0; i<step.right(); i++) {
					Vec2i newPos = getWrappedForwards(pos, facing, grid);
					Character next = grid.get(newPos);
					if (next!='#') {
						pos = newPos;
					} else {
						break;
					}
				}
				System.out.println("Moved "+step.right()+" spaces, now at "+pos);
			}
		}
		
		//We'll combine them in longs just in case
		long row = pos.y() + 1;
		long col = pos.x() + 1;
		long facingValue = facing.value();
		
		long password = row * 1000L + col * 4L + facingValue;
		System.out.println("Password: "+password);
	}
	
	public Vec2i getWrappedForwards(Vec2i pos, Facing facing, Grid<Character> map) {
		Vec2i unwrapped = pos.add(facing.displacement());
		Character cur = map.get(unwrapped);
		if (cur==null || cur==' ') {
			int dx = facing.displacement().x()*-1; //These are signum values still
			int dy = facing.displacement().y()*-1; // " "
			
			//Find the equivalent warp-space position in the opposite direction
			unwrapped = pos;
			cur = map.get(unwrapped);
			while (cur!=null && cur!=' ') {
				unwrapped = unwrapped.add(dx, dy);
				cur = map.get(unwrapped);
			}
			
			//Cur is now at the empty space backwards OFF the map. Step *forward* back onto the map and that's our result.
			return unwrapped.add(facing.displacement());
		} else {
			return unwrapped;
		}
	}
	
	public Vec2i walk(Vec2i pos, Facing facing, Grid<Character> map) {
		return pos; //TODO: Implement
	}
	
	public static enum Turn {
		LEFT,
		RIGHT;
		
		public Facing apply(Facing in) {
			return switch(in) {
				case RIGHT -> (this==LEFT) ? Facing.UP    : Facing.DOWN;
				case DOWN  -> (this==LEFT) ? Facing.RIGHT : Facing.LEFT;
				case LEFT  -> (this==LEFT) ? Facing.DOWN  : Facing.UP;
				case UP    -> (this==LEFT) ? Facing.LEFT  : Facing.RIGHT;
			};
		}
	}
	
	public static enum Facing {
		RIGHT(0, new Vec2i( 1,  0)),
		DOWN (1, new Vec2i( 0,  1)),
		LEFT (2, new Vec2i(-1,  0)),
		UP   (3, new Vec2i( 0, -1));
		
		private final int value;
		private final Vec2i vec;
		
		Facing(int value, Vec2i vec) {
			this.value = value;
			this.vec = vec;
		}
		
		public int value() { return value; }
		public Vec2i displacement() { return vec; }
		
		public static Facing valueOf(int i) {
			for(Facing facing : values()) if (i==facing.value) return facing;
			
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void partTwo(List<String> input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getRawSampleData() {
		return """
				        ...#
				        .#..
				        #...
				        ....
				...#.......#
				........#...
				..#....#....
				..........#.
				        ...#....
				        .....#..
				        .#......
				        ......#.
				
				10R5L5R10L4R5L5
				""";
	}
	
}
