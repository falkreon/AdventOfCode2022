package blue.endless.advent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import blue.endless.advent.util.Day;

public class Day20 extends Day {

	@Override
	public void partOne(List<String> input) {
		//NOTE: (-5864, 2510, 882) -> -2472 is wrong!
		
		List<Entry> buffer = new ArrayList<>();
		int i = 0;
		for(String s : input) {
			buffer.add(new Entry(Integer.parseInt(s), i));
			i++;
		}
		
		List<Entry> mixed = mix(buffer);
		System.out.println("1000th: "+nth(mixed, 1000));
		System.out.println("2000th: "+nth(mixed, 2000));
		System.out.println("3000th: "+nth(mixed, 3000));
		
		long sum = nth(mixed, 1000) + nth(mixed, 2000) + nth(mixed, 3000);
		System.out.println("Result: "+sum);
	}
	
	public static record Entry(long value, int originalPosition) {
		@Override
		public String toString() {
			return Long.toString(value);
		}
	}
	
	public List<Entry> mix(List<Entry> input) {
		//Check for duplicate numbers
		/*
		Set<Integer> check = new HashSet<>(input);
		if (check.size()!=input.size()) {
			System.out.println("WARNING: Duplicate values exist in this set!");
		}*/
		
		//Copy the list
		List<Entry> result = new ArrayList<Entry>(input);
		int position = 0;
		//System.out.println(result);
		for(Entry e : input) {
			if (e.value()==0) continue;
			int index = result.indexOf(e);
			if (index==-1) throw new IllegalStateException();
			result.remove(e);
			
			long newIndex = index+e.value();
			 //System.out.println(i+": OldIndex: "+index+" NewIndex: "+newIndex);
			while(newIndex<0) newIndex += result.size();
			while(newIndex>result.size()+1) newIndex -= result.size();
			/*if (newIndex==0 || newIndex==result.size()) {
				if (newIndex==0) {
					newIndex += result.size();
				} else {
					newIndex -= result.size();
				}
			}*/
			//System.out.println("Wrapped: "+newIndex);
			
			result.add((int) newIndex, e);
			//System.out.println(result);
		}
		
		return result;
	}
	
	public List<Entry> mix2(List<Entry> entries, int rounds) {
		List<Entry> cur = new ArrayList<>(entries);
		for(int i=0; i<rounds; i++) {
			cur = mix2(entries, cur);
		}
		
		return cur;
	}
	
	public List<Entry> mix2(List<Entry> originalInput, List<Entry> input) {
		//Copy the list
		List<Entry> result = new ArrayList<Entry>(input);
		
		//Mix
		for(Entry e : originalInput) {
			if (e.value()==0) continue;
			int index = result.indexOf(e);
			if (index==-1) throw new IllegalStateException();
			result.remove(e);
			
			long newIndex = index+e.value();
			
			//while(newIndex<0) newIndex += result.size();
			
			//newIndex = newIndex % result.size();
			newIndex = modulus(newIndex, result.size());
			//while(newIndex>result.size()+1) newIndex -= result.size();

			result.add((int) newIndex, e);
		}
		
		return result;
	}
	
	public long nth(List<Entry> input, int n) {
		//Find the first index of any zero-valued entry. There should be only one.
		int base = 0;
		for(int i=0; i<input.size(); i++) {
			if (input.get(i).value()==0) {
				base = i;
				break;
			}
		}
		//int base = input.indexOf(Integer.valueOf(0));
		int index = (n+base) % input.size();
		return input.get(index).value();
	}
	
	public static final long DECRYPTION_KEY = 811589153;
	
	@Override
	public void partTwo(List<String> input) {
		List<Entry> buffer = new ArrayList<>();
		int i = 0;
		for(String s : input) {
			buffer.add(new Entry(Long.parseLong(s) * DECRYPTION_KEY, i));
			i++;
		}
		
		List<Entry> mixed = new ArrayList<>(buffer);
		System.out.println("Original state: "+mixed);
		
		for(int j=0; j<10; j++) {
			mixed = mix2(buffer, mixed);
			System.out.println("New state: "+mixed);
		}
		
		System.out.println("1000th: "+nth(mixed, 1000));
		System.out.println("2000th: "+nth(mixed, 2000));
		System.out.println("3000th: "+nth(mixed, 3000));
		
		long sum = nth(mixed, 1000) + nth(mixed, 2000) + nth(mixed, 3000);
		System.out.println("Result: "+sum);
	}
	
	public static long modulus(long num, long denom) {
		//return (((num % denom) + denom) % denom);
		
		long result = num % denom;
		if (result<0) result += denom;
		
		return result;
	}

	@Override
	protected String getRawSampleData() {
		return """
				1
				2
				-3
				3
				-2
				0
				4
				""";
	}

}
