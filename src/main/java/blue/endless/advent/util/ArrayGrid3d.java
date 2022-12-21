package blue.endless.advent.util;

import java.util.Arrays;

public class ArrayGrid3d<T> implements Grid3d<T> {
	private T[] data;
	private int xsize;
	private int ysize;
	private int zsize;
	private T defaultValue = null;
	
	public ArrayGrid3d(int xsize, int ysize, int zsize) {
		this(xsize, ysize, zsize, null);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayGrid3d(int xsize, int ysize, int zsize, T defaultValue) {
		data = (T[]) new Object[xsize * ysize * zsize];
		this.xsize = xsize;
		this.ysize = ysize;
		this.zsize = zsize;
		this.defaultValue = defaultValue;
		clear();
	}
	
	@Override
	public int getXSize() {
		return xsize;
	}

	@Override
	public int getYSize() {
		return ysize;
	}

	@Override
	public int getZSize() {
		return zsize;
	}

	@Override
	public T get(int x, int y, int z) {
		if (x<0 || y<0 || z<0 || x>=xsize || y>=ysize || z>=zsize) return defaultValue;
		
		return data[xsize*zsize*y + xsize*z + x];
	}

	@Override
	public void set(int x, int y, int z, T value) {
		if (x<0 || y<0 || z<0 || x>=xsize || y>=ysize || z>=zsize) return;
		
		data[xsize*zsize*y + xsize*z + x] = value;
	}

	@Override
	public void clear(int x, int y, int z) {
		set(x,y,z,defaultValue);
	}

	@Override
	public void clear() {
		Arrays.fill(data, defaultValue);
	}
	
	public void setDefault(T defaultValue) {
		this.defaultValue = defaultValue;
	}
}
