package blue.endless.advent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Day3 {
	public void run() {
		try {
			List<String> lines = Files.readAllLines(Path.of("data", "day3.txt"));
			
			int prioritySum = 0;
			for(String line : lines) {
				int packSize = line.length()/2;
				String leftPack = line.substring(0,packSize);
				String rightPack = line.substring(packSize);
				char shared = 0;
				for(int i=0; i<leftPack.length(); i++) {
					char ch = leftPack.charAt(i);
					if (rightPack.contains(Character.toString(ch))) {
						shared = ch;
						break;
					}
				}
				
				prioritySum += getPriority(shared);
				System.out.println(leftPack+" and "+rightPack+" share '"+shared+"' -> "+getPriority(shared));
			}
			
			
			System.out.println("The sum of the priorities is "+prioritySum);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private int getPriority(char ch) {
		if (ch >= 'a' && ch <= 'z') {
			return ch - 'a' + 1;
		} else if (ch >= 'A' && ch <= 'Z') {
			return ch - 'A' + 27;
		} else {
			return 0;
		}
	}
	
	public void runPartB() {
		/*
		List<String> lines = new ArrayList<>();
		lines.add("vJrwpWtwJgWrhcsFMMfFFhFp");
		lines.add("jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL");
		lines.add("PmmdzqPrVvPwwTWBwg");
		lines.add("wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn");
		lines.add("ttgJtRGJQctTZtZT");
		lines.add("CrZsJsPPZsGzwwsLwLmpwMDw");
		*/
		
		try {
			List<String> lines = Files.readAllLines(Path.of("data", "day3.txt"));
		
			int prioritySum = 0;
			while(lines.size()>=3) {
				String a = lines.remove(0);
				String b = lines.remove(0);
				String c = lines.remove(0);
				for(int i=0; i<a.length(); i++) {
					char ch = a.charAt(i);
					if (b.contains(Character.toString(ch)) && c.contains(Character.toString(ch))) {
						int priority = getPriority(ch);
						prioritySum += priority;
						System.out.println("Group shares '"+ch+"' -> "+priority);
						break;
					}
				}
			}
			
			System.out.println("Sum of badge priorities is "+prioritySum);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
