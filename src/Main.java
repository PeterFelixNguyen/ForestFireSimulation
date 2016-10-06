/**
 * Copyright 2015 Peter "Felix" Nguyen & Emmanuel Medina Lopez
 * 
 * Forest Fire Simulation with 2D Graphics.
 * 
 * Main contains the main method and top-level GUI components
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import com.jd.swing.custom.component.button.ButtonType;
import com.jd.swing.custom.component.button.StandardButton;
import com.jd.swing.util.Theme;

public class Main {
	// GUI Frame
	private static JFrame frame = new JFrame();
	
	private static JPanel innerPanelNewMapRow4;
	private static JPanel sizePanel;
	private static JTextField jtfWidth;
	private static JTextField jtfHeight;
	private static StandardButton sbApply;
	private static boolean validWidth = false;
	private static boolean validHeight = false;
	
	// Need to refactor code (editMode)
	//private static boolean editMode = false;
	
	public static void main(String[] args) {		
		// Enable hardware acceleration
		System.setProperty("sun.java2d.opengl", "True");
		
		// Enable acceleration for translucent graphics
		System.setProperty("sun.java2d.translaccel", "True");
		System.setProperty("sun.java2d.ddforcevram", "True");
		
		// Verify acceleration enabled
		System.out.println("OpenGL = " + System.getProperty("sun.java2d.opengl"));

		ForestFire forestFire = new ForestFire();
		
		// Setup frame
		frame.setTitle("Forest Fire by Peter \"Felix\" Nguyen");
		frame.setSize(ForestFire.mapWidth, ForestFire.mapHeight);
		frame.setMinimumSize(new Dimension(ForestFire.mapWidth, ForestFire.mapHeight));
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
		frame.getContentPane().setBackground(new Color(50,50,50));
		
		// Container to lay out the map and button bar
		JPanel fullInterface = new JPanel();
		fullInterface.setLayout(new BoxLayout(fullInterface, BoxLayout.Y_AXIS));
		fullInterface.add(forestFire.getUpperPanel());
		fullInterface.add(ForestFire.jspForestFire); // Should not be static
		fullInterface.add(forestFire.getReplaySlider());
		
		// Add components to frame
		frame.add(Box.createHorizontalGlue()); // No longer required
		frame.add(fullInterface);
		frame.add(Box.createHorizontalGlue()); // No longer required

		// Maximize program screen
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Remove frame decoration
		frame.setUndecorated(true);
		
		// Menus
		JMenu jmFile = new JMenu("File");
		JMenu jmOptions = new JMenu("Options");
		JMenu jmGame = new JMenu("Game");
		JMenu jmHelp = new JMenu("Help");
		
		// Menu items (File)
		JMenuItem jmiNewMap = new JMenuItem("New Map");
		JMenuItem jmiEditMap = new JMenuItem("Edit Map");
		JMenuItem jmiSaveMap = new JMenuItem("Save Map");
		JMenuItem jmiLoadMap = new JMenuItem("Load Map");
		JMenuItem jmiExit = new JMenuItem("Exit Simulator");
		
		// Menu items (Options)
		JMenu jmView = new JMenu("View");
		JCheckBoxMenuItem jcbmiPositions = new JCheckBoxMenuItem("Tree Positions");
		jcbmiPositions.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					forestFire.setViewPositionEnabled(true);
					// Should re-work code as a method with argument
				} else {
					forestFire.setViewPositionEnabled(false);
				}
				
				forestFire.renderTreeO();
				forestFire.renderTreeA();
				
				forestFire.repaint();
			}
		});
		
		JCheckBoxMenuItem jcbmiHealth = new JCheckBoxMenuItem("Tree Health");
		jcbmiHealth.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					forestFire.setViewTreeHealthEnabled(true);
					// Should re-work code as a method with argument
				} else {
					forestFire.setViewTreeHealthEnabled(false);
				}

				forestFire.repaint();
			}
		});
		
		
		JCheckBoxMenuItem jcbmiGameStats = new JCheckBoxMenuItem("Game Stats");
		jcbmiGameStats.setSelected(true);
		jcbmiGameStats.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					forestFire.setGameStatsVisible(true);
				} else {
					forestFire.setGameStatsVisible(false);
				}
			}
		});
		
		JMenu jmPlayback = new JMenu("Playback");
		
		JCheckBoxMenuItem jcbmiClickUnpause = new JCheckBoxMenuItem("Click Unpauses");
		jcbmiClickUnpause.setSelected(true);
		jcbmiClickUnpause.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ie) {
				if (ie.getStateChange() == ItemEvent.SELECTED) {
					forestFire.setClickUnpauses(true);
					// Should re-work code as a method with argument
				} else {
					forestFire.setClickUnpauses(false);
				}
	
				forestFire.repaint();
			}
		});
		
		jmView.add(jcbmiPositions);
		jmView.add(jcbmiHealth);
		jmView.add(jcbmiGameStats);
		jmPlayback.add(jcbmiClickUnpause);

		JMenu jmTree = new JMenu("Tree");

		JPanel jpViewFire = new JPanel();
		jpViewFire.setLayout(new BoxLayout(jpViewFire, BoxLayout.Y_AXIS));
		
		CustomSlider clickSlider = new CustomSlider();
		clickSlider.setMajorTickSpacing(10);
		clickSlider.setPaintTicks(true);
		clickSlider.setPaintLabels(true);
		clickSlider.setMinimum(10);
		clickSlider.setMaximum(100);
		clickSlider.setValue(30);
		clickSlider.setPreferredSize(new Dimension(200, 50));
		clickSlider.setSnapToTicks(true);
		clickSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent ce) {
		        JSlider source = (JSlider)ce.getSource();
		        
		        if (!source.getValueIsAdjusting()) {
		        	// Note: redundancy
		            ForestFire.clickRadius = (int)source.getValue();
		            ForestFire.clickRadiusSqr = ForestFire.clickRadius * ForestFire.clickRadius;
		            forestFire.renderFireCursorBlack();
		            forestFire.renderFireCursorRed();
		        } 
			}
		});
		
		JPanel clickSliderContainer = new JPanel();
		clickSliderContainer.setLayout(new BoxLayout(clickSliderContainer, BoxLayout.Y_AXIS));		
		clickSliderContainer.setPreferredSize(new Dimension(250, 90));
		JPanel clickSliderRow1 = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
		clickSliderRow1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		clickSliderRow1.add(new JLabel("Click Radius"));
		clickSliderRow1.setBackground(LookAndFeel.COLOR_SOLID_PANELS);
		JPanel clickSliderRow2 = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
		
		clickSliderRow2.add(clickSlider);
		clickSliderContainer.add(clickSliderRow1);
		clickSliderContainer.add(clickSliderRow2);
		jpViewFire.add(clickSliderContainer);
		
		CustomSlider burnSlider = new CustomSlider();
		burnSlider.setMajorTickSpacing(10);
		burnSlider.setMinorTickSpacing(5);
		burnSlider.setPaintTicks(true);
		burnSlider.setPaintLabels(true);
		burnSlider.setMinimum(10);
		burnSlider.setMaximum(40);
		burnSlider.setValue(30);
		burnSlider.setPreferredSize(new Dimension(200, 50));
		burnSlider.setSnapToTicks(true);
		burnSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent ce) {
		        JSlider source = (JSlider)ce.getSource();
		        if (!source.getValueIsAdjusting()) {
		            ForestFire.burnRadius = (int)source.getValue();
		            ForestFire.burnRadiusSqr = ForestFire.burnRadius * ForestFire.burnRadius;
		            // Re-calculate nearbyTrees
		            //TreeGrouper.buildTreeSets(trees);
		            //forestFire.rebuildTreeSets();
		            
		            // Unfinished
		        }
			}
		});
		
		JPanel burnSliderContainer = new JPanel();
		burnSliderContainer.setLayout(new BoxLayout(burnSliderContainer, BoxLayout.Y_AXIS));		
		burnSliderContainer.setPreferredSize(new Dimension(250, 90));
		JPanel burnSliderRow1 = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
		burnSliderRow1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		burnSliderRow1.add(new JLabel("Burn Radius"));
		burnSliderRow1.setBackground(LookAndFeel.COLOR_SOLID_PANELS);
		JPanel burnSliderRow2 = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
		
		burnSliderRow2.add(burnSlider);
		burnSliderContainer.add(burnSliderRow1);
		burnSliderContainer.add(burnSliderRow2);
		jpViewFire.add(burnSliderContainer);

		jmTree.add(jpViewFire);
		
		// Menu Items (Game)
		JMenu jmWeather = new JMenu("Weather");
		JMenu jmWildLife = new JMenu("Wild Life");
		JMenu jmStructures = new JMenu("Structures");
		
		JMenuItem jmiRain = new JMenuItem("Rain");
		JMenuItem jmiWind = new JMenuItem("Wind");
		JMenuItem jmiTornado = new JMenuItem("Tornado");
		JMenuItem jmiLightning = new JMenuItem("Lightning");
		JMenuItem jmiSnow = new JMenuItem("Snow");
		JMenuItem jmiCombo = new JMenuItem("Combo");
		
		jmWeather.add(jmiRain);
		jmWeather.add(jmiWind);
		jmWeather.add(jmiTornado);
		jmWeather.add(jmiLightning);
		jmWeather.add(jmiSnow);
		jmWeather.add(jmiCombo);
		
		// Event handling for GAME menu ONLY start
		jmiRain.addActionListener(e -> {
			forestFire.startSimulationTimer();
			forestFire.setLeftClickAction(ForestFire.LEFT_ACTION_RAIN);
		});
		
		jmiWind.addActionListener(e -> {
			forestFire.startSimulationTimer();
			forestFire.setLeftClickAction(ForestFire.LEFT_ACTION_WIND);
		});
		
		// Event handling for GAME menu ONLY finish

		// Menu items (Help)
		JMenuItem jmiAbout = new JMenuItem("About");
		JMenuItem jmiTutorial = new JMenuItem("Tutorial");
		
		// Add functionality to menu items START
		JDialog jdNewMap = new JDialog();
		
		// Transparency tweak START
		jdNewMap.setUndecorated(true);
		jdNewMap.getRootPane().setOpaque(false);
		jdNewMap.getContentPane().setBackground(new Color(0, 0, 0, 0));
		jdNewMap.setBackground(new Color(0, 0, 0, 0));
		// Transparency tweak FINISH
		
		jdNewMap.setAlwaysOnTop(false);
		jdNewMap.setModalityType(ModalityType.APPLICATION_MODAL);
		jdNewMap.setModal(true);

		JPanel innerPanelNewMap = new JPanel();
		innerPanelNewMap.setLayout(new BoxLayout(innerPanelNewMap, BoxLayout.Y_AXIS));
		innerPanelNewMap.setBackground(LookAndFeel.COLOR_SOLID_PANELS);
		Font font = new Font(innerPanelNewMap.getFont().getFontName(), Font.BOLD, 14);
		CompoundBorder compoundBorder = new CompoundBorder(BorderFactory.createLineBorder(LookAndFeel.COLOR_SOLID_PANELS_BORDER, 4), BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), "Configure New Map", TitledBorder.CENTER, TitledBorder.CENTER, font));
		innerPanelNewMap.setBorder(compoundBorder);
		
		JPanel innerPanelNewMapRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		innerPanelNewMapRow1.setOpaque(false);
		JPanel innerPanelNewMapRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		innerPanelNewMapRow2.setOpaque(false);
		JPanel innerPanelNewMapRow3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		innerPanelNewMapRow3.setOpaque(false);
		innerPanelNewMapRow4 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		innerPanelNewMapRow4.setOpaque(false);
		JPanel innerPanelNewMapRow5 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		innerPanelNewMapRow5.setOpaque(false);
		
		JComboBox<String> jcbMapSize = new JComboBox<String>();
		jcbMapSize.setFont(new Font("Arial", Font.BOLD, 16));
		jcbMapSize.setPreferredSize(new Dimension(200, 30));
		jcbMapSize.setMaximumRowCount(15);
		jcbMapSize.addItem("Custom size");
		jcbMapSize.addItem("Fit to screen");
		jcbMapSize.addItem(ForestFire.RESOLUTION_WVGA);
		jcbMapSize.addItem(ForestFire.RESOLUTION_WSVGA);
		jcbMapSize.addItem(ForestFire.RESOLUTION_720P);
		jcbMapSize.addItem(ForestFire.RESOLUTION_SXGA);
		jcbMapSize.addItem(ForestFire.RESOLUTION_HDPLUS);
		jcbMapSize.addItem(ForestFire.RESOLUTION_UXGA);
		jcbMapSize.addItem(ForestFire.RESOLUTION_FHD);
		jcbMapSize.addItem(ForestFire.RESOLUTION_WQXGA);
		jcbMapSize.addItem("3000x3000");
		
		((JLabel) jcbMapSize.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

		jcbMapSize.setSelectedIndex(1);

		jcbMapSize.addItemListener(e -> {
			if(e.getItem().equals("Custom size")) {
				sbApply.setEnabled(false);
				jtfWidth.setText("");
				jtfHeight.setText("");
				validWidth = false;
				validHeight = false;
				innerPanelNewMapRow4.add(sizePanel);
				innerPanelNewMapRow4.repaint();
				innerPanelNewMapRow4.revalidate();
			} else {
				sbApply.setEnabled(true);
				innerPanelNewMapRow4.remove(sizePanel);
			}
		});
		sizePanel = new JPanel();
		sizePanel.setOpaque(false);
		sizePanel.add(new JLabel(" W:"));
		jtfWidth = new JTextField(4);
		sizePanel.add(jtfWidth);
		sizePanel.add(new JLabel(" H:"));
		jtfHeight = new JTextField(4);
		sizePanel.add(jtfHeight);
		
		AbstractDocument docWidth = (AbstractDocument) jtfWidth.getDocument();
		AbstractDocument docHeight = (AbstractDocument) jtfHeight.getDocument();
	
		docWidth.setDocumentFilter(new NumberFilter());
		docHeight.setDocumentFilter(new NumberFilter());
		
		jtfWidth.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent de) {
				// TODO Auto-generated method stub
			}

			@Override
			public void insertUpdate(DocumentEvent de) {
				try {
					if (Integer.valueOf(jtfWidth.getText()) < 100) {
						sbApply.setEnabled(false);
						validWidth = false;
					} else {
						validWidth = true;
						if (validHeight) {
							sbApply.setEnabled(true);
						}
					}					
				} catch (NumberFormatException ex) {
					validWidth = false;
					sbApply.setEnabled(false);
				}
			}

			@Override
			public void removeUpdate(DocumentEvent de) {
				try {
					if (Integer.valueOf(jtfWidth.getText()) < 100) {
						sbApply.setEnabled(false);
						validWidth = false;
					} else {
						validWidth = true;
						if (validHeight) {
							sbApply.setEnabled(true);
						}
					}					
				} catch (NumberFormatException ex) {
					validWidth = false;
					sbApply.setEnabled(false);
				}
			}
		});
		
		jtfHeight.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent de) {
				// TODO Auto-generated method stub
			}

			@Override
			public void insertUpdate(DocumentEvent de) {
				try {
					if (Integer.valueOf(jtfHeight.getText()) < 100) {
						sbApply.setEnabled(false);
						validHeight = false;
					} else {
						validHeight = true;
						if (validWidth) {
							sbApply.setEnabled(true);
						}
					}					
				} catch (NumberFormatException ex) {
					validHeight = false;
					sbApply.setEnabled(false);
				}
			}

			@Override
			public void removeUpdate(DocumentEvent de) {
				try {
					if (Integer.valueOf(jtfHeight.getText()) < 100) {
						sbApply.setEnabled(false);
						validHeight = false;
					} else {
						validHeight = true;
						if (validWidth) {
							sbApply.setEnabled(true);
						}
					}					
				} catch (NumberFormatException ex) {
					validHeight = false;
					sbApply.setEnabled(false);
				}
			}
		});
		
		sbApply = new StandardButton("Apply", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
		StandardButton sbCancel = new StandardButton("Cancel", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);

		// Spinner (Population)
		String[] values = {"ULTRA SMALL", "XXX SMALL", "XX SMALL", "X SMALL", "SMALL", "MEDIUM", "LARGE", "X LARGE", "XX LARGE", "XXX LARGE", "ULTRA LARGE"};
		SpinnerListModel listModel = new SpinnerListModel(values);
		JSpinner jsPopulation = new JSpinner(listModel);
		jsPopulation.setPreferredSize(new Dimension(200, 30));
		jsPopulation.setValue(values[5]); // This should be a variable for the current value
		JSpinner.DefaultEditor editor = new JSpinner.DefaultEditor(jsPopulation);
		editor.getTextField().setFont(new Font("Aria", Font.BOLD, 16));
		jsPopulation.setEditor(editor);
		
		Component mySpinnerEditor = jsPopulation.getEditor();
		JFormattedTextField jftf = ((JSpinner.DefaultEditor) mySpinnerEditor).getTextField();
		jftf.setColumns(15);
		jftf.setHorizontalAlignment(SwingConstants.CENTER);
		
		// Remove all listeners to this component so selection is disabled
		MouseMotionListener[] mouseMotionListeners = jftf.getMouseMotionListeners();
		for (int i = 0; i < mouseMotionListeners.length; i ++) {
			jftf.removeMouseMotionListener(mouseMotionListeners[i]);
		}
		
		MouseListener[] mouseListeners = jftf.getMouseListeners();
		for (int i = 0; i < mouseListeners.length; i ++) {
			jftf.removeMouseListener(mouseListeners[i]);
		}
		
		// Add a special listener to this component
		jftf.addMouseListener(new MouseAdapter() {
			int jftfIndex = 6; // should be a variable instead of literal

			@Override
			public void mousePressed(MouseEvent me) {
				if (me.getButton() == MouseEvent.BUTTON1) {
					if (jftfIndex < values.length - 1) {
						jftfIndex++;
					}
				} else if (me.getButton() == MouseEvent.BUTTON3) {
					if (jftfIndex > 0) {
						jftfIndex--;
					}
				}
				jsPopulation.setValue(values[jftfIndex]);
				// should set the variable's value
			}
		});

		// Spinner (Preset)
		String[] values2 = {"Create random map", "Create empty map"};
		SpinnerListModel listModel2 = new SpinnerListModel(values2);
		JSpinner jsPopulation2 = new JSpinner(listModel2);		
		jsPopulation2.setPreferredSize(new Dimension(200, 30));
		jsPopulation2.setValue(values2[0]); // This should be a variable for the current value
		JSpinner.DefaultEditor editor2 = new JSpinner.DefaultEditor(jsPopulation2);
		editor2.getTextField().setFont(new Font("Aria", Font.BOLD, 16));
		jsPopulation2.setEditor(editor2);
				
		Component mySpinnerEditor2 = jsPopulation2.getEditor();
		JFormattedTextField jftf2 = ((JSpinner.DefaultEditor) mySpinnerEditor2).getTextField();
		jftf2.setColumns(15);
		jftf2.setHorizontalAlignment(SwingConstants.CENTER);
		
		// Remove all listeners to this component so selection is disabled
		MouseMotionListener[] mouseMotionListeners2 = jftf2.getMouseMotionListeners();
		for (int i = 0; i < mouseMotionListeners2.length; i ++) {
			jftf2.removeMouseMotionListener(mouseMotionListeners2[i]);
		}
		
		MouseListener[] mouseListeners2 = jftf2.getMouseListeners();
		for (int i = 0; i < mouseListeners2.length; i ++) {
			jftf2.removeMouseListener(mouseListeners2[i]);
		}
		
		// Add a special listener to this component
		jftf2.addMouseListener(new MouseAdapter() {
			int jftfIndex = 0; // should be a variable instead of literal

			@Override
			public void mousePressed(MouseEvent me) {
				if (me.getButton() == MouseEvent.BUTTON1) {
					if (jftfIndex < values2.length - 1) {
						jftfIndex++;
					}
				} else if (me.getButton() == MouseEvent.BUTTON3) {
					if (jftfIndex > 0) {
						jftfIndex--;
					}
				}
				jsPopulation2.setValue(values2[jftfIndex]);
				// should set the variable's value
				System.out.println("hello world");
			}
		});
		
		JCheckBoxMenuItem jcbmiFillMap = new JCheckBoxMenuItem("Fill Map");
		jcbmiFillMap.setOpaque(false);
		
		JLabel jlPopulation = new JLabel("Tree Population");
		jlPopulation.setPreferredSize(new Dimension(130, 30));
		jlPopulation.setHorizontalAlignment(SwingConstants.RIGHT);
		jlPopulation.setFont(new Font("Arial", Font.BOLD, 16));
		innerPanelNewMapRow1.add(jlPopulation);
		innerPanelNewMapRow1.add(jsPopulation);
		
		JLabel jlPreset = new JLabel("Map Preset");
		jlPreset.setPreferredSize(new Dimension(130, 30));
		jlPreset.setHorizontalAlignment(SwingConstants.RIGHT);
		jlPreset.setFont(new Font("Arial", Font.BOLD, 16));
		innerPanelNewMapRow2.add(jlPreset);
		innerPanelNewMapRow2.add(jsPopulation2);

		JLabel jlMapSize = new JLabel("Map Size");
		jlMapSize.setPreferredSize(new Dimension(130, 20));
		jlMapSize.setHorizontalAlignment(SwingConstants.RIGHT);
		jlMapSize.setFont(new Font("Arial", Font.BOLD, 16));
		innerPanelNewMapRow3.add(jlMapSize);
		innerPanelNewMapRow3.add(jcbMapSize);
		innerPanelNewMapRow5.add(sbApply);
		innerPanelNewMapRow5.add(sbCancel);
		
		innerPanelNewMap.add(innerPanelNewMapRow1);
		innerPanelNewMap.add(innerPanelNewMapRow2);
		innerPanelNewMap.add(innerPanelNewMapRow3);
		innerPanelNewMap.add(innerPanelNewMapRow4);
		innerPanelNewMap.add(innerPanelNewMapRow5);
		innerPanelNewMap.add(new JSeparator(SwingConstants.HORIZONTAL));
		innerPanelNewMap.add(Box.createVerticalGlue());
		
		JPanel jpNewMap = new JPanel();
		jpNewMap.setBackground(LookAndFeel.COLOR_TRANSLUCENT_BLACK);
		jpNewMap.setLayout(new GridBagLayout());
		jpNewMap.add(innerPanelNewMap, new GridBagConstraints());

		jdNewMap.setContentPane(jpNewMap);
		jdNewMap.setSize(ForestFire.screenWidth, ForestFire.screenHeight);
		
		jpNewMap.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent me) {
				forestFire.setDialogOpen(false);
				forestFire.repaint();
				jdNewMap.setVisible(false);					
			}
			
		});
		
		innerPanelNewMap.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent me) {
				// Do nothing
				// This overrides the mouse listener in the parent (jpNewMap)
				// Without this, clicking the child component will trigger setVisible(false)
			}
		});
		
		JDialog jdChooseSize = new JDialog();
		
		// Transparency tweak START
		jdChooseSize.setUndecorated(true);
		jdChooseSize.getRootPane().setOpaque(false);
		jdChooseSize.getContentPane().setBackground(new Color(0, 0, 0, 0));
		jdChooseSize.setBackground(new Color(0, 0, 0, 0));
		// Transparency tweak FINISH
		
		jdChooseSize.setAlwaysOnTop(false);
		jdChooseSize.setModalityType(ModalityType.APPLICATION_MODAL);
		jdChooseSize.setModal(true);
		
		JPanel innerPanelChooseSize = new JPanel();
		innerPanelChooseSize.setLayout(new BoxLayout(innerPanelChooseSize, BoxLayout.Y_AXIS));
		innerPanelChooseSize.setBackground(LookAndFeel.COLOR_SOLID_PANELS);
		Font font2 = new Font(innerPanelNewMap.getFont().getFontName(), Font.BOLD, 14);
		CompoundBorder compoundBorder2 = new CompoundBorder(BorderFactory.createLineBorder(LookAndFeel.COLOR_SOLID_PANELS_BORDER, 4), BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), "Configure New Map", TitledBorder.CENTER, TitledBorder.CENTER, font2));
		innerPanelChooseSize.setBorder(compoundBorder2);
		
		innerPanelChooseSize.add(new JLabel("Choose Map Size"));
		
		JPanel jpChooseSize = new JPanel();
		jpChooseSize.setBackground(LookAndFeel.COLOR_TRANSLUCENT_BLACK);
		jpChooseSize.setLayout(new GridBagLayout());
		jpChooseSize.add(innerPanelChooseSize, new GridBagConstraints());

		jdChooseSize.setContentPane(jpChooseSize);
		jdChooseSize.setSize(ForestFire.screenWidth, ForestFire.screenHeight);
		
		/***
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 */
		
		JDialog jdAbout = new JDialog();
		
		// Transparency tweak START
		jdAbout.setUndecorated(true);
		jdAbout.getRootPane().setOpaque(false);
		jdAbout.getContentPane().setBackground(new Color(0, 0, 0, 0));
		jdAbout.setBackground(new Color(0, 0, 0, 0));
		// Transparency tweak FINISH
		
		jdAbout.setAlwaysOnTop(false);
		jdAbout.setModalityType(ModalityType.APPLICATION_MODAL);
		jdAbout.setModal(true);
		
		AboutPanel aboutPanel = new AboutPanel();
		aboutPanel.setBorder(BorderFactory.createLineBorder(LookAndFeel.COLOR_SOLID_PANELS_BORDER, 4));
		aboutPanel.setBackground(LookAndFeel.COLOR_SOLID_PANELS);
		
		JPanel jpAbout = new JPanel(); 
		jpAbout.setBackground(LookAndFeel.COLOR_TRANSLUCENT_BLACK);
		jpAbout.setLayout(new GridBagLayout());
		jpAbout.add(aboutPanel, new GridBagConstraints());

		jdAbout.setContentPane(jpAbout);
		jdAbout.setSize(ForestFire.screenWidth, ForestFire.screenHeight);

		jpAbout.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent me) {
				forestFire.setDialogOpen(false);
				forestFire.repaint();
				jdAbout.setVisible(false);					
			}
			
		});
		
		sbApply.addActionListener(e -> {			
			// Temporay solution for MapSize
			switch ((String)jcbMapSize.getSelectedItem()) {
				case "Custom size":
					forestFire.setMapSize(Integer.valueOf(jtfWidth.getText()), Integer.valueOf(jtfHeight.getText()));
					break;
				case "Fit to Screen":
					forestFire.setMapSize(ForestFire.screenWidth, ForestFire.screenHeight);
					break;
				case ForestFire.RESOLUTION_WVGA:
					forestFire.setMapSize(800, 480);
					break;
				case ForestFire.RESOLUTION_WSVGA:
					forestFire.setMapSize(1024, 600);
					break;
				case ForestFire.RESOLUTION_720P:
					forestFire.setMapSize(1280, 720);
					break;
				case ForestFire.RESOLUTION_SXGA:
					forestFire.setMapSize(1280, 1024);
					break;
				case ForestFire.RESOLUTION_HDPLUS:
					forestFire.setMapSize(1600, 900);
					break;
				case ForestFire.RESOLUTION_UXGA:
					forestFire.setMapSize(1600, 1200);
					break;
				case ForestFire.RESOLUTION_FHD:
					forestFire.setMapSize(1920, 1080);
					break;
				case ForestFire.RESOLUTION_WQXGA:
					forestFire.setMapSize(2560, 1600);
					break;
				case "3000x3000":
					forestFire.setMapSize(3000, 3000);
					break;
				default:
					forestFire.setMapSize(ForestFire.screenWidth, ForestFire.screenHeight);
			}
			
			// Temporary solution for Population			
			int selectedIndex = 0;
			switch ((String)jsPopulation.getValue()) {
				case "ULTRA LARGE":
					selectedIndex = 0;
					break;
				case "XXX LARGE":
					selectedIndex = 1;
					break;
				case "XX LARGE":
					selectedIndex = 2;
					break;
				case "X LARGE":
					selectedIndex = 3;
					break;
				case "LARGE":
					selectedIndex = 4;
					break;
				case "MEDIUM":
					selectedIndex = 5;
					break;
				case "SMALL":
					selectedIndex = 6;
					break;
				case "X SMALL":
					selectedIndex = 7;
					break;
				case "XX SMALL":
					selectedIndex = 8;
					break;
				case "XXX SMALL":
					selectedIndex = 9;
					break;
				case "ULTRA SMALL":
					selectedIndex = 10;
					break;
			}
			
			forestFire.setPopulationFactorAndSize(selectedIndex);
			forestFire.setDialogOpen(false);
			forestFire.setEditMode(true);
			
			if (jsPopulation2.getValue().equals("Create random map")) {
				forestFire.fillTrees();				
			} else {
				forestFire.emptyMap();
			}
			jdNewMap.setVisible(false);
		});
		
		sbCancel.addActionListener(e -> {
			forestFire.setDialogOpen(false);
			jdNewMap.setVisible(false);
		});
		
		jmiNewMap.addActionListener(e -> {
			forestFire.setDialogOpen(true);
			forestFire.getMapButtons().pause();
			jdNewMap.setVisible(true); // should set on timer
		});
		
		jmiEditMap.addActionListener(e -> {
			forestFire.setEditMode(true);
			jmiEditMap.setEnabled(false);
		});
		
		// Append functionality with parent's behavior
		forestFire.getEditButtons().getFinishButton().addActionListener(e -> {
			jmiEditMap.setEnabled(true);
		});
		
		jmiAbout.addActionListener(e -> {
			forestFire.setDialogOpen(true);
			forestFire.getMapButtons().pause();
			jdAbout.setVisible(true); // should set on timer
		});

		jmiSaveMap.addActionListener(e -> {
			JFileChooser jfcSave = new JFileChooser();
			jfcSave.showSaveDialog(null);
		});

		jmiLoadMap.addActionListener(e -> {
			JFileChooser jfcLoad = new JFileChooser();
			jfcLoad.showOpenDialog(null);
		});
		
		jmiExit.addActionListener(e -> {
			System.exit(0);
		});
		// Add functionality to menu items FINISH
		
		// Add items to menus
		jmFile.add(jmiNewMap);
		jmFile.add(jmiEditMap);
		jmFile.addSeparator();
		jmFile.add(jmiSaveMap);
		jmFile.add(jmiLoadMap);
		jmFile.addSeparator();
		jmFile.add(jmiExit);
		jmOptions.add(jmView);
		jmOptions.add(jmTree);
		jmOptions.add(jmPlayback);

		jmGame.add(jmWeather);
		jmGame.add(jmWildLife);
		jmGame.add(jmStructures);
		jmHelp.add(jmiAbout);
		jmHelp.add(jmiTutorial);
		
		// Add menus to menu bar
		JMenuBar jmbForestFire = new JMenuBar();
		jmbForestFire.add(jmFile);
		jmbForestFire.add(jmOptions);
		jmbForestFire.add(jmGame);
		jmbForestFire.add(jmHelp);

		jmFile.addMenuListener(new MenuListener() {
			
			@Override
			public void menuSelected(MenuEvent e) {
				forestFire.hideGameStats(true);
			}
			
			@Override
			public void menuDeselected(MenuEvent e) {
				forestFire.hideGameStats(false);
			}
			
			@Override
			public void menuCanceled(MenuEvent e) {
			}
		});
		
		jmOptions.addMenuListener(new MenuListener() {
			
			@Override
			public void menuSelected(MenuEvent e) {
				forestFire.hideGameStats(true);
			}
			
			@Override
			public void menuDeselected(MenuEvent e) {
				forestFire.hideGameStats(false);
			}
			
			@Override
			public void menuCanceled(MenuEvent e) {
			}
		});
		
		jmHelp.addMenuListener(new MenuListener() {
			
			@Override
			public void menuSelected(MenuEvent e) {
				forestFire.hideGameStats(true);
			}
			
			@Override
			public void menuDeselected(MenuEvent e) {
				forestFire.hideGameStats(false);
			}
			
			@Override
			public void menuCanceled(MenuEvent e) {
			}
		});
		
		jmGame.addMenuListener(new MenuListener() {
			
			@Override
			public void menuSelected(MenuEvent e) {
				forestFire.hideGameStats(true);
			}
			
			@Override
			public void menuDeselected(MenuEvent e) {
				forestFire.hideGameStats(false);
			}
			
			@Override
			public void menuCanceled(MenuEvent e) {
			}
		});
		
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowIconified(WindowEvent e) {
        		forestFire.setGameStatsVisible(false);
            }
            @Override
            public void windowDeiconified(WindowEvent e) {
        		forestFire.setGameStatsVisible(true);
            }
            @Override
            public void windowDeactivated(WindowEvent e) {
        		forestFire.setGameStatsVisible(false);
            }
            @Override
            public void windowActivated(WindowEvent e) {
        		forestFire.setGameStatsVisible(true);
            }
        });
		
		// Set menu bar
		frame.setJMenuBar(jmbForestFire);
		
		// Display GUI
		frame.setVisible(true);
		forestFire.setGameStatsVisible(true);
		forestFire.hideGameStats(false);
	}
	
    /*
     * Regular Expression tool:
     * 
     * http://utilitymill.com/utility/Regex_For_Range
     */
    static class NumberFilter extends DocumentFilter {
        private Pattern pattern;

        public NumberFilter() {
            pattern = Pattern.compile("([0-9]{1,4}|10000)");
        }

        // manual invocation
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            String newStr = fb.getDocument().getText(0, fb.getDocument().getLength()) + string;
            Matcher m = pattern.matcher(newStr);

            if (m.matches()) {
                super.insertString(fb, offset, string, attr);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        // automatic invocation
        @Override
        public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
            try {
            	
            } catch (NumberFormatException ex) {
            	System.out.println("EXCEPTION FOUND");
            }
        	super.remove(fb, offset, length);
        }

        // automatic invocation
        @Override
        public void replace(FilterBypass fb, int offset, int length, String string, AttributeSet attr) throws BadLocationException {
            if (length > 0) {
                fb.remove(offset, length);
            }

            insertString(fb, offset, string, attr);
        }
    }
}
