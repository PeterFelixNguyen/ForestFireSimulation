import java.awt.Color;

public abstract class WeatherObject {
	private int xSource;
	private int ySource;
	private int xPosition;
	private int yPosition;
	private int xDestination;
	private int yDestination;
	private int xDirection;
	private int yDirection;
	private int growthRate;
	private int effectRadius;
	private int duration;
	private int xDistance;
	private int yDistance;
	private int velocity = 1; // default
	public static final int MAX_VELOCITY = 10;
	public static final int MIN_VELOCITY = 1;
	public static final int MIN_SIZE = 10;
	public static final int MAX_SIZE = 300;
	private boolean still;
	
	private boolean alive = true;
	private boolean once = true; // true = off
	
	public abstract Color getLensColor();
	
	public abstract Color getBorderColor();
		
	public void move() {
		if (!still) {
			// Divide by zero is allowed for floats
			double slope = (double)(yDestination - ySource) / (double)(xDestination - xSource);
	
			int yIntercept = (int) (ySource - (slope * xSource)); 
	
			if (alive) {
				if (!Double.isNaN(slope)) {
					if (xDistance > yDistance) {
						if (xSource < xDestination) {
							xPosition += velocity;
							yPosition = (int) ((slope * xPosition) + yIntercept);
						} else {
							xPosition -= velocity;
							yPosition = (int) ((slope * xPosition) + yIntercept);
						}
					} else {
						if (xSource < xDestination) {
							if (ySource < yDestination) {
								yPosition += velocity;
								xPosition = (int) ((yPosition - yIntercept) / slope); 
							}  else {
								yPosition -= velocity;
								xPosition = (int) ((yPosition - yIntercept) / slope); 
							}
						} else {
							if (ySource < yDestination) {
								yPosition += velocity;
								xPosition = (int) ((yPosition - yIntercept) / slope); 
							} else {
								yPosition -= velocity;
								xPosition = (int) ((yPosition - yIntercept) / slope); 
							}
						}
					}
				}
				
				if (xSource < xDestination) {
					if (xPosition > xDestination) {
						alive = false;
					}
				} else {
					if (xPosition < xDestination) {
						alive = false;
					}
				}
			}
			
				
			if (!once) {
				System.out.println("xDestination - xSource = " + (xDestination - xSource));
				System.out.println("slope: " + slope);
				
				System.out.println("yIncercept: " + yIntercept);
				
				System.out.println("xSource: " + xSource);
				System.out.println("xPosition: " + xPosition);
				
				System.out.println("ySource: " + ySource);
				System.out.println("yPosition: " + yPosition);
				
				once = true;
			}
		}
	}
	
	public void setVelocity(int velocity) {
		if (velocity > MAX_VELOCITY) {
			velocity = MAX_VELOCITY;
		} else if (velocity < MIN_VELOCITY) {
			velocity = MIN_VELOCITY;
		} else {
			this.velocity = velocity;
		}
	}
	
	public boolean isAlive() {
		return alive;
	}
	
	public WeatherObject(int xSource, int ySource, int radius, int duration, int velocity) {
		this.xSource = xSource;
		this.ySource = ySource;
		this.effectRadius = radius;
		this.duration = duration;
		xPosition = xSource;
		yPosition = ySource;
		this.velocity = velocity;
		this.growthRate = 2;
		this.still = true;
		xDirection = 0;
		yDirection = 0;
		calculateDirection();
	}
	
	public void calculateDirection() {
		if (xSource < xDestination) {
			xDirection = 1;
		} else if (xSource > xDestination){
			xDirection = -1;
		} else {
			xDirection = 0;
		}
		
		if (ySource < yDestination) {
			yDirection = 1;
		} else if (ySource > yDestination) {
			yDirection = -1;
		} else {
			yDirection = 0;
		}
	}
	
	public void setDirection(int xDirection, int yDirection) {
		this.xDirection = xDirection;
		this.yDirection = yDirection;
	}
	
	public int getXDirection() {
		return xDirection;
	}
	
	public int getYDirection() {
		return yDirection;
	}
	
	public void setStill(boolean still) {
		this.still = still;
	}
	
	public boolean isStill() {
		return still;
	}
	
	public void interact(Tree tree) {
		
	}
	
	public void grow() {
		if (!still) {
			double random = Math.random();
			double probability = 0.1;
			
			if (random < probability) {
				if (effectRadius < MAX_SIZE) {
					effectRadius += growthRate;
				}
			}
		}
	}
	
	public void shrink() {
		if (!still) {
			double random = Math.random();
			double probability = 0.1;
		
			if (random < probability) {
				if (effectRadius > MIN_SIZE) {
					effectRadius -= growthRate;
				}
			}
		}
	}
	
	public void setGrowthRate(int growthRate) {
		this.growthRate = growthRate;
	}
	
	public void setEffectRadius(int effectRadius) {
		this.effectRadius = effectRadius;
	}
	
	public int getEffectRadius() {
		return effectRadius;
	}
	
	public int getEffectRadiusSqr() {
		return effectRadius * effectRadius;
	}
	
	public void setPosition(int xPosition, int yPosition) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
	}

	public int getXSource() {
		return xSource;
	}

	public int getYSource() {
		return ySource;
	}
	
	public void setXPositon(int xPosition) {
		this.xPosition = xPosition;
	}
	
	public void setYPosition(int yPosition) {
		this.yPosition = yPosition;
	}
	
	public int getXPosition() {
		return xPosition;
	}
	
	public int getYPosition() {
		return yPosition;
	}
	
	public void setDestination(int xDestination, int yDestination) {
		this.xDestination = xDestination;
		this.yDestination = yDestination;
		yDistance = Math.abs(yDestination - ySource);
		xDistance = Math.abs(xDestination - xSource);
	}
	
	public void setXDestination(int xDestination) {
		this.xDestination = xDestination;
		yDistance = Math.abs(yDestination - ySource);
		xDistance = Math.abs(xDestination - xSource);
	}
	
	public void setYDestination(int yDestination) {
		this.yDestination = yDestination;
		yDistance = Math.abs(yDestination - ySource);
		xDistance = Math.abs(xDestination - xSource);
	}
	
	public int getXDestination() {
		return xDestination;
	}
	
	public int getYDestination() {
		return yDestination;
	}
	
	public boolean tick() {
		if (duration > 0) {
			duration--;
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isExpired() {
		if (duration == 0) {
			return true;
		} else {
			return false;
		}
	}
}
