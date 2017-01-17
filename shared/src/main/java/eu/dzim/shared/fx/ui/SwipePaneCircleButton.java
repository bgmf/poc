package eu.dzim.shared.fx.ui;

import javafx.scene.Node;
import javafx.scene.control.Button;

public class SwipePaneCircleButton extends Button {
	
	public SwipePaneCircleButton() {
		super();
		init(null);
	}
	
	public SwipePaneCircleButton(String text) {
		super(text);
		init(null);
	}
	
	public SwipePaneCircleButton(String text, Node graphic) {
		super(text, graphic);
		init(graphic);
	}
	
	private void init(Node graphic) {
		getStyleClass().addAll("circle"); // "transparent"
		if (graphic != null) {
			setGraphic(graphic);
		} else {
			// MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.CHECKBOX_BLANK_CIRCLE_OUTLINE);
			// icon.setGlyphSize(15);
			// setGraphic(icon);
		}
	}
}
