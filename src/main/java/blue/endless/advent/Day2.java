package blue.endless.advent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import blue.endless.advent.util.Day;

public class Day2 extends Day {
	
	@Override
	public void partOne(List<String> lines) {
		int totalScore = 0;
		for(String s : lines) {
			String[] parts = s.split("\\s");
			Move opponent = Move.of(parts[0]);
			Move mine = Move.of(parts[1]);
			
			int score = score(opponent, mine);
			totalScore += score;
			System.out.println(""+opponent+", "+mine+" -> "+score+" ( "+totalScore+" )");
		}
		
		System.out.println("Final score: "+totalScore);
	}
	
	public void partTwo(List<String> lines) {
		int totalScore = 0;
		for(String s : lines) {
			String[] parts = s.split("\\s");
			Move opponent = Move.of(parts[0]);
			Outcome outcome = Outcome.of(parts[1]);
			Move mine = createOutcome(opponent, outcome);
			
			int score = score(opponent, mine);
			totalScore += score;
			System.out.println(""+opponent+", "+mine+" -> "+score+" ( "+totalScore+" )");
		}
		
		System.out.println("Final score: "+totalScore);
	}
	
	public int score(Move them, Move me) {
		int movePoints = me.pointValue();
		int winPoints = 0;
		if (
				(me==Move.ROCK && them==Move.SCISSORS) ||
				(me==Move.PAPER && them==Move.ROCK) ||
				(me==Move.SCISSORS && them==Move.PAPER)
			) {
			//We won
			winPoints = 6;
		} else if (me==them) {
			//Draw
			winPoints = 3;
		} else {
			//We lost
			winPoints = 0;
		}
		
		return movePoints + winPoints;
	}
	
	public Move createOutcome(Move them, Outcome o) {
		switch(o) {
			case WIN -> {
				return switch(them) {
					case ROCK -> Move.PAPER;
					case SCISSORS -> Move.ROCK;
					case PAPER -> Move.SCISSORS;
				};
			}
			
			case LOSE -> {
				return switch(them) {
					case ROCK -> Move.SCISSORS;
					case SCISSORS -> Move.PAPER;
					case PAPER -> Move.ROCK;
				};
			}
			
			case DRAW -> {
				return them;
			}
		}
		
		//We shouldn't be able to get here
		throw new IllegalArgumentException();
	}
	
	public static enum Move {
		ROCK    (1, "a", "x"),
		PAPER   (2, "b", "y"),
		SCISSORS(3, "c", "z"),
		;
		
		private final int points;
		private final Set<String> representations;
		
		Move(int points, String... representations) {
			this.points = points;
			this.representations = new HashSet<>();
			for(String s : representations) this.representations.add(s);
		}
		
		public int pointValue() {
			return points;
		}
		
		public boolean matches(char ch) {
			String toMatch = Character.toString(Character.toLowerCase(ch));
			return representations.contains(toMatch);
		}
		
		public boolean matches(String s) {
			return representations.contains(s.toLowerCase().trim());
		}
		
		public static Move of(char ch) {
			for(Move m : values()) {
				if (m.matches(ch)) return m;
			}
			throw new IllegalArgumentException();
		}
		
		public static Move of(String s) {
			for(Move m : values()) {
				if (m.matches(s)) return m;
			}
			throw new IllegalArgumentException();
		}
	}
	
	public static enum Outcome {
		WIN ("z", 6),
		DRAW("y", 3),
		LOSE("x", 0)
		;
		
		private String code;
		private int value;
		
		Outcome(String code, int value) {
			this.code = code;
			this.value = value;
		}
		
		public int value() {
			return value;
		}
		
		public static Outcome of(String code) {
			for(Outcome o : values()) {
				if (o.code.equalsIgnoreCase(code.trim())) return o;
			}
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected String getRawSampleData() {
		return """
				A Y
				B X
				C Z
				""";
	}

	@Override
	protected String getDataFileName() {
		return "day2.txt";
	}
}
