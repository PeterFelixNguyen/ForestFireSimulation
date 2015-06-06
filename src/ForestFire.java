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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import com.jd.swing.custom.component.button.ButtonType;
import com.jd.swing.custom.component.button.StandardButton;
import com.jd.swing.util.Theme;

@SuppressWarnings("serial")
public class ForestFire extends JPanel implements ActionListener {
	// Constants to specify map resolution
	public final static String RESOLUTION_WVGA = "800x480";
	public final static String RESOLUTION_WSVGA = "1024x600";
	public final static String RESOLUTION_ASUS = "1800x900";
	public final static String RESOLUTION_ALIEN = "1200x600";
	public final static String RESOLUTION_DEVICE = "DEVICE";
	public final static int WIDTH_DEFAULT = 1200;
	public final static int HEIGHT_DEFAULT = 600;
	public final static int WIDTH_TEST = 1200;
	public final static int HEIGHT_TEST = 600;

	// Constants to specify percentage of trees
	public static final double POPULATION_XXXXLARGE = 0.00125;
	public static final double  POPULATION_XXXLARGE = 0.00115;
	public static final double   POPULATION_XXLARGE = 0.00095;
	public static final double    POPULATION_XLARGE = 0.00085;
	public static final double     POPULATION_LARGE = 0.00075;
	public static final double    POPULATION_MEDIUM = 0.00065;
	public static final double     POPULATION_SMALL = 0.00055;
	public static final double    POPULATION_XSMALL = 0.00045;
	public static final double   POPULATION_XXSMALL = 0.00035;
	public static final double  POPULATION_XXXSMALL = 0.00025;
	public static final double POPULATION_XXXXSMALL = 0.00015;
	
	// Map settings
	private static double selectedPopulation;
	private static String selectedMapSize;
	
	// Constants to specify percentage of trees for testing 
	public static final double POPULATION_ULTRALARGE = 0.01000;
	public static final double POPULATION_ULTRASMALL = 0.00001;

	// Resolution options
	private static String resolution = "";
	public static int width = WIDTH_DEFAULT;
	public static int height = HEIGHT_DEFAULT;
	
	// Adjust height for MenuBar, ButtonBar, and ReplaySlider, 
	public static int heightOfOtherComponents = 112;
	
	// Tree objects
	private static int numTrees;
	private static ArrayList<Tree> sortedTrees;
	
	// Tree and fire characteristics
	public final static int TREE_DIAMETER = 20;
	public static int burnRadius = 40;
	public static int burnRadiusSqr = burnRadius * burnRadius;
	public static int clickRadius = 30;
	public static int clickRadiusSqr = clickRadius * clickRadius;

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
	private VolatileImage ViTreeO = createVolatileImage(21, 30, Transparency.TRANSLUCENT);
	private VolatileImage viTreeABurnt = createVolatileImage(21, 36, Transparency.TRANSLUCENT);
	private VolatileImage viTreeOBurnt = createVolatileImage(21, 30, Transparency.TRANSLUCENT);
	// subtract 6 from y2
	
	// Temporary lists used to manually start a fire
	private static ArrayList<Tree> tempTreesA = new ArrayList<Tree>();
	private static ArrayList<Tree> tempTreesB = new ArrayList<Tree>();

	// Resource loader
	private ClassLoader loader = getClass().getClassLoader();

	// Thread timers
	private static Timer timerSimulation;
	private static Timer timerReplay;
	private int firstAltSequenceCounter = 0;
	private int secondAltSequenceCounter = 0;	

	// Position tracker
	private int tick = 0;
	private int replayDuration = 0;
	
	// Playback buttons
	private StandardButton sbPlay = new StandardButton("PLAY", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
	private StandardButton sbPause = new StandardButton("PAUSE", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
	private StandardButton sbStop = new StandardButton("STOP", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
	private StandardButton sbReplay = new StandardButton("REPLAY", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
	private StandardButton sbSlow = new StandardButton("0.5x", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
	private StandardButton sbNormal = new StandardButton("1.0x", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
	private StandardButton sbFast = new StandardButton("2.0x", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
	private StandardButton sbFaster = new StandardButton("4.0x", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
		
	// Playback states
	private static boolean pausePressed = false;
	private static boolean paused = false;
	private static boolean stopped = true;
	private static boolean dialogOpen = false;
	
	// Speed states
	private static final float speedSlow = 0.5f;
	private static final float speedNormal = 1.0f;
	private static final float speedFast = 2.0f;
	private static final float speedFaster = 4.0f;
	public int simDelay = 50;
	
	// Replay track (considering using a Stack instead of an ArrayList)
	private ArrayList<ClickAction> clickHistory = new ArrayList<ClickAction>();
	private ArrayList<ClickAction> clickHistoryStack = new ArrayList<ClickAction>();
	
	private boolean replayMode = false;
	
	// Subcomponents
	private JPanel upperPanel;
	private MapButtons mapButtons;
	private EditButtons editButtons;
	private ReplaySlider replaySlider;
	
	// Mouse Cursor
	private int mouseXPosition = -100;
	private int mouseYPosition = -100;
	private boolean fireCursorClicked = false;
	private VolatileImage viFireCursorBlack = createVolatileImage(clickRadius * 2 + 3, clickRadius * 2 + 3, Transparency.TRANSLUCENT);
	private VolatileImage viFireCursorRed = createVolatileImage(clickRadius * 2 + 3, clickRadius * 2 + 3, Transparency.TRANSLUCENT);

	// Boolean View Flags
	private boolean positionOverlayEnabled = false;
	private boolean healthOverlayEnabled = false;
	
	// Boolean Playback Flags
	private boolean clickUnpauses = true;
	
	// Boolean Mouse Flags
	private boolean mousePressedLeft = false;
	
	// Edit Mode
	private boolean editMode = false;
	private String paintBrush = "Tree";
	private final String BRUSH_TREE = "Tree";
	private final String BRUSH_LAND = "Land";
	private final String BRUSH_SEA = "Sea";
	
	public ForestFire() {
		// Get device resolution
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int screenWidth = gd.getDisplayMode().getWidth();
		int screenHeight = gd.getDisplayMode().getHeight();
		System.out.println("Device resolution = " + screenWidth + "x" + screenHeight);
		
		// Select resolution name
		resolution = RESOLUTION_DEVICE;

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
		} else if (resolution == RESOLUTION_DEVICE) {
			width = screenWidth;
			height = screenHeight;
		}
		
		// prevent width and height from being negative
		if (width < 100) {
			width = width + 100;
		}
		if (height < 300) {
			height = height + 300;
		}
		
		// numTrees = x-percentage of total pixels
		selectedPopulation = POPULATION_MEDIUM;
		setNumTrees(selectedPopulation);
		System.out.println("numTrees = " + numTrees);
		
		// Create array of trees and sort them
		sortedTrees = new ArrayList<Tree>();
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

			renderFire(viFire11, biFire11);
			renderFire(viFire12, biFire12);
			renderFire(viFire13, biFire13);
			renderFire(viFire21, biFire21);
			renderFire(viFire22, biFire22);
			renderFire(viFire23, biFire23);
			renderFire(viFire31, biFire31);
			renderFire(viFire32, biFire32);
			renderFire(viFire33, biFire33);
			renderFire(viFire41, biFire41);
			renderFire(viFire42, biFire42);
			renderFire(viFire43, biFire43);
			renderFire(viFire51, biFire51);
			renderFire(viFire52, biFire52);
			renderFire(viFire53, biFire53);
			renderTreeO();
			renderTreeOBurnt();
			renderTreeA();
			renderTreeABurnt();
			renderFireCursorBlack();
			renderFireCursorRed();
			
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
			public void mousePressed(MouseEvent me) {				
				fireCursorClicked = true;
				
				if (!editMode) {
					if (me.getButton() == MouseEvent.BUTTON1) {
						mousePressedLeft = true;
						
						if (stopped || paused) {
							repaint();
						}
						
						if (!replayMode) {
							boolean treeClicked = clickFunction(me.getX(), me.getY());
									
							if (treeClicked) {
								stopped = false;
								clickHistory.add(new ClickAction(me.getX(), me.getY(), tick));						
							}
						}
					}
				} else {
					if (paintBrush.equals(BRUSH_TREE)) {
						System.out.println("MAKE TREES");
					} else if (paintBrush.equals(BRUSH_LAND)) {
						System.out.println("MAKE LAND");
					} else if (paintBrush.equals(BRUSH_SEA)) {
						System.out.println("MAKE SEA");
					} else {
						System.out.println("NO ACTION");
					}
				}
				
				if (paused && clickUnpauses) {
					mapButtons.play();
				}
			}
			

			@Override
			public void mouseReleased(MouseEvent e) {
				mousePressedLeft = false;
				fireCursorClicked = false;
				
				if (!editMode) {
					if (stopped || paused) {
						repaint();
					}
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				mouseXPosition = -100;
				mouseYPosition = -100;
				repaint();
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseClicked(MouseEvent e) {

			}
		});
		
		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent me) {
				mouseXPosition = me.getX();
				mouseYPosition = me.getY();
				
				if (stopped || paused) {
					repaint();
				}
				
				if (!editMode) {
					if (mousePressedLeft && !replayMode) {
						boolean treeClicked = clickFunction(me.getX(), me.getY());
								
						if (treeClicked) {
							stopped = false;
							clickHistory.add(new ClickAction(me.getX(), me.getY(), tick));						
						}
					}
				} else {
					if (paintBrush.equals(BRUSH_TREE)) {
						System.out.println("MAKE TREES");
					} else if (paintBrush.equals(BRUSH_LAND)) {
						System.out.println("MAKE LAND");
					} else if (paintBrush.equals(BRUSH_SEA)) {
						System.out.println("MAKE SEA");
					} else {
						System.out.println("NO ACTION");
					}
				}
			}

			@Override
			public void mouseMoved(MouseEvent me) {
				mouseXPosition = me.getX();
				mouseYPosition = me.getY();
			    
				if (stopped || paused) {
					repaint();
				}
				
				// Draw invisible cursor
				Toolkit toolkit = Toolkit.getDefaultToolkit();
			    Point hotSpot = new Point(0,0);
			    BufferedImage cursorImage = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT); 
			    Cursor invisibleCursor = toolkit.createCustomCursor(cursorImage, hotSpot, "InvisibleCursor");        
			    setCursor(invisibleCursor);
			}
		});

		// Set the size of container
		Dimension fixedSize = new Dimension(width, height - heightOfOtherComponents);
		setPreferredSize(fixedSize);
		setSize(fixedSize);
		setMinimumSize(fixedSize);
		setMaximumSize(fixedSize);
		
		// Timer for simulation
		timerSimulation = new Timer(simDelay, this);
		
		// Timer for replay (should consider using ONE timer)
		timerReplay = new Timer(simDelay, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tick < 10000 && !paused) {
					
					getReplaySlider().setPercentPosition(tick, replayDuration);

					repaint();
					
					while (clickHistoryStack.size() > 0 && tick == clickHistoryStack.get(0).getTick()) {
						int xClicked = clickHistoryStack.get(0).getX();
						int yClicked = clickHistoryStack.get(0).getY();
						clickFunction(xClicked, yClicked);
						clickHistoryStack.remove(0);
//						System.out.print("Pop (Pre-seek)");
//						System.out.print(" -> size: " + clickHistoryStack.size());
//						System.out.println(" (Tick = " + tick + ")");
					}				
					
					
					Set<Tree> newlyIgnitedTrees = new HashSet<Tree>();
					secondAltSequenceCounter++;

					if (secondAltSequenceCounter % 15 == 0) {
						secondAltSequenceCounter = 0;
						int sizeBurning = ignitedTrees.size();
						Tree[] ignitedTreesArray = (Tree[]) ignitedTrees.toArray(new Tree[sizeBurning]);
						for (int i = 0; i < sizeBurning; i++) {
							int nearbySize = ignitedTreesArray[i].getNearbyTrees().size();
							Tree[] nearbyTrees = (Tree[]) ignitedTreesArray[i].getNearbyTrees().toArray(new Tree[nearbySize]);

							for (int j = 0; j < nearbyTrees.length; j++) {
								if (nearbyTrees[j].getState().equals(Tree.GREEN)) {
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
		upperPanel = new JPanel();
		upperPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		// Remove gaps or padding
		FlowLayout layout = (FlowLayout) upperPanel.getLayout();
		layout.setVgap(0);
		layout.setHgap(0);
		
		// Continue subcomponents
		upperPanel.add(mapButtons = new MapButtons());
		upperPanel.setBackground(LookAndFeel.COLOR_SOLID_PANELS);
		upperPanel.setSize(new Dimension(width, 70));
		upperPanel.setMinimumSize(new Dimension(width, 70));
		upperPanel.setMaximumSize(new Dimension(width, 70));		
		editButtons = new EditButtons();
		replaySlider = new ReplaySlider();
	}

	public boolean clickFunction(int xClick, int yClick) {
		System.out.println("Clicked: (" + xClick + "," + yClick + ")");
//		System.out.println("Tick at Click: " + tick);
		int x1 = xClick - clickRadius;
		int x2 = xClick + clickRadius;
		int y1 = yClick - clickRadius;
		int y2 = yClick + clickRadius;
		
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
		int y = TreeGrouper.yBinarySearch(sortedTrees, y1 - (TREE_DIAMETER / 2));
		int yEnd = TreeGrouper.yBinarySearch(sortedTrees, y2 - (TREE_DIAMETER / 2));

		for (; y <= yEnd; y++) {
			tempTreesA.add(sortedTrees.get(y));
		}
		Collections.sort(tempTreesA, new TreeXComparator());

		tempTreesB.clear();
		int x = TreeGrouper.xBinarySearch(tempTreesA, x1 - (TREE_DIAMETER / 2));
		int xEnd = TreeGrouper.xBinarySearch(tempTreesA, x2 - (TREE_DIAMETER / 2));

		for (; x <= xEnd; x++) {
			tempTreesB.add(tempTreesA.get(x));
		}
		
		boolean fireStarted = false;
		
		for (int j = 0; j < tempTreesB.size(); j++) {
			int originX = xClick - (TREE_DIAMETER / 2);
			int originY = yClick - (TREE_DIAMETER / 2);
			int nearbyX = tempTreesB.get(j).getX();
			int nearbyY = tempTreesB.get(j).getY();

			int value = (nearbyX - originX) * (nearbyX - originX) + (nearbyY - originY) * (nearbyY - originY);
			
			if (value <= clickRadiusSqr) {
				if (!replayMode && !timerSimulation.isRunning()) {
					tick = 0;
					timerSimulation.start();
					
					sbPause.setEnabled(true);
					sbStop.setEnabled(true);
					sbReplay.setEnabled(true);
					
					clickHistory.clear();
				}
				
				tempTreesB.get(j).setState(Tree.RED);
				ignitedTrees.add(tempTreesB.get(j));
				
				fireStarted = true;
			}
		}
		
		return fireStarted;
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

	public void renderTreeO() {
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
			g2d.draw(new Line2D.Float(0 + TREE_DIAMETER / 2, 0 + 12, 0 + TREE_DIAMETER / 2, 0 + 28));

			// Anti aliasing and stroke to render smooth line
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setStroke(new BasicStroke(0.40f));

			// Draw circular leaves
			g2d.setColor(new Color(30, 150, 70));
			g2d.fillOval(0, 0, TREE_DIAMETER, TREE_DIAMETER);
			g2d.setColor(Color.BLACK);
			g2d.drawOval(0, 0, TREE_DIAMETER, TREE_DIAMETER);

			// Positioning overlay
			if (positionOverlayEnabled) {
				// Draw positioning
				g2d.setColor(Color.RED);
				g2d.fillOval(0, 0, 2, 2);
	
				// Graphical positioning
				g2d.setColor(Color.BLUE);
				g2d.fillOval(0 + TREE_DIAMETER / 2, 0 + TREE_DIAMETER / 2, 2, 2);
			}
			
			g2d.dispose();
		} while (viTreeA.contentsLost());
	}

	private void renderTreeOBurnt() {
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

	public void renderTreeA() {
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

			// Positioning overlay
			if (positionOverlayEnabled) {
				// Draw positioning
				g2d.setColor(Color.RED);
				g2d.fillOval(0, 0, 2, 2);
	
				// Graphical positioning
				g2d.setColor(Color.BLUE);
				g2d.fillOval(0 + TREE_DIAMETER / 2, 0 + TREE_DIAMETER / 2, 2, 2);
			}
			
			g2d.dispose();
		} while (viTreeA.contentsLost());
	}

	private void renderTreeABurnt() {
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
	
	private void renderFire(VolatileImage vImg, BufferedImage bImg) {
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
	
	public void renderFireCursorBlack() {
		do {
			if (viFireCursorBlack.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
				viFireCursorBlack = createVolatileImage(viFireCursorBlack.getWidth(), viFireCursorBlack.getHeight());
			}
			Graphics2D g2d = viFireCursorBlack.createGraphics();

			// Fill a transparent background 
			g2d.setComposite(AlphaComposite.DstOut); // TRANSPARENCY STEP 1
			g2d.fillRect(0, 0, viFireCursorBlack.getWidth() + 3, viFireCursorBlack.getHeight() + 3); // TRANSPARENCY STEP 2

			// Draw over this background
			g2d.setComposite(AlphaComposite.SrcOver); // Draw over destination

			// Draw mouse cursor
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setStroke(new BasicStroke(2));
			g2d.drawOval(1, 1, clickRadius * 2, clickRadius * 2);

			g2d.dispose();
		} while (viFireCursorBlack.contentsLost());
	}
	
	public void renderFireCursorRed() {
		do {
			if (viFireCursorRed.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
				viFireCursorRed = createVolatileImage(viFireCursorRed.getWidth(), viFireCursorRed.getHeight());
			}
			Graphics2D g2d = viFireCursorRed.createGraphics();

			// Fill a transparent background 
			g2d.setComposite(AlphaComposite.DstOut); // TRANSPARENCY STEP 1
			g2d.fillRect(0, 0, viFireCursorRed.getWidth() + 3, viFireCursorRed.getHeight() + 3); // TRANSPARENCY STEP 2

			// Draw over this background
			g2d.setComposite(AlphaComposite.SrcOver); // Draw over destination

			// Draw mouse cursor
			g2d.setColor(Color.RED);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setStroke(new BasicStroke(2));
			g2d.drawOval(1, 1, clickRadius * 2, clickRadius * 2);

			g2d.dispose();
		} while (viFireCursorRed.contentsLost());
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
				
		Graphics2D g2d = (Graphics2D) g;

		// Draw land
		g.setColor(new Color(90, 170, 90));
		g.fillRect(0, 0, width, height - heightOfOtherComponents);

		// Go back to black
		g.setColor(new Color(0,0,0));
		
		// For all trees
		if (!paused) {
			firstAltSequenceCounter++;
		}
		
		for (int i = 0; i < sortedTrees.size(); i++) {
			int x = sortedTrees.get(i).getX();
			int y = sortedTrees.get(i).getY();

			// Draw circular trees
			if (sortedTrees.get(i).type == Tree.CIRCLE) {
				if (!sortedTrees.get(i).getState().equals(Tree.BLACK)) {
					g.drawImage(ViTreeO, x, y, this);
				} else {
					g.drawImage(viTreeOBurnt, x, y, this);
				}
			} else if (sortedTrees.get(i).type == Tree.TRIANGLE) {
				// Draw triangular trees
				if (!sortedTrees.get(i).getState().equals(Tree.BLACK)) {
					g.drawImage(viTreeA, x, y, this);				
				} else {
					g.drawImage(viTreeABurnt, x, y, this);
				}
			}

			// Fire animation
			if (sortedTrees.get(i).getState().equals(Tree.RED)) {
				
				if (sortedTrees.get(i).getHealth() < ANIMATION_LENGTH - 1 && firstAltSequenceCounter % 3 == 0) {
					if (!paused) {
						firstAltSequenceCounter = 0;
						sortedTrees.get(i).tickHealth();
					}
				}

				g2d.drawImage(vfireAnimation[sortedTrees.get(i).getHealth()], x, y - 6, this);
			}
			
			// Draw current life of tree
			if (healthOverlayEnabled && sortedTrees.get(i).getState().equals(Tree.RED)) {
				char[] ch = Integer.toString(sortedTrees.get(i).getHealth() + 1).toCharArray();
				// Does this need to be hardware accelerated?
				g.drawChars(ch, 0, ch.length, x, y);
				// Why didn't I just use drawString?
			}
		}
			
		if (paused) {
			g.setColor(LookAndFeel.COLOR_SOLID_DARK_TEXT);
			g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, 50));
			
			String string = "PAUSED";
			
			int stringWidth = (int)
		            g2d.getFontMetrics().getStringBounds(string, g2d).getWidth();
			int offsetX = width/2 - stringWidth/2;
			
			int stringHeight = (int)
		            g2d.getFontMetrics().getStringBounds(string, g2d).getHeight();
			int offsetY = height/2 - stringHeight/2;
	        
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.drawString(string, offsetX, offsetY);
			
			if (!dialogOpen) {
				g.setColor(LookAndFeel.COLOR_TRANSLUCENT_BLACK);
				g.fillRect(0, 0, width, height);
			}
		}
		
		// Mouse cursor
		if (fireCursorClicked) {
			g2d.drawImage(viFireCursorRed, mouseXPosition - clickRadius, mouseYPosition - clickRadius, this);
		} else {
			g2d.drawImage(viFireCursorBlack, mouseXPosition - clickRadius, mouseYPosition - clickRadius, this);
		}
	}

	private static void makeTrees(int sortMethod) {
		// Generate trees
		for (int i = 0; i < numTrees; i++) {
			int x = (int) (10 + Math.random() * (width - TREE_DIAMETER - 15));
			int y = (int) (10 + Math.random() * (height - 50 - heightOfOtherComponents));

			sortedTrees.add(new Tree(x, y));
		}
		
		// Set initial burning tree
		boolean isAutoBurn = false;

		// If flag is true, set initial tree to burn
		if (isAutoBurn && sortedTrees.size() > 0) {
			sortedTrees.get(0).setState(Tree.RED);
			ignitedTrees.add(sortedTrees.get(0));
		}

		// Different sort methods are for my own learning
		if (sortMethod == 1) {
			Collections.sort(sortedTrees, new TreeXComparator());
			Collections.sort(sortedTrees, new TreeYComparator());
		} else if (sortMethod == 2) {
			Collections.sort(sortedTrees, new TreeComparator());
		} else {
			System.out.println("Invalid sort method, trees are unsorted");
		}
	}


	@Override
	public void actionPerformed(ActionEvent e) {		
		if (tick < 10000 && !paused) {
			repaint();
			
			Set<Tree> newlyIgnitedTrees = new HashSet<Tree>();
			secondAltSequenceCounter++;

			if (secondAltSequenceCounter % 15 == 0) {
				secondAltSequenceCounter = 0;
				int sizeBurning = ignitedTrees.size();
				Tree[] ignitedTreesArray = (Tree[]) ignitedTrees.toArray(new Tree[sizeBurning]);
				for (int i = 0; i < sizeBurning; i++) {
					int nearbySize = ignitedTreesArray[i].getNearbyTrees().size();
					Tree[] nearbyTrees = (Tree[]) ignitedTreesArray[i].getNearbyTrees().toArray(new Tree[nearbySize]);

					for (int j = 0; j < nearbyTrees.length; j++) {
						if (nearbyTrees[j].getState().equals(Tree.GREEN)) {
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
	
	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
		upperPanel.removeAll();
		
		if (editMode == true) {
			getMapButtons().stop();
			upperPanel.add(editButtons);
		} else {
			upperPanel.add(mapButtons);
		}
		
		upperPanel.repaint();
		upperPanel.revalidate();
	}
	
	public void setViewPositionEnabled(boolean positionOverlayEnabled) {
		this.positionOverlayEnabled = positionOverlayEnabled;
	}
	
	public void setViewTreeHealthEnabled(boolean healthOverlayEnabled) {
		this.healthOverlayEnabled = healthOverlayEnabled;
	}
	
	public void setClickUnpauses(boolean clickUnpauses) {
		this.clickUnpauses = clickUnpauses;
	}
	
	public void rebuildTreeSets() {
		for (int i = 0; i < sortedTrees.size(); i++) {
			sortedTrees.get(i).clearNearbyTrees();
		}
		
		TreeGrouper.buildTreeSets(sortedTrees);
	}
	
	// I don't like how I am implementing setPopulation
	public void setPopulationFactorAndSize(int selectedPopulation) {
		switch (selectedPopulation) {
		case 0: 
			ForestFire.selectedPopulation = POPULATION_ULTRALARGE;
			break;
		case 1:
			ForestFire.selectedPopulation = POPULATION_XXXXLARGE;
			break;
		case 2:
			ForestFire.selectedPopulation = POPULATION_XXXLARGE;
			break;
		case 3:
			ForestFire.selectedPopulation = POPULATION_XXLARGE;
			break;
		case 4:
			ForestFire.selectedPopulation = POPULATION_XLARGE;
			break;
		case 5:
			ForestFire.selectedPopulation = POPULATION_LARGE;
			break;
		case 6:
			ForestFire.selectedPopulation = POPULATION_MEDIUM;
			break;
		case 7:
			ForestFire.selectedPopulation = POPULATION_SMALL;
			break;
		case 8:
			ForestFire.selectedPopulation = POPULATION_XSMALL;
			break;
		case 9:
			ForestFire.selectedPopulation = POPULATION_XXSMALL;
			break;
		case 10:
			ForestFire.selectedPopulation = POPULATION_XXXSMALL;
			break;
		case 11:
			ForestFire.selectedPopulation = POPULATION_XXXXSMALL;
			break;
		case 12:
			ForestFire.selectedPopulation = POPULATION_ULTRASMALL;
			break;
		default:
			System.out.println("No Action");
		}
		
		setNumTrees(ForestFire.selectedPopulation);
	}
	
	public void setNumTrees(double populationFactor) {
		numTrees = (int) (populationFactor * (width * height));
	}
	
	class MapButtons extends JPanel {
		
		private MapButtons() {
			sbPlay.setEnabled(false);
			sbPause.setEnabled(false);
			sbStop.setEnabled(false);
			sbReplay.setEnabled(false);
			sbNormal.setEnabled(false);
			
			sbPlay.setFocusPainted(false);
			sbPause.setFocusPainted(false);
			sbStop.setFocusPainted(false);
			sbReplay.setFocusPainted(false);
			sbSlow.setFocusPainted(false);
			sbNormal.setFocusPainted(false);
			sbFast.setFocusPainted(false);
			sbFaster.setFocusPainted(false);
			
			// Event handling START
			sbPlay.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					play();
					pausePressed = false; 
				}
			});
			
			sbPause.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					pause();
					pausePressed = true;
				}
			});

			sbStop.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {					
					stop();
				}
			});
	
			sbReplay.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					
					clickHistoryStack.clear();
					for (int i = 0; i < clickHistory.size(); i++) {
						clickHistoryStack.add(clickHistory.get(i));
					}

					if (timerSimulation.isRunning() && !timerReplay.isRunning()) {
						replayDuration = tick;						
					}
						
					System.out.println("replayDuration REPLAY: " + replayDuration);

					replayMode = true;
					
					tick = 0;
					
					timerSimulation.stop();
					timerReplay.start();
					
					ignitedTrees.clear();
				
					for (Tree tree : sortedTrees) {
						tree.resetState();
					}

					paused = false;
					stopped = false;
					
					sbPlay.setEnabled(true);
					sbPause.setEnabled(true);
					sbStop.setEnabled(true);
					sbReplay.setEnabled(false);
				}
			});
			
			sbSlow.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					timerSimulation.setDelay((int)(simDelay / speedSlow));
					timerReplay.setDelay((int)(simDelay / speedSlow));
						sbSlow.setEnabled(false);
						sbNormal.setEnabled(true);
						sbFast.setEnabled(true);
						sbFaster.setEnabled(true);
				}
			});
			
			sbNormal.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					timerSimulation.setDelay((int)(simDelay / speedNormal));
					timerReplay.setDelay((int)(simDelay / speedNormal));
					sbSlow.setEnabled(true);
					sbNormal.setEnabled(false);
					sbFast.setEnabled(true);
					sbFaster.setEnabled(true);
				}
			});
			
			sbFast.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					timerSimulation.setDelay((int)(simDelay / speedFast));
					timerReplay.setDelay((int)(simDelay / speedFast));
					sbSlow.setEnabled(true);
					sbNormal.setEnabled(true);
					sbFast.setEnabled(false);
					sbFaster.setEnabled(true);
				}
			});
			
			sbFaster.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					timerSimulation.setDelay((int)(simDelay / speedFaster));
					timerReplay.setDelay((int)(simDelay / speedFaster));
					sbSlow.setEnabled(true);
					sbNormal.setEnabled(true);
					sbFast.setEnabled(true);
					sbFaster.setEnabled(false);
				}
			});
			// Event handling END
			
			// Playback standard button group
			JPanel groupPlayback = new JPanel(new FlowLayout(FlowLayout.LEFT));
			groupPlayback.setOpaque(false);
			groupPlayback.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), "Playback Controls"));
			groupPlayback.add(sbPlay);
			groupPlayback.add(sbPause);
			groupPlayback.add(sbStop);
			groupPlayback.add(sbReplay);
			
			// Simulation speed button group
			JPanel groupSpeed = new JPanel(new FlowLayout(FlowLayout.LEFT));
			groupSpeed.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), "Simulation Speed"));
			groupSpeed.setOpaque(false);
			groupSpeed.add(sbSlow);
			groupSpeed.add(sbNormal);
			groupSpeed.add(sbFast);
			groupSpeed.add(sbFaster);
			
			// Button bar for user controls and settings
			setOpaque(false);

			add(groupPlayback);
			add(groupSpeed);
		}
		
		public void play() {
			paused = false;
			
			sbPlay.setEnabled(false);
			sbPause.setEnabled(true);
			sbStop.setEnabled(true);
		}
		
		public void pause() {
			paused = true;

			sbPlay.setEnabled(true);
			sbPause.setEnabled(false);
			sbStop.setEnabled(true);
			
			ForestFire.this.repaint();
		}
		
		public void stop() {
			if (!replayMode) {
				replayDuration = tick;
			}
			
			if (replayMode) {
				replayMode = false;
			}
			
			timerReplay.stop();
			timerSimulation.stop();
			
			ignitedTrees.clear();
		
			for (Tree tree : sortedTrees) {
				tree.resetState();
			}

			paused = false;
			stopped = true;
			
			sbPlay.setEnabled(false);
			sbPause.setEnabled(false);
			sbStop.setEnabled(false);
			sbReplay.setEnabled(true);
			
			ForestFire.this.repaint();
			getReplaySlider().repaint();
		}
	}
	
	public JPanel getUpperPanel() {
		return upperPanel;
	}
	
	public MapButtons getMapButtons() {
		return mapButtons;
	}
	
	class EditButtons extends JPanel {
		private StandardButton sbMakeTrees = new StandardButton("Tree", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
		private StandardButton sbMakeLand = new StandardButton("Land", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
		private StandardButton sbMakeSea = new StandardButton("Sea", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
		private JComboBox<String> jcbFill = new JComboBox<String>();
		private StandardButton sbFill = new StandardButton("Fill", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
		private StandardButton sbFinish = new StandardButton("Finish", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
		
		public EditButtons() {		
			sbMakeTrees.setEnabled(false);
			
			// See through
			setOpaque(false);

			// Fill options
			jcbFill.addItem("Trees");
			jcbFill.addItem("Land");
			jcbFill.addItem("Sea");
			
			// Buttons to place map objects
			JPanel landscapeButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
			landscapeButtons.setOpaque(false);
			landscapeButtons.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), "Landscape Controls"));
			landscapeButtons.add(sbMakeTrees);
			landscapeButtons.add(sbMakeLand);
			landscapeButtons.add(sbMakeSea);
			
			// Buttons to fill out map
			JPanel fillButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
			fillButtons.setOpaque(false);
			fillButtons.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), "Fill Controls"));
			fillButtons.add(jcbFill);
			fillButtons.add(sbFill);
			
			// Exit edit mode
			JPanel otherButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
			
			otherButtons.setBackground(new Color(215,199,151));
			otherButtons.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), "Options"));
			otherButtons.add(sbFinish);
			
			// Listeners START
			sbMakeTrees.addActionListener(e -> {
				paintBrush = BRUSH_TREE;
				
				sbMakeTrees.setEnabled(false);
				sbMakeLand.setEnabled(true);
				sbMakeSea.setEnabled(true);				
			});
			
			sbMakeLand.addActionListener(e -> {
				paintBrush = BRUSH_LAND;
				
				sbMakeTrees.setEnabled(true);
				sbMakeLand.setEnabled(false);
				sbMakeSea.setEnabled(true);				
			});
			
			sbMakeSea.addActionListener(e -> {
				paintBrush = BRUSH_SEA;
				
				sbMakeTrees.setEnabled(true);
				sbMakeLand.setEnabled(true);
				sbMakeSea.setEnabled(false);				
			});
			
			sbFill.addActionListener(e -> {
				int selection = jcbFill.getSelectedIndex();
				
				if (selection == 0) {
					// I don't think I need to clear the burning trees
					if (!timerSimulation.isRunning() && !timerReplay.isRunning()) {
						
						// Create array of trees and sort them
						sortedTrees = new ArrayList<Tree>();
						makeTrees(2);
						
						// Add neighboring trees to each tree
						TreeGrouper.buildTreeSets(sortedTrees);	
						
						ForestFire.this.repaint();
					}
				} else if (selection == 1) {
					
				} else if (selection == 2) {

				} else {
					System.out.println("NO ACTION");
				}
			});
			
			sbFinish.addActionListener(e -> {
				setEditMode(false);
			}); 
			// Listeners FINISH
			
			add(landscapeButtons);
			add(fillButtons);
			add(otherButtons);
		}
		
		public StandardButton getFinishButton() {
			return sbFinish;
		}
	}
	
	public EditButtons getEditButtons() {
		return editButtons;
	}
	
	class ReplaySlider extends JPanel {
		private int xPosition = 0;
		private int percentPosition = 0;
		// Recommended: combination of both colors instead of just one
		private Color lightBluePosition = new Color(131,208,201);
		private Color lightGreenPosition = new Color(90,170,90);
		private JLabel trackSlider;
		// Boolean flags
		private boolean mousePressedLeft = false;

		private ReplaySlider() {
			trackSlider = new JLabel();
						
			add(trackSlider);
			setLayout(new GridLayout());
			
			trackSlider.addMouseListener(new MouseAdapter() {
				
				@Override
				public void mousePressed(MouseEvent me) {
					if (me.getButton() == MouseEvent.BUTTON1 && replayMode) {	
						mousePressedLeft = true;
						seekReplay(me);
					}
				}
				
				@Override
				public void mouseReleased(MouseEvent me) {
					if (replayMode) {
						mousePressedLeft = false;
						rebuildStates();
						if (!pausePressed) {
							paused = false;
						}
						if (paused) {
							ForestFire.this.repaint();
						}
					}
				}
			});
			
			trackSlider.addMouseMotionListener(new MouseMotionListener() {
				
				@Override
				public void mouseMoved(MouseEvent me) {
				}
				
				@Override
				public void mouseDragged(MouseEvent me) {
					if (mousePressedLeft && replayMode) {						
						seekReplay(me);
					}
				}
			});
			
			setBackground(new Color(215,199,151));
			setSize(new Dimension(width, 20));
			setMinimumSize(new Dimension(width, 20));
			setMaximumSize(new Dimension(width, 20));		
		}
		
		public void seekReplay(MouseEvent me) {
			paused = true;
			
			// Be careful me.getX() can be less than zero or greater than width
			xPosition = me.getX();
			int xWidth = ((JComponent) me.getSource()).getWidth();
			percentPosition = (int) (100 * (float) xPosition / (float) xWidth);
			
			if (percentPosition < 0) {
				percentPosition = 0;
			} else if (percentPosition > 100) {
				percentPosition = 100;
			}
				
			tick = (int) (((float) percentPosition / 100f) * replayDuration);
			
			repaint();
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
									
			if (replayMode) {
				trackSlider.setText(" Replay progress: " + percentPosition + "%");
				Graphics2D g2d = (Graphics2D) g;
				GradientPaint upperGradient = new GradientPaint(0, 0, Color.WHITE, 0, this.getHeight()/2, lightBluePosition);
				GradientPaint lowerGradient = new GradientPaint(0, this.getHeight()/2, lightGreenPosition, 0, this.getHeight(), Color.WHITE);

				g2d.setPaint(upperGradient);
				g2d.fill(new Rectangle2D.Double(0, 0, xPosition, this.getHeight()/2));
				g2d.setPaint(lowerGradient);
				g2d.fill(new Rectangle2D.Double(0, this.getHeight()/2, xPosition, this.getHeight()));
			} else {
				trackSlider.setText("");
				super.paintComponent(g);
			}
		}
		
		public void rebuildStates() {
			ignitedTrees.clear();
			
			for (Tree tree : sortedTrees) {
				tree.resetState();
			}
			
			int replayTick = 0;

			clickHistoryStack.clear();
			for (int i = 0; i < clickHistory.size(); i++) {
				clickHistoryStack.add(clickHistory.get(i));
			}
			
			while (replayTick <= tick) {	
				
				for (int i = 0; i < sortedTrees.size(); i++) {
					// Fire animation (not drawn)
					if (sortedTrees.get(i).getState().equals(Tree.RED)) {
						if (sortedTrees.get(i).getHealth() < ANIMATION_LENGTH - 1) {
								sortedTrees.get(i).tickHealth();
						}
					}
				}
				
				while (clickHistoryStack.size() > 0 && replayTick == clickHistoryStack.get(0).getTick()) {
					int xClicked = clickHistoryStack.get(0).getX();
					int yClicked = clickHistoryStack.get(0).getY();
					clickFunction(xClicked, yClicked);
					clickHistoryStack.remove(0);
//					System.out.print("Pop (Post-seek)");
//					System.out.print(" -> size: " + clickHistoryStack.size());
//					System.out.println(" (Tick = " + replayTick + ")");
				}				
				
				Set<Tree> newlyIgnitedTrees = new HashSet<Tree>();
				secondAltSequenceCounter++;

				if (secondAltSequenceCounter % 15 == 0) {
					secondAltSequenceCounter = 0;
					int sizeBurning = ignitedTrees.size();
					Tree[] ignitedTreesArray = (Tree[]) ignitedTrees.toArray(new Tree[sizeBurning]);
					for (int i = 0; i < sizeBurning; i++) {
						int nearbySize = ignitedTreesArray[i].getNearbyTrees().size();
						Tree[] nearbyTrees = (Tree[]) ignitedTreesArray[i].getNearbyTrees().toArray(new Tree[nearbySize]);

						for (int j = 0; j < nearbyTrees.length; j++) {
							if (nearbyTrees[j].getState().equals(Tree.GREEN)) {
								newlyIgnitedTrees.add(nearbyTrees[j]);
							}
							nearbyTrees[j].setState(Tree.RED);
						}
					}

					ignitedTrees.clear();

					for (Tree tree : newlyIgnitedTrees) {
						ignitedTrees.add(tree);
					}

				}
				newlyIgnitedTrees.clear();
				replayTick++;
			}
		}
		
		@Override
		public void repaint() {
			super.repaint();
		}
		
		public void setPercentPosition(int tick, int replayDuration) {
			percentPosition = (int) (100 * (float) tick / (float) replayDuration);
			xPosition = (int) (((float) percentPosition / 100f) * trackSlider.getWidth());

			if (xPosition > trackSlider.getWidth()) {
				xPosition = trackSlider.getWidth();
			}

			repaint();
		}
	}
	
	public ReplaySlider getReplaySlider() {
		return replaySlider;
	}
	
	public void setDialogOpen(boolean dialogOpen) {
		ForestFire.dialogOpen = dialogOpen;
	}
}