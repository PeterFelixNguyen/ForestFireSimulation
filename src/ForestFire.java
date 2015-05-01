/**
 * Copyright 2015 Peter "Felix" Nguyen & Emmanuel Medina Lopez
 * 
 * Forest Fire Simulation with 2D Graphics.
 * 
 * ForestFire is the map that contains the trees and simulates the forest fire.
 */
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class ForestFire extends JPanel implements ActionListener {
	// Constants to specify map resolution
	private final static String RESOLUTION_WVGA = "800x480";
	private final static String RESOLUTION_WSVGA = "1024x600";
	private final static String RESOLUTION_ASUS = "1800x900";
	private final static String RESOLUTION_ALIEN = "1200x600";
	private final static int WIDTH_DEFAULT = 1200;
	private final static int HEIGHT_DEFAULT = 600;
	public final static int WIDTH_TEST = 1200;
	public final static int HEIGHT_TEST = 600;

	// Constants to specify percentage of trees
	public static final double TREE_FACTOR_XXXXLARGE = 0.0300;
	public static final double TREE_FACTOR_XXXLARGE = 0.0150;
	public static final double TREE_FACTOR_XXLARGE = 0.0030;
	public static final double TREE_FACTOR_XLARGE = 0.0025;
	public static final double TREE_FACTOR_LARGE = 0.0020;
	public static final double TREE_FACTOR_MEDIUM = 0.0015;
	public static final double TREE_FACTOR_SMALL = 0.0010;

	// Resolution options
	private static String resolution = "";
	public static int width = WIDTH_DEFAULT;
	public static int height = HEIGHT_DEFAULT;

	// Tree objects
	private static int numTrees;
	private static Tree[] sortedTrees;
	
	// Tree and fire characteristics
	public final static int TREE_DIAMETER = 20;
	public final static int BURN_RADIUS = 40;
	public final static int BURN_RADIUS_SQR = BURN_RADIUS * BURN_RADIUS;
	public final static int CLICK_RADIUS = 30;
	public final static int CLICK_RADIUS_SQR = CLICK_RADIUS * CLICK_RADIUS;

	// Set of burning trees
	static Set<Tree> ignitedTrees = new HashSet<Tree>();

	// Fire animation
	private static final int ANIMATION_LENGTH = 75;
	private static VolatileImage[] vfireAnimation = new VolatileImage[ANIMATION_LENGTH];

	// Individual frames for fire animation (drawn over VolatileImage)
	private static BufferedImage biFire11, biFire12, biFire13;
	private static BufferedImage biFire21, biFire22, biFire23;
	private static BufferedImage biFire31, biFire32, biFire33;
	private static BufferedImage biFire41, biFire42, biFire43;
	private static BufferedImage biFire51, biFire52, biFire53;
	
	// VolatileImage used to increase performance when discrete graphics are available
	private VolatileImage viFire11 = createVolatileImage(20, 28, Transparency.TRANSLUCENT);
	private VolatileImage viFire12 = createVolatileImage(20, 28, Transparency.TRANSLUCENT);
	private VolatileImage viFire13 = createVolatileImage(20, 28, Transparency.TRANSLUCENT);
	private VolatileImage viFire21 = createVolatileImage(20, 28, Transparency.TRANSLUCENT);
	private VolatileImage viFire22 = createVolatileImage(20, 28, Transparency.TRANSLUCENT);
	private VolatileImage viFire23 = createVolatileImage(20, 28, Transparency.TRANSLUCENT);
	private VolatileImage viFire31 = createVolatileImage(20, 28, Transparency.TRANSLUCENT);
	private VolatileImage viFire32 = createVolatileImage(20, 28, Transparency.TRANSLUCENT);
	private VolatileImage viFire33 = createVolatileImage(20, 28, Transparency.TRANSLUCENT);
	private VolatileImage viFire41 = createVolatileImage(20, 28, Transparency.TRANSLUCENT);
	private VolatileImage viFire42 = createVolatileImage(20, 28, Transparency.TRANSLUCENT);
	private VolatileImage viFire43 = createVolatileImage(20, 28, Transparency.TRANSLUCENT);
	private VolatileImage viFire51 = createVolatileImage(20, 28, Transparency.TRANSLUCENT);
	private VolatileImage viFire52 = createVolatileImage(20, 28, Transparency.TRANSLUCENT);
	private VolatileImage viFire53 = createVolatileImage(20, 28, Transparency.TRANSLUCENT);

	// Tree graphics
	private VolatileImage viTreeA = createVolatileImage(21, 36, Transparency.TRANSLUCENT);
	private VolatileImage ViTreeO = createVolatileImage(21, 36, Transparency.TRANSLUCENT);
	private VolatileImage viTreeABurnt = createVolatileImage(21, 36, Transparency.TRANSLUCENT);
	private VolatileImage viTreeOBurnt = createVolatileImage(21, 36, Transparency.TRANSLUCENT);

	// Temporary lists used to manually start a fire
	private static ArrayList<Tree> tempTreesA = new ArrayList<Tree>();
	private static ArrayList<Tree> tempTreesB = new ArrayList<Tree>();

	// Resource loader
	private ClassLoader loader = getClass().getClassLoader();

	// Thread timers
	private static Timer timerSimulation;
	private static Timer timerReplay;
	private int altSequenceCounter = 0;

	// Position tracker
	int tick = 0;

	// Playback buttons
	static JButton jbPlay = new JButton("PLAY");
	static JButton jbPause = new JButton("PAUSE");
	static JButton jbStop = new JButton("STOP");
	static JButton jbReplay = new JButton("REPLAY");
	static JButton jbSlow = new JButton("0.5x");
	static JButton jbNormal = new JButton("1.0x");
	static JButton jbFast = new JButton("1.5x");
	static JButton jbFaster = new JButton("2.0x");
	static JButton jbMap = new JButton("MAP");
	
	// Playback states
	private static boolean paused = false;
	
	// Speed states
	private static final float speedSlow = 0.5f;
	private static final float speedNormal = 1.0f;
	private static final float speedFast = 1.5f;
	private static final float speedFaster = 2.0f;
	public static int simDelay = 150;
	
	// Replay track
	private static ArrayList<ClickAction> clickTrack = new ArrayList<ClickAction>(); // for now
	@SuppressWarnings("unused")
	private static Stack<ClickAction> clickStack = new Stack<ClickAction>(); // future use
	private static boolean replayMode = false;
	
	// Subcomponents
	private static MapButtons mapButtons;
	
	public ForestFire() {
		// Select resolution name
		resolution = RESOLUTION_ALIEN;

		// Configure map resolution
		if (resolution == RESOLUTION_WVGA) {
			width = 800;
			height = 480;
		} else if (resolution == RESOLUTION_WSVGA) {
			width = 1024;
			height = 600;
		} else if (resolution == RESOLUTION_ASUS) {
			width = 1800;
			height = 900;
		} else if (resolution == RESOLUTION_ALIEN) {
			width = 1200;
			height = 600;
		}

		// numTrees = x-percentage of total pixels
		numTrees = (int) (TREE_FACTOR_MEDIUM * (width * height));
		System.out.println("numTrees = " + numTrees);
		
		// Create array of trees and sort them
		sortedTrees = new Tree[numTrees];
		makeTrees(2);
		
		// Add neighboring trees to each tree
		TreeGrouper.buildTreeSets(sortedTrees);

		try {
			biFire11 = ImageIO.read(loader.getResource("img/fire11big.png"));
			biFire12 = ImageIO.read(loader.getResource("img/fire12big.png"));
			biFire13 = ImageIO.read(loader.getResource("img/fire13big.png"));
			biFire21 = ImageIO.read(loader.getResource("img/fire21big.png"));
			biFire22 = ImageIO.read(loader.getResource("img/fire22big.png"));
			biFire23 = ImageIO.read(loader.getResource("img/fire23big.png"));
			biFire31 = ImageIO.read(loader.getResource("img/fire31big.png"));
			biFire32 = ImageIO.read(loader.getResource("img/fire32big.png"));
			biFire33 = ImageIO.read(loader.getResource("img/fire33big.png"));
			biFire41 = ImageIO.read(loader.getResource("img/fire41big.png"));
			biFire42 = ImageIO.read(loader.getResource("img/fire42big.png"));
			biFire43 = ImageIO.read(loader.getResource("img/fire43big.png"));
			biFire51 = ImageIO.read(loader.getResource("img/fire51big.png"));
			biFire52 = ImageIO.read(loader.getResource("img/fire52big.png"));
			biFire53 = ImageIO.read(loader.getResource("img/fire53big.png"));

			renderOffscreenFire(viFire11, biFire11);
			renderOffscreenFire(viFire12, biFire12);
			renderOffscreenFire(viFire13, biFire13);
			renderOffscreenFire(viFire21, biFire21);
			renderOffscreenFire(viFire22, biFire22);
			renderOffscreenFire(viFire23, biFire23);
			renderOffscreenFire(viFire31, biFire31);
			renderOffscreenFire(viFire32, biFire32);
			renderOffscreenFire(viFire33, biFire33);
			renderOffscreenFire(viFire41, biFire41);
			renderOffscreenFire(viFire42, biFire42);
			renderOffscreenFire(viFire43, biFire43);
			renderOffscreenFire(viFire51, biFire51);
			renderOffscreenFire(viFire52, biFire52);
			renderOffscreenFire(viFire53, biFire53);
			renderOffscreenTreeO();
			renderOffscreenTreeOBurnt();
			renderOffscreenTreeA();
			renderOffscreenTreeABurnt();

			for (int i = 0; i < ANIMATION_LENGTH; i++) {
				if (i < 15) {
					if (i % 3 == 0) {
						vfireAnimation[i] = viFire11;
					} else if (i % 3 == 1) {
						vfireAnimation[i] = viFire12;
					} else if (i % 3 == 2) {
						vfireAnimation[i] = viFire13;
					}
				} else if (i < 30) {
					if (i % 3 == 0) {
						vfireAnimation[i] = viFire21;
					} else if (i % 3 == 1) {
						vfireAnimation[i] = viFire22;
					} else if (i % 3 == 2) {
						vfireAnimation[i] = viFire23;
					}
				} else if (i < 45) {
					if (i % 3 == 0) {
						vfireAnimation[i] = viFire31;
					} else if (i % 3 == 1) {
						vfireAnimation[i] = viFire32;
					} else if (i % 3 == 2) {
						vfireAnimation[i] = viFire33;
					}
				} else if (i < 60) {
					if (i % 3 == 0) {
						vfireAnimation[i] = viFire41;
					} else if (i % 3 == 1) {
						vfireAnimation[i] = viFire42;
					} else if (i % 3 == 2) {
						vfireAnimation[i] = viFire43;
					}
				} else if (i < ANIMATION_LENGTH) {
					if (i % 3 == 0) {
						vfireAnimation[i] = viFire51;
					} else if (i % 3 == 1) {
						vfireAnimation[i] = viFire52;
					} else if (i % 3 == 2) {
						vfireAnimation[i] = viFire53;
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (!replayMode) {
					clickTrack.add(new ClickAction(e.getX(), e.getY(), tick));
					
					clickFunction(e.getX(), e.getY());
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseClicked(MouseEvent e) {

			}
		});

		// Set the size of container
		Dimension fixedSize = new Dimension(width, height);
		setPreferredSize(fixedSize);
		setSize(fixedSize);
		setMinimumSize(fixedSize);
		setMaximumSize(fixedSize);
		
		// Timer for animation and state change
		timerSimulation = new Timer(simDelay, this);
		timerSimulation.start();
		
		timerReplay = new Timer(simDelay, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tick < numTrees * 2 && !paused) {
					repaint();
					//System.out.println("Replay Timer Running");
					
					if (clickTrack.size() > 0) {
						if (tick == clickTrack.get(0).getTick()) {
							int xClicked = clickTrack.get(0).getX();
							int yClicked = clickTrack.get(0).getY();
							clickFunction(xClicked, yClicked);
							clickTrack.remove(0);
						}				
					}
					Set<Tree> newlyIgnitedTrees = new HashSet<Tree>();
					altSequenceCounter++;

					if (altSequenceCounter % 5 == 0) {
						altSequenceCounter = 0;
						int sizeBurning = ignitedTrees.size();
						Tree[] ignitedTreesArray = (Tree[]) ignitedTrees.toArray(new Tree[sizeBurning]);
						for (int i = 0; i < sizeBurning; i++) {
							int nearbySize = ignitedTreesArray[i].getNearbyTrees().size();
							Tree[] nearbyTrees = (Tree[]) ignitedTreesArray[i].getNearbyTrees().toArray(new Tree[nearbySize]);

							for (int j = 0; j < nearbyTrees.length; j++) {
								if (nearbyTrees[j].getState() == Tree.GREEN) {
									newlyIgnitedTrees.add(nearbyTrees[j]);
								}
								nearbyTrees[j].setState(Tree.RED);
							}
						}
						// System.out.println("newlyIgnitedTrees Size: " + newlyIgnitedTrees.size());
						// System.out.println("ingitedTrees size: " + ignitedTrees.size());

						ignitedTrees.clear();
						// System.out.println("ignitedTrees size after clear: " + ignitedTrees.size());

						for (Tree tree : newlyIgnitedTrees) {
							ignitedTrees.add(tree);
						}

					}
					newlyIgnitedTrees.clear();
					// System.out.println("newlyIgnitedTrees size after clear: " + newlyIgnitedTrees.size());
					tick++;
				}
			}
		});
		
		// Subcomponents
		mapButtons = new MapButtons();
	}

	public void clickFunction(int xClick, int yClick) {
		System.out.println("Clicked: (" + xClick + "," + yClick + ")");
		int x1 = xClick - ForestFire.CLICK_RADIUS / 2;
		int x2 = xClick + ForestFire.CLICK_RADIUS / 2;
		int y1 = yClick - ForestFire.CLICK_RADIUS / 2;
		int y2 = yClick + ForestFire.CLICK_RADIUS / 2;

		if (x1 < 0) {
			x1 = 0;
		}
		if (x2 > ForestFire.width) {
			x2 = ForestFire.width;
		}
		if (y1 < 0) {
			y1 = 0;
		}
		if (y2 > ForestFire.height) {
			y2 = ForestFire.height;
		}

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
			int originX = xClick;
			int originY = yClick;
			int nearbyX = tempTreesB.get(j).getX();
			int nearbyY = tempTreesB.get(j).getY();

			int value = (nearbyX - originX) * (nearbyX - originX) + (nearbyY - originY) * (nearbyY - originY);

			if (value <= ForestFire.BURN_RADIUS_SQR) {
				tempTreesB.get(j).setState(Tree.RED);

				ignitedTrees.add(tempTreesB.get(j));
			}
		}
	}

	private VolatileImage createVolatileImage(int width, int height, int transparency) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
		VolatileImage image = null;

		image = gc.createCompatibleVolatileImage(width, height, transparency);

		int valid = image.validate(gc);

		if (valid == VolatileImage.IMAGE_INCOMPATIBLE) {
			image = this.createVolatileImage(width, height, transparency);
			return image;
		}

		return image;
	}

	void renderOffscreenTreeO() {
		do {
			if (ViTreeO.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
				// old vImg doesn't work with new GraphicsConfig; re-create it
				ViTreeO = createVolatileImage(ViTreeO.getWidth(), ViTreeO.getHeight());
			}
			Graphics2D g2d = ViTreeO.createGraphics();

			// Fill a transparent background
			g2d.setComposite(AlphaComposite.DstOut); // TRANSPARENCY STEP 1
			g2d.fillRect(0, 0, ViTreeO.getWidth(), ViTreeO.getHeight()); // TRANSPARENCY STEP 2

			// Draw over this background
			g2d.setComposite(AlphaComposite.SrcOver); // Draw over destination

			// Draw trunks
			g2d.setColor(new Color(160, 110, 60));
			g2d.setStroke(new BasicStroke(3));
			g2d.draw(new Line2D.Float(0 + TREE_DIAMETER / 2, 0 + 12 + 6, 0 + TREE_DIAMETER / 2, 0 + 28 + 6));

			// Anti aliasing and stroke to render smooth line
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setStroke(new BasicStroke(0.40f));

			// Draw circular leaves
			g2d.setColor(new Color(30, 150, 70));
			g2d.fillOval(0, 6, TREE_DIAMETER, TREE_DIAMETER);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(0, 6, TREE_DIAMETER, TREE_DIAMETER);

			g2d.dispose();
		} while (viTreeA.contentsLost());
	}

	void renderOffscreenTreeOBurnt() {
		do {
			if (viTreeOBurnt.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
				viTreeOBurnt = createVolatileImage(viTreeOBurnt.getWidth(), viTreeOBurnt.getHeight());
			}
			Graphics2D g2d = viTreeOBurnt.createGraphics();

			// Fill a transparent background
			g2d.setComposite(AlphaComposite.DstOut); // TRANSPARENCY STEP 1
			g2d.fillRect(0, 0, viTreeOBurnt.getWidth(), viTreeOBurnt.getHeight()); // TRANSPARENCY STEP 2

			// Draw over this background
			g2d.setComposite(AlphaComposite.SrcOver); // Draw over destination

			// Draw trunks
			g2d.setColor(new Color(70, 70, 70));
			g2d.setStroke(new BasicStroke(3));
			g2d.draw(new Line2D.Float(0 + TREE_DIAMETER / 2, 0 + 12, 0 + TREE_DIAMETER / 2, 0 + 28));

			g2d.dispose();
		} while (viTreeA.contentsLost());
	}

	void renderOffscreenTreeA() {
		do {
			if (viTreeA.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
				viTreeA = createVolatileImage(viTreeA.getWidth(), viTreeA.getHeight());
			}
			Graphics2D g2d = viTreeA.createGraphics();
			
			// Fill a transparent background
			g2d.setComposite(AlphaComposite.DstOut);
			g2d.fillRect(0, 0, viTreeA.getWidth(), viTreeA.getHeight());

			// Draw over this background
			g2d.setComposite(AlphaComposite.SrcOver);

			// Draw trunks
			g2d.setColor(new Color(160, 110, 60));
			g2d.setStroke(new BasicStroke(3));
			g2d.draw(new Line2D.Float(0 + TREE_DIAMETER / 2, 0 + 12 + 5, 0 + TREE_DIAMETER / 2, 0 + 28 + 5));

			// Anti aliasing and stroke to render smooth line
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setStroke(new BasicStroke(0.40f));
			
			// Array of polygon points
			int[] triX = new int[3];
			int[] triY = new int[3];
			
			// Assign values to polygon points
			triX[0]=0; triX[1]=0+TREE_DIAMETER/2; triX[2]=0+TREE_DIAMETER;
			triY[0]=0+25; triY[1]=0; triY[2]=0+25;
			int n = 3;
			
			// Render polygon
			g2d.setColor(new Color(30, 150, 70));
			Polygon p = new Polygon(triX, triY, n);
			g2d.fillPolygon(p);
			g2d.setColor(Color.BLACK);
			g2d.drawLine(triX[0], triY[0], triX[2], triY[2]);
			g2d.drawLine(triX[2], triY[2], triX[1], triY[1]);
			g2d.drawLine(triX[1], triY[1], triX[0], triY[0]);

			g2d.dispose();
		} while (viTreeA.contentsLost());
	}

	void renderOffscreenTreeABurnt() {
		do {
			if (viTreeABurnt.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
				viTreeABurnt = createVolatileImage(viTreeABurnt.getWidth(), viTreeABurnt.getHeight());
			}
			Graphics2D g2d = viTreeABurnt.createGraphics();

			// Fill a transparent background 
			g2d.setComposite(AlphaComposite.DstOut); // TRANSPARENCY STEP 1
			g2d.fillRect(0, 0, viTreeABurnt.getWidth(), viTreeABurnt.getHeight()); // TRANSPARENCY STEP 2

			// Draw over this background
			g2d.setComposite(AlphaComposite.SrcOver); // Draw over destination

			// Draw burnt trunks
			g2d.setColor(new Color(70, 70, 70));
			g2d.setStroke(new BasicStroke(3));
			g2d.draw(new Line2D.Float(0 + TREE_DIAMETER / 2, 0 + 12 + 5, 0 + TREE_DIAMETER / 2, 0 + 28 + 5));

			g2d.dispose();
		} while (viTreeA.contentsLost());
	}
	
	void renderOffscreenFire(VolatileImage vImg, BufferedImage bImg) {
		do {
			if (vImg.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
				vImg = createVolatileImage(bImg.getWidth(), bImg.getHeight());
			}
			Graphics2D g = vImg.createGraphics();

			// Fill a transparent background 
			g.setComposite(AlphaComposite.DstOut);
			g.fillRect(0, 0, bImg.getWidth(), bImg.getHeight());

			// Draw over this background
			g.setComposite(AlphaComposite.SrcOut); 
			
			// Draw specified frame of animated fire
			g.drawImage(bImg, 0, 0, null);

			g.dispose();
		} while (vImg.contentsLost());
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		// Draw land
		g.setColor(new Color(90, 170, 90));
		g.fillRect(0, 0, width, height);

		// For all trees
		for (int i = 0; i < numTrees; i++) {
			int x = sortedTrees[i].getX();
			int y = sortedTrees[i].getY();

			// Draw circular trees
			if (sortedTrees[i].type == Tree.CIRCLE) {
				if (sortedTrees[i].getState() != Tree.BLACK) {
					g.drawImage(ViTreeO, x, y, this);
				} else {
					g.drawImage(viTreeOBurnt, x, y, this);
				}
			} else if (sortedTrees[i].type == Tree.TRIANGLE) {
				// Draw triangular trees
				if (sortedTrees[i].getState() != Tree.BLACK) {
					g.drawImage(viTreeA, x, y, this);				
				} else {
					g.drawImage(viTreeABurnt, x, y, this);
				}
			}

			// Fire animation
			if (sortedTrees[i].getState() == Tree.RED) {
				if (sortedTrees[i].fireIndex < ANIMATION_LENGTH - 1) {
					sortedTrees[i].tick();
				}
				g2.drawImage(vfireAnimation[sortedTrees[i].fireIndex], x, y - 6, this);
			}
		}
	}

	private static void makeTrees(int sortMethod) {
		// Generate trees
		for (int i = 0; i < numTrees; i++) {
			int x = (int) (10 + Math.random() * (width - TREE_DIAMETER - 15));
			int y = (int) (10 + Math.random() * (height - 50));

			sortedTrees[i] = new Tree(x, y);
		}
		
		// Set initial burning tree
		boolean isAutoBurn = false;

		// If flag is true, set initial tree to burn
		if (isAutoBurn && sortedTrees.length > 0) {
			sortedTrees[0].setState(Tree.RED);
			ignitedTrees.add(sortedTrees[0]);
		}

		// Different sort methods are for my own learning
		if (sortMethod == 1) {
			Arrays.sort(sortedTrees, new TreeXComparator());
			Arrays.sort(sortedTrees, new TreeYComparator());
		} else if (sortMethod == 2) {
			Arrays.sort(sortedTrees, new TreeComparator());
		} else {
			System.out.println("Invalid sort method, trees are unsorted");
		}
	}


	@Override
	public void actionPerformed(ActionEvent e) {		
		if (tick < numTrees * 2 && !paused) {
			repaint();
			//System.out.println("Normal Timer Running");
			
			Set<Tree> newlyIgnitedTrees = new HashSet<Tree>();
			altSequenceCounter++;

			if (altSequenceCounter % 5 == 0) {
				altSequenceCounter = 0;
				int sizeBurning = ignitedTrees.size();
				Tree[] ignitedTreesArray = (Tree[]) ignitedTrees.toArray(new Tree[sizeBurning]);
				for (int i = 0; i < sizeBurning; i++) {
					int nearbySize = ignitedTreesArray[i].getNearbyTrees().size();
					Tree[] nearbyTrees = (Tree[]) ignitedTreesArray[i].getNearbyTrees().toArray(new Tree[nearbySize]);

					for (int j = 0; j < nearbyTrees.length; j++) {
						if (nearbyTrees[j].getState() == Tree.GREEN) {
							newlyIgnitedTrees.add(nearbyTrees[j]);
						}
						nearbyTrees[j].setState(Tree.RED);
					}
				}
				// System.out.println("newlyIgnitedTrees Size: " + newlyIgnitedTrees.size());
				// System.out.println("ingitedTrees size: " + ignitedTrees.size());

				ignitedTrees.clear();
				// System.out.println("ignitedTrees size after clear: " + ignitedTrees.size());

				for (Tree tree : newlyIgnitedTrees) {
					ignitedTrees.add(tree);
				}

			}
			newlyIgnitedTrees.clear();
			// System.out.println("newlyIgnitedTrees size after clear: " + newlyIgnitedTrees.size());
			tick++;
		}
	}
	
	class MapButtons extends JPanel {
		
		public MapButtons() {
			jbPlay.setEnabled(false);
			jbNormal.setEnabled(false);
			
			// Event handling START
			jbPlay.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
						paused = false;
						
						jbPlay.setEnabled(false);
						jbPause.setEnabled(true);
						jbStop.setEnabled(true);
				}
			});
			
			jbPause.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
						paused = true;
						
						jbPlay.setEnabled(true);
						jbPause.setEnabled(false);
						jbStop.setEnabled(true);
				}
			});
	
			jbStop.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					replayMode = false;

					tick = 0;
					timerReplay.stop();
					timerSimulation.start();

					ignitedTrees.clear();
				
					for (Tree tree : sortedTrees) {
						tree.resetState();
					}

					paused = false;
					clickTrack.clear();
					
					jbPlay.setEnabled(false);
					jbPause.setEnabled(true);
					//jbStop.setEnabled(false);
					jbReplay.setEnabled(true);
				}
			});
	
			jbReplay.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					replayMode = true;
					
					tick = 0;
					timerSimulation.stop();
					timerReplay.start();
					
					ignitedTrees.clear();
				
					for (Tree tree : sortedTrees) {
						tree.resetState();
					}

					paused = false;
					// add code to load replay script
					jbPlay.setEnabled(true);
					jbPause.setEnabled(true);
					jbStop.setEnabled(true);
					jbReplay.setEnabled(false);
					}
			});
			
			jbSlow.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					timerSimulation.setDelay((int)(simDelay / speedSlow));
					timerReplay.setDelay((int)(simDelay / speedSlow));
						jbSlow.setEnabled(false);
						jbNormal.setEnabled(true);
						jbFast.setEnabled(true);
						jbFaster.setEnabled(true);
				}
			});
			
			jbNormal.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					timerSimulation.setDelay((int)(simDelay / speedNormal));
					timerReplay.setDelay((int)(simDelay / speedNormal));
					jbSlow.setEnabled(true);
					jbNormal.setEnabled(false);
					jbFast.setEnabled(true);
					jbFaster.setEnabled(true);
				}
			});
			
			jbFast.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					timerSimulation.setDelay((int)(simDelay / speedFast));
					timerReplay.setDelay((int)(simDelay / speedFast));
					jbSlow.setEnabled(true);
					jbNormal.setEnabled(true);
					jbFast.setEnabled(false);
					jbFaster.setEnabled(true);
				}
			});
			
			jbFaster.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					timerSimulation.setDelay((int)(simDelay / speedFaster));
					timerReplay.setDelay((int)(simDelay / speedFaster));
					jbSlow.setEnabled(true);
					jbNormal.setEnabled(true);
					jbFast.setEnabled(true);
					jbFaster.setEnabled(false);
				}
			});
			// Event handling END
			
			// Playback standard button group
			JPanel groupPlayback = new JPanel(new FlowLayout(FlowLayout.LEFT));
			groupPlayback.setBackground(new Color(215,199,151));
			groupPlayback.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), "Playback Controls"));
			groupPlayback.add(jbPlay);
			groupPlayback.add(jbPause);
			groupPlayback.add(jbStop);
			groupPlayback.add(jbReplay);
			
			// Simulation speed button group
			JPanel groupSpeed = new JPanel(new FlowLayout(FlowLayout.LEFT));
			groupSpeed.setBackground(new Color(215,199,151));
			groupSpeed.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), "Simulation Speed"));
			groupSpeed.add(jbSlow);
			groupSpeed.add(jbNormal);
			groupSpeed.add(jbFast);
			groupSpeed.add(jbFaster);
			
			// Configure button group
			JPanel groupConfigure = new JPanel(new FlowLayout(FlowLayout.LEFT));
			groupConfigure.setBackground(new Color(215,199,151));
			groupConfigure.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), "Configure..."));
			groupConfigure.add(jbMap);
			
			// Button bar for user controls and settings
			setLayout(new FlowLayout(FlowLayout.LEFT));
			setBackground(new Color(215,199,151));
			setSize(new Dimension(width, 70));
			setMinimumSize(new Dimension(width, 70));
			setMaximumSize(new Dimension(width, 70));		
			add(groupPlayback);
			add(groupSpeed);
			add(groupConfigure);
		}
	}
	
	public MapButtons getMapButtons() {
		return mapButtons;
	}
}