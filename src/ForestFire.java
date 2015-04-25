/**
 * Copyright 2015 Peter "Felix" Nguyen & Emmanuel Medina Lopez
 * 
 * Forest Fire Simulation with 2D Graphics
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
	// GUI Frame (fast: 800x480) (slow:1200x600)
	private static JFrame frame = new JFrame();
	public final static int WIDTH = 1200;
	public final static int HEIGHT = 600;
	// Tree Stuff
	public final static int TREE_DIAMETER = 20;
	public final static int BURN_RADIUS = 40;
	public final static int BURN_RADIUS_SQR = BURN_RADIUS * BURN_RADIUS;
	public final static int CLICK_RADIUS = 30;
	public final static int CLICK_RADIUS_SQR = CLICK_RADIUS * CLICK_RADIUS;
	
	// Tree objects (fast: 600) (slow:1300)
	private static int numTrees = 1300;
	private static Tree[] sortedTrees = new Tree[numTrees];
	private static BufferedImage[] fireAnimation = new BufferedImage[75];
	private static BufferedImage stage1fire1;
	private static BufferedImage stage1fire2;
	private static BufferedImage stage1fire3;
	private static BufferedImage stage2fire1;
	private static BufferedImage stage2fire2;
	private static BufferedImage stage2fire3;
	private static BufferedImage stage3fire1;
	private static BufferedImage stage3fire2;
	private static BufferedImage stage3fire3;
	private static BufferedImage stage4fire1;
	private static BufferedImage stage4fire2;
	private static BufferedImage stage4fire3;
	private static BufferedImage stage5fire1;
	private static BufferedImage stage5fire2;
	private static BufferedImage stage5fire3;
	
	// Set of burning trees
	static Set<Tree> ignitedTrees = new HashSet<Tree>();
	
	// For manual invocation of fire
	private static ArrayList<Tree> tempTreesA = new ArrayList<Tree>();
	private static ArrayList<Tree> tempTreesB = new ArrayList<Tree>();
	
	// Load files
	private ClassLoader loader = getClass().getClassLoader();
	
	// Threads
	private static Timer timer;

	// Testing
	int tick = 0;

	public ForestFire() {
		try {
			stage1fire1 = ImageIO.read(loader.getResource("img/stage1fire1big.png"));
			stage1fire2 = ImageIO.read(loader.getResource("img/stage1fire2big.png"));
			stage1fire3 = ImageIO.read(loader.getResource("img/stage1fire3big.png"));
			stage2fire1 = ImageIO.read(loader.getResource("img/stage2fire1big.png"));
			stage2fire2 = ImageIO.read(loader.getResource("img/stage2fire2big.png"));
			stage2fire3 = ImageIO.read(loader.getResource("img/stage2fire3big.png"));
			stage3fire1 = ImageIO.read(loader.getResource("img/stage3fire1big.png"));
			stage3fire2 = ImageIO.read(loader.getResource("img/stage3fire2big.png"));
			stage3fire3 = ImageIO.read(loader.getResource("img/stage3fire3big.png"));
			stage4fire1 = ImageIO.read(loader.getResource("img/stage4fire1big.png"));
			stage4fire2 = ImageIO.read(loader.getResource("img/stage4fire2big.png"));
			stage4fire3 = ImageIO.read(loader.getResource("img/stage4fire3big.png"));
			stage5fire1 = ImageIO.read(loader.getResource("img/stage5fire1big.png"));
			stage5fire2 = ImageIO.read(loader.getResource("img/stage5fire2big.png"));
			stage5fire3 = ImageIO.read(loader.getResource("img/stage5fire3big.png"));
			
			for (int i = 0; i < 75; i++) {
				if (i < 15) {
					if (i % 3 == 0) {
						fireAnimation[i] = stage1fire1;
					} else if (i % 3 == 1) {
						fireAnimation[i] = stage1fire2;						
					} else if (i % 3 == 2) {
						fireAnimation[i] = stage1fire3;
					}
				} else if (i < 30) {
					if (i % 3 == 0) {
						fireAnimation[i] = stage2fire1;
					} else if (i % 3 == 1) {
						fireAnimation[i] = stage2fire2;						
					} else if (i % 3 == 2) {
						fireAnimation[i] = stage2fire3;
					}
				} else if (i < 45) {
					if (i % 3 == 0) {
						fireAnimation[i] = stage3fire1;
					} else if (i % 3 == 1) {
						fireAnimation[i] = stage3fire2;						
					} else if (i % 3 == 2) {
						fireAnimation[i] = stage3fire3;
					}
				} else if (i < 60) {
					if (i % 3 == 0) {
						fireAnimation[i] = stage4fire1;
					} else if (i % 3 == 1) {
						fireAnimation[i] = stage4fire2;						
					} else if (i % 3 == 2) {
						fireAnimation[i] = stage4fire3;
					}
				} else if (i < 75) {
					if (i % 3 == 0) {
						fireAnimation[i] = stage5fire1;
					} else if (i % 3 == 1) {
						fireAnimation[i] = stage5fire2;						
					} else if (i % 3 == 2) {
						fireAnimation[i] = stage5fire3;
					}
				} 
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Set the size of container
		Dimension fixedSize = new Dimension(WIDTH, HEIGHT);
		setPreferredSize(fixedSize);
		setSize(fixedSize);
		setMinimumSize(fixedSize);
		setMaximumSize(fixedSize);
		setBackground(Color.RED);
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g.setColor(new Color(90, 170, 90));

		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		for (int i = 0; i < numTrees; i++) {
			int x = sortedTrees[i].getX();
			int y = sortedTrees[i].getY();

			// Draw trunks
			g2.setColor(sortedTrees[i].getTrunkColor());
			g2.setStroke(new BasicStroke(3));
			g2.draw(new Line2D.Float(x + TREE_DIAMETER / 2, y + 12, x
					+ TREE_DIAMETER / 2, y + 28));

			// Anti aliasing and stroke to render smooth line
			g2.setRenderingHint(
				    RenderingHints.KEY_ANTIALIASING,
				    RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setStroke(new BasicStroke(0.40f));
			
			// Draw circular leaves
			if (sortedTrees[i].type == Tree.CIRCLE) {
				g.setColor(sortedTrees[i].getLeafColor());
				g.fillOval(x, y, TREE_DIAMETER, TREE_DIAMETER);
				if (sortedTrees[i].getState() != Tree.BLACK) {
					g.setColor(Color.BLACK);
				}
				g.drawOval(x, y, TREE_DIAMETER, TREE_DIAMETER);
			} else {
				// Draw triangular leaves
				int[] triX = new int[3];
				int[] triY = new int[3];
				
				triX[0]=x; triX[1]=x+TREE_DIAMETER/2; triX[2]=x+TREE_DIAMETER;
				triY[0]=y+20; triY[1]=y+12-17; triY[2]=y+20;
				int n = 3;
				
				g.setColor(sortedTrees[i].getLeafColor());
				Polygon p = new Polygon(triX, triY, n); 
				g.fillPolygon(p);
				if (sortedTrees[i].getState() != Tree.BLACK) {
					g.setColor(Color.BLACK);
				}
				g.drawLine(triX[0], triY[0], triX[2], triY[2]);
				g.drawLine(triX[2], triY[2], triX[1], triY[1]);
				g.drawLine(triX[1], triY[1], triX[0], triY[0]);
			}
			
			// Fire animation
			if (sortedTrees[i].getState() == Tree.RED) {
				if (sortedTrees[i].fireIndex < 74) {
					sortedTrees[i].tick();					
				}
				g2.drawImage(fireAnimation[sortedTrees[i].fireIndex], x, y - 12, this);
			}
		}
	}

	private static void makeTrees() {
		// Generate burning tree
		int burnStartX = (int) (Math.random() * WIDTH);
		int burnStartY = (int) (Math.random() * HEIGHT);
		sortedTrees[0] = new Tree(burnStartX, burnStartY);
		sortedTrees[0].setState(Tree.RED);
		ignitedTrees.add(sortedTrees[0]);

		// Generate trees
		for (int i = 1; i < numTrees; i++) {
			int x = (int) (10 + Math.random() * (WIDTH - TREE_DIAMETER - 15));
			int y = (int) (10 + Math.random() * (HEIGHT - 40));

			sortedTrees[i] = new Tree(x, y);
		}

		// Sort y, then x (method 1)
		// Arrays.sort(treesSorted, new TreeXComparator());
		// Arrays.sort(treesSorted, new TreeYComparator());
		
		// Sort y, then x (method 2)
		Arrays.sort(sortedTrees, new TreeComparator());
	}

	public static void main(String[] args) {
	    System.setProperty("sun.java2d.opengl", "True");
		System.out.println("PETER! " + System.getProperty("sun.java2d.opengl"));
		
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
										
					int value = (nearbyX - originX) * (nearbyX - originX) 
							+ (nearbyY - originY) * (nearbyY - originY);
					
					if (value <= ForestFire.BURN_RADIUS_SQR) {
						tempTreesB.get(j).setState(Tree.RED);
						
						ignitedTrees.add(tempTreesB.get(j));
					}
				}
			}
		});
		
		frame.setTitle("Forest Fire");
		frame.setSize(WIDTH + 30, HEIGHT + 50);
		frame.setLocationRelativeTo(null);
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(
				new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));

		frame.add(Box.createHorizontalGlue());
		frame.add(forestFire, BorderLayout.CENTER);
		frame.add(Box.createHorizontalGlue());
		
		timer = new Timer(150, forestFire);
		timer.start();

		frame.setVisible(true);
	}

	private int altSequenceCounter = 0;
	
	@Override
	public void actionPerformed(ActionEvent e) {
			if (tick < numTrees * 2) { // numTrees * 2

			repaint();

			altSequenceCounter++;
			
			Set<Tree> newlyIgnitedTrees = new HashSet<Tree>();
			
			if (altSequenceCounter % 5 == 0) {
				altSequenceCounter = 0;
				int sizeBurning = ignitedTrees.size();
				Tree[] ignitedTreesArray = (Tree[]) ignitedTrees.toArray(new Tree[sizeBurning]);
				for (int i = 0; i < sizeBurning;  i++) {
					int nearbySize = ignitedTreesArray[i].getNearbyTrees().size();
					Tree[] nearbyTrees = (Tree[]) ignitedTreesArray[i].getNearbyTrees().toArray(new Tree[nearbySize]);
					
					for (int j = 0; j < nearbyTrees.length; j++) {
						if (nearbyTrees[j].getState() == Tree.GREEN) {
							newlyIgnitedTrees.add(nearbyTrees[j]);
						}
						nearbyTrees[j].setState(Tree.RED);
					}
				}
				System.out.println("newlyIgnitedTrees Size: " + newlyIgnitedTrees.size());
				System.out.println("Ingited Trees Size: " + ignitedTrees.size());

				ignitedTrees.clear();
				System.out.println("ignitedTrees cleared size: " + ignitedTrees.size());
				
				for (Tree tree : newlyIgnitedTrees) {
					ignitedTrees.add(tree);
				}
				
			}
			newlyIgnitedTrees.clear();
			System.out.println("newlyIgnitedTrees cleared size: " + newlyIgnitedTrees.size());
		} 
	}
}

// Major bugs
// 1. [FIXED] ignited and newlyIgnitedTrees are added even though they are not on fire...
//    happens when user clicks on an area that has trees that have already been burnt
// 2. [POSSIBLY FIXED] Sometimes trees are not cleared (remains a constant number)
//    also triggered by a click (exact cause is unknown)
// 3. [FIXED] Sometimes, a tree positioned beside the northern edge of the map can not be ignited when clicked.

// Major design issues
// 1. Clicking needs to be more accurate (should target nearest tree instead of trees within click radius)

// Ideas
// 1. [DONE] Click on area to start fire at given location
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
// 12. [DONE] If burning and nearbyTrees added, remove from list of burning trees (or have two sets of burning trees: new burning trees and old burning trees)