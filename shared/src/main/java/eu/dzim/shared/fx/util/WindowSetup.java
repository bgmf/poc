package eu.dzim.shared.fx.util;

public class WindowSetup {
	
	private boolean maximized;
	private double width;
	private double height;
	private double posX;
	private double posY;
	
	private WindowSetup() {
		// empty - for default definition
	}
	
	public WindowSetup(final WindowSetup defaultSetup) {
		maximized = defaultSetup.maximized;
		width = defaultSetup.width;
		height = defaultSetup.height;
		posX = defaultSetup.posX;
		posY = defaultSetup.posY;
	}
	
	public boolean isMaximized() {
		return maximized;
	}
	
	public void setMaximized(boolean maximized) {
		this.maximized = maximized;
	}
	
	public double getHeight() {
		return height;
	}
	
	public void setHeight(double height) {
		this.height = height;
	}
	
	public double getWidth() {
		return width;
	}
	
	public void setWidth(double width) {
		this.width = width;
	}
	
	public double getPosX() {
		return posX;
	}
	
	public void setPosX(double posX) {
		this.posX = posX;
	}
	
	public double getPosY() {
		return posY;
	}
	
	public void setPosY(double posY) {
		this.posY = posY;
	}
	
	public static class Builder {
		
		private final WindowSetup setup;
		
		public Builder() {
			setup = new WindowSetup();
		}
		
		public Builder maximized(boolean maximized) {
			setup.maximized = maximized;
			return this;
		}
		
		public Builder width(double width) {
			setup.width = width;
			return this;
		}
		
		public Builder height(double height) {
			setup.height = height;
			return this;
		}
		
		public Builder posX(double posX) {
			setup.posX = posX;
			return this;
		}
		
		public Builder posY(double posY) {
			setup.posY = posY;
			return this;
		}
		
		public WindowSetup build() {
			return setup;
		}
	}
}