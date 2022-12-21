package blue.endless.advent.util;

public record Vec3i(int x, int y, int z) {
	public Vec3i add(int x, int y, int z) {
		return new Vec3i(this.x + x, this.y + y, this.z + z);
	}
	
	public Vec3i add(Vec3i vec) {
		return new Vec3i(this.x + vec.x, this.y + vec.y, this.z + vec.z);
	}
	
	public Vec3i subtract(int x, int y, int z) {
		return new Vec3i(this.x - x, this.y - y, this.z - z);
	}
	
	public Vec3i subtract(Vec3i vec) {
		return new Vec3i(this.x - vec.x, this.y - vec.y, this.z - vec.z);
	}
}
