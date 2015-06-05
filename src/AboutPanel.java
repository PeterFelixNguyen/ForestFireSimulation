/**
 * Copyright 2015 Peter "Felix" Nguyen & Emmanuel Medina Lopez
 * 
 * Forest Fire Simulation with 2D Graphics.
 * 
 * AboutPanel contains the HTML that provides the user information about the program.
 */
import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

@SuppressWarnings("serial")
public class AboutPanel extends JPanel {
    private JEditorPane JepAbout = new JEditorPane();
    private UrlListener urlListener = new UrlListener();

    public AboutPanel() {
        JepAbout.setEditable(false);
        JepAbout.addHyperlinkListener(urlListener);
        JepAbout.setBorder(null);
        JepAbout.setOpaque(false);

        ClassLoader cLoader = this.getClass().getClassLoader();

        try {
            JepAbout.setPage(cLoader.getResource("html/AboutPage.html"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        add(JepAbout);
    }
}

class UrlListener implements HyperlinkListener {

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                Desktop.getDesktop().browse(e.getURL().toURI());
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
            catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
        }
    }
}