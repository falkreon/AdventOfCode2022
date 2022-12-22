package blue.endless.advent;

import java.util.ArrayList;
import java.util.List;

import blue.endless.advent.util.Day;

public class AdventOfCode {
	public static Day[] Calendar = {
		new Day1(), new Day2(), null, null, null, null, null,
		null, null, null, null, null, new Day13(), new Day14(),
		new Day15(), new Day16(), new Day17(), new Day18(), new Day19(), new Day20(), new Day21(),
		new Day22(), null, null, null,
	};
	
	public static void main(String[] args) {
		if (args.length==0) {
			System.out.println("usage: java -jar AdventOfCode.jar <day> [params]");
			System.exit(-1);
		}
		
		boolean debug = (args.length>1 && args[1].equals("debug"));
		
		switch(args[0]) {
			case "3"  -> new Day3().run();
			case "3b" -> new Day3().runPartB();
			case "4"  -> new Day4().run();
			case "4b" -> new Day4().runPartB();
			case "5"  -> new Day5().run();
			case "5b" -> new Day5().runPartB();
			case "6"  -> new Day6().run();
			case "6b" -> new Day6().runPartB();
			case "7"  -> new Day7().run();
			case "7b" -> new Day7().runPartB();
			case "8"  -> new Day8().run();
			case "8b" -> new Day8().runPartB();
			case "9"  -> new Day9().run();
			case "9b" -> new Day9().runPartB();
			case "10" -> new Day10().run();
			case "10b"-> new Day10().runPartB();
			case "11" -> new Day11().run();
			case "11b"-> new Day11().runPartB();
			case "12" -> new Day12().run();
			case "12b"-> new Day12().runPartB();
			
			default -> {
				if (args[0].endsWith("b")) {
					args[0] = args[0].substring(0, args[0].length()-1);
					int dayNumber = Integer.parseInt(args[0]) - 1;
					if (Calendar[dayNumber]!=null) {
						Calendar[dayNumber].partTwo(debug);
						return;
					}
				} else {
					int dayNumber = Integer.parseInt(args[0]) - 1;
					if (Calendar[dayNumber]!=null) {
						Calendar[dayNumber].partOne(debug);
						return;
					}
				}
				
				throw new IllegalArgumentException();
			}
		}
	}
	
	public static List<String> processSampleInput(String s) {
		String[] lineArray = s.split("\\n");
		List<String> result = new ArrayList<>();
		for(String line : lineArray) result.add(line);
		return result;
	}
}
