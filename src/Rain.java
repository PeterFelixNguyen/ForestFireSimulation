import java.awt.Color;

public class Rain extends WeatherObject {
	public static final Color LENS_COLOR = LookAndFeel.COLOR_TRANSLUCENT_LENS_RAIN;
	public static final Color BORDER_COLOR = LookAndFeel.COLOR_SOLID_BORDER_RAIN;
	
	public Rain(int x, int y, int radius, int duration) {
		super(x, y, radius, duration, 1);
	}
	
	public Color getLensColor() {
		return LENS_COLOR;
	}
	
	public Color getBorderColor() {
		return BORDER_COLOR;
	}
	
}
