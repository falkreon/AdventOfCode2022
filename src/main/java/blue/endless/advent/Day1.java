package blue.endless.advent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Day1 {
	public void run() {
		
		try {
			List<String> lines = Files.readAllLines(Path.of("data", "day1.txt"));
			List<Elf> elves = new ArrayList<>();
			Elf bestElf = null;
			
			//Load up first elf
			Elf curElf = new Elf();
			bestElf = curElf;
			
			for(String line : lines) {
				if (line.isBlank()) {
					elves.add(curElf);
					curElf = new Elf();
				} else {
					int calories = Integer.parseInt(line);
					curElf.caloriesCarried += calories;
					if (curElf.caloriesCarried > bestElf.caloriesCarried) bestElf = curElf;
				}
			}
			if (curElf.caloriesCarried > 0) {
				elves.add(curElf);
				if (curElf.caloriesCarried > bestElf.caloriesCarried) bestElf = curElf;
			}
			
			
			
			System.out.println("The biggest calorie payload carried by any elf is "+bestElf.caloriesCarried);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void runPartB() {
		try {
			List<String> lines = Files.readAllLines(Path.of("data", "day1.txt"));
			List<Elf> elves = new ArrayList<>();
			
			//Load up first elf
			Elf curElf = new Elf();
			
			//Catalogue all elves
			for(String line : lines) {
				if (line.isBlank()) {
					elves.add(curElf);
					curElf = new Elf();
				} else {
					int calories = Integer.parseInt(line);
					curElf.caloriesCarried += calories;
				}
			}
			if (curElf.caloriesCarried > 0) {
				elves.add(curElf);
			}
			
			//Grab top 3
			List<Elf> topThree = new ArrayList<>();
			for(int i=0; i<3; i++) {
				Elf bestElf = elves.get(0);
				
				for(Elf elf : elves) {
					if (elf.caloriesCarried > bestElf.caloriesCarried) bestElf = elf;
				}
				
				elves.remove(bestElf);
				topThree.add(bestElf);
			}
			
			int total = topThree.stream().collect(Collectors.summingInt(it->it.caloriesCarried));
			
			System.out.println("Top three elves carry "+topThree.get(0).caloriesCarried+", "+topThree.get(1).caloriesCarried+", and "+topThree.get(2).caloriesCarried+" calories, for a total of "+total);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static class Elf {
		public int caloriesCarried = 0;
	}
}
