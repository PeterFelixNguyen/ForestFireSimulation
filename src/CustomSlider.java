/**
 * This solution is derived from: 
 * http://stackoverflow.com/questions/518471/jslider-question-position-after-leftclick
 */
import javax.swing.JSlider;
import javax.swing.plaf.metal.MetalSliderUI;

@SuppressWarnings("serial")
public class CustomSlider extends JSlider {
	
	public CustomSlider() {
		setUI(new MetalSliderUI() {
		    protected void scrollDueToClickInTrack(int direction) {
		        int value = slider.getValue(); 

		        if (slider.getOrientation() == JSlider.HORIZONTAL) {
		            value = this.valueForXPosition(slider.getMousePosition().x);
		        } else if (slider.getOrientation() == JSlider.VERTICAL) {
		            value = this.valueForYPosition(slider.getMousePosition().y);
		        }
		        slider.setValue(value);
		    }
		});
	}
}
