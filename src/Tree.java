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
	private int initialHealth;
	private int health;
	public static final int TREE_LIFE = ForestFire.ANIMATION_LENGTH;
	public static final int TREE_LIFE_INDEX = TREE_LIFE - 1;
	
	// States
	private String state;
	public static final String GREEN = "alive";
	public static final String RED = "burning";
	public static final String BLACK = "burnt";

	// Boolean States
	private boolean wet = false;
	private int wetDuration = 0;
	
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
	
	public void clearNearbyTrees() {
		nearbyTrees.clear();
	}
	
	public Tree(int x, int y) {
		initialHealth = (int) (Math.random() * 3 + 1);
		health = initialHealth;
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
		health = initialHealth;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getXCentered() {
		return x  + (ForestFire.TREE_DIAMETER / 2);
	}

	public int getYCentered() {
		return y + (ForestFire.TREE_DIAMETER / 2);
	}
	
	public void tickHealth() {
		if (!wet) {
			health++;
		} else if (health > initialHealth) {
			health--;
		} else if (health == initialHealth) {
			health = initialHealth;
			setState(GREEN);
		}
		
		if (health == TREE_LIFE_INDEX) {
			setState(BLACK);
		}
	}
	
	public int getHealth() {
		return health;
	}
	
	public void tickState() {
		if (wetDuration > 0) {
			wetDuration--;
		} else {
			wet = false;
		}
	}
	
	public void setWet(boolean wet, int wetDuration) {
		this.wetDuration = wetDuration;
		this.wet = wet;
	}
	
	public boolean isWet() {
		return wet;
	}
}
