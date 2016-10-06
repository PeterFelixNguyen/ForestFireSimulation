import java.awt.Color;

public class Wind extends WeatherObject {
	public static final Color LENS_COLOR = LookAndFeel.COLOR_TRANSLUCENT_LENS_WIND;
	public static final Color BORDER_COLOR = LookAndFeel.COLOR_SOLID_BORDER_WIND;
	
	public Wind(int x, int y, int radius, int duration) {
		super(x, y, radius, duration, 2);
	}
	
	public Color getLensColor() {
		return LENS_COLOR;
	}
	
	public Color getBorderColor() {
		return BORDER_COLOR;
	}
}
