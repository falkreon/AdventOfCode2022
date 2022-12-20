package blue.endless.advent;

import java.awt.Color;
import java.util.List;

import blue.endless.advent.util.ArrayGrid;
import blue.endless.advent.util.Day;
import blue.endless.advent.util.Display;
import blue.endless.advent.util.Grid;
import blue.endless.advent.util.SubGrid;

public class Day17 extends Day {
	public static final String[] SHAPE_INPUT = {
		"""
		aaaa
		""",
		
		"""
		.b.
		bbb
		.b.
		""",
		
		"""
		..c
		..c
		ccc
		""",
		
		"""
		d
		d
		d
		d
		""",
		
		"""
		ee
		ee
		"""
	};
	
	public static ArrayGrid<Character>[] PROCESSED_SHAPES;
	
	private int zappedRows = 0;
	private int liveRows = 0;
	private ArrayGrid<Character> column = new ArrayGrid<>(7, 200, '.');
	private SubGrid<Character> columnView;
	private FallingShape activeShape = null;
	private int shapeIndex = 0;
	private int gustIndex = 0;
	private long totalRocks = 0L;
	
	public static class FallingShape extends ArrayGrid<Character> {
		private int x;
		private int y;
		
		private FallingShape(int width, int height) {
			super(width, height, '.');
		}
		
		public static FallingShape of(String s) {
			String[] lines = s.split("\\n");
			int width = lines[0].length();
			int height = lines.length;
			FallingShape result = new FallingShape(width, height);
			for(int y=0; y<height; y++) {
				String line = lines[y];
				for(int x=0; x<width; x++) {
					if (x<line.length()) {
						result.set(x, y, lines[y].charAt(x));
					}
				}
			}
			
			return result;
		}
		
		public void setPosition(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int getX() { return x; }
		public int getY() { return y; }
		
		public void stampInto(Grid<Character> dest, boolean capitalize) {
			for(int y=0; y<this.getHeight(); y++) {
				for(int x=0; x<this.getWidth(); x++) {
					Character ch = this.get(x, y);
					if (ch.equals('.')) continue;
					if (capitalize) ch = Character.toUpperCase(ch);
					dest.set(x+this.x, y+this.y, ch);
				}
			}
		}
		
		public boolean collides(Grid<Character> dest) {
			//Check to see if this hits the bottom of the screen
			if (this.x<0 || this.y<0) return true;
			if (this.y+this.getHeight()>dest.getHeight()) return true;
			if (this.x+this.getWidth()>dest.getWidth()) return true;
			
			for(int y=0; y<this.getHeight(); y++) {
				for(int x=0; x<this.getWidth(); x++) {
					char ch = this.get(x, y);
					if (ch=='.') continue;
					char destBlock = dest.get(x+this.x, y+this.y);
					if (destBlock!='.') return true;
				}
			}
			
			return false;
		}
	}
	
	public void scroll(int lines) {
		zappedRows += lines;
		liveRows -= lines;
		for(int y=column.getHeight()-1; y>0; y--) {
			int from = y-lines;
			for(int x=0; x<column.getWidth(); x++) {
				Character replacement = (from<0) ? '.' : column.get(x, from);
				column.set(x, y, replacement);
			}
		}
	}
	
	@Override
	public void partOne(List<String> input) {
		String gusts = input.get(0);
		
		columnView = new SubGrid<>(7, 20, column);
		columnView.scrollToBottom();
		
		Display display = new Display();
		display.setGrid(columnView);
		display.mapColor(null, new Color(0xFF_000000));
		display.mapColor('.', new Color(0xFF_222222));
		display.mapColor('a', new Color(0xFF_FF7777));
		display.mapColor('b', new Color(0xFF_FF8077));
		display.mapColor('c', new Color(0xFF_FFFE77));
		display.mapColor('d', new Color(0xFF_C0F677));
		display.mapColor('e', new Color(0xFF_777EF4));
		display.mapColor('A', new Color(0xFF_FD0100));
		display.mapColor('B', new Color(0xFF_F76915));
		display.mapColor('C', new Color(0xFF_EEDE04));
		display.mapColor('D', new Color(0xFF_A0D636));
		display.mapColor('E', new Color(0xFF_333ED4));
		
		display.setOnTick(partialTime->{
			for(int i=0; i<12; i++) {
				tick(gusts, 2022);
			}
		});
		
		display.setVisible(true);
	}

	private void tick(String gusts, long maxRocks) {
		if (activeShape==null) {
			activeShape = FallingShape.of(SHAPE_INPUT[shapeIndex]);
			shapeIndex = (shapeIndex + 1) % SHAPE_INPUT.length;
			
			int newShapeY = column.getHeight()-liveRows-activeShape.getHeight()-3;
			if (newShapeY < 0) {
				//System.out.println("Scrolling");
				scroll(-newShapeY);
				newShapeY = column.getHeight()-liveRows-activeShape.getHeight()-3;
			}
			
			activeShape.setPosition(2, column.getHeight()-liveRows-activeShape.getHeight()-3);
		}
		
		//Apply wiggle to the rock due to wind gusts
		char curGust = gusts.charAt(gustIndex);
		gustIndex = (gustIndex + 1) % gusts.length();
		if (curGust=='<') {
			//Move 1 left
			activeShape.x--;
			if (activeShape.collides(column)) activeShape.x++;
		} else if (curGust=='>') {
			//Move 1 right
			activeShape.x++;
			if (activeShape.collides(column)) activeShape.x--;
		} else {
			System.out.println("PROBLEM WITH GUST INPUT!");
		}
		
		//Move 1 down
		activeShape.y++;
		if (activeShape.collides(column)) {
			activeShape.y--;
			activeShape.stampInto(column, true);
			int newPeak = column.getHeight() - activeShape.y;
			if (liveRows<newPeak) liveRows = newPeak;
			activeShape = null;
			
			totalRocks++;
			if (totalRocks % 10000L == 0) {
				System.out.println("Progress: "+ (int) ((totalRocks / (double) maxRocks)*100)+"%");
			}
			if (totalRocks==maxRocks) {
				long towerHeight = (long) liveRows + (long) zappedRows;
				System.out.println("Total height after "+totalRocks+" rocks: "+towerHeight);
			}
		}
		
		int fullyScrolled = column.getHeight()-columnView.getHeight();
		int getTopInView = fullyScrolled - Math.max(0, liveRows - 8);
		columnView.scroll(0, getTopInView);
		
		Grid<Character> temp = column.copy();
		if (activeShape!=null) activeShape.stampInto(temp, false);
		columnView.setDelegate(temp);
	}
	
	@Override
	public void partTwo(List<String> input) {
		String gusts = input.get(0);
		columnView = new SubGrid<>(7, 20, column);
		columnView.scrollToBottom();
		
		while(totalRocks < 1_000_000_000_000L) {
			tick(gusts, 1_000_000_000_000L);
		}
	}

	@Override
	protected String getRawSampleData() {
		return ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>";
	}

	@Override
	protected String getDataFileName() {
		return "day17.txt";
	}

}
