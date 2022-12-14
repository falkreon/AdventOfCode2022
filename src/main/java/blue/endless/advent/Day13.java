package blue.endless.advent;

import java.util.ArrayList;
import java.util.List;

import blue.endless.advent.util.Day;
import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonPrimitive;
import blue.endless.jankson.api.SyntaxError;

public class Day13 extends Day {

	@Override
	protected String getRawSampleData() {
		return """
				[1,1,3,1,1]
				[1,1,5,1,1]
				
				[[1],[2,3,4]]
				[[1],4]
				
				[9]
				[[8,7,6]]
				
				[[4,4],4,4]
				[[4,4],4,4,4]
				
				[7,7,7,7]
				[7,7,7]
				
				[]
				[3]
				
				[[[]]]
				[[]]
				
				[1,[2,[3,[4,[5,6,7]]]],8,9]
				[1,[2,[3,[4,[5,6,0]]]],8,9]
				""";
	}

	@Override
	protected String getDataFileName() {
		return "day13.txt";
	}

	@Override
	public void partOne(List<String> input) {
		Jankson jankson = Jankson.builder().build();
		
		
		int pairNum = 0;
		int result = 0;
		
		for(int i=0; i<input.size(); i += 2) {
			if (input.get(i).isBlank()) i++;
			
			try {
				pairNum++;
				JsonElement leftElem = jankson.loadElement(input.get(i));
				JsonElement rightElem = jankson.loadElement(input.get(i+1));
				
				int comparison = compare(leftElem, rightElem);
				
				System.out.println("Pair "+pairNum+": "+((comparison<0)?"correct order":"INCORRECT order"));
				
				if (comparison<0) result += pairNum;
				//System.out.println("Left: "+leftElem+" Right: "+rightElem+" CorrectOrder?: "+(compare(leftElem, rightElem)<0));
				
			} catch (SyntaxError e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Sum of correct pair indices: "+result);
		
	}
	
	public int compare(JsonElement left, JsonElement right) {
		if (left instanceof JsonPrimitive leftPrim && right instanceof JsonPrimitive rightPrim) {
			//Integer <-> Integer
			return Integer.compare(leftPrim.asInt(0), rightPrim.asInt(0));
			
		} else if (left instanceof JsonPrimitive leftPrim) {
			//Integer <-> Array
			JsonArray newLeft = new JsonArray();
			newLeft.add(leftPrim);
			return compare(newLeft, right);
			
		} else if (right instanceof JsonPrimitive rightPrim) {
			//Array <-> Integer
			JsonArray newRight = new JsonArray();
			newRight.add(rightPrim);
			return compare(left, newRight);
			
		} else if (left instanceof JsonArray leftArr && right instanceof JsonArray rightArr) {
			//Array <-> Array
			int toCompare = Math.min(leftArr.size(), rightArr.size());
			for(int i=0; i<toCompare; i++) {
				int comp = compare(leftArr.get(i), rightArr.get(i));
				if (comp!=0) return comp;
			}
			//No decision has been made. Decide based on who ran out of elements first
			return Integer.compare(leftArr.size(), rightArr.size());
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void partTwo(List<String> input) {
		//Add divider packets
		input.add("[[2]]");
		input.add("[[6]]");
		
		Jankson jankson = Jankson.builder().build();
		ArrayList<JsonElement> packets = new ArrayList<>();
		
		for(String s : input) {
			if (s.isBlank()) continue;
			
			try {
				packets.add(jankson.loadElement(s));
			} catch (SyntaxError ex) {
				throw new RuntimeException(ex);
			}
		}
		
		packets.sort(this::compare);
		
		System.out.println("Reordered packets:");
		for(JsonElement elem : packets) {
			System.out.println(elem);
		}
		
		int divider2 = -1;
		int divider6 = -1;
		for(int i=0; i<packets.size(); i++) {
			if (packets.get(i).toString().equals("[ [ 2 ] ]")) divider2 = i+1;
			if (packets.get(i).toString().equals("[ [ 6 ] ]")) divider6 = i+1;
		}
		
		System.out.println();
		System.out.println("Result: "+divider2+" * "+divider6+" == "+(divider2*divider6));
	}
}
