package blue.endless.advent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;

public class Day6 {
	public void run() {
		//run("mjqjpqmgbljsphdztnvjfqwrcgsmlb");
		//run("bvwbjplbgvbhsrlpgdmjqwftvncz");
		//run("nppdvjthqldpwncqszvftbrmjlhg");
		//run("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg");
		//run("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw");
		
		try {
			String input = Files.readString(Path.of("data", "day6.txt"));
			run(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void run(String input) {
		System.out.println("First Marker: "+findFirstMarker(input));
	}
	
	public int findFirstMarker(String signal) {
		Deque<Character> last4 = new ArrayDeque<>();
		
		for(int i=0; i<signal.length(); i++) {
			//at the start of each loop, i == the number of processed characters.
			//at the end of each loop, i+1 == the number of processed characters.
			
			char cur = signal.charAt(i);
			
			last4.addLast(cur);
			while(last4.size() > 4) last4.removeFirst();
			
			
			if (last4.size() >= 4) {
				if (startsWithUniqueCodon(last4)) return i+1; //We constructed a valid packet start codon by processing this character
			}
		}
		
		return -1;
	}
	
	public int findMessageMarker(String signal) {
		Deque<Character> last14 = new ArrayDeque<>();
		
		for(int i=0; i<signal.length(); i++) {
			//at the start of each loop, i == the number of processed characters.
			//at the end of each loop, i+1 == the number of processed characters.
			
			char cur = signal.charAt(i);
			
			last14.addLast(cur);
			while(last14.size() > 14) last14.removeFirst();
			
			
			if (last14.size() >= 14) {
				if (allUnique(last14)) return i+1;
			}
		}
		
		return -1;
	}
	
	private boolean startsWithUniqueCodon(Collection<Character> col) {
		Iterator<Character> peek = col.iterator();
		char a = peek.next();
		char b = peek.next();
		char c = peek.next();
		char d = peek.next();
		
		return
				a!=b && a!=c && a!=d &&
				b!=c && b!=d &&
				c!=d;
	}
	
	private boolean allUnique(Collection<Character> col) {
		//We're going to do this the quick and dirty way: Dump them into a Set. If the set of unique characters is
		//the same cardinality as the input set, return true
		
		HashSet<Character> tmp = new HashSet<>(col);
		return tmp.size() == col.size();
	}
	
	public void runPartB() {
		//runPartB("mjqjpqmgbljsphdztnvjfqwrcgsmlb");
		try {
			String input = Files.readString(Path.of("data", "day6.txt"));
			runPartB(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void runPartB(String input) {
		System.out.println("First Message Marker: "+findMessageMarker(input));
	}
}
