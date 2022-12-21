package blue.endless.advent.util;

public interface Grid3d<T> {
	int getXSize();
	int getYSize();
	int getZSize();
	T get(int x, int y, int z);
	default T get(Vec3i vec) {
		return get(vec.x(), vec.y(), vec.z());
	}
	void set(int x, int y, int z, T value);
	default void set(Vec3i vec, T value) {
		set(vec.x(), vec.y(), vec.z(), value);
	}
	void clear(int x, int y, int z);
	default void clear(Vec3i vec) {
		clear(vec.x(), vec.y(), vec.z());
	}
	void clear();
}
