package blue.endless.advent.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleConsumer;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Display extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private GridPanel gridPanel = new GridPanel();
	
	public Display() {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setBackground(Color.BLACK);
		
		Dimension d = new Dimension(600, 500);
		this.setMinimumSize(d);
		this.setPreferredSize(d);
		this.setSize(d);
		
		this.getContentPane().add(gridPanel);
	}
	
	public void setGrid(Grid<?> grid) {
		gridPanel.setGrid(grid);
	}
	
	public void setTicksPerSecond(int perSecond) {
		gridPanel.setTicksPerSecond(perSecond);
	}
	
	public void setOnTick(DoubleConsumer consumer) {
		gridPanel.setOnTick(consumer);
	}
	
	public void mapColor(Object o, Color c) {
		gridPanel.mapColor(o, c);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		this.repaint(20);
	}
	
	private static class GridPanel extends JPanel {
		private static final long serialVersionUID = -3636721400626995706L;
		private Grid<?> grid = null;
		private long tickPeriod = (long) ((1/20.0) * 1_000);
		private long lastTick = 0L;
		private long fractionalTicks = 0L;
		private DoubleConsumer onTick = null;
		private Map<Object, Color> colorMap = new HashMap<>();

		public GridPanel() {
			this.setBackground(Color.BLACK);
			this.setDoubleBuffered(true);
		}
		
		public void setTicksPerSecond(int perSecond) {
			tickPeriod = (long) ((1/(double)perSecond) * 1_000);
		}
		
		public void setGrid(Grid<?> grid) {
			this.grid = grid;
		}
		
		public void setOnTick(DoubleConsumer consumer) {
			this.onTick = consumer;
		}
		
		public void mapColor(Object o, Color c) {
			colorMap.put(o, c);
		}
		
		@Override
		public void paint(Graphics g) {
			if (onTick!=null) {
				long now = System.nanoTime() / 1_000_000L;
				fractionalTicks += now - lastTick;
				if (fractionalTicks > tickPeriod) {
					fractionalTicks = fractionalTicks % tickPeriod;
					onTick.accept(fractionalTicks / (double) tickPeriod);
				}
				lastTick = now;
			}
			
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			
			if (grid!=null) {
				int xmul = this.getWidth() / grid.getWidth();
				int ymul = this.getHeight() / grid.getHeight();
				int mul = Math.max(Math.min(xmul, ymul), 1);
				
				for(int y=0; y<grid.getHeight(); y++) {
					for(int x=0; x<grid.getWidth(); x++) {
						Object curCell = grid.get(x, y);
						//TODO: Find a color for curCell
						Color cellColor = colorMap.get(curCell);
						if (cellColor==null) cellColor = Color.RED;
						g.setColor(cellColor);
						g.fillRect(x*mul, y*mul, mul, mul);
					}
				}
			}
		}
	}
}
