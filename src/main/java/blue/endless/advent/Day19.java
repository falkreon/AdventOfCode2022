package blue.endless.advent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import blue.endless.advent.util.Day;
import blue.endless.advent.util.Strings;

/*
 * Day 19: Optimization again, but completely unhinged. We need to parse the blueprints, we need to optimize our robot
 * construction for each blueprint, then we need to take all 30 blueprint optimization results and combine them. With
 * even moderate optimization it would take me about two hours to generate all the useful combinations for *one*
 * blueprint so I'm going to skip this one.
 */

public class Day19 extends Day {

	public static final int AVAILABLE_TIME = 24;
	
	private long combos = 0L;
	
	@Override
	public void partOne(List<String> input) {
		String sampleSolution = "CCCOCOGG";
		
		int blueprintNum = 1;
		for(String blueprintLine : input) {
			Map<Material, Recipe> blueprint = new HashMap<>();
			
			String prefix = "Blueprint "+blueprintNum+": ";
			blueprintLine = Strings.removePrefix(blueprintLine, prefix);
			
			//System.out.println(blueprintLine);
			System.out.println("Blueprint "+blueprintNum);
			
			String[] recipeStrings = blueprintLine.split(Pattern.quote("."));
			for(String s : recipeStrings) {
				if (s.isBlank()) continue;
				
				String[] parts = s.trim().split("\\s");
				String materialResultString = parts[1];
				s = Strings.removePrefix(s.trim(), "Each "+materialResultString+" robot costs ");
				Recipe r = Recipe.of(s);
				r.setResult(Material.of(materialResultString));
				blueprint.put(r.result, r);
				
				System.out.println("  "+r);
			}
			
			System.out.println();
			long max = getTheoreticalComboCount(blueprint);
			System.out.println("Theoretical count: "+max);
			
			combos = 0L;
			forEachUseful("", blueprint, it->{
				combos++;
				if (combos%1000000==0) System.out.println("Working ("+combos+" combinations)");
			});
			
			System.out.println("Total combinations: "+combos);
			
			blueprintNum++;
		}
		
		//System.out.println("Combination count: "+(long)Math.pow(4, 24));
		//System.out.println("Generating combinations...");
		
		//forEachStrategy(it->{});
		System.out.println("Complete.");
	}
	
	public void forEachStrategy(Consumer<String> consumer) {
		long strategyCount = (long) Math.pow(Material.values().length, AVAILABLE_TIME);
		for(long l = 0; l<strategyCount; l++) {
			String s = "";
			int r = 0;
			int c = 0;
			int o = 0;
			int g = 0;
			for(int i=0; i<AVAILABLE_TIME; i++) {
				long m = l >>(i*2); //2 bits per move is enough to encode R/C/O/G
				s += switch(((int) m) & 0x3) {
					case 0 -> 'R';
					case 1 -> 'C';
					case 2 -> 'O';
					case 3 -> 'G';
					default -> throw new IllegalStateException();
				};
			}
			
			consumer.accept(s);
		}
	}
	
	public long getTheoreticalComboCount(Map<Material, Recipe> blueprint) {
		long rMax = 0;
		long cMax = 0;
		long oMax = 0;
		long gMax = 0;
		
		for(Recipe r : blueprint.values()) {
			for(Map.Entry<Material, Integer> entry : r.cost.entrySet()) {
				switch(entry.getKey()) {
				case ORE      -> { if (r.result!=Material.ORE) rMax = Math.max(rMax, entry.getValue()); } //More ore bots to make more ore bots isn't useful
				case CLAY     -> cMax = Math.max(cMax, entry.getValue());
				case OBSIDIAN -> oMax = Math.max(oMax, entry.getValue());
				case GEODE    -> gMax = Math.max(gMax, entry.getValue());
				}
			}
		}
		
		long numCharacters = rMax * cMax * oMax;
		return (long) Math.pow(numCharacters, 4);
	}
	
	public void forEachUseful(String prefix, Map<Material, Recipe> blueprint, Consumer<String> consumer) {
		int rCount = 0;
		int cCount = 0;
		int oCount = 0;
		//int gCount = 0;
		
		int rMax = 0;
		int cMax = 0;
		int oMax = 0;
		//int gMax = 0;
		
		for(Recipe r : blueprint.values()) {
			//System.out.println("Adding in "+r);
			for(Map.Entry<Material, Integer> entry : r.cost.entrySet()) {
				switch(entry.getKey()) {
				case ORE      -> { if (r.result!=Material.ORE) rMax = Math.max(rMax, entry.getValue()); } //More ore bots to make more ore bots isn't useful
				case CLAY     -> cMax = Math.max(cMax, entry.getValue());
				case OBSIDIAN -> oMax = Math.max(oMax, entry.getValue());
				case GEODE    -> {}//gMax = Math.max(gMax, entry.getValue());
				}
			}
		}
		
		for(int i=0; i<prefix.length(); i++) {
			Material cur = Material.ofCode(prefix.charAt(i));
			switch(cur) {
			case ORE -> rCount++;
			case CLAY -> cCount++;
			case OBSIDIAN -> oCount++;
			case GEODE -> {}//gCount++;
			}
		}
		
		//System.out.println("Useful materials: "+oMax+"/"+cMax+"/"+oMax+"/"+gMax);
		
		if (rCount>=rMax && cCount>=cMax && oCount>=oMax) {
			return;
		}
		
		//Now that we know about this prefix, let's add some valid suffixes
		if (prefix.length()<AVAILABLE_TIME) {
			if (rCount<rMax) {
				String cur = prefix+'R';
				consumer.accept(cur);
				forEachUseful(cur, blueprint, consumer);
			}
			
			//if (gCount<gMax) {
			//{
				//We'll always consider G
			//	String cur = prefix+'G';
			//	consumer.accept(cur);
			//	forEachUseful(cur, blueprint, consumer);
			//}
			
			if (cCount<cMax) {
				String cur = prefix+'C';
				consumer.accept(cur);
				forEachUseful(cur, blueprint, consumer);
			}
			
			if (oCount<oMax) {
				String cur = prefix+'O';
				consumer.accept(cur);
				forEachUseful(cur, blueprint, consumer);
			}
		}
	}

	@Override
	public void partTwo(List<String> input) {
		// TODO Auto-generated method stub
		
	}
	
	public static class Recipe {
		private Map<Material, Integer> cost = new HashMap<>();
		private Material result; //The Material type the resulting robot can harvest
		
		public void setResult(Material result) {
			this.result = result;
		}
		
		public boolean canCraft(Map<Material, Integer> backpack) {
			for(Map.Entry<Material, Integer> entry : cost.entrySet()) {
				Integer onHand = backpack.get(entry.getKey());
				if (onHand==null || onHand<entry.getValue()) return false;
			}
			
			return true;
		}
		
		public Material getResult() {
			return result;
		}
		
		@Override
		public String toString() {
			if (cost.size()==0) return "nothing";
			if (cost.size()==1) {
				Map.Entry<Material, Integer> entry = cost.entrySet().stream().findFirst().orElseThrow();
				return entry.getValue().intValue()+" "+entry.getKey().name().toLowerCase() + " -> "+result.toString().toLowerCase();
			}
			
			if (cost.size()==2) {
				Iterator<Map.Entry<Material, Integer>> i = cost.entrySet().iterator();
				Map.Entry<Material, Integer> entry = i.next();
				String result = entry.getValue().intValue()+" "+entry.getKey().name().toLowerCase();
				result += " and ";
				entry = i.next();
				result += entry.getValue().intValue()+" "+entry.getKey().name().toLowerCase() + " -> "+this.result.toString().toLowerCase();
				
				return result;
			}
			
			return cost.toString() + " -> "+result.toString().toLowerCase();
		}
		
		public static Recipe of(String s) {
			s = s.trim();
			Recipe result = new Recipe();
			if (s.endsWith(".")) s = s.substring(0, s.length()-1);
			if (s.contains("and")) {
				String[] ingredients = s.split("and");
				for(String ingredient : ingredients) {
					String[] parts = ingredient.trim().split(" ");
					int count = Integer.parseInt(parts[0]);
					Material material = Material.of(parts[1]);
					result.cost.put(material, count);
				}
			} else {
				String[] parts = s.split(" ");
				int count = Integer.parseInt(parts[0]);
				Material material = Material.of(parts[1]);
				result.cost.put(material, count);
			}
			
			return result;
		}
	}
	
	public static enum Material {
		ORE('R'),
		CLAY('C'),
		OBSIDIAN('O'),
		GEODE('G');
		
		private final char code;
		
		Material(char code) {
			this.code = code;
		}
		
		public static Material of(String s) {
			for(Material m : values()) {
				if (m.name().toLowerCase().equals(s)) return m;
			}
			
			throw new IllegalArgumentException();
		}
		
		public static Material ofCode(char ch) {
			for(Material m : values()) {
				if (m.code == ch) return m;
			}
			
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected String getRawSampleData() {
		return
			"""
			Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
			Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
			""";
	}
	
}
