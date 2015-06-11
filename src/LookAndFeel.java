/**
 * Copyright 2015 Peter "Felix" Nguyen & Emmanuel Medina Lopez
 * 
 * Forest Fire Simulation with 2D Graphics.
 * 
 * LookAndFeel is a class that provides GUI "Look and Feel" constants 
 * to streamline the implementation of common GUI characterstics.
 * 
 * Note: this class could potentially be expanded with 
 *       implementations to change application theme 
 */
import java.awt.Color;

public class LookAndFeel {	
	// Universal colors
	public static final Color COLOR_SOLID_BLACK = new Color(0,0,0, 255);
	public static final Color COLOR_SOLID_WHITE = new Color(255,255,255, 255);

	// GUI colors
	public static final Color COLOR_SOLID_PANELS_BORDER = new Color(55, 50, 22, 255);
	public static final Color COLOR_SOLID_MAP_BORDER = new Color(20, 80, 40, 255);
	public static final Color COLOR_SOLID_PANELS = new Color(215, 199, 151, 255);
	public static final Color COLOR_SOLID_JWINDOW = new Color(215, 199, 151, 254);
	public static final Color COLOR_SOLID_DARK_TEXT = new Color(40, 40, 40, 255);
	public static final Color COLOR_TRANSLUCENT_BLACK = new Color(0, 0, 0, 127);
	
	// Game object colors
	public static final Color COLOR_SOLID_TREE_LEAVES = new Color(30, 150, 70, 255);
}
