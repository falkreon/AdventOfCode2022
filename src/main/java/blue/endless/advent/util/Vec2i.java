package blue.endless.advent.util;

public record Vec2i(int x, int y) {
	public Vec2i add(int x, int y) {
		return new Vec2i(this.x + x, this.y + y);
	}
	
	public Vec2i add(Vec2i vec) {
		return new Vec2i(this.x + vec.x, this.y + vec.y);
	}
	
	public Vec2i subtract(int x, int y) {
		return new Vec2i(this.x - x, this.y - y);
	}
	
	public Vec2i subtract(Vec2i vec) {
		return new Vec2i(this.x - vec.x, this.y - vec.y);
	}
}
