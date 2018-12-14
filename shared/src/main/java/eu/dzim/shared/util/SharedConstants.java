package eu.dzim.shared.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class SharedConstants {

    public static final long CURRENT_FILE_VERSION = 1L;

    public static final DateFormat DEFAULT_JSON_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat DEFAULT_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    public static final String MANIFEST = "META-INF/MANIFEST.MF";
    public static final String MANIFEST_ENTRY_MAIN_CLASS = "Main-Class";
    public static final String BASE_DIR = "eu.dzim.poc";
    public static final String VERSION_DIR = "0.0.0";
    public static final String CONFIG_FILE = "config.json";
    public static final String BASE_CONFIG_FILE = "app" + CONFIG_FILE;
    public static final String CORE_RESOURCE_PATH = "/";
    public static final String FXML_SUFFIX = ".fxml";
    public static final String CORE_CSS = CORE_RESOURCE_PATH + "css/core.css";
    public static final String PLUGIN_PARAM_LANGUAGE = "plugin.language";
    public static final String PLUGIN_PARAM_CLASSADAPTER = "plugin.classadapter";
    public static final String PLUGIN_PARAM_SCENICVIEW = "plugin.scenicview";
    public static final double WINDOW_MIN_HEIGHT_1 = 640.0;
    public static final double WINDOW_MIN_WIDTH_1 = 1100.0;
    public static final String WINDOW_PARAM_SCREEN = "window.screen";
    public static final String WINDOW_PARAM_MAXIMIZED = "window.maximized";
    public static final String WINDOW_PARAM_HEIGHT = "window.heigth";
    public static final String WINDOW_PARAM_WIDTH = "window.width";
    public static final String WINDOW_PARAM_POS_X = "window.pos.x";
    public static final String WINDOW_PARAM_POS_Y = "window.pos.y";
    public static final int MAX_RECENTS_FILE = 10;
    public static final int TEXT_SIZE_XSMALL = -2;
    public static final int TEXT_SIZE_SMALL = -1;
    public static final int TEXT_SIZE_DEFAULT = 0;
    public static final int TEXT_SIZE_LARGE = 1;
    public static final int TEXT_SIZE_XLARGE = 2;
    public static final String SC_TEXT_XSMALL = "content-text-xsmall";
    public static final String SC_TEXT_SMALL = "content-text-small";
    public static final String SC_TEXT_DEFAULT = "content-text-default";
    public static final String SC_TEXT_LARGE = "content-text-large";
    public static final String SC_TEXT_XLARGE = "content-text-xlarge";
    public static final String SC_TEXT_XXLARGE = "content-text-xxlarge";
    public static final String SC_TEXT_XXXLARGE = "content-text-xxxlarge";
    public static final String SC_TEXT_XXXXLARGE = "content-text-xxxxlarge";
    private static final String MANIFEST_ENTRY_BASE = "Tool-Base";
    private static final String MANIFEST_ENTRY_VERSION = "Tool-Version";
    public static final Path DEFAULT_CONFIG_DIR = Paths
            .get(System.getProperty("user.home", "."), SharedUtils.getCustomManifestEntry(MANIFEST_ENTRY_BASE, BASE_DIR),
                    SharedUtils.getCustomManifestEntry(MANIFEST_ENTRY_VERSION, VERSION_DIR));

    static {
        DEFAULT_JSON_DATE_TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        DEFAULT_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        DEFAULT_TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    protected SharedConstants() {
        // hide the constructor
    }
}
