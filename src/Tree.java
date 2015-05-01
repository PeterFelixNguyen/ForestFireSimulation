/**
 * Copyright 2015 Peter "Felix" Nguyen & Emmanuel Medina Lopez
 * 
 * Forest Fire Simulation with 2D Graphics.
 * 
 * Tree encapsulates the characteristics and states of individual trees.
 */
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

public class Tree {
	// Characteristics
	public final int x;
	public final int y;
	public int initialFireIndex;
	public int fireIndex;
	
	// States
	private String state;
	public static final String GREEN = "alive";
	public static final String RED = "burning";
	public static final String BLACK = "burnt";

	// Types
	public final int type;
	public static final int CIRCLE = 1;
	public static final int TRIANGLE = 2;
	
	// Nearby Trees
	private Set<Tree> nearbyTrees = new HashSet<Tree>(); 

	// Tree Modes
	private boolean isHomogenous;
	
	public void addNearbyTree(Tree tree) {
		nearbyTrees.add(tree);
	}
	
	public Set<Tree> getNearbyTrees() {
		return nearbyTrees;
	}
	
	public Tree(int x, int y) {
		initialFireIndex = (int) (Math.random() * 3 + 1);
		fireIndex = initialFireIndex;
		state = GREEN;
		
		isHomogenous = false;
		
		if (isHomogenous) { 
			type = TRIANGLE;
		} else {
			type = (int) (Math.random() * 2 + 1);
		}
		this.x = x;
		this.y = y;
	}

	/**
	 * This method is deprecated but may have use in the future
	 */
	public Color getLeafColor() {
		if (state == GREEN) {
			return new Color(30, 150, 70);
		} else if (state == RED) {
			return new Color(30, 150, 70);
		} else {
			return new Color(0,0,0,0);
		}
	}

	/**
	 * This method is deprecated but may have use in the future
	 */
	public Color getTrunkColor() {
		if (state == GREEN) {
			return new Color(160, 110, 60);
		} else if (state == RED) {
			return new Color(160, 110, 60);
		} else {
			return new Color(70,70,70);
		}
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		if (this.state != BLACK) {
			this.state = state;
		}
	}

	public void resetState() {
		this.state = GREEN;
		fireIndex = initialFireIndex;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public void tick() {
		fireIndex++;
		if (fireIndex == 74) {
			setState(BLACK);
		}
	}
}
