/**
 * Copyright 2015 Peter "Felix" Nguyen & Emmanuel Medina Lopez
 * 
 * Forest Fire Simulation with 2D Graphics.
 * 
 * TreeXComparator is used to sort two Trees based on the x coordinate.
 */
import java.util.Comparator;

public class TreeXComparator implements Comparator<Tree> {

	@Override
	public int compare(Tree tree1, Tree tree2) {
		return tree1.getX() - tree2.getX();
	}
}
