/**
 * Forest Fire Simulation with 2D Graphics
 * 
 * @author Peter Nguyen, Emmanuel Medina Lopez
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
