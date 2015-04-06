/**
 * Forest Fire Simulation with 2D Graphics
 * 
 * @author Peter Nguyen, Emmanuel Medina Lopez
 */
import java.util.Comparator;

public class TreeYComparator implements Comparator<Tree> {

	@Override
	public int compare(Tree tree1, Tree tree2) {
		return tree1.getY() - tree2.getY();
	}
}
