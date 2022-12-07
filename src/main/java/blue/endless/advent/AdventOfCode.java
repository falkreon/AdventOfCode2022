package blue.endless.advent;

import java.util.ArrayList;
import java.util.List;

public class AdventOfCode {
	public static void main(String[] args) {
		if (args.length==0) {
			System.out.println("usage: java -jar AdventOfCode.jar <day> [params]");
			System.exit(-1);
		}
		
		switch(args[0]) {
		case "1"  -> new Day1().run();
		case "1b" -> new Day1().runPartB();
		case "2"  -> new Day2().run();
		case "2b" -> new Day2().runPartB();
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
		
		default -> throw new IllegalArgumentException();
		}
	}
	
	public static List<String> processSampleInput(String s) {
		String[] lineArray = s.split("\\n");
		List<String> result = new ArrayList<>();
		for(String line : lineArray) result.add(line);
		return result;
	}
}
