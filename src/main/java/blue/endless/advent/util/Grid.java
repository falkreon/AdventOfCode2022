package blue.endless.advent.util;

public interface Grid<T> {
	int getWidth();
	int getHeight();
	T get(int x, int y);
	default T get(Vec2i vec) {
		return get(vec.x(), vec.y());
	}
	void set(int x, int y, T value);
	default void set(Vec2i vec, T value) {
		set(vec.x(), vec.y(), value);
	}
	void clear(int x, int y);
	default void clear(Vec2i vec) {
		clear(vec.x(), vec.y());
	}
	void clear();
}
