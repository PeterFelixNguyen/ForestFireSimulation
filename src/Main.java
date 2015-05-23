import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;


public class Main {
	// GUI Frame
	private static JFrame frame = new JFrame();
	
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
		JMenu jmiFile = new JMenu("File");
		JMenu jmOptions = new JMenu("Options");
		
		// Menu items
		JMenuItem jmiNewMap = new JMenuItem("New Map");
		JMenuItem jmiSave = new JMenuItem("Save Map");
		JMenuItem jmiLoad = new JMenuItem("Load Map");
		JMenuItem jmiExit = new JMenuItem("Exit Map");
		
		JMenu jmMode = new JMenu("Select Mode");
		JMenuItem jmiEditor= new JMenuItem("Map Editor");
		JMenuItem jmiSim = new JMenuItem("Simulator");
		
		
		
		// Add functionality to menu items
		jmiExit.addActionListener(e -> {
			System.exit(0);
		});
		
		// Add items to menus
		jmiFile.add(jmiNewMap);
		jmiFile.addSeparator();
		jmiFile.add(jmiSave);
		jmiFile.add(jmiLoad);
		jmiFile.addSeparator();
		jmiFile.add(jmiExit);
		
		
		//
		JMenuBar jmbForestFire = new JMenuBar();
		jmbForestFire.add(jmiFile);
		jmbForestFire.add(jmOptions);
		frame.setJMenuBar(jmbForestFire);
		
		frame.setVisible(true);
	}
}
