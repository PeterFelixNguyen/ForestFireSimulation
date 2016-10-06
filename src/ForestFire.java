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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.MouseInfo;
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
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
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
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JViewport;
import javax.swing.JWindow;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import com.jd.swing.custom.component.button.ButtonType;
import com.jd.swing.custom.component.button.StandardButton;
import com.jd.swing.util.Theme;

@SuppressWarnings("serial")
public class ForestFire extends JPanel {
	protected static JScrollPane jspForestFire;
	private GameStats gameStats = new GameStats();
	private JPanel mapPanel = new JPanel();

	// I will use Strings for left-click actions (for now)
	public static final String LEFT_ACTION_FIRE = "Fire";
	public static final String LEFT_ACTION_RAIN = "Rain";
	public static final String LEFT_ACTION_WIND = "Wind";
	public static final String LEFT_ACTION_TORNADO = "Tornado";
	public static final String LEFT_ACTION_LIGHTNING = "Lightning";
	public static final String LEFT_ACTION_COMBO = "Combo";
	private String leftClickAction = LEFT_ACTION_FIRE;
	
	// Array of WeatherObjects
	private ArrayList<WeatherObject> weatherObjects = new ArrayList<WeatherObject>();
	private WeatherObject currentWeatherObject;

	public void setLeftClickAction(String leftClickAction) {
		this.leftClickAction = leftClickAction;
        renderFireCursorBlack(); // no if statement required
	}
	
	// Constants to specify map resolution
	public final static String RESOLUTION_WVGA = "800x480";
	public final static String RESOLUTION_WSVGA = "1024x600";
	public final static String RESOLUTION_720P = "1280x720";
	public final static String RESOLUTION_SXGA = "1280x1024";
	public final static String RESOLUTION_HDPLUS = "1600x900";
	public final static String RESOLUTION_UXGA = "1600x1200";
	public final static String RESOLUTION_FHD = "1920x1080";
	public final static String RESOLUTION_WQXGA = "2560x1600";
	
	public final static String RESOLUTION_DEVICE = "DEVICE";
	public final static int WIDTH_DEFAULT = 1200;
	public final static int HEIGHT_DEFAULT = 600;
	public final static int WIDTH_TEST = 1200;
	public final static int HEIGHT_TEST = 600;
	public final static int MIN_WIDTH = 100;
	public final static int MIN_HEIGHT = 100;

	// Constants to specify percentage of trees
	public static final double  POPULATION_XXXLARGE = 0.003290625;
	public static final double   POPULATION_XXLARGE = 0.00219375;
	public static final double    POPULATION_XLARGE = 0.0014625;
	public static final double     POPULATION_LARGE = 0.000975;
	public static final double    POPULATION_MEDIUM = 0.00065;
	public static final double     POPULATION_SMALL = 0.00060;
	public static final double    POPULATION_XSMALL = 0.00050;
	public static final double   POPULATION_XXSMALL = 0.00040;
	public static final double  POPULATION_XXXSMALL = 0.00030;
	
	// Map settings
	private static double selectedPopulation;
	@SuppressWarnings("unused")
	private static String selectedMapSize;
	
	// Constants to specify percentage of trees for testing 
	public static final double POPULATION_ULTRALARGE = 0.01000;
	public static final double POPULATION_ULTRASMALL = 0.00001;

	// Resolution options
	private static String resolution = "";
	public static int screenWidth;
	public static int screenHeight;
	public static int mapWidth = WIDTH_DEFAULT;
	public static int mapHeight = HEIGHT_DEFAULT;
	
	// Adjust height for MenuBar, ButtonBar, and ReplaySlider, 
	public static int heightOfOtherComponents = 112;
	
	// Tree objects
	private static int maxTrees;
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
	public static final int SEQUENCE_LENGTH = 3;
	public static final int SEQUENCE_MULTIPLIER = 8; // ONLY THIS VARIABLE SHOULD BE CHANGED
	public static final int SEQUENCE_LENGTH_MULTIPLIED = SEQUENCE_LENGTH * SEQUENCE_MULTIPLIER;
	public static final int UNIQUE_SEQUENCES = 5;
	public static final int ANIMATION_LENGTH = SEQUENCE_LENGTH * SEQUENCE_MULTIPLIER * UNIQUE_SEQUENCES;
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
	private static Timer timerView;
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
	private boolean unpausedOnClick = true;
	private boolean playing = false;
	
	// Boolean Mouse Flags
	private boolean mousePressedLeft = false;
	private boolean mousePressedRight = false;
	private boolean mouseEntered = false;
	
	// Edit Mode
	private boolean editMode = false;
	private String makeType = "Tree";
	private final String MAKE_TREE = "Tree";
	private final String MAKE_LAND = "Land";
	private final String MAKE_SEA = "Sea";
	private String paintType = "Brush";
	private final String PAINT_PEN = "Pen";
	private final String PAINT_BRUSH = "Brush";
	private final String PAINT_RANDOM = "Random";
	private final String PAINT_FILL = "Fill";
	
	// Map control
	private int xClick;
	private int yClick;
	
	public ForestFire() {
		// Get device resolution
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		screenWidth = gd.getDisplayMode().getWidth();
		screenHeight = gd.getDisplayMode().getHeight();
		System.out.println("Device resolution = " + screenWidth + "x" + screenHeight);
		
		// Select resolution name
		resolution = RESOLUTION_DEVICE;

		jspForestFire = new JScrollPane(mapPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jspForestFire.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE); // disable repaint
		jspForestFire.setBorder(null); 
		Dimension viewDimension = new Dimension(screenWidth, screenHeight - heightOfOtherComponents);
		jspForestFire.setPreferredSize(viewDimension);
		jspForestFire.setSize(viewDimension);
		jspForestFire.setMinimumSize(viewDimension);
		jspForestFire.setMaximumSize(viewDimension);
		
		// Set the size of container
		setMapSize(2560, 1600); // test
		
		// maxTrees = x-percentage of total pixels
		selectedPopulation = POPULATION_MEDIUM;
		setMaxTrees(selectedPopulation);
		
		// Create array of trees and sort them
		sortedTrees = new ArrayList<Tree>();
		makeTrees(2, maxTrees);

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
				if (i < SEQUENCE_LENGTH_MULTIPLIED * 1) {
					if (i % 3 == 0) {
						vfireAnimation[i] = viFire11;
					} else if (i % 3 == 1) {
						vfireAnimation[i] = viFire12;
					} else if (i % 3 == 2) {
						vfireAnimation[i] = viFire13;
					}
				} else if (i < SEQUENCE_LENGTH_MULTIPLIED * 2) {
					if (i % 3 == 0) {
						vfireAnimation[i] = viFire21;
					} else if (i % 3 == 1) {
						vfireAnimation[i] = viFire22;
					} else if (i % 3 == 2) {
						vfireAnimation[i] = viFire23;
					}
				} else if (i < SEQUENCE_LENGTH_MULTIPLIED * 3) {
					if (i % 3 == 0) {
						vfireAnimation[i] = viFire31;
					} else if (i % 3 == 1) {
						vfireAnimation[i] = viFire32;
					} else if (i % 3 == 2) {
						vfireAnimation[i] = viFire33;
					}
				} else if (i < SEQUENCE_LENGTH_MULTIPLIED * 4) {
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
		
		JPopupMenu jpmGameObjects = new JPopupMenu();
		JMenuItem jmiFire = new JMenuItem("Fire");
		JMenu jmWeather = new JMenu("Weather");
		JMenuItem jmiRain = new JMenuItem("Rain");
		JMenuItem jmiWind = new JMenuItem("Wind");
		JMenuItem jmiTornado = new JMenuItem("Tornado");
		JMenuItem jmiLightning = new JMenuItem("Lightning");
		JMenuItem jmiSnow = new JMenuItem("Snow");
		JMenuItem jmiCombo = new JMenuItem("Combo");
		jpmGameObjects.add(new JMenuItem("Pause/Resume"));
		jpmGameObjects.add(new JSeparator(SwingConstants.HORIZONTAL));
		jpmGameObjects.add(jmiFire);
		jmWeather.add(jmiRain);
		jmWeather.add(jmiWind);
		jmWeather.add(jmiTornado);
		jmWeather.add(jmiLightning);
		jmWeather.add(jmiSnow);
		jmWeather.add(jmiCombo);
		
		jmiFire.addActionListener(e -> {
			startSimulationTimer();
			setLeftClickAction(LEFT_ACTION_FIRE);
		});
		
		jmiRain.addActionListener(e -> {
			startSimulationTimer();
			setLeftClickAction(LEFT_ACTION_RAIN);
		});
		
		jmiWind.addActionListener(e -> {
			startSimulationTimer();
			setLeftClickAction(LEFT_ACTION_WIND);
		});
		
		jpmGameObjects.add(jmWeather);

		addMouseListener(new MouseListener() {
			
			@Override
			public void mousePressed(MouseEvent me) {				
				fireCursorClicked = true;
								
				if (me.getButton() == MouseEvent.BUTTON1) {
					mousePressedLeft = true;

					if (!editMode) {
						if (!replayMode) {
							if (leftClickAction.equals(LEFT_ACTION_FIRE)) {
								boolean treeClicked = clickIgniteTrees(me.getX(), me.getY());
	
								if (treeClicked) {
									stopped = false;
									clickHistory.add(new ClickAction(me.getX(), me.getY(), tick));						
								}
							} else if (leftClickAction.equals(LEFT_ACTION_RAIN)) {
								currentWeatherObject = new Rain(me.getX(), me.getY(), clickRadius, clickRadius);
								weatherObjects.add(currentWeatherObject);
								currentWeatherObject.setDestination(me.getX(), me.getY());
							} else if (leftClickAction.equalsIgnoreCase(LEFT_ACTION_WIND)) {
								currentWeatherObject = new Wind(me.getX(), me.getY(), clickRadius, clickRadius);
								weatherObjects.add(currentWeatherObject);
								currentWeatherObject.setDestination(me.getX(), me.getY());
							}
						}
						
						if (paused && unpausedOnClick) {
							mapButtons.play();
						}
					} else {
						if (makeType.equals(MAKE_TREE)) {
							if (paintType.equals(PAINT_PEN)) {
								clickMakeTree(me.getX(), me.getY());								
							} else if (paintType.equals(PAINT_BRUSH)) {
								clickMakeTrees(me.getX(), me.getY());
							} else if (paintType.equals(PAINT_RANDOM)) {
								clickRandomTrees(me.getX(), me.getY());
							} else if (paintType.equals(PAINT_FILL)) {
									fillTrees();
							}

							Collections.sort(sortedTrees, new TreeComparator());

							// Add neighboring trees to each tree
							TreeGrouper.buildTreeSets(sortedTrees);	
						} else if (makeType.equals(MAKE_LAND)) {
							if (paintType.equals(PAINT_BRUSH)) {
								clickMakeLand(me.getX(), me.getY());
							} else if (paintType.equals(PAINT_FILL)) {
								emptyMap();
							}
						} else if (makeType.equals(MAKE_SEA)) {

						} else {
							System.out.println("NO ACTION");
						}
					}
				} else if (me.getButton() == MouseEvent.BUTTON3) {
					mousePressedRight = true; // PROBABLY NOT NEEDED
	                setCursor(new Cursor(Cursor.MOVE_CURSOR));

					xClick = (int) me.getLocationOnScreen().getX();
					yClick = (int) me.getLocationOnScreen().getY();
					
					if (me.getClickCount() == 2) {
						jpmGameObjects.show(me.getComponent(), me.getX(), me.getY());
					}
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent me) {
				mousePressedRight = false;
				fireCursorClicked = false;
				
				if (leftClickAction != LEFT_ACTION_FIRE && mousePressedLeft) {
					leftClickAction = LEFT_ACTION_FIRE;
					currentWeatherObject.setDestination(me.getX(), me.getY());
					currentWeatherObject.setStill(false);
					currentWeatherObject.calculateDirection();
					clickRadius = 100;
					clickRadiusSqr = clickRadius * clickRadius;
					renderFireCursorBlack();
					renderFireCursorRed();
				}
				
				mousePressedLeft = false;

				makeCursorInvisible();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				mouseEntered = false;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				mouseEntered = true;
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
				
				if (mousePressedLeft) {
					if (!editMode && !replayMode) {
						if (leftClickAction == LEFT_ACTION_FIRE) {
							boolean treeClicked = clickIgniteTrees(me.getX(), me.getY());
							if (treeClicked) {
								stopped = false;
								clickHistory.add(new ClickAction(me.getX(), me.getY(), tick));						
							}
						} else {
							currentWeatherObject.setDestination(me.getX(), me.getY());
						}
					} else {
						if (makeType.equals(MAKE_TREE)) {
							if (paintType.equals(PAINT_PEN)) {
								dragMakeTree(me.getX(), me.getY());								
							} else if (paintType.equals(PAINT_BRUSH)) {
								clickMakeTrees(me.getX(), me.getY());
							} else if (paintType.equals(PAINT_RANDOM)) {
								clickRandomTrees(me.getX(), me.getY());
							}
							
							Collections.sort(sortedTrees, new TreeComparator());
							
							// Add neighboring trees to each tree
							TreeGrouper.buildTreeSets(sortedTrees);	
						} else if (makeType.equals(MAKE_LAND)) {
							if (paintType.equals(PAINT_BRUSH)) {
								clickMakeLand(me.getX(), me.getY());
							} else if (paintType.equals(PAINT_FILL)) {
								
							}
						} else if (makeType.equals(MAKE_SEA)) {

						} else {

						}
					}
				} else if (me.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK) {
	                double mx = me.getLocationOnScreen().getX();
	                double my = me.getLocationOnScreen().getY();
	                int xPixelShift = (int) (xClick - mx);
	                int yPixelShift = (int) (yClick - my);
	                
	                xClick = (int) mx;
	                yClick = (int) my;
	                
	                jspForestFire.getHorizontalScrollBar().setValue(
	                		jspForestFire.getHorizontalScrollBar().getValue()
	                        + xPixelShift);
	                jspForestFire.getVerticalScrollBar().setValue(
	                		jspForestFire.getVerticalScrollBar().getValue()
	                        + yPixelShift);
	                
	                mousePressedRight = true;
	                setCursor(new Cursor(Cursor.MOVE_CURSOR));
	            }				
			}

			@Override
			public void mouseMoved(MouseEvent me) {
				mouseXPosition = me.getX();
				mouseYPosition = me.getY();
				
				makeCursorInvisible();
			}
		});
		
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent mwe) {
				int direction = mwe.getWheelRotation();
				
				if (direction > 0) {
					if (leftClickAction == LEFT_ACTION_FIRE) {
						if (clickRadius > 10) {
							if (clickRadius - 20 < 10) {
								clickRadius = 10;
							} else {
								clickRadius = clickRadius - 20;
							}
						}
					} else {
						if (clickRadius > 100) {
							if (clickRadius - 20 < 100) {
								clickRadius = 100;
							} else {
								clickRadius = clickRadius - 20;								
							}
						} 
					}
				} else {
					if (leftClickAction == LEFT_ACTION_FIRE) {
						if (clickRadius < 100) {
							if (clickRadius + 20 > 100) {
								clickRadius = 100;
							} else {
								clickRadius = clickRadius + 20;
							}
						} 
					} else {
						if (clickRadius < 1000) {
							if (clickRadius + 20 > 1000) {
								clickRadius = 1000;
							} else {
								clickRadius = clickRadius + 20;
							}
			            } 
					}
				}
				
				// Note: redundancy
	            clickRadiusSqr = clickRadius * clickRadius;
	            renderFireCursorBlack();
	            renderFireCursorRed();
			}
		});

		mapPanel.setBackground(LookAndFeel.COLOR_SOLID_BLACK);
		mapPanel.add(ForestFire.this, new GridBagConstraints());
		
		// Timer for simulation
		timerSimulation = new Timer(simDelay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {						
				if (tick < 10000 && !paused) {
					if (!paused) {
						firstAltSequenceCounter++;
					}
					
					for (int i = 0; i < sortedTrees.size(); i++) {
						if (sortedTrees.get(i).getState().equals(Tree.RED) && sortedTrees.get(i).getHealth() < ANIMATION_LENGTH - 1 && firstAltSequenceCounter % 3 == 0) {
							if (!paused) {
								firstAltSequenceCounter = 0;
								sortedTrees.get(i).tickHealth();
							}
						}
						sortedTrees.get(i).tickState();
					}
					
					for (int i = 0; i < weatherObjects.size(); i++) {
						if (!paused) {
							weatherObjects.get(i).move();
							weatherObjects.get(i).grow();
						}
					}
					
					// New weather effects (add to timerReplay)
					for (int i = 0; i < weatherObjects.size(); i++) {
						clickFunction(weatherObjects.get(i).getXPosition(), weatherObjects.get(i).getYPosition(), weatherObjects.get(i).getEffectRadius());
						
						for (int j = 0; j < tempTreesB.size(); j++) {
							int originX = weatherObjects.get(i).getXPosition() - (TREE_DIAMETER / 2);
							int originY = weatherObjects.get(i).getYPosition() - (TREE_DIAMETER / 2);
							int nearbyX = tempTreesB.get(j).getX();
							int nearbyY = tempTreesB.get(j).getY();
							
							int value = (nearbyX - originX) * (nearbyX - originX) + (nearbyY - originY) * (nearbyY - originY);
							
							if (value <= weatherObjects.get(i).getEffectRadiusSqr() && tempTreesB.get(j).getState() != Tree.BLACK) {
								if (weatherObjects.get(i) instanceof Rain) {
									// (a) Removes fire completely
//									tempTreesB.get(j).resetState();
									
									// (b) Reduces fire gradually
									tempTreesB.get(j).setWet(true, 5);

								} else if (weatherObjects.get(i) instanceof Wind) {
									// (a) Spread fire immediately at direction
									if (tempTreesB.get(j).getState().equals(Tree.RED)) {
										Set<Tree> treesOnFire = tempTreesB.get(j).getNearbyTrees();
										for (Tree tree : treesOnFire) {
											boolean isIgniteableX = false;
											boolean isIgniteableY = false;

											if (weatherObjects.get(i).getXDirection() < 0) {
												if (tempTreesB.get(j).getX() > tree.getX()) {
													isIgniteableX = true; 
												}	
											} else if (weatherObjects.get(i).getXDirection() > 0) {
												if (tempTreesB.get(j).getX() < tree.getX()) {
													isIgniteableX = true;
												}
											}
											
											if (weatherObjects.get(i).getYDirection() < 0) {
												if (tempTreesB.get(j).getY() > tree.getY()) {
													isIgniteableY = true; 
												}	
											} else if (weatherObjects.get(i).getYDirection() > 0) {
												if (tempTreesB.get(j).getY() < tree.getY()) {
													isIgniteableY = true;
												}
											}
																						
											if (isIgniteableX && isIgniteableY) {
												tree.setState(Tree.RED);
												ignitedTrees.add(tree);
											} 
										}
									}
								}
								ignitedTrees.remove(tempTreesB.get(j));
							}
						}
					}
					
					Set<Tree> newlyIgnitedTrees = new HashSet<Tree>();
					secondAltSequenceCounter++;

					if (secondAltSequenceCounter % (SEQUENCE_LENGTH_MULTIPLIED * 3) == 0) {
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
		
		// Timer for replay (should consider using ONE timer)
		timerReplay = new Timer(simDelay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tick < 10000 && !paused) {
					
					getReplaySlider().setPercentPosition(tick, replayDuration);
					
					if (!paused) {
						firstAltSequenceCounter++;
					}
					
					for (int i = 0; i < sortedTrees.size(); i++) {
						if (sortedTrees.get(i).getState().equals(Tree.RED) && sortedTrees.get(i).getHealth() < ANIMATION_LENGTH - 1 && firstAltSequenceCounter % 3 == 0) {
							if (!paused) {
								firstAltSequenceCounter = 0;
								sortedTrees.get(i).tickHealth();
							}
						}
					}
					
					while (clickHistoryStack.size() > 0 && tick == clickHistoryStack.get(0).getTick()) {
						int xClicked = clickHistoryStack.get(0).getX();
						int yClicked = clickHistoryStack.get(0).getY();
						clickIgniteTrees(xClicked, yClicked);
						clickHistoryStack.remove(0);
					}				
					
					Set<Tree> newlyIgnitedTrees = new HashSet<Tree>();
					secondAltSequenceCounter++;

					if (secondAltSequenceCounter % (SEQUENCE_LENGTH_MULTIPLIED * 3) == 0) {
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
					
					tick++;
				}
			}
		});
		
		timerView = new Timer(simDelay - 20, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {			
				if (!mousePressedRight) {
					JViewport viewport = jspForestFire.getViewport();
	
					JScrollBar verticalScrollBar = jspForestFire.getVerticalScrollBar();
					
					int yMin = verticalScrollBar.getMinimum();
					int yMax = (int) (viewport.getView().getHeight() - viewport.getExtentSize().getHeight());
	
					// The height of ViewPort subtracted by height of ChildView is the actual yMax
					int oldValueY = jspForestFire.getVerticalScrollBar().getValue();
					int newValueY;
	
					int yDragDirection;
	
					/* I had to come up with my own technique that works for diagonal edge scroll because
					   getVerticalScrollBar.setValue() and getHorizontalScrollBar.setValue() 
					   does not happen concurrently so the animation would be choppy. ~Peter "Felix" Nguyen */
		
					/* With the regular method, the map would scroll vertically and then horizontally 
					   (not truly diagonal). My implementation ensures that if the mouse is at the 
					   corner of the map, the JScrollPane would scroll diagonally. ~Peter "Felix" Nguyen */
					
					/* I call this Octo-directional scrolling */
					
					if (MouseInfo.getPointerInfo().getLocation().getY() < 0 + 2) {
						yDragDirection = -40;
						newValueY = Math.max(oldValueY + yDragDirection, yMin);
					} else if (MouseInfo.getPointerInfo().getLocation().getY() > ForestFire.screenHeight - 2) {
						yDragDirection = +40;
						newValueY = Math.min(oldValueY + yDragDirection, yMax);
					} else {
						yDragDirection = 0;
						newValueY = oldValueY + yDragDirection;
					}
	
					JScrollBar horizontalScrollBar = jspForestFire.getHorizontalScrollBar();
					
					// The width of ViewPort subtracted by width of ChildView is the actual xMax
					int xMin = horizontalScrollBar.getMinimum();
					int xMax = (int) (viewport.getView().getWidth() - viewport.getExtentSize().getWidth());
					
					int oldValueX = jspForestFire.getHorizontalScrollBar().getValue();
					int newValueX;
					
					int xDragDirection;
	
					if (MouseInfo.getPointerInfo().getLocation().getX() < 0 + 2) {
						xDragDirection = -40;
						newValueX = Math.max(oldValueX + xDragDirection, xMin);
					} else if (MouseInfo.getPointerInfo().getLocation().getX() > ForestFire.screenWidth - 2) {
						xDragDirection = +40;
						newValueX = Math.min(oldValueX + xDragDirection, xMax);
					} else {
						xDragDirection = 0;
						newValueX = oldValueX + xDragDirection;
					}
					
					// Update mouse cursor location
					mouseXPosition += xDragDirection;
					mouseYPosition += yDragDirection;
					
					// Octo-directional scrolling!
					jspForestFire.getViewport().setViewPosition(new Point(newValueX, newValueY));
				}
				repaint();
			}				
		});
		
		timerView.start();
		
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
		upperPanel.setSize(new Dimension(mapWidth, 70));
		upperPanel.setMinimumSize(new Dimension(mapWidth, 70));
		upperPanel.setMaximumSize(new Dimension(mapWidth, 70));		
		editButtons = new EditButtons();
		replaySlider = new ReplaySlider();
	}

	public void startSimulationTimer() {
		timerSimulation.start();
	}
	
	public void clickFunction(int xClick, int yClick, int clickRadius) {
		if (sortedTrees.size() > 0) {
			int x1 = xClick - clickRadius;
			int x2 = xClick + clickRadius;
			int y1 = yClick - clickRadius;
			int y2 = yClick + clickRadius;
			
			if (x1 < 0) {
				x1 = 0;
			}
			if (x2 > ForestFire.mapWidth) {
				x2 = ForestFire.mapWidth;
			}
			if (y1 < 0) {
				y1 = 0;
			}
			if (y2 > ForestFire.mapHeight) {
				y2 = ForestFire.mapHeight;
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
		}
	}
	
	public boolean clickIgniteTrees(int xClick, int yClick) {
		clickFunction(xClick, yClick, clickRadius);
		
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

	public void clickRandomTrees(int xClick, int yClick) {
		int i = 0;
		int count = maxTrees / 10;
		while (i < count && sortedTrees.size() < maxTrees) {
			int x = (int) (10 + Math.random() * (mapWidth - TREE_DIAMETER - 15));
			int y = (int) (10 + Math.random() * (mapHeight - 50));

			sortedTrees.add(new Tree(x, y));
			i++;
		}
	}
	
	public void clickMakeTree(int xClick, int yClick) {
		if (sortedTrees.size() < maxTrees) {
			sortedTrees.add(new Tree(xClick - TREE_DIAMETER / 2, yClick - TREE_DIAMETER / 2));
		}
	}
	
	private int count = 0;
	
	public void dragMakeTree(int xClick, int yClick) {
		if (sortedTrees.size() < maxTrees) {
			int mode = 2;
			
			if (mode == 0) {
				double random = Math.random();
				double probability = 0.1;
				
				if (random < probability) {
					sortedTrees.add(new Tree(xClick - TREE_DIAMETER / 2, yClick - TREE_DIAMETER / 2));
				}
			} else if (mode == 1) {
				count++;
				if (count == 3) {
					count = 0;
					sortedTrees.add(new Tree(xClick - TREE_DIAMETER / 2, yClick - TREE_DIAMETER / 2));					
				}
			} else {
				sortedTrees.add(new Tree(xClick - TREE_DIAMETER / 2, yClick - TREE_DIAMETER / 2));					
			}
		}
	}
	
	public void clickMakeTrees(int xClick, int yClick) {
		if (sortedTrees.size() < maxTrees) {
			boolean validSpot = false;
			int x = xClick;
			int y = yClick;
			
			while (!validSpot) {
				x = (int) ((xClick - clickRadius) + Math.random() * (clickRadius * 2));
				y = (int) ((yClick - clickRadius) + Math.random() * (clickRadius * 2));		
	
				int value = (x - xClick) * (x - xClick) + (y - yClick) * (y - yClick);
					
				if (value <= clickRadiusSqr) {
					validSpot = true;
				} 
			}
			
			sortedTrees.add(new Tree(x - TREE_DIAMETER / 2, y - TREE_DIAMETER / 2));
		}
	}
	
	public void clickMakeLand(int xClick, int yClick) {
		clickFunction(xClick, yClick, clickRadius);
				
		for (int j = 0; j < tempTreesB.size(); j++) {
			int originX = xClick - (TREE_DIAMETER / 2);
			int originY = yClick - (TREE_DIAMETER / 2);
			int nearbyX = tempTreesB.get(j).getX();
			int nearbyY = tempTreesB.get(j).getY();

			int value = (nearbyX - originX) * (nearbyX - originX) + (nearbyY - originY) * (nearbyY - originY);
			
			if (value <= clickRadiusSqr) {
				sortedTrees.remove(tempTreesB.get(j));
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
			g2d.setColor(LookAndFeel.COLOR_SOLID_TREE_LEAVES);
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
			g2d.setColor(LookAndFeel.COLOR_SOLID_TREE_LEAVES);
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
			viFireCursorBlack = createVolatileImage(clickRadius * 2 + 3, clickRadius * 2 + 3 + 15, Transparency.TRANSLUCENT);

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

			String string = "Type";
			
			// Fill mouse cursor
			if (leftClickAction == LEFT_ACTION_FIRE) {
				string = "Fire";
				g2d.setColor(LookAndFeel.COLOR_TRANSLUCENT_LENS_FIRE);
			} else if (leftClickAction == LEFT_ACTION_RAIN) {
				string = "Rain";
				g2d.setColor(LookAndFeel.COLOR_TRANSLUCENT_LENS_RAIN);
			} else if (leftClickAction == LEFT_ACTION_WIND) { 
				string = "Wind";
				g2d.setColor(LookAndFeel.COLOR_TRANSLUCENT_LENS_WIND);
			}
			g2d.fillOval(2, 2, clickRadius * 2 - 1, clickRadius * 2 - 1);
			
			// Draw center dot
			g2d.setColor(LookAndFeel.COLOR_SOLID_BLACK);
			g2d.fillOval(clickRadius - 1, clickRadius - 1, 5, 5);
			
			// Name of cursor
			g2d.setFont(new Font(g2d.getFont().getFontName(), Font.BOLD, g2d.getFont().getSize() + 2));
			int stringWidth = (int)
		            g2d.getFontMetrics().getStringBounds(string, g2d).getWidth();
			int offsetX = clickRadius - stringWidth/2;
			g2d.drawString(string, offsetX, clickRadius * 2 + 15);
			
			g2d.dispose();
		} while (viFireCursorBlack.contentsLost());
	}
	
	public void renderFireCursorRed() {
		do {
			viFireCursorRed = createVolatileImage(clickRadius * 2 + 3, clickRadius * 2 + 3, Transparency.TRANSLUCENT);

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
	
	public void makeCursorInvisible() {
		// Draw invisible cursor
		Toolkit toolkit = Toolkit.getDefaultToolkit();
	    Point hotSpot = new Point(0,0);
	    BufferedImage cursorImage = new BufferedImage(1, 1, BufferedImage.TRANSLUCENT); 
	    Cursor invisibleCursor = toolkit.createCustomCursor(cursorImage, hotSpot, "InvisibleCursor");        
	    setCursor(invisibleCursor);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		gameStats.updateGameStats();

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Draw land
		g.setColor(new Color(90, 170, 90));
		g.fillRect(0, 0, mapWidth, mapHeight);

		// Go back to black
		g.setColor(new Color(0,0,0));
		
		// For all trees
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
			int offsetX = jspForestFire.getHorizontalScrollBar().getValue() + (screenWidth - 40)/2 - stringWidth/2;
			
			int stringHeight = (int)
		            g2d.getFontMetrics().getStringBounds(string, g2d).getHeight();
			int offsetY = jspForestFire.getVerticalScrollBar().getValue() + (screenHeight - 40)/2 - stringHeight/2;
	        
			g.drawString(string, offsetX, offsetY);

			if (!dialogOpen) {
				g.setColor(LookAndFeel.COLOR_TRANSLUCENT_BLACK);
				g.fillRect(0, 0, mapWidth, mapHeight);
			}
		}
		
		// Draw weather objects
		if (timerReplay.isRunning() || timerSimulation.isRunning()) {
			// Lambda expression makes things easier
			weatherObjects.removeIf(wo -> !wo.isAlive());
			
			for (int i = 0; i < weatherObjects.size(); i++) {
				int xPosition = weatherObjects.get(i).getXPosition();
				int yPosition = weatherObjects.get(i).getYPosition();
				int xDestination = weatherObjects.get(i).getXDestination();
				int yDestination = weatherObjects.get(i).getYDestination();
				int radius = weatherObjects.get(i).getEffectRadius();
				
				// Draw WeatherObject
				g.setColor(weatherObjects.get(i).getLensColor());
				g.fillOval(xPosition - radius, yPosition - radius, radius * 2, radius * 2);
				
				// Draw WeatherObject border
				g.setColor(weatherObjects.get(i).getBorderColor());
				g.drawOval(xPosition - radius, yPosition - radius, radius * 2, radius * 2);
				
				// Draw direction line
				g.setColor(LookAndFeel.COLOR_SOLID_BLACK);
				g.drawLine(xPosition, yPosition, xDestination, yDestination);
		
				// Draw final destination (large dot)
				g.fillOval(xDestination - 5, yDestination - 5, 9, 9);
			}
		}
		
		// Mouse cursor
		if (mouseEntered) {
			if (fireCursorClicked) {
				g2d.drawImage(viFireCursorRed, mouseXPosition - clickRadius, mouseYPosition - clickRadius, this);
			} else {
				g2d.drawImage(viFireCursorBlack, mouseXPosition - clickRadius, mouseYPosition - clickRadius, this);
			}
		}
	}

	private void makeTrees(int sortMethod, int numTrees) {
		// Generate trees
		for (int i = 0; i < numTrees; i++) {
			int x = (int) (10 + Math.random() * (mapWidth - TREE_DIAMETER - 15));
			int y = (int) (10 + Math.random() * (mapHeight - 50));

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
		this.unpausedOnClick = clickUnpauses;
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
			ForestFire.selectedPopulation = POPULATION_XXXLARGE;
			break;
		case 2:
			ForestFire.selectedPopulation = POPULATION_XXLARGE;
			break;
		case 3:
			ForestFire.selectedPopulation = POPULATION_XLARGE;
			break;
		case 4:
			ForestFire.selectedPopulation = POPULATION_LARGE;
			break;
		case 5:
			ForestFire.selectedPopulation = POPULATION_MEDIUM;
			break;
		case 6:
			ForestFire.selectedPopulation = POPULATION_SMALL;
			break;
		case 7:
			ForestFire.selectedPopulation = POPULATION_XSMALL;
			break;
		case 8:
			ForestFire.selectedPopulation = POPULATION_XXSMALL;
			break;
		case 9:
			ForestFire.selectedPopulation = POPULATION_XXXSMALL;
			break;
		case 10:
			ForestFire.selectedPopulation = POPULATION_ULTRASMALL;
			break;
		default:
			System.out.println("No Action");
		}
		
		setMaxTrees(ForestFire.selectedPopulation);
	}
	
	public void setMaxTrees(double populationFactor) {
		maxTrees = (int) (populationFactor * (mapWidth * mapHeight));
		
		System.out.println("maxTrees (before rounding): " + maxTrees);
		
		// Round up maxTrees
		if (maxTrees >= 100) {
			maxTrees = (maxTrees + 50) / 100 * 100;
		} else if (maxTrees >= 10) {
			maxTrees = (maxTrees + 5) / 10 * 10;
		} else {
			maxTrees = 10;
		}
		System.out.println("maxTrees (after rounding): " + maxTrees);
	}
	
	public void emptyMap() {
		// I don't think I need to clear the burning trees
		if (!timerSimulation.isRunning() && !timerReplay.isRunning()) {
			
			// Create array of trees and sort them
			sortedTrees = new ArrayList<Tree>();
			makeTrees(2, 0);
			
			// Add neighboring trees to each tree
			TreeGrouper.buildTreeSets(sortedTrees);	
		}
	}
	
	public void fillTrees() {
		// I don't think I need to clear the burning trees
		if (!timerSimulation.isRunning() && !timerReplay.isRunning()) {
			
			// Create array of trees and sort them
			sortedTrees = new ArrayList<Tree>();
			makeTrees(2, maxTrees);
			
			// Add neighboring trees to each tree
			TreeGrouper.buildTreeSets(sortedTrees);	
		}
	}
	
	public void setMapSize(int mapWidth, int mapHeight) {
		// prevent width and height from being negative
		if (mapWidth < MIN_WIDTH) {
			mapWidth = MIN_WIDTH;
		}
		if (mapHeight < MIN_HEIGHT) {
			mapHeight = MIN_HEIGHT;
		}

		ForestFire.mapWidth = mapWidth;
		ForestFire.mapHeight = mapHeight;
		
		Dimension panelDimension = new Dimension(ForestFire.mapWidth + 40, ForestFire.mapHeight + 40);
		mapPanel.setLayout(new GridBagLayout());
		mapPanel.setPreferredSize(panelDimension);
		mapPanel.setSize(panelDimension);
		mapPanel.setMinimumSize(panelDimension);
		mapPanel.setMaximumSize(panelDimension);
		
		Dimension mapDimension = new Dimension(ForestFire.mapWidth, ForestFire.mapHeight);
		setPreferredSize(mapDimension);
		setSize(mapDimension);
		setMinimumSize(mapDimension);
		setMaximumSize(mapDimension);
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
				public void actionPerformed(ActionEvent ae) {
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
						
					replayMode = true;
					
					tick = 0;
					
					timerSimulation.stop();
					timerReplay.start();
					
					ignitedTrees.clear();
				
					for (Tree tree : sortedTrees) {
						tree.resetState();
					}

					paused = false;
					playing = true;
					stopped = false;
					
					sbPlay.setEnabled(true);
					sbPause.setEnabled(true);
					sbStop.setEnabled(true);
					sbReplay.setEnabled(false);
				}
			});
			
			sbSlow.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
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
				public void actionPerformed(ActionEvent ae) {
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
				public void actionPerformed(ActionEvent ae) {
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
				public void actionPerformed(ActionEvent ae) {
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
			playing = true;
			
			sbPlay.setEnabled(false);
			sbPause.setEnabled(true);
			sbStop.setEnabled(true);
		}
		
		public void pause() {
			paused = true;
			playing = false;
			
			sbPlay.setEnabled(true);
			sbPause.setEnabled(false);
			sbStop.setEnabled(true);
		}
		
		public void stop() {
			playing = false;
			
			if (!replayMode) {
				replayDuration = tick;
			}
			
			if (replayMode) {
				replayMode = false;
			}
			
			timerReplay.stop();
			timerSimulation.stop();
			
			ignitedTrees.clear();
			weatherObjects.clear();
			
			for (Tree tree : sortedTrees) {
				tree.resetState();
			}

			paused = false;
			stopped = true;
			
			sbPlay.setEnabled(false);
			sbPause.setEnabled(false);
			sbStop.setEnabled(false);
			sbReplay.setEnabled(true);
			
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
		private JComboBox<String> jcbFill = new JComboBox<String>();
		private StandardButton sbPen = new StandardButton("Pen", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
		private StandardButton sbBrush = new StandardButton("Brush", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
		private StandardButton sbRandom = new StandardButton("Random", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
		private StandardButton sbFill = new StandardButton("Fill", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
		private StandardButton sbFinish = new StandardButton("Finish", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
		
		public EditButtons() {					
			// See through
			setOpaque(false);

			// Fill options
			jcbFill.addItem("Tree");
			jcbFill.addItem("Land");
			jcbFill.addItem("Sea");
			
			// Buttons to edit map
			JPanel fillButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
			fillButtons.setOpaque(false);
			fillButtons.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), "Landscape Controls"));
			fillButtons.add(jcbFill);

			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel.setOpaque(false);
			FlowLayout layout = (FlowLayout) panel.getLayout();
			layout.setVgap(0);
			panel.add(sbPen);
			panel.add(sbBrush);
			panel.add(sbRandom);
			panel.add(sbFill);
			sbBrush.setEnabled(false);
			fillButtons.add(panel);
			
			jcbFill.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					String selection = (String) jcbFill.getSelectedItem();
					switch (selection) {
						case "Tree":
							makeType = MAKE_TREE;
							
							panel.removeAll();
							panel.add(sbPen);
							panel.add(sbBrush);
							panel.add(sbRandom);
							panel.add(sbFill);
							break;
						case "Land":
							makeType = MAKE_LAND;

							panel.removeAll();
							panel.add(sbBrush);
							panel.add(sbFill);
							break;
						case "Sea":
							makeType = MAKE_SEA;

							panel.removeAll();
							panel.add(sbBrush);
							panel.add(sbRandom);
							panel.add(sbFill);
							break;
						default:
							// No action
							System.out.println("NO ACTION");
					}
					paintType = PAINT_BRUSH;
					
					sbPen.setEnabled(true);
					sbBrush.setEnabled(false);
					sbRandom.setEnabled(true);
					sbFill.setEnabled(true);
					
					fillButtons.revalidate();
				}
			});
			
			// Exit edit mode
			JPanel otherButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
			
			otherButtons.setBackground(new Color(215,199,151));
			otherButtons.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), "Options"));
			otherButtons.add(sbFinish);
			
			// Listeners START
			sbPen.addActionListener(e -> {
				paintType = PAINT_PEN;
				
				sbPen.setEnabled(false);
				sbBrush.setEnabled(true);
				sbRandom.setEnabled(true);
				sbFill.setEnabled(true);
			});
			
			sbBrush.addActionListener(e -> {
				paintType = PAINT_BRUSH;
				
				sbPen.setEnabled(true);
				sbBrush.setEnabled(false);
				sbRandom.setEnabled(true);
				sbFill.setEnabled(true);
			});
			
			sbRandom.addActionListener(e -> {
				paintType = PAINT_RANDOM;
				
				sbPen.setEnabled(true);
				sbBrush.setEnabled(true);
				sbRandom.setEnabled(false);
				sbFill.setEnabled(true);
			});
			
			sbFill.addActionListener(e -> {
				int selection = jcbFill.getSelectedIndex();
				
				paintType = PAINT_FILL;
				
				sbPen.setEnabled(true);
				sbBrush.setEnabled(true);
				sbRandom.setEnabled(true);
				sbFill.setEnabled(false);
			});
			
			sbFinish.addActionListener(e -> {
				rebuildTreeSets();
				setEditMode(false);
			}); 
			// Listeners FINISH
			
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
			setSize(new Dimension(mapWidth, 20));
			setMinimumSize(new Dimension(mapWidth, 20));
			setMaximumSize(new Dimension(mapWidth, 20));		
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
					clickIgniteTrees(xClicked, yClicked);
					clickHistoryStack.remove(0);
				}				
				
				Set<Tree> newlyIgnitedTrees = new HashSet<Tree>();
				secondAltSequenceCounter++;

				if (secondAltSequenceCounter % (SEQUENCE_LENGTH_MULTIPLIED * 3) == 0) {
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
		
		public void setPercentPosition(int tick, int replayDuration) {
			percentPosition = (int) (100 * (float) tick / (float) replayDuration);
			xPosition = (int) (((float) percentPosition / 100f) * trackSlider.getWidth());

			if (xPosition > trackSlider.getWidth()) {
				xPosition = trackSlider.getWidth();
			}
		}
	}
	
	public ReplaySlider getReplaySlider() {
		return replaySlider;
	}
	
	public void setDialogOpen(boolean dialogOpen) {
		ForestFire.dialogOpen = dialogOpen;
	}
	
	class GameStats extends JWindow {
		// What is displayed depend on the mode
		private JPanel panel  = new JPanel();
		private JLabel jlPopulation = new JLabel();
//		private JLabel jlPopulationType = new JLabel();
		private JLabel jlMapSize = new JLabel();
		
		public GameStats() {
			setLayout(new GridBagLayout());
			panel.setOpaque(false);
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(jlPopulation);
			panel.add(jlMapSize);
			
			setSize(panel.getPreferredSize());
			setAlwaysOnTop(false);
			setBackground(LookAndFeel.COLOR_SOLID_JWINDOW);
			getRootPane().setBorder(new LineBorder(LookAndFeel.COLOR_SOLID_PANELS_BORDER, 3));
			add(panel);
			setLocation(10, 100);

			addMouseListener(new MouseAdapter() {
				boolean left = true;

				@Override
				public void mouseEntered(MouseEvent e) {
					left = !left;
					
					if (left) {
						setLocation(10, 100);
					} else {
						setLocation(ForestFire.screenWidth - gameStats.getWidth() - 10, 100);
					}
				}
			});
		}		
		
		public void updateGameStats() {
			jlPopulation.setText("Population: " + sortedTrees.size() + "/" + maxTrees);
			jlMapSize.setText("Map Size: " + mapWidth + "x" + mapHeight);

			Dimension dimension = new Dimension(getPreferredSize().width + 25, getPreferredSize().height + 25);
			setSize(dimension);
		}
	}
	
	public void hideGameStats(boolean hide) {
		if (hide) {
			gameStats.toBack();
		} else {
			gameStats.toFront();
		}
	}
	
	public void setGameStatsVisible(boolean visible) {
		gameStats.setVisible(visible);
	}
}