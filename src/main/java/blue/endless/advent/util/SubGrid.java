package blue.endless.advent.util;

public class SubGrid<T> implements Grid<T> {
	private Grid<T> delegate;
	private final int width;
	private final int height;
	private int scrollX = 0;
	private int scrollY = 0;
	
	public SubGrid(Grid<T> delegate) {
		this(delegate.getWidth(), delegate.getHeight(), delegate);
	}
	
	public SubGrid(int width, int height, Grid<T> delegate) {
		this.delegate = delegate;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public T get(int x, int y) {
		if (x<0 || y<0 || x>=width || y>=height) return delegate.get(-1, -1); //Force the delegate to give us null or a defaultValue
		
		int srcX = x + scrollX;
		int srcY = y + scrollY;
		return delegate.get(srcX, srcY);
	}

	@Override
	public void set(int x, int y, T value) {
		if (x<0 || y<0 || x>=width || y>=height) return; //refuse to mutate elements outside the window
		
		delegate.set(x + scrollX, y + scrollY, value);
	}

	@Override
	public void clear(int x, int y) {
		if (x<0 || y<0 || x>=width || y>=height) return; //refuse to mutate elements outside the window
		
		delegate.clear(x + scrollX, y + scrollY);
	}

	@Override
	public void clear() {
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				clear(x, y);
			}
		}
	}
	
	public void setDelegate(Grid<T> grid) {
		this.delegate = grid;
	}
	
	public ArrayGrid<T> copy() {
		ArrayGrid<T> result = new ArrayGrid<T>(width, height);
		
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				result.set(x, y, this.get(x, y));
			}
		}
		
		return result;
	}
	
	public void scroll(int x, int y) {
		scrollX = x;
		scrollY = y;
	}
	
	public void scrollToBottom() {
		scrollY = delegate.getHeight() - this.height;
	}
	
	public void scrollToRight() {
		scrollX = delegate.getWidth() - this.width;
	}
}
