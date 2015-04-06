/**
 * Forest Fire Simulation with 2D Graphics
 * 
 * @author Peter Nguyen, Emmanuel Medina Lopez
 */
import java.util.Comparator;

public class TreeXComparator implements Comparator<Tree> {

	@Override
	public int compare(Tree tree1, Tree tree2) {
		return tree1.getX() - tree2.getX();
	}
}
