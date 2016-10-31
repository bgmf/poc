package eu.dzim.shared.fx.text;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public interface TextFlowService {
	
	public static enum TextFlowStyle {
		//@formatter:off
		DEFAULT,
		SUPERTEXT,
		SUBTEXT;
		//@formatter:on
	}
	
	Text fromString(String text, TextFlowStyle style);
	
	TextFlow fromString(String text);
	
	List<Node> listFromString(String text);
	
	FlowPane flowPaneFromString(String text);
}
