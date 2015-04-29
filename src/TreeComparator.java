/**
 * Copyright 2015 Peter "Felix" Nguyen & Emmanuel Medina Lopez
 * 
 * Forest Fire Simulation with 2D Graphics.
 *
 * TreeComparator is used to compare the position of two trees by comparing x and y coordinates. 
 * Trees sorted in ascending order should have trees in top-left corner indexed first in a list.
 * Trees sorted in ascending order should have trees in bottom-right corner indexed last in a list.
 */
import java.util.Comparator;

public class TreeComparator implements Comparator<Tree> {

	@Override
	public int compare(Tree tree1, Tree tree2) {
		int difference = tree1.getY() - tree2.getY();
		
		if (difference == 0) {
			difference = tree1.getX() - tree2.getX();
		}
		
		return difference;
	}
}
