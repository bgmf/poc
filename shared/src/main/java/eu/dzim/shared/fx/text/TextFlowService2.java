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
		SUBTEXT;
		//@formatter:on
	}
	
	Text fromString(String text, Set<TextFlowStyle> styles, String... additionalStyleClass);
	
	TextFlow fromString(String text, String... additionalStyleClass);
	
	List<Node> listFromString(String text, String... additionalStyleClass);
	
	FlowPane flowPaneFromString(String text, String... additionalStyleClass);
}
