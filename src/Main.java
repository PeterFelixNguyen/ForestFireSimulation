import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
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
		fullInterface.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
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
		
		jmView.add(jmiPositions);
		jmView.add(jmiHealth);
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
		clickSliderRow1.setBackground(LookAndFeel.COLOR_PANELS);
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
		burnSliderRow1.setBackground(LookAndFeel.COLOR_PANELS);
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
		jdNewMap.setUndecorated(true);
		jdNewMap.setAlwaysOnTop(false);
		jdNewMap.setModalityType(ModalityType.APPLICATION_MODAL);
		jdNewMap.setModal(true);
		JPanel jpNewMap = new JPanel();
		jpNewMap.setBackground(LookAndFeel.COLOR_BLACK);
				
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(LookAndFeel.COLOR_PANELS);
		
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

		JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row1.setOpaque(false);
		JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row2.setOpaque(false);
		JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row3.setOpaque(false);
		JPanel row4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row4.setOpaque(false);
		JPanel row5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		row5.setOpaque(false);
		
		StandardButton sbApply = new StandardButton("Apply", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);
		StandardButton sbCancel = new StandardButton("Cancel", ButtonType.BUTTON_ROUNDED_RECTANGLUR, Theme.STANDARD_BLUEGREEN_THEME, Theme.STANDARD_PALEBROWN_THEME, Theme.STANDARD_BLACK_THEME);

		row1.add(new JLabel("Tree Population"));
		row2.add(jcbPopulation);
		row3.add(new JLabel("Map Size"));
		row4.add(jcbMapSize);
		row5.add(sbApply);
		row5.add(sbCancel);
		
		panel.add(row1);
		panel.add(row2);
		panel.add(row3);
		panel.add(row4);
		panel.add(row5);
		panel.add(Box.createVerticalGlue());

		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), "Configure New Map"));

		jpNewMap.setLayout(new GridBagLayout());
		jpNewMap.add(panel, new GridBagConstraints());

		jdNewMap.setContentPane(jpNewMap);
		jdNewMap.setSize(ForestFire.width, ForestFire.height);
		
		sbApply.addActionListener(e -> {
			jdNewMap.setVisible(false);
		});
		
		
		sbCancel.addActionListener(e -> {
			jdNewMap.setVisible(false);
		});
		
		jmiNewMap.addActionListener(e -> {
			jdNewMap.setVisible(true);
		});
		
		jmiEditMap.addActionListener(e -> {
			forestFire.setEditMode(true);
			jmiEditMap.setEnabled(false);
		});
		
		// Append functionality with parent's behavior
		forestFire.getEditButtons().getFinishButton().addActionListener(e -> {
			jmiEditMap.setEnabled(true);
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
