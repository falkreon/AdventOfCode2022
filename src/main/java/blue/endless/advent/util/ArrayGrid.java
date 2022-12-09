package blue.endless.advent.util;

import java.util.Arrays;

public class ArrayGrid<T> {
	private T[] data;
	private int width;
	private int height;
	private T defaultValue = null;
	
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
	
	public void set(int x, int y, T value) {
		if (x<0 || y<0 || x>=width || y>=height) return;
		data[y * width + x] = value;
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
				result.append(altToString(get(x,y)));
			}
			result.append("\n");
		}
		
		result.deleteCharAt(result.length()-1);
		
		return result.toString();
	}
	
	private String altToString(Object o) {
		if (o instanceof Boolean) {
			if (o.equals(Boolean.TRUE)) return "#";
			return ".";
		} else {
			return o.toString();
		}
	}
}
