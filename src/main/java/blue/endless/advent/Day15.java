package blue.endless.advent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import blue.endless.advent.util.ArrayGrid;
import blue.endless.advent.util.Day;
import blue.endless.advent.util.Range;
import blue.endless.advent.util.Vec2i;
import blue.endless.jankson.annotation.Nullable;

public class Day15 extends Day {

	@Override
	public void partOne(List<String> input) {
		List<Sensor> sensors = unpackSensors(input);
		
		System.out.println(sensors);
		
		//for(Sensor s : sensors) {
		//	System.out.println(s.getXExclusion(2000000));
		//}
		
		/*//Visualization
		Vec2i offset = new Vec2i(-2, 0);
		ArrayGrid<Character> viz = new ArrayGrid<>(30, 30, '.');
		viz.elementToString(String::valueOf, false);
		
		for(int y=0; y<viz.getHeight(); y++) {
			ArrayList<Range> exclusionZones = new ArrayList<>();
			for(Sensor s : sensors) {
				Range r = s.getXExclusion(y);
				if (r!=null) exclusionZones.add(r);
			}
			
			for(int x=0; x<viz.getWidth(); x++) {
				int realX = x + offset.x();
				for(Range r : exclusionZones) {
					if (r.contains(realX)) {
						viz.set(x, y, '#');
					}
				}
			}
		}
		
		for(Sensor s : sensors) {
			viz.set(s.beaconX()-offset.x(), s.beaconY()-offset.y(), 'B');
			viz.set(s.x()-offset.x(), s.y()-offset.y(), 'S');
		}
		System.out.println(viz);
		
		System.out.println();*/
		System.out.println(getExcludedCells(sensors, 2_000_000));
	}

	@Override
	public void partTwo(List<String> input) {
		List<Sensor> sensors = unpackSensors(input);
		for(int y=0; y<=4_000_000; y++) {
			List<Range> exclusions = getExclusionZones(sensors, y);
			if (exclusions.size()==1) {
				Range r = exclusions.get(0);
				if (r.start()<=0) {
					if (!r.contains(4_000_000)) {
						System.out.println("Range on line "+y+": "+r);
						return;
					}
				}
			} else if (exclusions.size()==2) {
				Range r = exclusions.get(0);
				Range s = exclusions.get(1);
				
				int holeStart = r.start()+r.length();
				int holeEnd = s.start();
				if (holeEnd-holeStart>=1) {
					System.out.println("Hole found at "+holeStart+", "+y+" lasting for "+(holeEnd-holeStart)+" cells.");
					
					long tuningFrequency = ((long)holeStart) * 4_000_000 + (long)y;
					
					System.out.println("Tuning frequency for indicated beacon: "+tuningFrequency);
					return;
				}
			} else {
				throw new IllegalStateException("Too many exclusion zones: "+exclusions);
			}
		}
	}
	
	public List<Sensor> unpackSensors(List<String> lines) {
		List<Sensor> result = new ArrayList<>();
		for(String line : lines) {
			line = removePrefix(line, "Sensor at x=");
			
			String xString = line.split(",")[0];
			line = removePrefix(line, xString+",");
			
			line = removePrefix(line, " y=");
			
			String yString = line.split(":")[0];
			line = removePrefix(line, yString+":");
			
			line = removePrefix(line, " closest beacon is at x=");
			
			String beaconXString = line.split(",")[0];
			line = removePrefix(line, beaconXString+",");
			
			line = removePrefix(line, " y=");
			String beaconYString = line;
			
			result.add(new Sensor(Integer.parseInt(xString), Integer.parseInt(yString), Integer.parseInt(beaconXString), Integer.parseInt(beaconYString)));
		}
		
		return result;
	}
	
	public int getExcludedCells(List<Sensor> sensors, int y) {
		ArrayList<Range> exclusionZones = new ArrayList<>();
		for(Sensor s : sensors) {
			Range r = s.getXExclusion(y);
			if (r!=null) exclusionZones.add(r);
		}
		
		exclusionZones.sort((a,b)->Integer.compare(a.start(), b.start()));
		
		//System.out.println(exclusionZones);
		
		//Unify of all the ranges that can be unioned
		while (unifyRanges(exclusionZones)) {}
		//System.out.println(exclusionZones);
		
		int result = exclusionZones.stream().collect(Collectors.summingInt(it->it.length()));
		
		//Dedupe and subtract beacons from this line
		Set<Vec2i> beaconsOnLine = new HashSet<>();
		for(Sensor s : sensors) {
			if (s.beaconY==y) beaconsOnLine.add(new Vec2i(s.beaconX, s.beaconY));
		}
		result -= beaconsOnLine.size();
		
		return result;
	}
	
	public List<Range> getExclusionZones(List<Sensor> sensors, int y) {
		ArrayList<Range> exclusionZones = new ArrayList<>();
		for(Sensor s : sensors) {
			Range r = s.getXExclusion(y);
			if (r!=null) exclusionZones.add(r);
		}
		
		exclusionZones.sort((a,b)->Integer.compare(a.start(), b.start()));
		
		//System.out.println(exclusionZones);
		
		//TODO: Unify of all the ranges that can be unioned
		while (unifyRanges(exclusionZones)) {}
		//System.out.println(exclusionZones);
		
		return exclusionZones;
	}
	
	public boolean unifyRanges(List<Range> ranges) {
		if (ranges.size()<2) return false;
		for(int i=1; i<ranges.size(); i++) {
			Range a = ranges.get(i-1);
			Range b = ranges.get(i);
			if (a.canUnion(b)) {
				Range ab = a.union(b);
				//System.out.println("Unifying "+a+" and "+b+" -> "+ab);
				ranges.remove(i-1);
				ranges.set(i-1, ab);
				return true;
			}
		}
		
		return false;
	}
	
	public static String removePrefix(String s, String toRemove) {
		if (!s.startsWith(toRemove)) throw new IllegalArgumentException();
		return s.substring(toRemove.length());
	}
	
	//public int getWidthAt(int y, List<Sensor> beacons) {
	//	
	//}
	
	public record Sensor(int x, int y, int beaconX, int beaconY) {
		
		/**
		 * Gets the answer to part A of the problem for a single beacom: how wide is the exclusion zone of this beacon?
		 * We will subtract the beacons themselves out of the merged sample
		 * @param y The Y value at which to grab a horizontal slice
		 * @return A Range describing the result
		 */
		public @Nullable Range getXExclusion(int y) {
			int dx = Math.abs(beaconX - this.x);
			int dy = Math.abs(beaconY - this.y);
			int fullExclusionWidth = dx + dy;
			int scanlineDisplacement = Math.abs(y-this.y);
			if (scanlineDisplacement > dx+dy) return null;
			
			int scanlineExclusionWidth = fullExclusionWidth - scanlineDisplacement;
			
			return new Range(x-scanlineExclusionWidth, scanlineExclusionWidth*2 + 1);
		}
	}

	@Override
	protected String getRawSampleData() {
		return """
				Sensor at x=2, y=18: closest beacon is at x=-2, y=15
				Sensor at x=9, y=16: closest beacon is at x=10, y=16
				Sensor at x=13, y=2: closest beacon is at x=15, y=3
				Sensor at x=12, y=14: closest beacon is at x=10, y=16
				Sensor at x=10, y=20: closest beacon is at x=10, y=16
				Sensor at x=14, y=17: closest beacon is at x=10, y=16
				Sensor at x=8, y=7: closest beacon is at x=2, y=10
				Sensor at x=2, y=0: closest beacon is at x=2, y=10
				Sensor at x=0, y=11: closest beacon is at x=2, y=10
				Sensor at x=20, y=14: closest beacon is at x=25, y=17
				Sensor at x=17, y=20: closest beacon is at x=21, y=22
				Sensor at x=16, y=7: closest beacon is at x=15, y=3
				Sensor at x=14, y=3: closest beacon is at x=15, y=3
				Sensor at x=20, y=1: closest beacon is at x=15, y=3
				""";
	}

	@Override
	protected String getDataFileName() {
		return "day15.txt";
	}

}
