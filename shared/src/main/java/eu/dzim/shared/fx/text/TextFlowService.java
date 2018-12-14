package eu.dzim.shared.fx.text;

import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.List;

public interface TextFlowService {

    Text fromString(String text, TextFlowStyle style);

    TextFlow fromString(String text);

    List<Node> listFromString(String text);

    FlowPane flowPaneFromString(String text);

    public static enum TextFlowStyle {
        //@formatter:off
		DEFAULT,
		SUPERTEXT,
		SUBTEXT;
		//@formatter:on
    }
}
