package blue.endless.advent.util;

public record Rect(int x, int y, int width, int height) {
	
	public boolean contains(int x, int y) {
		return
				x>=this.x &&
				y>=this.y &&
				x<this.x+this.width &&
				y<this.y+this.height;
	}
	
	public boolean contains(Vec2i vec) {
		return contains(vec.x(), vec.y());
	}
	
	public Rect add(int x, int y) {
		Rect result = this;
		if (x<result.x) result = new Rect(x, result.y, result.width + Math.abs(x-result.x), result.height);
		if (y<result.y) result = new Rect(result.x, y, result.width, result.height + Math.abs(y-result.y));
		if (x>=result.x+result.width) result = new Rect(result.x, result.y, x-result.x+1, result.height);
		if (y>=result.y+result.height) result = new Rect(result.x, result.y, result.width, y-result.y+1);
		
		return result;
	}
	
	public Rect add(Vec2i vec) {
		return add(vec.x(), vec.y());
	}
}
