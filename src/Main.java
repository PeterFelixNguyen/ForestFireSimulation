import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

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
		fullInterface.add(forestFire.getMapButtons());
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
		JMenuItem jmiEdit = new JMenuItem("Edit Map");
		JMenuItem jmiSave = new JMenuItem("Save Map");
		JMenuItem jmiLoad = new JMenuItem("Load Map");
		JMenuItem jmiExit = new JMenuItem("Exit Simulator");
		
		// Menu items (Options)
		JMenu jmView = new JMenu("View");
		JCheckBoxMenuItem jmiPositions = new JCheckBoxMenuItem("Tree Positions");
		JCheckBoxMenuItem jmiHealth = new JCheckBoxMenuItem("Tree Health");
		jmView.add(jmiPositions);
		jmView.add(jmiHealth);
		JMenu jmFire = new JMenu("Fire");
		JMenuItem jmBurnRadius = new JMenuItem("Burn Radius");
		JMenuItem jmClickRadius = new JMenuItem("Click Radius");
		
		JPanel jpViewFire = new JPanel(new GridLayout(0,1));

		// JSlider tweak:
		// http://stackoverflow.com/questions/518471/jslider-question-position-after-leftclick
		jpViewFire.add(new JLabel("  Click Radius"));
		jpViewFire.add(new JSlider());
		
		jpViewFire.add(new JLabel("  Burn Radius"));
		jpViewFire.add(new JSlider());
		
		jmFire.add(jpViewFire);

		// Menu items (Help)
		JMenuItem jmiAbout = new JMenuItem("About");
		JMenuItem jmiTutorial = new JMenuItem("Tutorial");
		
		// Add functionality to menu items
		jmiEdit.addActionListener(e -> {
			forestFire.setEditMode(true);
		});
		
		jmiExit.addActionListener(e -> {
			System.exit(0);
		});
		
		// Add items to menus
		jmFile.add(jmiNewMap);
		jmFile.add(jmiEdit);
		jmFile.addSeparator();
		jmFile.add(jmiSave);
		jmFile.add(jmiLoad);
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
