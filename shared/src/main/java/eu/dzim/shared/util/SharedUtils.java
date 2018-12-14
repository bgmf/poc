package eu.dzim.shared.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import eu.dzim.shared.fx.util.WindowSetup;
import eu.dzim.shared.model.config.ConfigService;
import eu.dzim.shared.resource.Resource;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class SharedUtils {

    private static final Logger LOG = LogManager.getLogger(SharedUtils.class);

    protected SharedUtils() {
        // sonar
    }

    public static String getCustomManifestEntry(String name, String defaultValue) {
        URL manifestUrl = SharedUtils.class.getClassLoader().getResource(SharedConstants.MANIFEST);
        if (manifestUrl == null)
            return defaultValue;
        try {
            Manifest manifest = new Manifest(manifestUrl.openStream());
            Attributes attr = manifest.getMainAttributes();
            String value = attr.getValue(name);
            return value == null ? defaultValue : value;
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return defaultValue;
        }
    }

    public static String getVersionString(Resource resource) {
        String str = getVersionOrNull();
        if (str != null) {
            return str;
        } else {
            return resource.getGuaranteedString("about.version.na");
        }
    }

    public static String getVersionOrNull() {
        Package pkg = SharedUtils.class.getPackage();
        if (pkg != null) {
            String str = pkg.getSpecificationVersion();
            return str;
        } else {
            return null;
        }
    }

    public static String getBuildVersionString() {
        Package pkg = SharedUtils.class.getPackage();
        if (pkg != null) {
            String str = pkg.getImplementationVersion();
            return str == null || str.isEmpty() ? "" : str.substring(0, 7);
        } else {
            return "";
        }
    }

    public static String getImplementationTitleString() {
        Package pkg = SharedUtils.class.getPackage();
        if (pkg != null) {
            String str = pkg.getImplementationTitle();
            return str == null || str.isEmpty() ? "" : str;
        } else {
            return "";
        }
    }

    /**
     * Compares two version strings.
     * <p>
     * Use this instead of String.compareTo() for a non-lexicographical comparison that works for version strings. e.g. "1.10".compareTo("1.6").
     *
     * @param str1 a string of ordinal numbers separated by decimal points.
     * @param str2 a string of ordinal numbers separated by decimal points.
     * @return The result is a negative integer if str1 is _numerically_ less than str2. The result is a positive integer if str1 is _numerically_
     * greater than str2. The result is zero if the strings are _numerically_ equal.
     * @note It does not work if "1.10" is supposed to be equal to "1.10.0".
     */
    public static int versionCompare(String str1, String str2) {
        String[] vals1 = str1.split("\\.");
        String[] vals2 = str2.split("\\.");
        int i = 0;
        // set index to first non-equal ordinal or length of shortest version string
        while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }
        // compare first non-equal ordinal number
        if (i < vals1.length && i < vals2.length) {
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        }
        // the strings are equal or one string is a substring of the other
        // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
        return Integer.signum(vals1.length - vals2.length);
    }

    /**
     * List directory contents for a resource folder. Not recursive. This is basically a brute-force implementation. Works for regular files and also
     * JARs.
     *
     * @param clazz Any java class that lives in the same place as the resources you want.
     * @param path  Should end with "/", but not start with one.
     * @return Just the name of each member item, not the full paths.
     * @throws URISyntaxException
     * @throws IOException
     * @author Greg Briggs
     */
    public static String[] getResourceListing(Class<?> clazz, String path) throws URISyntaxException, IOException {
        URL dirURL = clazz.getClassLoader().getResource(path);
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            /* A file path: easy enough */
            return new File(dirURL.toURI()).list();
        }

        if (dirURL == null) {
            // In case of a jar file, we can't actually find a directory. Have to assume the same jar as clazz.
            String me = clazz.getName().replace(".", "/") + ".class";
            dirURL = clazz.getClassLoader().getResource(me);
        }

        if (dirURL.getProtocol().equals("jar")) {
            // a JAR path
            // strip out only the JAR file
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
            try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));) {

                // retrieved ALL entries in jar
                Enumeration<JarEntry> entries = jar.entries();
                // avoid duplicates in case it is a subdirectory
                Set<String> result = new HashSet<String>();
                while (entries.hasMoreElements()) {
                    String name = entries.nextElement().getName();
                    // filter according to the path
                    if (name.startsWith(path)) {
                        String entry = name.substring(path.length());
                        int checkSubdir = entry.indexOf("/");
                        if (checkSubdir >= 0) {
                            // if it is a subdirectory, we just return the directory name
                            entry = entry.substring(0, checkSubdir);
                        }
                        result.add(entry);
                    }
                }
                return result.toArray(new String[result.size()]);
            }
        }

        throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
    }

    public static String enumValueToString(Resource resource, Enum<?> enumeration) {
        return BaseEnumType.class.isAssignableFrom(enumeration.getClass()) ?
                resource.getGuaranteedString(((BaseEnumType) enumeration).getKey()) :
                enumeration.toString();
    }

    public static ObservableList<String> enumValuesToList(Resource resource, Enum<?>... values) {
        ObservableList<String> result = FXCollections.observableArrayList();
        for (Enum<?> value : values) {
            result.add(enumValueToString(resource, value));
        }
        return result;
    }

    public static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        mapper.setDateFormat(SharedConstants.DEFAULT_JSON_DATE_TIME_FORMAT);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        // deprectated setVisibilityChecker in Jackson version > 2.6
        mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker().withFieldVisibility(JsonAutoDetect.Visibility.NONE)
                .withGetterVisibility(JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC)
                .withSetterVisibility(JsonAutoDetect.Visibility.PROTECTED_AND_PUBLIC).withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        return mapper;
    }

    public static <T> T cloneObject(Class<T> clazz, Object object, ObjectMapper mapper) throws IOException {
        return mapper.readValue(mapper.writeValueAsString(object), clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T cloneObjectJaxb(Class<T> clazz, Object object) throws IOException {
        StringReader reader = null;
        try (StringWriter writer = new StringWriter()) {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(object, writer);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            reader = new StringReader(writer.toString());
            Object o = jaxbUnmarshaller.unmarshal(reader);
            if (clazz.isInstance(o)) {
                return (T) o;
            }
        } catch (JAXBException jaxb) {
            LOG.error("JAXBException", jaxb);
        }
        if (reader != null) {
            reader.close();
        }
        return null;
    }

    public static void unbindAllProperties(Object modelInstance, String... exclude) {
        Set<String> includes = new HashSet<>();
        Set<String> excludes = new HashSet<>(Arrays.asList(exclude));
        unbindProperties(modelInstance, includes, excludes, false);
    }

    public static void unbindProperties(Object modelInstance, String... include) {
        Set<String> includes = new HashSet<>(Arrays.asList(include));
        Set<String> excludes = new HashSet<>();
        unbindProperties(modelInstance, includes, excludes, false);
    }

    public static void unbindProperties(Object modelInstance, Set<String> includes, Set<String> excludes, boolean verbose) {
        Method[] publicMethods = modelInstance.getClass().getMethods();
        for (Method method : publicMethods) {
            if (checkIgnoreMethod(method, includes, excludes, verbose)) {
                continue;
            }
            try {
                Property<?> property = (Property<?>) method.invoke(modelInstance);
                // TODO uncomment, if we don't want to see all those lines
                // if (verbose)
                LOG.debug("Unbinding property '{}'.", property.getName());
                property.unbind();
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private static boolean checkIgnoreMethod(Method method, Set<String> includes, Set<String> excludes, boolean verbose) {
        String methodName = method.getName();
        if (!methodName.endsWith("Property")) {
            if (verbose)
                LOG.trace("Ignoring method '{}'. Does not end with 'Property'.", methodName);
            return true;
        }
        if (!excludes.isEmpty() && checkMethodName(excludes, methodName)) {
            if (verbose)
                LOG.trace("Ignoring method '{}'. Excluded.", methodName);
            return true;
        }
        if (!includes.isEmpty() && !checkMethodName(includes, methodName)) {
            if (verbose)
                LOG.trace("Ignoring method '{}'. Not included.", methodName);
            return true;
        }
        if (method.getReturnType().isAssignableFrom(Property.class)) {
            if (verbose)
                LOG.trace("Ignoring method '{}': return type '{}' is not assignable from '{}'.", methodName, method.getReturnType().getName(),
                        Property.class.getName());
            return true;
        }
        return false;
    }

    private static boolean checkMethodName(Set<String> set, String methodName) {
        if (set.contains(methodName))
            return true;
        for (String s : set) {
            if (methodName.startsWith(s))
                return true;
        }
        return false;
    }

    public static String windowParam(String applicationPrefix, String coreType) {
        return applicationPrefix + (!applicationPrefix.endsWith(".") && !coreType.startsWith(".") ? "." : "") + coreType;
    }

    public static WindowSetup defaultWindowSetup() {
        return new WindowSetup.Builder().maximized(false).height(800).width(1350).posX(Double.NEGATIVE_INFINITY).posY(Double.NEGATIVE_INFINITY)
                .build();
    }

    public static WindowSetup windowSetup(Stage stage, ConfigService configService) {

        WindowSetup defaultSetup = defaultWindowSetup();
        WindowSetup windowSetup = loadWindowSetup(configService, defaultSetup);

        ObservableList<Screen> intersect = Screen
                .getScreensForRectangle(windowSetup.getPosX(), windowSetup.getPosY(), windowSetup.getWidth(), windowSetup.getHeight());
        if (intersect.isEmpty()) {
            // use primary screen
            intersect.add(Screen.getPrimary());
        } else if (intersect.size() > 1) {
            // get screen with the upper left corner
            ObservableList<Screen> cornerIntersect = Screen.getScreensForRectangle(windowSetup.getPosX(), windowSetup.getPosY(), 1.0, 1.0);
            if (cornerIntersect.isEmpty()) {
                double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
                for (Screen screen : intersect) {
                    minX = Math.min(minX, screen.getVisualBounds().getMinX());
                    minY = Math.min(minY, screen.getVisualBounds().getMinY());
                }
                intersect = Screen.getScreensForRectangle(minX, minY, 1.0, 1.0);

                windowSetup.setPosX(minX);
                windowSetup.setPosY(minY);
            } else {
                intersect = cornerIntersect;
            }
        }

        Rectangle2D screenBounds = intersect.get(0).getVisualBounds();

        if (windowSetup.isMaximized()) {
            windowSetup.setWidth(defaultSetup.getWidth());
            windowSetup.setHeight(defaultSetup.getHeight());
            // windowSetup.setPosX(defaultSetup.getPosX());
            // windowSetup.setPosY(defaultSetup.getPosY());
        }

        if (windowSetup.getWidth() > screenBounds.getWidth()) {
            windowSetup.setWidth(screenBounds.getWidth());
            windowSetup.setPosX(screenBounds.getMinX());
        }
        if (windowSetup.getHeight() > screenBounds.getHeight()) {
            windowSetup.setHeight(screenBounds.getHeight());
            windowSetup.setPosY(screenBounds.getMinY());
        }

        stage.setMaximized(windowSetup.isMaximized());
        stage.setHeight(windowSetup.getHeight());
        stage.setWidth(windowSetup.getWidth());

        if (Double.NEGATIVE_INFINITY != windowSetup.getPosX()) {
            if (windowSetup.getPosX() + windowSetup.getWidth() > screenBounds.getMaxX())
                windowSetup.setPosX(screenBounds.getMaxX() - windowSetup.getWidth());
            stage.setX(windowSetup.getPosX());
        }
        if (Double.NEGATIVE_INFINITY != windowSetup.getPosY()) {
            if (windowSetup.getPosY() + windowSetup.getHeight() > screenBounds.getMaxY())
                windowSetup.setPosY(screenBounds.getMaxY() - windowSetup.getHeight());
            stage.setY(windowSetup.getPosY());
        }

        return windowSetup;
    }

    protected static WindowSetup loadWindowSetup(ConfigService configService, WindowSetup defaultSetup) {
        Optional<Boolean> maximized = configService.getAttribute(SharedConstants.WINDOW_PARAM_MAXIMIZED);
        Optional<Double> height = configService.getAttribute(SharedConstants.WINDOW_PARAM_HEIGHT);
        Optional<Double> width = configService.getAttribute(SharedConstants.WINDOW_PARAM_WIDTH);
        Optional<Double> posX = configService.getAttribute(SharedConstants.WINDOW_PARAM_POS_X);
        Optional<Double> posY = configService.getAttribute(SharedConstants.WINDOW_PARAM_POS_Y);
        WindowSetup.Builder builder = new WindowSetup.Builder();
        builder.maximized(maximized.orElse(defaultSetup.isMaximized()));
        builder.height(height.orElse(defaultSetup.getHeight()));
        builder.width(width.orElse(defaultSetup.getWidth()));
        builder.posX(posX.orElse(defaultSetup.getPosX()));
        builder.posY(posY.orElse(defaultSetup.getPosY()));
        return builder.build();
    }

    public static void saveWindowSetup(ConfigService configService, WindowSetup windowSetup) {
        boolean b;
        b = configService.setAttribute(SharedConstants.WINDOW_PARAM_MAXIMIZED, windowSetup.isMaximized());
        if (!b)
            LOG.warn("Could not set apptribute '{}' to value '{}'.", SharedConstants.WINDOW_PARAM_MAXIMIZED, windowSetup.isMaximized());
        b = configService.setAttribute(SharedConstants.WINDOW_PARAM_HEIGHT, windowSetup.getHeight());
        if (!b)
            LOG.warn("Could not set apptribute '{}' to value '{}'.", SharedConstants.WINDOW_PARAM_HEIGHT, windowSetup.getHeight());
        b = configService.setAttribute(SharedConstants.WINDOW_PARAM_WIDTH, windowSetup.getWidth());
        if (!b)
            LOG.warn("Could not set apptribute '{}' to value '{}'.", SharedConstants.WINDOW_PARAM_WIDTH, windowSetup.getWidth());
        b = configService.setAttribute(SharedConstants.WINDOW_PARAM_POS_X, windowSetup.getPosX());
        if (!b)
            LOG.warn("Could not set apptribute '{}' to value '{}'.", SharedConstants.WINDOW_PARAM_POS_X, windowSetup.getPosX());
        b = configService.setAttribute(SharedConstants.WINDOW_PARAM_POS_Y, windowSetup.getPosY());
        if (!b)
            LOG.warn("Could not set apptribute '{}' to value '{}'.", SharedConstants.WINDOW_PARAM_POS_Y, windowSetup.getPosY());
        b = configService.update();
        if (!b)
            LOG.warn("Could not update the configuration.");
    }
}
