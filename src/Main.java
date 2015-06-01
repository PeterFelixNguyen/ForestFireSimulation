import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Main {
	// GUI Frame
	private static JFrame frame = new JFrame();
	// Need to refactor code (editMode)
	//private static boolean editMode = false;
	
	@SuppressWarnings("unused")
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
		int widthPadding = 30;
		int heightPadding = 150;
		
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
		frame.add(Box.createHorizontalGlue());
		frame.add(fullInterface);
		frame.add(Box.createHorizontalGlue());

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
		jmView.add(jmiPositions);
		jmView.add(jmiHealth);
		JMenu jmFire = new JMenu("Fire");
		JMenuItem jmBurnRadius = new JMenuItem("Burn Radius");
		JMenuItem jmClickRadius = new JMenuItem("Click Radius");
		
		JPanel jpViewFire = new JPanel(new GridLayout(0,1));

		CustomSlider clickSlider = new CustomSlider();
		clickSlider.setMajorTickSpacing(10);
		clickSlider.setMinorTickSpacing(5);
		clickSlider.setPaintTicks(true);
		clickSlider.setPaintLabels(true);
		clickSlider.setMinimum(10);
		clickSlider.setMaximum(30);
		clickSlider.setValue(30);
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
		
		jpViewFire.add(new JLabel("  Click Radius"));
		jpViewFire.add(clickSlider);

		CustomSlider burnSlider = new CustomSlider();
		burnSlider.setMajorTickSpacing(10);
		burnSlider.setMinorTickSpacing(5);
		burnSlider.setPaintTicks(true);
		burnSlider.setPaintLabels(true);
		burnSlider.setMinimum(10);
		burnSlider.setMaximum(40);
		burnSlider.setValue(30);
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
		
		jpViewFire.add(new JLabel("  Burn Radius"));
		jpViewFire.add(burnSlider);

		jmFire.add(jpViewFire);

		// Menu items (Help)
		JMenuItem jmiAbout = new JMenuItem("About");
		JMenuItem jmiTutorial = new JMenuItem("Tutorial");
		
		// Add functionality to menu items
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
