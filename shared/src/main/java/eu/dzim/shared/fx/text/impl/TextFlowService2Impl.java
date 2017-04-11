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
	
	private static final String SC_TEXT_DEFAULT = "content-text-default";
	private static final String SC_TEXT_BOLD = "content-text-bold";
	private static final String SC_TEXT_ITALIC = "content-text-italic";
	private static final String SC_TEXT_UNDERLINED = "content-text-underlined";
	private static final String SC_TEXT_SUP = "content-text-sup";
	private static final String SC_TEXT_SUB = "content-text-sub";
	private static final String SC_TEXT_SMALL = "content-text-small";
	private static final String SC_TEXT_LARGE = "content-text-large";
	private static final String SC_TEXT_BLACK = "textflow-black";
	private static final String SC_TEXT_WHITE = "textflow-white";
	private static final String SC_TEXT_PRIMARY = "textflow-primary";
	private static final String SC_TEXT_SECONDARY = "textflow-secondary";
	private static final String SC_TEXT_TERTIARY = "textflow-tertiary";
	private static final String SC_TEXT_ERROR = "textflow-error";
	private static final String SC_TEXT_POSITIVE = "textflow-positive";
	private static final String SC_TEXT_WARNING = "textflow-warning";
	private static final String SC_TEXT_NEUTRAL = "textflow-neutral";
	
	// (?s)
	// -> http://www.regular-expressions.info/modifiers.html
	// -> for "single line mode" makes the dot match all characters, including line breaks
	
	// (?s)(<M(?:\s+t="([biuhl\-\+091234567]*){0,1}"\s*)/>).*
	private static final String DEFAULT_PATTERN = "(?s)(<M(?:\\s+t=\"([biuhl\\-\\+091234567]*){0,1}\"\\s*)/>).*";
	private static final int GROUP_DEFAULT_TAG = 1;
	private static final int GROUP_DEFAULT_ATTR = 2;
	
	// (?s)([^\<\>]+)|(<m>([^\<\>]+)</m>)|(<m(?:\s+t="([biuhl\-\+091234567]*){0,1}")\s*>([^\<\>]+)</m>)
	private static final String PATTERN = "(?s)([^\\<\\>]+)|(<m>([^\\<\\>]+)</m>)|(<m(?:\\s+t=\"([biuhl\\-\\+091234567]*){0,1}\")\\s*>([^\\<\\>]+)</m>)";
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
	private Pattern defaultPattern = null;
	private Pattern varPattern = null;
	
	private void initiate() {
		if (initiated)
			return;
		try {
			defaultPattern = Pattern.compile(DEFAULT_PATTERN);
		} catch (NumberFormatException e) {
			LOG.error(e);
			return;
		}
		try {
			varPattern = Pattern.compile(PATTERN, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);
		} catch (NumberFormatException e) {
			LOG.error(e);
			return;
		}
		initiated = true;
	}
	
	@Override
	public Text fromString(String string, Set<TextFlowStyle> defaultStyles, Set<TextFlowStyle> styles, String... additionalStyleClass) {
		if (!initiated)
			initiate();
		Text text = new Text(replaceHtmlEntities(string));
		addStyles(text, defaultStyles);
		addStyles(text, styles);
		if (additionalStyleClass != null && additionalStyleClass.length > 0)
			text.getStyleClass().addAll(additionalStyleClass);
		return text;
	}
	
	private void addStyles(Text text, Set<TextFlowStyle> styles) {
		if (text == null || styles == null || styles.isEmpty())
			return;
		if (styles.contains(TextFlowStyle.DEFAULT)) {
			text.getStyleClass().add(SC_TEXT_DEFAULT);
		}
		if (styles.contains(TextFlowStyle.BOLD)) {
			text.getStyleClass().add(SC_TEXT_BOLD);
		}
		if (styles.contains(TextFlowStyle.ITALIC)) {
			text.getStyleClass().add(SC_TEXT_ITALIC);
		}
		if (styles.contains(TextFlowStyle.UNDERLINED)) {
			text.getStyleClass().add(SC_TEXT_UNDERLINED);
		}
		if (styles.contains(TextFlowStyle.SUPERTEXT)) {
			text.getStyleClass().add(SC_TEXT_SUP);
		}
		if (styles.contains(TextFlowStyle.SUBTEXT)) {
			text.getStyleClass().add(SC_TEXT_SUB);
		}
		if (styles.contains(TextFlowStyle.SMALL)) {
			text.getStyleClass().add(SC_TEXT_SMALL);
		}
		if (styles.contains(TextFlowStyle.LARGE)) {
			text.getStyleClass().add(SC_TEXT_LARGE);
		}
		if (styles.contains(TextFlowStyle.COLOR_BLACK)) {
			text.getStyleClass().add(SC_TEXT_BLACK);
		}
		if (styles.contains(TextFlowStyle.COLOR_WHITE)) {
			text.getStyleClass().add(SC_TEXT_WHITE);
		}
		if (styles.contains(TextFlowStyle.COLOR_PRIMARY)) {
			text.getStyleClass().add(SC_TEXT_PRIMARY);
		}
		if (styles.contains(TextFlowStyle.COLOR_SECONDARY)) {
			text.getStyleClass().add(SC_TEXT_SECONDARY);
		}
		if (styles.contains(TextFlowStyle.COLOR_TERTIARY)) {
			text.getStyleClass().add(SC_TEXT_TERTIARY);
		}
		if (styles.contains(TextFlowStyle.COLOR_ERROR)) {
			text.getStyleClass().add(SC_TEXT_ERROR);
		}
		if (styles.contains(TextFlowStyle.COLOR_POSITIVE)) {
			text.getStyleClass().add(SC_TEXT_POSITIVE);
		}
		if (styles.contains(TextFlowStyle.COLOR_WARNING)) {
			text.getStyleClass().add(SC_TEXT_WARNING);
		}
		if (styles.contains(TextFlowStyle.COLOR_NEUTRAL)) {
			text.getStyleClass().add(SC_TEXT_NEUTRAL);
		}
	}
	
	@Override
	public Text fromString(String string, Set<TextFlowStyle> styles, String... additionalStyleClass) {
		return fromString(string, null, styles, additionalStyleClass);
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
		FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL,
				listFromString(string, additionalStyleClass).stream().toArray(size -> new Node[size]));
		flowPane.setAlignment(Pos.CENTER_LEFT);
		flowPane.getStyleClass().add(STYLECLASS_FLOW_PANE);
		return flowPane;
	}
	
	private void appendTextToList(String string, ObservableList<Node> container, String... additionalStyleClass) {
		Set<TextFlowStyle> defaultStyles = null;
		Matcher md = defaultPattern.matcher(string);
		if (md.matches()) {
			String tag = md.group(GROUP_DEFAULT_TAG);
			defaultStyles = fromAttributes(md.group(GROUP_DEFAULT_ATTR));
			string = string.replace(tag, "");
		}
		Matcher m = varPattern.matcher(string);
		while (m.find()) {
			String content = contentfromMatcher(m);
			container.add(fromString(content, defaultStyles, fromMatcher(m), additionalStyleClass));
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
			styles.addAll(fromAttributes(attr));
		}
		// XXX append other styles here, if necessary
		return styles;
	}
	
	private Set<TextFlowStyle> fromAttributes(String attributes) {
		Set<TextFlowStyle> styles = new HashSet<>();
		String attr = attributes.toLowerCase(Locale.ROOT);
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
		if (attr.contains("-")) {
			styles.add(TextFlowStyle.SMALL);
		}
		if (attr.contains("+")) {
			styles.add(TextFlowStyle.LARGE);
		}
		if (attr.contains("0")) {
			styles.add(TextFlowStyle.COLOR_BLACK);
		}
		if (attr.contains("9")) {
			styles.add(TextFlowStyle.COLOR_WHITE);
		}
		if (attr.contains("1")) {
			styles.add(TextFlowStyle.COLOR_PRIMARY);
		}
		if (attr.contains("2")) {
			styles.add(TextFlowStyle.COLOR_SECONDARY);
		}
		if (attr.contains("3")) {
			styles.add(TextFlowStyle.COLOR_TERTIARY);
		}
		if (attr.contains("4")) {
			styles.add(TextFlowStyle.COLOR_ERROR);
		}
		if (attr.contains("5")) {
			styles.add(TextFlowStyle.COLOR_POSITIVE);
		}
		if (attr.contains("6")) {
			styles.add(TextFlowStyle.COLOR_WARNING);
		}
		if (attr.contains("7")) {
			styles.add(TextFlowStyle.COLOR_NEUTRAL);
		}
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
