package blue.endless.advent;

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
		}
	}
}
