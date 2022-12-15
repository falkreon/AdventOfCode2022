package blue.endless.advent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import blue.endless.advent.util.ArrayGrid;
import blue.endless.advent.util.Day;
import blue.endless.advent.util.Display;
import blue.endless.advent.util.Rect;
import blue.endless.advent.util.Vec2i;

public class Day14 extends Day {
	
	public ArrayGrid<Character> map = new ArrayGrid<>(1,1, '.');
	public Rect extents = new Rect(500, 0, 1, 1);
	public int sandAtRest = 0;
	public boolean atRest = false;
	
	@Override
	public void partOne(List<String> input) {
		Display display = new Display();
		display.setVisible(true);
		
		List<LineSegment> lines = new ArrayList<>();
		
		//Field Extents; 500, 0 is where the sand comes from
		extents = new Rect(500, 0, 1, 1);
		
		for(String s : input) {
			String[] pieces = s.split(Pattern.quote("->"));
			Vec2i previous = null;
			for(String point : pieces) {
				point = point.trim();
				String[] pointPieces = point.split(",");
				Vec2i cur = new Vec2i(
						Integer.parseInt(pointPieces[0]),
						Integer.parseInt(pointPieces[1])
						);
				
				extents = extents.add(cur);
				
				if (previous!=null) {
					lines.add(new LineSegment(previous, cur));
				}
				
				previous = cur;
			}
		}
		
		//System.out.println(lines);
		extents = new Rect(extents.x()-10, extents.y(), extents.width()+20, extents.height()+10);
		System.out.println(extents);
		
		map = new ArrayGrid<>(extents.width(), extents.height(), '.');
		map.elementToString(String::valueOf, false);
		
		for(LineSegment segment : lines) {
			Vec2i a = segment.a().subtract(extents.x(), extents.y());
			Vec2i b = segment.b().subtract(extents.x(), extents.y());
			
			if (a.x()!=b.x() && a.y()!=b.y()) {
				throw new IllegalArgumentException(); //Diagonal lines not allowed in the problem!
				//If they were, we'd use the bresenham alg instead.
			} else if (a.x()!=b.x()) {
				//Horizontal
				int x1 = Math.min(a.x(), b.x());
				int x2 = Math.max(a.x(), b.x());
				
				for(int x=x1; x<=x2; x++) {
					map.set(x, a.y(), '#');
				}
			} else {
				//Vertical
				int y1 = Math.min(a.y(), b.y());
				int y2 = Math.max(a.y(), b.y());
				
				for(int y=y1; y<=y2; y++) {
					map.set(a.x(), y, '#');
				}
			}
		}
		
		display.mapColor('.', Color.BLACK);
		display.mapColor('#', new Color(31,71,102));
		display.mapColor('*', new Color(60, 100, 150));
		display.mapColor('~', new Color(222,142,71));
		display.mapColor('o', new Color(252,199,150));
		display.setGrid(map);
		display.setTicksPerSecond(40);
		
		//System.out.println();
		//System.out.println(map);
		System.out.println();
		
		//int sandCount = 0;
		display.setOnTick(time->{
			if (!atRest) {
				if (map.get(500-extents.x(),0)=='o') {
					atRest = true;
					System.out.println("Sand at rest: "+sandAtRest);
					return;
				}
				
				if (dropOne(map, extents)) {
					sandAtRest++;
				} else {
					System.out.println("Sand at rest: "+sandAtRest);
					atRest = true;
				}
			}
		});
	}
	
	/**
	 * Simulates one grain of sand dropping into place
	 * @param map The map of sand and cave walls
	 * @param extents The valid area that is not the void
	 * @return true if the sand came to rest
	 */
	public boolean dropOne(ArrayGrid<Character> map, Rect extents) {
		Vec2i pos = new Vec2i(500, 0);
		char curCell = map.get(pos.subtract(extents.x(), extents.y()));
		if (curCell!='.' && curCell!='~') return false; //sand can't enter the simulation
		
		while(true) {
			Vec2i down = new Vec2i(pos.x(), pos.y()+1);
			if (extents.contains(down)) {
				char downCell = map.get(down.subtract(extents.x(), extents.y()));
				if (downCell=='.' || downCell=='~') {
					pos = down;
					map.set(pos.subtract(extents.x(), extents.y()), '~');
					continue;
				}
				
				if (downCell!='o') map.set(down.subtract(extents.x(), extents.y()), '*');
				
				Vec2i downLeft = new Vec2i(pos.x()-1, pos.y()+1);
				if (extents.contains(downLeft)) {
					char downLeftCell = map.get(downLeft.subtract(extents.x(), extents.y()));
					if (downLeftCell=='.' || downLeftCell=='~') {
						pos = downLeft;
						map.set(pos.subtract(extents.x(), extents.y()), '~');
						continue;
					}
					
					if (downLeftCell!='o') map.set(downLeft.subtract(extents.x(), extents.y()), '*');
					
					Vec2i downRight = new Vec2i(pos.x()+1, pos.y()+1);
					if (extents.contains(downRight)) {
						char downRightCell = map.get(downRight.subtract(extents.x(), extents.y()));
						if (downRightCell=='.' || downRightCell=='~') {
							pos = downRight;
							map.set(pos.subtract(extents.x(), extents.y()), '~');
							continue;
						}
						
						if (downRightCell!='o') map.set(downRight.subtract(extents.x(), extents.y()), '*');
						
						//This sand is coming to rest
						map.set(pos.subtract(extents.x(), extents.y()), 'o');
						return true;
					} else {
						//This sand fell into the void.
						System.out.println("Sand flowed into the abyss from "+pos);
						map.set(pos.subtract(extents.x(), extents.y()), '~');
						return false;
					}
					
				} else {
					//This sand fell into the void.
					System.out.println("Sand flowed into the abyss from "+pos);
					map.set(pos.subtract(extents.x(), extents.y()), '~');
					return false;
				}
			} else {
				//This sand fell into the void.
				System.out.println("Sand flowed into the abyss from "+pos);
				map.set(pos.subtract(extents.x(), extents.y()), '~');
				return false;
			}
		}
	}

	@Override
	public void partTwo(List<String> input) {
		Display display = new Display();
		display.setVisible(true);
		
		List<LineSegment> lines = new ArrayList<>();
		
		//Field Extents; 500, 0 is where the sand comes from
		extents = new Rect(500, 0, 1, 1);
		
		for(String s : input) {
			String[] pieces = s.split(Pattern.quote("->"));
			Vec2i previous = null;
			for(String point : pieces) {
				point = point.trim();
				String[] pointPieces = point.split(",");
				Vec2i cur = new Vec2i(
						Integer.parseInt(pointPieces[0]),
						Integer.parseInt(pointPieces[1])
						);
				
				extents = extents.add(cur);
				
				if (previous!=null) {
					lines.add(new LineSegment(previous, cur));
				}
				
				previous = cur;
			}
		}
		
		//System.out.println(lines);
		int toAdd = 200;
		extents = new Rect(extents.x()-toAdd, extents.y(), extents.width()+(toAdd*2), extents.height()+2);
		lines.add(new LineSegment(new Vec2i(extents.x(), extents.y()+extents.height()-1), new Vec2i(extents.x()+extents.width(), extents.y()+extents.height()-1)));
		System.out.println(extents);
		
		map = new ArrayGrid<>(extents.width(), extents.height(), '.');
		map.elementToString(String::valueOf, false);
		
		for(LineSegment segment : lines) {
			Vec2i a = segment.a().subtract(extents.x(), extents.y());
			Vec2i b = segment.b().subtract(extents.x(), extents.y());
			
			if (a.x()!=b.x() && a.y()!=b.y()) {
				throw new IllegalArgumentException(); //Diagonal lines not allowed in the problem!
				//If they were, we'd use the bresenham alg instead.
			} else if (a.x()!=b.x()) {
				//Horizontal
				int x1 = Math.min(a.x(), b.x());
				int x2 = Math.max(a.x(), b.x());
				
				for(int x=x1; x<=x2; x++) {
					map.set(x, a.y(), '#');
				}
			} else {
				//Vertical
				int y1 = Math.min(a.y(), b.y());
				int y2 = Math.max(a.y(), b.y());
				
				for(int y=y1; y<=y2; y++) {
					map.set(a.x(), y, '#');
				}
			}
		}
		
		display.mapColor('.', Color.BLACK);
		display.mapColor('#', new Color(31,71,102));
		display.mapColor('*', new Color(60, 100, 150));
		display.mapColor('~', new Color(222,142,71));
		display.mapColor('o', new Color(252,199,150));
		display.setGrid(map);
		display.setTicksPerSecond(40);
		
		//System.out.println();
		//System.out.println(map);
		System.out.println();
		
		//int sandCount = 0;
		display.setOnTick(time->{
			if (!atRest) {
				for (int i=0; i<8; i++) {
					if (dropOne(map, extents)) {
						sandAtRest++;
					} else {
						System.out.println("Sand at rest: "+sandAtRest);
						atRest = true;
						return;
					}
				}
			}
		});
	}
	
	private record LineSegment(Vec2i a, Vec2i b) {}

	@Override
	protected String getRawSampleData() {
		return """
				498,4 -> 498,6 -> 496,6
				503,4 -> 502,4 -> 502,9 -> 494,9
				""";
	}

	@Override
	protected String getDataFileName() {
		return "day14.txt";
	}

}
