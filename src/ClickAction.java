/**
 * Copyright 2015 Peter "Felix" Nguyen & Emmanuel Medina Lopez
 * 
 * Forest Fire Simulation with 2D Graphics.
 * 
 * ClickAction is an object used to store information on clicks for the purpose
 * of implementing a script-based replay feature to the simulator.
 */
public class ClickAction {
	private final int x;
	private final int y;
	private final int tick;
	
	public ClickAction(int x, int y, int tick) {
		this.x = x;
		this.y = y;
		this.tick = tick;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getTick() {
		return tick;
	}
}
