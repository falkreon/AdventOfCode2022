package blue.endless.advent.util;

public record Range(int start, int length) {
	public boolean contains(int value) {
		return value>=start && value<start+length;
	}
	
	public Range growToInclude(int value) {
		if (value<start) {
			int delta = start - value;
			return new Range(value, length+delta);
		} else if (value>=start+length) {
			return new Range(start, value-start+1);
		} else {
			return this;
		}
	}
	
	
	public boolean intersects(Range other) {
		return
				this.start  < other.start + other.length &&
				other.start < this.start  + this.length;
	}
	
	public boolean canUnion(Range other) {
		//We can union if we intersect OR if we're end-to-end
		return
				intersects(other) ||
				other.start == this.start + this.length ||
				this.start == other.start + other.length;
		
	}
	
	public Range union(Range other) {
		if (!canUnion(other)) throw new IllegalArgumentException();
		
		if (this.start<other.start) {
			return this.growToInclude(other.start+other.length-1);
		} else {
			return other.growToInclude(this.start+this.length-1);
		}
	}
	
	public static Range inclusive(int from, int to) {
		if (from<to) {
			return new Range(from, to-from + 1);
		} else {
			return new Range(to, from-to + 1);
		}
	}
}
