package blue.endless.advent;

import java.util.List;

import blue.endless.advent.util.Day;
import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.api.SyntaxError;

public class Day13 extends Day {
	public static final String SAMPLE_INPUT = """
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

	@Override
	protected String getRawSampleData() {
		return SAMPLE_INPUT;
	}

	@Override
	protected String getDataFileName() {
		return "day13.txt";
	}

	@Override
	public void partOne(List<String> input) {
		Jankson jankson = Jankson.builder().build();
		
		for(int i=0; i<input.size(); i += 2) {
			if (input.get(i).isBlank()) i++;
			
			try {
				JsonElement leftElem = jankson.loadElement(input.get(i));
				JsonElement rightElem = jankson.loadElement(input.get(i+1));
				
				System.out.println("Left: "+leftElem+" Right: "+rightElem);
			} catch (SyntaxError e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void partTwo(List<String> input) {
		// TODO Auto-generated method stub
		
	}
}
