package eu.dzim.shared.fx.ui;

import java.util.Locale;

import eu.dzim.shared.fx.util.UIComponentType;

public enum SharedUIComponentType implements UIComponentType {
	
	//@formatter:off
	BREADCRUMB_BAR("BreadcrumbBar", true),
	EMPTY("Empty", true),
	EXTENDED_TEXT_FIELD("ExtendedTextField", true),
	SIMPLE_DIALOG("SimpleDialog", true),
	SWIPE_PANE("SwipePane", true),
	;
	//@formatter:on
	
	public static final String BASE_PATH = "/fxml/";
	public static final String SUFFIX = ".fxml";
	public static final String BASE_CSS = "/css/root.css";
	
	private final String iconResource;
	private final boolean defaultPath;
	
	private SharedUIComponentType(final String iconResource, final boolean defaultPath) {
		this.iconResource = iconResource;
		this.defaultPath = defaultPath;
	}
	
	@Override
	public String getAbsoluteLocation() {
		String path = (this.defaultPath ? BASE_PATH : "") + this.iconResource;
		if (!path.toLowerCase(Locale.US).endsWith(SUFFIX.toLowerCase(Locale.US)))
			path += SUFFIX;
		return path;
	}
}
