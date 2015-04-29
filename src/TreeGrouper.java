/**
 * Copyright 2015 Peter "Felix" Nguyen & Emmanuel Medina Lopez
 * 
 * Forest Fire Simulation with 2D Graphics.
 * 
 * TreeGrouper is used to build a list of neihgboring trees for each tree.
 * It also performs a custom binary search that speeds up the 
 * process of finding nearby trees to burn upon triggering a fire.
 */
import java.util.ArrayList;
import java.util.Collections;


public class TreeGrouper {
	private static ArrayList<Tree> tempTreesA = new ArrayList<Tree>();
	private static ArrayList<Tree> tempTreesB = new ArrayList<Tree>();
	
	public static void buildTreeSets(Tree[] trees) {
		for (int i = 0; i < trees.length; i++) {
			// Establish nearby square area
			int x1 = trees[i].getX() - ForestFire.BURN_RADIUS / 2;
			int x2 = trees[i].getX() + ForestFire.BURN_RADIUS / 2;
			int y1 = trees[i].getY() - ForestFire.BURN_RADIUS / 2;
			int y2 = trees[i].getY() + ForestFire.BURN_RADIUS / 2;

			if (x1 < 0)      { x1 = 0; } 
			if (x2 > ForestFire.width)  { x2 = ForestFire.width;  }
			if (y1 < 0)      { y1 = 0; }
			if (y2 > ForestFire.height) { y2 = ForestFire.height; }

			tempTreesA.clear();
			int y = yBinarySearch(trees, y1);
			int yEnd = yBinarySearch(trees, y2);
			
			for (; y <= yEnd; y++) {
				tempTreesA.add(trees[y]);
			}			
			Collections.sort(tempTreesA, new TreeXComparator());
			
			tempTreesB.clear();
			int x = xBinarySearch(tempTreesA, x1);
			int xEnd = xBinarySearch(tempTreesA, x2);
			
			for (; x <= xEnd; x++) {
				tempTreesB.add(tempTreesA.get(x));
			}
			
			//System.out.print("[" + trees[i].getX() + "," + trees[i].getY() + "] " + "size: " + tempTreesB.size());
			
			for (int j = 0; j < tempTreesB.size(); j++) {
				int originX = trees[i].getX();
				int originY = trees[i].getY();
				int nearbyX = tempTreesB.get(j).getX();
				int nearbyY = tempTreesB.get(j).getY();
				
				//System.out.print(" (" + tempTreesB.get(j).getX() + "," + tempTreesB.get(j).getY() + ")");
				
				int value = (nearbyX - originX) * (nearbyX - originX) 
						+ (nearbyY - originY) * (nearbyY - originY);
				
				if (value <= ForestFire.BURN_RADIUS_SQR) {
					if (!trees[i].equals(tempTreesB.get(j))) {
						trees[i].addNearbyTree(tempTreesB.get(j));
					}
				}
			}
			//System.out.println();
		}
	}
	
	public static int xBinarySearch(ArrayList<Tree> trees, int key) {
		int low = 0;
		int high = trees.size() - 1;
		
		int mid = (low + high) / 2;
		
		while (high >= low) {
			mid = (low + high) / 2;
			if (key < trees.get(mid).getX()) {
				high = mid - 1;
			} else if (key == trees.get(mid).getX()) {
				return mid;
			} else {
				low = mid + 1;
			}
		}
		
		return mid; // not found
	}
	
	public int xBinarySearch(Tree[] trees, int key) {
		int low = 0;
		int high = trees.length - 1;
		
		int mid = (low + high) / 2;
		
		while (high >= low) {
			mid = (low + high) / 2;
			if (key < trees[mid].getX()) {
				high = mid - 1;
			} else if (key == trees[mid].getX()) {
				return mid;
			} else {
				low = mid + 1;
			}
		}
		
		return mid; // not found
	}
	
	public static int yBinarySearch(Tree[] trees, int key) {
		int low = 0;
		int high = trees.length - 1;
		
		int mid = (low + high) / 2;

		while (high >= low) {
			mid = (low + high) / 2;
			if (key < trees[mid].getY()) {
				high = mid - 1;
			} else if (key == trees[mid].getY()) {
				return mid;
			} else {
				low = mid + 1;
			}
		}
		
		return mid; // not found
	}
}
