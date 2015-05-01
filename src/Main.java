import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
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
		
		// Setup frame
		frame.setTitle("Forest Fire by Peter \"Felix\" Nguyen");
		frame.setSize(ForestFire.width + 30, ForestFire.height + 120);
		frame.setMinimumSize(new Dimension(ForestFire.width + 30, ForestFire.height + 120));
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
		
		// Add components to frame
		frame.add(Box.createHorizontalGlue());
		frame.add(fullInterface);
		frame.add(Box.createHorizontalGlue());

		frame.setVisible(true);
	}
}
