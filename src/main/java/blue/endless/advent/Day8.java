package blue.endless.advent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import blue.endless.advent.util.ArrayGrid;

public class Day8 {
	public static final String SAMPLE_INPUT =
			"""
			30373
			25512
			65332
			33549
			35390
			""";
	
	public void run() {
		//run(AdventOfCode.processSampleInput(SAMPLE_INPUT));
		
		try {
			run(Files.readAllLines(Path.of("data", "day8.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(List<String> input) {
		//Create tree grid
		int width = input.get(0).length();
		int height = input.size();
		ArrayGrid<Integer> grid = new ArrayGrid<>(width, height, 0);
		
		for(int y=0; y<height; y++) {
			String line = input.get(y);
			for(int x=0; x<width; x++) {
				char curCell = line.charAt(x);
				int cellHeight = Integer.parseInt(""+curCell);
				grid.set(x, y, cellHeight);
			}
		}
		
		System.out.println(grid);
		
		//Create visibility bitmap
		ArrayGrid<Boolean> visibility = new ArrayGrid<>(width, height, false);
		for(int y=0; y<grid.getHeight(); y++) {
			for(int x=0; x<grid.getWidth(); x++) {
				visibility.set(x, y, checkVisibility(x,y,grid));
			}
		}
		
		System.out.println();
		System.out.println(visibility);
		
		int visibleTrees = 0;
		for(int y=0; y<grid.getHeight(); y++) {
			for(int x=0; x<grid.getWidth(); x++) {
				if (visibility.get(x, y)) visibleTrees++;
			}
		}
		
		System.out.println("Visible Tree Count: "+visibleTrees);
	}
	
	private boolean checkVisibility(int x, int y, ArrayGrid<Integer> grid) {
		if (x==0 || y==0 || x==grid.getWidth()-1 || y==grid.getHeight()-1) return true; //Edges are always visible
		
		int cellValue = grid.get(x,y);
		boolean visible = true;
		
		//Search west for obstructions
		visible = true;
		for(int xi=x-1; xi>=0; xi--) {
			if (grid.get(xi, y) >= cellValue) visible = false;
		}
		if (visible) return true;
		
		//Search north for obstructions
		visible = true;
		for(int yi=y-1; yi>=0; yi--) {
			if (grid.get(x, yi) >= cellValue) visible = false;
		}
		if (visible) return true;
		
		//Search east for obstructions
		visible = true;
		for(int xi=x+1; xi<grid.getWidth(); xi++) {
			if (grid.get(xi, y) >= cellValue) visible = false;
		}
		if (visible) return true;
		
		//Search south for obstructions
		visible = true;
		for(int yi=y+1; yi<grid.getHeight(); yi++) {
			if (grid.get(x, yi) >= cellValue) visible = false;
		}
		if (visible) return true;
		
		return false;
	}
	
	public void runPartB() {
		//runPartB(AdventOfCode.processSampleInput(SAMPLE_INPUT));
		
		try {
			runPartB(Files.readAllLines(Path.of("data", "day8.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void runPartB(List<String> input) {
		//Create tree grid
		int width = input.get(0).length();
		int height = input.size();
		ArrayGrid<Integer> grid = new ArrayGrid<>(width, height, 0);
		
		for(int y=0; y<height; y++) {
			String line = input.get(y);
			for(int x=0; x<width; x++) {
				char curCell = line.charAt(x);
				int cellHeight = Integer.parseInt(""+curCell);
				grid.set(x, y, cellHeight);
			}
		}
		
		System.out.println(grid);
		
		int bestX = -1;
		int bestY = -1;
		int bestVisibility = -1;
		for(int y=0; y<grid.getHeight(); y++) {
			for(int x=0; x<grid.getWidth(); x++) {
				int cur = getScenicScore(x, y, grid);
				if (cur>bestVisibility) {
					bestVisibility = cur;
					bestX = x;
					bestY = y;
				}
			}
		}
		
		System.out.println();
		System.out.println("Best visibility is "+bestX+", "+bestY+" with a scenic score of "+bestVisibility);
	}
	
	public int getScenicScore(int x, int y, ArrayGrid<Integer> grid) {
		return walk(x,y,-1,0, grid) * walk(x,y,1,0,grid) * walk(x,y,0,-1,grid) * walk(x,y,0,1,grid);
		
	}
	
	public int walk(int x, int y, int dx, int dy, ArrayGrid<Integer> grid) {
		int initial = grid.get(x, y);
		
		int result = 0;
		int steps = 0;
		while(x>=0 && y>=0 && x<grid.getWidth() && y<grid.getHeight()) {
			x += dx;
			y += dy;
			if (x>=0 && y>=0 && x<grid.getWidth() && y<grid.getHeight()) {
				steps++;
				
				int cur = grid.get(x, y);
				if (cur>=initial) return steps;
			} else {
				return steps;
			}
		}
		return steps;
	}
}
