package eu.dzim.shared.fx.util;

import javafx.animation.Transition;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class ResizeHeightTransition extends Transition {
	
	protected Region region;
	protected double startHeight;
	protected double newHeight;
	protected double heightDiff;
	
	public ResizeHeightTransition(Duration duration, Region region, double newHeight) {
		setCycleDuration(duration);
		this.region = region;
		this.newHeight = newHeight;
		this.startHeight = region.getHeight();
		this.heightDiff = newHeight - startHeight;
	}
	
	@Override
	protected void interpolate(double fraction) {
		region.setMinHeight(startHeight + (heightDiff * fraction));
		region.setMaxHeight(startHeight + (heightDiff * fraction));
		region.setPrefHeight(startHeight + (heightDiff * fraction));
	}
}
