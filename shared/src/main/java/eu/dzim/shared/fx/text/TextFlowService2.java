package eu.dzim.shared.fx.text;

import java.util.List;
import java.util.Set;

import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public interface TextFlowService2 {
	
	public static enum TextFlowStyle {
		//@formatter:off
		DEFAULT,
		BOLD,
		ITALIC,
		UNDERLINED,
		SUPERTEXT,
		SUBTEXT,
		SMALL,
		LARGE,
		COLOR_BLACK,
		COLOR_WHITE,
		COLOR_PRIMARY,
		COLOR_SECONDARY,
		COLOR_TERTIARY,
		COLOR_ERROR,
		COLOR_POSITIVE,
		COLOR_WARNING,
		COLOR_NEUTRAL,
		;
		//@formatter:on
	}
	
	Text fromString(String text, Set<TextFlowStyle> defaultStyles, Set<TextFlowStyle> styles, String... additionalStyleClass);
	
	Text fromString(String text, Set<TextFlowStyle> styles, String... additionalStyleClass);
	
	TextFlow fromString(String text, String... additionalStyleClass);
	
	List<Node> listFromString(String text, String... additionalStyleClass);
	
	FlowPane flowPaneFromString(String text, String... additionalStyleClass);
}
