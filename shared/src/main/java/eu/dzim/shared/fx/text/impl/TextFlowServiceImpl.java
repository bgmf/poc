package eu.dzim.shared.fx.text.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.dzim.shared.fx.text.TextFlowService;
import eu.dzim.shared.resource.BaseResource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class TextFlowServiceImpl implements TextFlowService {
	
	private static final Logger LOG = LogManager.getLogger(TextFlowServiceImpl.class);
	
	private static final String STYLECLASS_FLOW_PANE = "adaptive-text-flow";
	
	private static final String STYLECLASS_TEXT_DEFAULT = "content-text-default";
	// private static final String STYLECLASS_TEXT_WHITE = "content-text-white";
	private static final String STYLECLASS_TEXT_SUP = "content-text-sup";
	private static final String STYLECLASS_TEXT_SUB = "content-text-sub";
	
	@Inject private BaseResource baseResource;
	
	private boolean initiated = false;
	private Pattern varPattern = null;
	private int varDefGroup = 0;
	private int varSupGroup = 0;
	private int varSubGroup = 0;
	
	private void initiate() {
		if (initiated)
			return;
		try {
			varPattern = Pattern.compile(baseResource.getGuaranteedString("table.var.pattern"),
					Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);
			varDefGroup = Integer.parseInt(baseResource.getGuaranteedString("table.var.pattern.def"));
			varSupGroup = Integer.parseInt(baseResource.getGuaranteedString("table.var.pattern.sup"));
			varSubGroup = Integer.parseInt(baseResource.getGuaranteedString("table.var.pattern.sub"));
			initiated = true;
		} catch (NumberFormatException e) {
			LOG.error(e);
		}
	}
	
	@Override
	public Text fromString(String string, TextFlowStyle style) {
		if (!initiated)
			initiate();
		Text text = new Text(string);
		text.getStyleClass().add(STYLECLASS_TEXT_DEFAULT);
		switch (style) {
		case DEFAULT:
			break;
		case SUPERTEXT:
			text.getStyleClass().add(STYLECLASS_TEXT_SUP);
			break;
		case SUBTEXT:
			text.getStyleClass().add(STYLECLASS_TEXT_SUB);
			break;
		default:
			break;
		}
		return text;
	}
	
	@Override
	public TextFlow fromString(String string) {
		if (!initiated)
			initiate();
		TextFlow textFlow = new TextFlow();
		appendTextToList(string, textFlow.getChildren());
		return textFlow;
	}
	
	@Override
	public List<Node> listFromString(String string) {
		if (!initiated)
			initiate();
		ObservableList<Node> list = FXCollections.observableArrayList();
		appendTextToList(string, list);
		return list;
	}
	
	@Override
	public FlowPane flowPaneFromString(String string) {
		FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL, listFromString(string).stream().toArray(size -> new Node[size]));
		flowPane.setAlignment(Pos.CENTER_LEFT);
		flowPane.getStyleClass().add(STYLECLASS_FLOW_PANE);
		return flowPane;
	}
	
	private void appendTextToList(String string, ObservableList<Node> container) {
		Matcher m = varPattern.matcher(string);
		while (m.find()) {
			String content = normalize(m.group(0));
			container.add(fromString(content, fromMatcher(m)));
		}
	}
	
	private String normalize(String string) {
		return string.replace("<sup>", "").replace("</sup>", "").replace("<sub>", "").replace("</sub>", "");
	}
	
	private TextFlowStyle fromMatcher(Matcher m) {
		if (m.group(varDefGroup) != null) {
			return TextFlowStyle.DEFAULT;
		} else if (m.group(varSupGroup) != null) {
			return TextFlowStyle.SUPERTEXT;
		} else if (m.group(varSubGroup) != null) {
			return TextFlowStyle.SUBTEXT;
		}
		// XXX append other styles here, if necessary
		return TextFlowStyle.DEFAULT;
	}
}
