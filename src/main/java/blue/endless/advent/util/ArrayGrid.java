package blue.endless.advent.util;

import java.util.Arrays;
import java.util.function.Function;

public class ArrayGrid<T> {
	private T[] data;
	private int width;
	private int height;
	private T defaultValue = null;
	private Function<T, String> toString = this::altToString;
	private boolean commas = true;
	
	public ArrayGrid(int width, int height) {
		this(width, height, null);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayGrid(int width, int height, T defaultValue) {
		data = (T[]) new Object[width * height];
		Arrays.fill(data, defaultValue);
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public T get(int x, int y) {
		if (x<0 || y<0 || x>=width || y>=height) return defaultValue;
		return data[y * width + x];
	}
	
	public T get(Vec2i vec) {
		return get(vec.x(), vec.y());
	}
	
	public void set(int x, int y, T value) {
		if (x<0 || y<0 || x>=width || y>=height) return;
		data[y * width + x] = value;
	}
	
	public void set(Vec2i vec, T value) {
		set(vec.x(), vec.y(), value);
	}
	
	public void elementToString(Function<T, String> func, boolean commas) {
		this.toString = func;
		this.commas = commas;
	}
	
	public void clear(int x, int y) {
		set(x, y, defaultValue);
	}
	
	public void clear() {
		Arrays.fill(data, defaultValue);
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				result.append(toString.apply(get(x,y)));
				if (commas) result.append(',');
				//result.append(altToString(get(x,y)));
			}
			result.append("\n");
		}
		
		result.deleteCharAt(result.length()-1); //Delete last newline
		if (commas) result.deleteCharAt(result.length()-1); //Delete last comma
		
		return result.toString();
	}
	
	private String altToString(T o) {
		if (o instanceof Boolean) {
			if (o.equals(Boolean.TRUE)) return "#";
			return ".";
		} else {
			return (o==null) ? "null" : o.toString();
		}
	}
}
