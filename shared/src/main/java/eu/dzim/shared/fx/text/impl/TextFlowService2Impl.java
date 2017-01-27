package eu.dzim.shared.fx.text.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.dzim.shared.fx.text.TextFlowService2;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class TextFlowService2Impl implements TextFlowService2 {
	
	private static final Logger LOG = LogManager.getLogger(TextFlowService2Impl.class);
	
	private static final String STYLECLASS_FLOW_PANE = "adaptive-text-flow";
	
	private static final String STYLECLASS_TEXT_DEFAULT = "content-text-default";
	private static final String STYLECLASS_TEXT_BOLD = "content-text-bold";
	private static final String STYLECLASS_TEXT_ITALIC = "content-text-italic";
	private static final String STYLECLASS_TEXT_UNDERLINED = "content-text-underlined";
	private static final String STYLECLASS_TEXT_SUP = "content-text-sup";
	private static final String STYLECLASS_TEXT_SUB = "content-text-sub";
	
	// ([^\<\>]+)|(<m>([^\<\>]+)</m>)|(<m(?: t="([biuhl]*){0,1}")>([^\<\>]+)</m>)
	private static final String PATTERN = "([^\\<\\>]+)|(<m>([^\\<\\>]+)</m>)|(<m(?: t=\"([biuhl]*){0,1}\")>([^\\<\\>]+)</m>)";
	private static final int GROUP_SIMPLE_CONTENT = 1;
	private static final int GROUP_M_EMPTY = 2;
	private static final int GROUP_M_EMPTY_CONTENT = 3;
	private static final int GROUP_M = 4;
	private static final int GROUP_M_ATTR = 5;
	private static final int GROUP_M_CONTENT = 6;
	
	private static final String HTML_NBSP = "&nbsp;";
	private static final String HTML_LT = "&lt;";
	private static final String HTML_GT = "&gt;";
	private static final String HTML_AMP = "&amp;";
	private static final String HTML_QUOT = "&quot;";
	private static final String HTML_APOS = "&apos;";
	private static final String HTML_CURREN = "&curren;";
	private static final String HTML_CENT = "&cent;";
	private static final String HTML_POUND = "&pound;";
	private static final String HTML_YEN = "&yen;";
	private static final String HTML_EURO = "&euro;";
	private static final String HTML_COPY = "&copy;";
	private static final String HTML_REG = "&reg;";
	
	private boolean initiated = false;
	private Pattern varPattern = null;
	
	private void initiate() {
		if (initiated)
			return;
		try {
			varPattern = Pattern.compile(PATTERN, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);
			initiated = true;
		} catch (NumberFormatException e) {
			LOG.error(e);
		}
	}
	
	@Override
	public Text fromString(String string, Set<TextFlowStyle> styles, String... additionalStyleClass) {
		if (!initiated)
			initiate();
		Text text = new Text(replaceHtmlEntities(string));
		if (styles.contains(TextFlowStyle.DEFAULT)) {
			text.getStyleClass().add(STYLECLASS_TEXT_DEFAULT);
		}
		if (styles.contains(TextFlowStyle.BOLD)) {
			text.getStyleClass().add(STYLECLASS_TEXT_BOLD);
		}
		if (styles.contains(TextFlowStyle.ITALIC)) {
			text.getStyleClass().add(STYLECLASS_TEXT_ITALIC);
		}
		if (styles.contains(TextFlowStyle.UNDERLINED)) {
			text.getStyleClass().add(STYLECLASS_TEXT_UNDERLINED);
		}
		if (styles.contains(TextFlowStyle.SUPERTEXT)) {
			text.getStyleClass().add(STYLECLASS_TEXT_SUP);
		}
		if (styles.contains(TextFlowStyle.SUBTEXT)) {
			text.getStyleClass().add(STYLECLASS_TEXT_SUB);
		}
		if (additionalStyleClass != null && additionalStyleClass.length > 0)
			text.getStyleClass().addAll(additionalStyleClass);
		return text;
	}
	
	@Override
	public TextFlow fromString(String string, String... additionalStyleClass) {
		if (!initiated)
			initiate();
		TextFlow textFlow = new TextFlow();
		appendTextToList(string, textFlow.getChildren(), additionalStyleClass);
		return textFlow;
	}
	
	@Override
	public List<Node> listFromString(String string, String... additionalStyleClass) {
		if (!initiated)
			initiate();
		ObservableList<Node> list = FXCollections.observableArrayList();
		appendTextToList(string, list, additionalStyleClass);
		return list;
	}
	
	@Override
	public FlowPane flowPaneFromString(String string, String... additionalStyleClass) {
		FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL, listFromString(string).stream().toArray(size -> new Node[size]));
		flowPane.setAlignment(Pos.CENTER_LEFT);
		flowPane.getStyleClass().add(STYLECLASS_FLOW_PANE);
		return flowPane;
	}
	
	private void appendTextToList(String string, ObservableList<Node> container, String... additionalStyleClass) {
		Matcher m = varPattern.matcher(string);
		while (m.find()) {
			String content = contentfromMatcher(m);
			container.add(fromString(content, fromMatcher(m), additionalStyleClass));
		}
	}
	
	private Set<TextFlowStyle> fromMatcher(Matcher m) {
		Set<TextFlowStyle> styles = new HashSet<>();
		styles.add(TextFlowStyle.DEFAULT);
		if (m.group(GROUP_SIMPLE_CONTENT) != null) {
			styles.add(TextFlowStyle.DEFAULT);
		} else if (m.group(GROUP_M_EMPTY) != null) {
			styles.add(TextFlowStyle.DEFAULT);
		} else if (m.group(GROUP_M) != null) {
			String attr = m.group(GROUP_M_ATTR);
			attr = attr.toLowerCase(Locale.ROOT);
			if (attr.contains("b")) {
				styles.add(TextFlowStyle.BOLD);
			}
			if (attr.contains("i")) {
				styles.add(TextFlowStyle.ITALIC);
			}
			if (attr.contains("u")) {
				styles.add(TextFlowStyle.UNDERLINED);
			}
			if (attr.contains("h")) {
				styles.add(TextFlowStyle.SUPERTEXT);
			}
			if (attr.contains("l")) {
				styles.add(TextFlowStyle.SUBTEXT);
			}
		}
		// XXX append other styles here, if necessary
		return styles;
	}
	
	private String contentfromMatcher(Matcher m) {
		if (m.group(GROUP_SIMPLE_CONTENT) != null) {
			return m.group(GROUP_SIMPLE_CONTENT);
		} else if (m.group(GROUP_M_EMPTY) != null) {
			return m.group(GROUP_M_EMPTY_CONTENT);
		} else if (m.group(GROUP_M) != null) {
			return m.group(GROUP_M_CONTENT);
		}
		return "";
	}
	
	private String replaceHtmlEntities(String input) {
		// @formatter:off
		return input
				.replace(HTML_NBSP, "\u00A0")
				.replace(HTML_LT, "<")
				.replace(HTML_GT, ">")
				.replace(HTML_AMP, "&")
				.replace(HTML_QUOT, "\"")
				.replace(HTML_APOS, "'")
				.replace(HTML_CURREN, "\u00A4")
				.replace(HTML_CENT, "\u00A2")
				.replace(HTML_POUND, "\u00A3")
				.replace(HTML_YEN, "\u00A5")
				.replace(HTML_EURO, "\u20AC")
				.replace(HTML_COPY, "\u00A9")
				.replace(HTML_REG, "\u00AE");
		// @formatter:on
	}
}
