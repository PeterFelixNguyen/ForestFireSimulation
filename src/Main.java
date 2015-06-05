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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
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
import javax.swing.SpinnerListModel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jd.swing.custom.component.button.ButtonType;
import com.jd.swing.custom.component.button.StandardButton;
import com.jd.swing.util.Theme;

public class Main {
	// GUI Frame
	private static JFrame frame = new JFrame();
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
		
		// Optional
//		int widthPadding = 30;
//		int heightPadding = 150;
		
		// Setup frame
		frame.setTitle("Forest Fire by Peter \"Felix\" Nguyen");
		frame.setSize(ForestFire.width, ForestFire.height);
		frame.setMinimumSize(new Dimension(ForestFire.width, ForestFire.height));
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
		frame.getContentPane().setBackground(new Color(50,50,50));
		
		// Container to lay out the map and button bar
		JPanel fullInterface = new JPanel();
		fullInterface.setLayout(new BoxLayout(fullInterface, BoxLayout.Y_AXIS));
		fullInterface.add(forestFire.getUpperPanel());
		fullInterface.add(forestFire);
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
		JMenu jmHelp = new JMenu("Help");
		
		// Menu items (File)
		JMenuItem jmiNewMap = new JMenuItem("New Map");
		JMenuItem jmiEditMap = new JMenuItem("Edit Map");
		JMenuItem jmiSaveMap = new JMenuItem("Save Map");
		JMenuItem jmiLoadMap = new JMenuItem("Load Map");
		JMenuItem jmiExit = new JMenuItem("Exit Simulator");
		
		// Menu items (Options)
		JMenu jmView = new JMenu("View");
		JCheckBoxMenuItem jmiPositions = new JCheckBoxMenuItem("Tree Positions");
		jmiPositions.addItemListener(new ItemListener() {
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
		
		JCheckBoxMenuItem jmiHealth = new JCheckBoxMenuItem("Tree Health");
		jmiHealth.addItemListener(new ItemListener() {
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
		
		jmView.add(jmiPositions);
		jmView.add(jmiHealth);
		jmPlayback.add(jcbmiClickUnpause);

		JMenu jmFire = new JMenu("Fire");

		JPanel jpViewFire = new JPanel();
		jpViewFire.setLayout(new BoxLayout(jpViewFire, BoxLayout.Y_AXIS));
		
		CustomSlider clickSlider = new CustomSlider();
		clickSlider.setMajorTickSpacing(10);
		clickSlider.setMinorTickSpacing(5);
		clickSlider.setPaintTicks(true);
		clickSlider.setPaintLabels(true);
		clickSlider.setMinimum(10);
		clickSlider.setMaximum(30);
		clickSlider.setValue(30);
		clickSlider.setPreferredSize(new Dimension(200, 50));
		clickSlider.setSnapToTicks(true);
		clickSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent ce) {
		        JSlider source = (JSlider)ce.getSource();
		        
		        if (!source.getValueIsAdjusting()) {
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

		jmFire.add(jpViewFire);
		
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
		
		jdNewMap.setUndecorated(true);
		jdNewMap.setAlwaysOnTop(false);
		jdNewMap.setModalityType(ModalityType.APPLICATION_MODAL);
		jdNewMap.setModal(true);

		JPanel innerPanelNewMap = new JPanel();
		innerPanelNewMap.setLayout(new BoxLayout(innerPanelNewMap, BoxLayout.Y_AXIS));
		innerPanelNewMap.setBackground(LookAndFeel.COLOR_SOLID_PANELS);
		Font font = new Font(innerPanelNewMap.getFont().getFontName(), Font.BOLD, 14);
		CompoundBorder compoundBorder = new CompoundBorder(BorderFactory.createLineBorder(new Color(55, 50, 22), 4), BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), "Configure New Map", TitledBorder.CENTER, TitledBorder.CENTER, font));
		innerPanelNewMap.setBorder(compoundBorder);
		
		JComboBox<String> jcbPopulation = new JComboBox<String>();
		jcbPopulation.setMaximumRowCount(15);
		jcbPopulation.addItem("ULTRA LARGE");
		jcbPopulation.addItem("XXXX LARGE");
		jcbPopulation.addItem("XXX LARGE");
		jcbPopulation.addItem("XX LARGE");
		jcbPopulation.addItem("X LARGE");
		jcbPopulation.addItem("LARGE");
		jcbPopulation.addItem("MEDIUM");
		jcbPopulation.addItem("SMALL");
		jcbPopulation.addItem("X SMALL");
		jcbPopulation.addItem("XX SMALL");
		jcbPopulation.addItem("XXX SMALL");
		jcbPopulation.addItem("XXXX SMALL");
		jcbPopulation.addItem("ULTRA SMALL");
		jcbPopulation.setSelectedIndex(6);
		
		JComboBox<String> jcbMapSize = new JComboBox<String>();
		jcbMapSize.setMaximumRowCount(15);
		jcbMapSize.addItem("Automatic");
		jcbMapSize.addItem(ForestFire.RESOLUTION_WVGA);
		jcbMapSize.addItem(ForestFire.RESOLUTION_WSVGA);
		jcbMapSize.addItem(ForestFire.RESOLUTION_ASUS);
		jcbMapSize.addItem(ForestFire.RESOLUTION_ALIEN);
		jcbMapSize.setSelectedIndex(0);

		JPanel innerPanelNewMapRow1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		innerPanelNewMapRow1.setOpaque(false);
		JPanel innerPanelNewMapRow2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		innerPanelNewMapRow2.setOpaque(false);
		JPanel innerPanelNewMapRow3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		innerPanelNewMapRow3.setOpaque(false);
		JPanel innerPanelNewMapRow4 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		innerPanelNewMapRow4.setOpaque(false);
		JPanel innerPanelNewMapRow5 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		innerPanelNewMapRow5.setOpaque(false);
		
		StandardButton sbApply = new StandardButton("Apply", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
		StandardButton sbCancel = new StandardButton("Cancel", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);

		// Spinner
		String[] values = {"ULTRA SMALL", "XXXX SMALL", "XXX SMALL", "XX SMALL", "X SMALL", "SMALL", "MEDIUM", "LARGE", "X LARGE", "XX LARGE", "XXX LARGE", "XXXX LARGE", "ULTRA LARGE"};
		SpinnerListModel listModel = new SpinnerListModel(values);
		JSpinner jsPopulation = new JSpinner(listModel);
		jsPopulation.setValue(values[6]); // This should be a variable for the current value
		jsPopulation.setEditor(new JSpinner.DefaultEditor(jsPopulation));
		
//		jsPopulation.getEditor().setMinimumSize(new JLabel("ULTRA LARGE").getSize());
		Component mySpinnerEditor = jsPopulation.getEditor();
		JFormattedTextField jftf = ((JSpinner.DefaultEditor) mySpinnerEditor).getTextField();
		jftf.setColumns(8);
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
					if (jftfIndex == values.length - 1) {
						jftfIndex = 0;
					} else {
						jftfIndex++;
					}
				} else if (me.getButton() == MouseEvent.BUTTON3) {
					if (jftfIndex == 0) {
						jftfIndex = values.length - 1;
					} else {
						jftfIndex--;
					}
				}
				jsPopulation.setValue(values[jftfIndex]);
				// should set the variable's value
			}
		});
		
		innerPanelNewMapRow1.add(new JLabel("Tree Population"));
//		innerPanelNewMapRow2.add(jcbPopulation);
		innerPanelNewMapRow2.add(jsPopulation);
		innerPanelNewMapRow3.add(new JLabel("Map Size"));
		innerPanelNewMapRow4.add(jcbMapSize);
		innerPanelNewMapRow5.add(sbApply);
		innerPanelNewMapRow5.add(sbCancel);
		
		innerPanelNewMap.add(innerPanelNewMapRow1);
		innerPanelNewMap.add(innerPanelNewMapRow2);
		innerPanelNewMap.add(new JSeparator(SwingConstants.HORIZONTAL));
		innerPanelNewMap.add(innerPanelNewMapRow3);
		innerPanelNewMap.add(innerPanelNewMapRow4);
		innerPanelNewMap.add(new JSeparator(SwingConstants.HORIZONTAL));
		innerPanelNewMap.add(innerPanelNewMapRow5);
		innerPanelNewMap.add(Box.createVerticalGlue());
		
		JPanel jpNewMap = new JPanel();
		jpNewMap.setBackground(LookAndFeel.COLOR_TRANSLUCENT_BLACK);
		jpNewMap.setLayout(new GridBagLayout());
		jpNewMap.add(innerPanelNewMap, new GridBagConstraints());

		jdNewMap.setContentPane(jpNewMap);
		jdNewMap.setSize(ForestFire.width, ForestFire.height);
		
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
		
		JDialog jdAbout = new JDialog();
		
		// Transparency tweak START
		jdAbout.setUndecorated(true);
		jdAbout.getRootPane().setOpaque(false);
		jdAbout.getContentPane().setBackground(new Color(0, 0, 0, 0));
		jdAbout.setBackground(new Color(0, 0, 0, 0));
		// Transparency tweak FINISH
		
		jdAbout.setUndecorated(true);
		jdAbout.setAlwaysOnTop(false);
		jdAbout.setModalityType(ModalityType.APPLICATION_MODAL);
		jdAbout.setModal(true);
		
		AboutPanel aboutPanel = new AboutPanel();
		aboutPanel.setBorder(BorderFactory.createLineBorder(new Color(55, 50, 22), 4));
		aboutPanel.setBackground(LookAndFeel.COLOR_SOLID_PANELS);
		
		JPanel jpAbout = new JPanel(); 
		jpAbout.setBackground(LookAndFeel.COLOR_TRANSLUCENT_BLACK);
		jpAbout.setLayout(new GridBagLayout());
		jpAbout.add(aboutPanel, new GridBagConstraints());

		jdAbout.setContentPane(jpAbout);
		jdAbout.setSize(ForestFire.width, ForestFire.height);

		jpAbout.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent me) {
				forestFire.setDialogOpen(false);
				forestFire.repaint();
				jdAbout.setVisible(false);					
			}
			
		});
		
		sbApply.addActionListener(e -> {
//			int selectedPopulation = jcbPopulation.getSelectedIndex();
//			forestFire.setPopulationFactorAndSize(selectedPopulation);
//			int selectedMapSize = jcbPopulation.getSelectedIndex();
			
			// Temporary solution for JSpinner
			System.out.println("Value: " + (String)jsPopulation.getValue());
			
			int selectedIndex = 0;
			switch ((String)jsPopulation.getValue()) {
				case "ULTRA LARGE":
					selectedIndex = 0;
					break;
				case "XXXX LARGE":
					selectedIndex = 1;
					break;
				case "XXX LARGE":
					selectedIndex = 2;
					break;
				case "XX LARGE":
					selectedIndex = 3;
					break;
				case "X LARGE":
					selectedIndex = 4;
					break;
				case "LARGE":
					selectedIndex = 5;
					break;
				case "MEDIUM":
					selectedIndex = 6;
					break;
				case "SMALL":
					selectedIndex = 7;
					break;
				case "X SMALL":
					selectedIndex = 8;
					break;
				case "XX SMALL":
					selectedIndex = 9;
					break;
				case "XXX SMALL":
					selectedIndex = 10;
					break;
				case "XXXX SMALL":
					selectedIndex = 11;
					break;
				case "ULTRA SMALL":
					selectedIndex = 12;
					break;
			}
			forestFire.setPopulationFactorAndSize(selectedIndex);
			forestFire.setDialogOpen(false);

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
		jmOptions.add(jmFire);
		jmOptions.add(jmPlayback);
		jmHelp.add(jmiAbout);
		jmHelp.add(jmiTutorial);
		
		// Add menus to menu bar
		JMenuBar jmbForestFire = new JMenuBar();
		jmbForestFire.add(jmFile);
		jmbForestFire.add(jmOptions);
		jmbForestFire.add(jmHelp);
		
		// Set menu bar
		frame.setJMenuBar(jmbForestFire);
		
		// Display GUI
		frame.setVisible(true);
	}
}
