/**
 * Forest Fire Simulation with 2D Graphics
 * 
 * @author Peter Nguyen, Emmanuel Medina Lopez
 */
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class ForestFire extends JPanel implements ActionListener {
	// GUI Frame (preferred: 700x700)
	private static JFrame frame = new JFrame();
	public final static int WIDTH = 400;
	public final static int HEIGHT = 400;
	// Tree Stuff
	public final static int TREE_DIAMETER = 12;
	public final static int BURN_RADIUS = 30;
	public final static int BURN_RADIUS_SQR = BURN_RADIUS * BURN_RADIUS;
	public final static int CLICK_RADIUS = 25;
	public final static int CLICK_RADIUS_SQR = CLICK_RADIUS * CLICK_RADIUS;
	
	// Tree objects (preferred: 1800)
	private static int numTrees = 800;
	private static Tree[] sortedTrees = new Tree[numTrees];
	private static BufferedImage fireImage1;
	private static BufferedImage fireImage2;
	private static BufferedImage fireImage3;
	private static BufferedImage fireImage4;
	// Set of burning trees
	static Set<Tree> burningTrees = new HashSet();
	// For manual invocation of fire
	private static ArrayList<Tree> tempTreesA = new ArrayList<Tree>();
	private static ArrayList<Tree> tempTreesB = new ArrayList<Tree>();
	
	// Threads
	private static Timer timer;

	// Testing
	int tick = 0;

	public ForestFire() {
		// Set the size of container
		Dimension fixedSize = new Dimension(700, 700);
		setPreferredSize(fixedSize);
		setSize(fixedSize);
		setMinimumSize(fixedSize);
		setMaximumSize(fixedSize);
		setBackground(Color.RED);
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		//g.setColor(new Color(240, 220, 200));
		g.setColor(new Color(90, 170, 90));

		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		for (int i = 0; i < numTrees; i++) {
			int x = sortedTrees[i].getX();
			int y = sortedTrees[i].getY();

			// Draw trunks
			g2.setColor(sortedTrees[i].getTrunkColor());
			g2.setStroke(new BasicStroke(3));
			g2.draw(new Line2D.Float(x + TREE_DIAMETER / 2, y + 12, x
					+ TREE_DIAMETER / 2, y + 18));

			// Anti aliasing and stroke to render smooth line
			g2.setRenderingHint(
				    RenderingHints.KEY_ANTIALIASING,
				    RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setStroke(new BasicStroke(0.40f));
			
			// Draw circular leaves
//			System.out.println(sortedTrees[i].type);
			if (sortedTrees[i].type == Tree.CIRCLE) {
				g.setColor(sortedTrees[i].getLeafColor());
				g.fillOval(x, y, TREE_DIAMETER, TREE_DIAMETER);
				g.setColor(Color.BLACK);
				g.drawOval(x, y, TREE_DIAMETER, TREE_DIAMETER);
			} else {
				// Draw triangular leaves
				int[] triX = new int[3];
				int[] triY = new int[3];
				
				triX[0]=x; triX[1]=x+TREE_DIAMETER/2; triX[2]=x+TREE_DIAMETER;
				triY[0]=y+12; triY[1]=y+12-17; triY[2]=y+12;
				int n = 3;
				
				g.setColor(sortedTrees[i].getLeafColor());
				Polygon p = new Polygon(triX, triY, n); 
				g.fillPolygon(p);
				g.setColor(Color.BLACK);
				g.drawLine(triX[0], triY[0], triX[2], triY[2]);
				g.drawLine(triX[2], triY[2], triX[1], triY[1]);
				g.drawLine(triX[1], triY[1], triX[0], triY[0]);
			}
			
			// Prep fire state
			if (sortedTrees[i].fireIndex < 3) {
				sortedTrees[i].fireIndex++;
			} else {
				sortedTrees[i].fireIndex = 1;
			}
			
			// Draw fire
			if (sortedTrees[i].getState() == Tree.RED) {
				switch (sortedTrees[i].fireIndex) {
				case 1:
					g.drawImage(fireImage2, x, y - 7, this);
					break;
				case 2:
					g.drawImage(fireImage3, x, y - 7, this);
					break;
				case 3:
					g.drawImage(fireImage4, x, y - 7, this);
					break;
				}
			}
		}
	}

	private static void makeTrees() {
		// Generate burning tree
		int burnStartX = (int) (Math.random() * WIDTH);
		int burnStartY = (int) (Math.random() * HEIGHT);
		sortedTrees[0] = new Tree(burnStartX, burnStartY);
		sortedTrees[0].setState(Tree.RED);
		burningTrees.add(sortedTrees[0]);

		// Generate trees
		for (int i = 1; i < numTrees; i++) {
			int x = (int) (Math.random() * (WIDTH - TREE_DIAMETER));
			int y = (int) (Math.random() * (HEIGHT - 20));

			sortedTrees[i] = new Tree(x, y);
		}

		// Sort y, then x (method 1)
		// Arrays.sort(treesSorted, new TreeXComparator());
		// Arrays.sort(treesSorted, new TreeYComparator());
		
		// Sort y, then x (method 2)
		Arrays.sort(sortedTrees, new TreeComparator());
	}

	public static void main(String[] args) {
		makeTrees();
		TreeGrouper.buildTreeSets(sortedTrees);
		ForestFire forestFire = new ForestFire();
		forestFire.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {

			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("Clicked: (" + e.getX() +"," + e.getY() + ")");
				int x1 = e.getX() - ForestFire.CLICK_RADIUS / 2;
				int x2 = e.getX() + ForestFire.CLICK_RADIUS / 2;
				int y1 = e.getY() - ForestFire.CLICK_RADIUS / 2;
				int y2 = e.getY() + ForestFire.CLICK_RADIUS / 2;
				
				if (x1 < 0)      { x1 = 0; } 
				if (x2 > ForestFire.WIDTH)  { x2 = ForestFire.WIDTH;  }
				if (y1 < 0)      { y1 = 0; }
				if (y2 > ForestFire.HEIGHT) { y2 = ForestFire.HEIGHT; }

				tempTreesA.clear();
				int y = TreeGrouper.yBinarySearch(sortedTrees, y1);
				int yEnd = TreeGrouper.yBinarySearch(sortedTrees, y2);
				
				for (; y <= yEnd; y++) {
					tempTreesA.add(sortedTrees[y]);
				}			
				Collections.sort(tempTreesA, new TreeXComparator());
				
				tempTreesB.clear();
				int x = TreeGrouper.xBinarySearch(tempTreesA, x1);
				int xEnd = TreeGrouper.xBinarySearch(tempTreesA, x2);
				
				for (; x <= xEnd; x++) {
					tempTreesB.add(tempTreesA.get(x));
				}
				
				for (int j = 0; j < tempTreesB.size(); j++) {
					int originX = e.getX();
					int originY = e.getY();
					int nearbyX = tempTreesB.get(j).getX();
					int nearbyY = tempTreesB.get(j).getY();
					
					//System.out.print(" (" + tempTreesB.get(j).getX() + "," + tempTreesB.get(j).getY() + ")");
					
					int value = (nearbyX - originX) * (nearbyX - originX) 
							+ (nearbyY - originY) * (nearbyY - originY);
					
					if (value <= ForestFire.BURN_RADIUS_SQR) {
						tempTreesB.get(j).setState(Tree.RED);
						
						burningTrees.add(tempTreesB.get(j));

						//tempTreesB.get(j).getNearbyTrees();
//						nearbyTrees[j].setState(Tree.RED);
//						burningTrees.add(nearbyTrees[j]);
					}
				}
			}
		});
		
		frame.setTitle("Forest Fire");
		//frame.setSize(WIDTH + 100, HEIGHT + 100);
		frame.setSize(WIDTH + 15, HEIGHT + 38);
		//frame.setMinimumSize(new Dimension(WIDTH + 100, HEIGHT + 100));
		frame.setLocationRelativeTo(null);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(
				new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));

		frame.add(Box.createHorizontalGlue());
		frame.add(forestFire, BorderLayout.CENTER);
		frame.add(Box.createHorizontalGlue());

		try {
			fireImage1 = ImageIO.read(new File("src/fire1.png"));
			fireImage2 = ImageIO.read(new File("src/fire2.png"));
			fireImage3 = ImageIO.read(new File("src/fire3.png"));
			fireImage4 = ImageIO.read(new File("src/fire4.png"));

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		timer = new Timer(150, forestFire);
		timer.start();

		frame.setVisible(true);
	}

	private int altSequenceCounter = 0;
	
	@Override
	public void actionPerformed(ActionEvent e) {
			if (tick < numTrees * 2) { // numTrees * 2

			// Omnidirectional fire spreading pattern
			altSequenceCounter++;
			if (altSequenceCounter % 2 == 0) {
				altSequenceCounter = 0;
				int sizeBurning = burningTrees.size();
				Tree[] burningTreesArray = (Tree[]) burningTrees.toArray(new Tree[sizeBurning]);
					for (int i = 0; i < sizeBurning;  i++) {
						
						int nearbySize = burningTreesArray[i].getNearbyTrees().size();
						Tree[] nearbyTrees = (Tree[]) burningTreesArray[i].getNearbyTrees().toArray(new Tree[nearbySize]);
						for (int j = 0; j < nearbyTrees.length; j++) {
							nearbyTrees[j].setState(Tree.RED);
							burningTrees.add(nearbyTrees[j]);
						}
					}
			}
			
			repaint();
		} 
	}
}

// Ideas
// 1. Click on area to start fire at given location
// 2. Prompt user for numTrees, burnRadius, burnSpeed
// 3. Notify the end of forest fire by checking for lack of fire
// 4. Draw polygon obstacles, terrain
// 5. Different trees have different spread rates
// 6. Rain, wind, lightning, weather
// 7. Critters, tree size, elevation
// 8. Square bushes and shrubs
// 9. Flammable  structures
// 10. Dynamic weather (rng or God-mode)
// 11. Map editor, map loader
// 12. If burning and nearbyTrees added, remove from list of burning trees (or have two sets of burning trees: new burning trees and old burning trees)