package blue.endless.advent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Day4 {
	public List<String> sampleData() {
		List<String> sampleData = new ArrayList<>();
		String[] sampleDataArray = """
		2-4,6-8
		2-3,4-5
		5-7,7-9
		2-8,3-7
		6-6,4-6
		2-6,4-8
		""".split("\\n");
		for(String s : sampleDataArray) if (!s.isBlank()) sampleData.add(s);
		
		return sampleData;
	}
	
	public void run() {
		try {
			List<String> input = Files.readAllLines(Path.of("data", "day4.txt"));
			run(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(List<String> input) {
		int totalContained = 0;
		
		for(String s : input) {
			String[] parts = s.split(",");
			Range a = Range.of(parts[0].trim());
			Range b = Range.of(parts[1].trim());
			
			boolean fullyContained = a.contains(b) || b.contains(a);
			if (fullyContained==true) totalContained++;
			System.out.println("[ "+a+", "+b+" ] - fullyContained?: "+fullyContained);
		}
		
		System.out.println("Total teams where one set fully contains the other: "+totalContained);
	}
	
	public void runPartB() {
		try {
			List<String> input = Files.readAllLines(Path.of("data", "day4.txt"));
			runPartB(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void runPartB(List<String> input) {
		int totalIntersected = 0;
		
		for(String s : input) {
			String[] parts = s.split(",");
			Range a = Range.of(parts[0].trim());
			Range b = Range.of(parts[1].trim());
			
			if (a.intersects(b)) {
				System.out.println(""+a+" intersects "+b);
				totalIntersected++;
			}
		}
		
		System.out.println("Total Intersected: "+totalIntersected);
	}
	
	public record Range(int start, int end) {
		
		
		public boolean contains(Range other) {
			return other.start >= this.start && other.end <= this.end;
		}
		
		public boolean intersects(Range other) {
			return (other.start<=end && start<=other.end);
		}
		
		public static Range of(String desc) {
			String[] parts = desc.split("-");
			int start = Integer.parseInt(parts[0]);
			int end = Integer.parseInt(parts[1]);
			return new Range(start,end);
		}
		
		
	}
}
