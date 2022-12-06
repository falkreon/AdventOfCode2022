package blue.endless.advent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class Day5 {
	public static final String SAMPLE_INPUT = """
			    [D]    
			[N] [C]    
			[Z] [M] [P]
			 1   2   3 
			
			move 1 from 2 to 1
			move 3 from 1 to 3
			move 2 from 2 to 1
			move 1 from 1 to 2
			""";
	
	public void run() {
		//run(AdventOfCode.processSampleInput(SAMPLE_INPUT));
		
		try {
			run(Files.readAllLines(Path.of("data", "day5.txt")));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void run(List<String> input) {
		CrateStack stack = toStack(input);
		
		int blankLineNumber = 0;
		for(int i=0; i<input.size(); i++) {
			if (input.get(i).isBlank()) {
				blankLineNumber = i;
				break;
			}
		}
		int crateNumbersLineNumber = blankLineNumber - 1;
		
		
		for(int y=0; y<crateNumbersLineNumber; y++) {
			String curLine = input.get(y);
			//String row = "";
			for(int j=0; j<stack.getWidth(); j++) {
				int ofs = (j * 4) + 1;
				if (ofs >= curLine.length()) break;
				char curCrate = curLine.charAt(ofs);
				stack.setCrate(j, (crateNumbersLineNumber-1) - y, curCrate);
				//row += curCrate;
			}
			//System.out.println("Crate Line: "+row);
		}
		
		System.out.println(stack);
		
		for(int i=blankLineNumber+1; i<input.size(); i++) {
			String instruction = input.get(i);
			String[] parts = instruction.split("\\s");
			if (parts.length==6 && parts[0].equals("move") && parts[2].equals("from") && parts[4].equals("to")) {
				int howMany = Integer.parseInt(parts[1]);
				int from = Integer.parseInt(parts[3]);
				int to = Integer.parseInt(parts[5]);
				
				System.out.println("Moving "+howMany+" crates from "+from+" to "+to);
				
				for(int j=0; j<howMany; j++) {
					char crate = stack.liftCrate(from);
					stack.dropCrate(to, crate);
				}
			} else {
				System.out.println("Unknown instruction: "+instruction);
			}
			System.out.println(stack);
		}
		//char toMove = stack.liftCrate(2);
		//stack.dropCrate(1, toMove);
		
		System.out.println(stack);
		
		System.out.println("Result: "+stack.getTopCrates());
	}
	
	public CrateStack toStack(List<String> input) {
		//Finding the location of the empty line between the crates and the instructions
		int blankLineNumber = 0;
		for(int i=0; i<input.size(); i++) {
			if (input.get(i).isBlank()) {
				blankLineNumber = i;
				break;
			}
		}
		
		//The number row is the one before the blank line
		int crateNumbersLineNumber = blankLineNumber - 1;
		
		//Get the number row and parse the rightmost number to figure out how many crates wide the input is
		String crateNumbersLine = input.get(crateNumbersLineNumber).trim();
		String[] crateNumbersParts = crateNumbersLine.split("\\s");
		String highestNumberString = crateNumbersParts[crateNumbersParts.length-1];
		int highestNumber = Integer.parseInt(highestNumberString);
		
		//Parse the input and place the crates into a new CrateStack
		CrateStack stack = new CrateStack(highestNumber, crateNumbersLineNumber);
		
		for(int y=0; y<crateNumbersLineNumber; y++) {
			String curLine = input.get(y);
			for(int j=0; j<highestNumber; j++) {
				int ofs = (j * 4) + 1;
				if (ofs >= curLine.length()) break;
				char curCrate = curLine.charAt(ofs);
				stack.setCrate(j, (crateNumbersLineNumber-1) - y, curCrate);
			}
		}
		
		return stack;
	}
	
	public void runPartB() {
		//runPartB(AdventOfCode.processSampleInput(SAMPLE_INPUT));
		
		try {
			runPartB(Files.readAllLines(Path.of("data", "day5.txt")));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void runPartB(List<String> input) {
		CrateStack stack = toStack(input);
		
		System.out.println("Initial State: ");
		System.out.println(stack);
		
		boolean started = false;
		for(String s : input) {
			if (!started) {
				if (s.isBlank()) started = true;
			} else {
				String[] parts = s.split("\\s");
				if (parts.length==6 && parts[0].equals("move") && parts[2].equals("from") && parts[4].equals("to")) {
					int howMany = Integer.parseInt(parts[1]);
					int from = Integer.parseInt(parts[3]);
					int to = Integer.parseInt(parts[5]);
					
					System.out.println("Moving "+howMany+" crates from "+from+" to "+to);
					
					//We'll use a Deque for our FILO CrateMover 9001 queue
					Deque<Character> queue = new ArrayDeque<>();
					//Pick em all up
					for(int i=0; i<howMany; i++) {
						char curCrate = stack.liftCrate(from);
						queue.push(curCrate);
					}
					
					//Put em down
					for(int i=0; i<howMany; i++) {
						char curCrate = queue.pop();
						stack.dropCrate(to, curCrate);
					}
					System.out.println(stack);
				} else {
					System.out.println("Unknown instruction: "+s);
				}
			}
			
			System.out.println("Loading complete. Top crates are: "+stack.getTopCrates());
		}
	}
	
	public class CrateStack {
		public static final char EMPTY = ' ';
		private char[] data;
		private int width;
		private int height;
		
		public CrateStack(int width, int height) {
			this.width = width;
			this.height = height;
			this.data = new char[width * height];
			Arrays.fill(data, EMPTY);
		}
		
		public int getWidth() {
			return this.width;
		}
		
		public char getCrate(int x, int y) {
			if (x<0 || x>=width || y<0 || y>=height) return EMPTY;
			int index = y * width + x;
			return data[index];
		}
		
		public void setCrate(int x, int y, char crate) {
			if (x<0 || x>=width || y<0 || y>=height) return;
			int index = y * width + x;
			data[index] = crate;
		}
		
		/**
		 * Lifts the top crate off the stack at this location
		 * @param x A number from 1 to width, inclusive
		 */
		public char liftCrate(int x) {
			x = x - 1;
			
			for(int y=height-1; y>=0; y--) {
				char cur = getCrate(x, y);
				if (cur!=EMPTY) {
					setCrate(x, y, EMPTY);
					return cur;
				}
			}
			
			/*
			for(int y=0; y<height; y++) {
				char cur = getCrate(x, y);
				if (cur!=EMPTY) {
					setCrate(x, y, EMPTY);
					return cur;
				}
			}*/
			
			return EMPTY;
		}
		
		/**
		 * Sets down the specified crate so that it will be the top crate on its stack
		 */
		public void dropCrate(int x, char crate) {
			x = x - 1;
			
			for(int y=height-1; y>=0; y--) {
				char cur = getCrate(x, y);
				if (cur!=EMPTY) {
					
					if (y==height-1) {
						//System.out.println("Growing. . .");
						//We don't have room at y+1 to put the crate, so grow
						data = Arrays.copyOf(data, data.length + width);
						
						Arrays.fill(data, width*height, (width*height) + width, EMPTY);
						height = height + 1;
						//System.out.println("New State:");
						//System.out.println(this);
						
						setCrate(x, y+1, crate);
						return;
					} else {
						setCrate(x, y+1, crate);
						return;
					}
				}
			}
			
			/*
			for(int y=0; y<height; y++) {
				char cur = getCrate(x, y);
				if (cur!=EMPTY) {
					setCrate(x, y-1, crate);
					return;
				}
			}*/
			
			//Whole stack was empty - put it on the floor
			setCrate(x, 0, crate);
		}
		
		public String getTopCrates() {
			StringBuilder result = new StringBuilder();
			for(int x=0; x<width; x++) {
				for(int y=height-1; y>=0; y--) {
					char cur = getCrate(x, y);
					if (cur!=EMPTY) {
						result.append(cur);
						break;
					}
				}
			}
			return result.toString();
		}
		
		@Override
		public String toString() {
			StringBuilder result = new StringBuilder();
			
			/*
			for(int x=0; x<width; x++) {
				result.append("____");
			}
			result.append("\n");*/
			
			for(int y=height-1; y>=0; y--) {
				for(int x=0; x<width; x++) {
					char cur = getCrate(x, y);
					
					if (cur==EMPTY) {
						result.append("    ");
					} else {
						result.append('[');
						result.append(getCrate(x, y));
						result.append("] ");
					}
				}
				result.append("\n");
			}
			
			for(int i=0; i<width; i++) {
				String s = Integer.toString(i+1);
				while(s.length()<4) {
					if (s.startsWith(" ")) {
						s = s + " ";
					} else {
						s = " " + s;
					}
				}
				
				result.append(s);
			}
			
			//result.deleteCharAt(result.length()-1);
			
			return result.toString();
		}
	}
}
