package eu.dzim.poc.fx.resource;

import eu.dzim.shared.util.BaseEnumType;
import javafx.beans.Observable;
import javafx.beans.binding.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

import java.util.*;

/**
 * Implementation for the {@link Resource} interface.<br>
 * <br>
 * Intened to be used as a singleton instance via Guice.
 *
 * @author daniel.zimmermann@cnlab.ch
 */
public abstract class BaseResource implements Resource {

    private static final String DEFAULT_PACKAGE_NAME = BaseResource.class.getPackage().getName();
    private static final String DEFAULT_PROPERTIES_NAME = "strings";

    /**
     * The path, where the property file - containing translated strings - can be found. Be aware, that in the moment that means: inside the
     * {@link ClassLoader}s hierarchy. Be also aware, that the {@link ResourceBundle} mechanism doesn't need neither the underscore-language part nor
     * the dot-properties file ending to be attached.
     */
    private final String bundleName;

    /**
     * A reference to the current active {@link Locale}. Per default the System locale will be used, but is likly overwritten on the constructor.
     */
    private final ReadOnlyObjectWrapper<Locale> locale = new ReadOnlyObjectWrapper<>(this, "locale", Locale.getDefault());

    /**
     * A reference to the current active language bundle. Per default the bundle for the English locale will be loaded.
     */
    private final ReadOnlyObjectWrapper<ResourceBundle> resourceBundle = new ReadOnlyObjectWrapper<>(this, "resourceBundle");

    /**
     * a container for fast access to <b>unparameterized</b> {@link Binding}s - {@link StringBinding}s with parameters are created on the fly
     */
    private final Map<String, StringBinding> keyStringBindings = new HashMap<>();
    private final Map<String, BooleanBinding> keyBooleanBindings = new HashMap<>();
    private final Map<String, IntegerBinding> keyIntegerBindings = new HashMap<>();
    private final Map<String, LongBinding> keyLongBindings = new HashMap<>();
    private final Map<String, DoubleBinding> keyDoubleBindings = new HashMap<>();

    protected BaseResource(String stringsContainerPackage, String resourceFilePrefix, Locale defaultLocale) {

        if (stringsContainerPackage == null || stringsContainerPackage.isEmpty())
            throw new IllegalArgumentException("The package with the strings.properties file must not be empty!");

        if (resourceFilePrefix == null || resourceFilePrefix.isEmpty())
            throw new IllegalArgumentException("The resource file prefix (e.g. 'strings' for 'strings.properties') must not be empty!");

        bundleName = String.format("%s.%s", stringsContainerPackage, resourceFilePrefix);
        Locale locale = defaultLocale != null ?
                new Locale(defaultLocale.getLanguage().toLowerCase()) :
                new Locale(Locale.getDefault().getLanguage().toLowerCase());
        this.locale.set(locale);
        this.resourceBundle.set(ResourceBundle.getBundle(bundleName, locale, getClass().getClassLoader()));
    }

    protected BaseResource(String stringsContainerPackage, Locale defaultLocale) {
        this(stringsContainerPackage, DEFAULT_PROPERTIES_NAME, defaultLocale);
    }

    protected BaseResource(String stringsContainerPackage, String resourceFilePrefix) {
        this(stringsContainerPackage, resourceFilePrefix, Locale.ENGLISH);
    }

    protected BaseResource(String stringsContainerPackage) {
        this(stringsContainerPackage, DEFAULT_PROPERTIES_NAME, Locale.ENGLISH);
    }

    protected BaseResource(Locale defaultLocale) {
        this(DEFAULT_PACKAGE_NAME, DEFAULT_PROPERTIES_NAME, defaultLocale);
    }

    protected BaseResource() {
        this(DEFAULT_PACKAGE_NAME, DEFAULT_PROPERTIES_NAME, Locale.ENGLISH);
    }

    @Override
    public synchronized boolean setLanguage(String language) {
        Locale next = new Locale(language.toLowerCase());
        return setLocale(next);
    }

    @Override
    public boolean setLocale(Locale next) {
        Locale current = Locale.getDefault();
        if (current == null || !current.equals(next)) {
            resourceBundle.set(ResourceBundle.getBundle(bundleName, next, getClass().getClassLoader()));
            locale.set(next);
        }
        return false;
    }

    public ReadOnlyObjectProperty<Locale> localeProperty() {
        return locale.getReadOnlyProperty();
    }

    @Override
    public Locale getLocale() {
        return locale.get();
    }

    @Override
    public ReadOnlyObjectProperty<ResourceBundle> resourceBundleProperty() {
        return resourceBundle.getReadOnlyProperty();
    }

    @Override
    public synchronized ResourceBundle getResourceBundle() {
        return resourceBundle.get();
    }

    @Override
    public synchronized String getGuranteedString(String key) {
        try {
            return resourceBundle.get().getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

    @Override
    public String getGuranteedString(String key, Object... args) {
        String value = "!empty!";
        try {
            value = resourceBundle.get().getString(key);
        } catch (MissingResourceException e) {
            value = '!' + key + '!';
        }
        if (value.startsWith("!") && value.endsWith("!"))
            return value;
        return String.format(value, args);
    }

    @Override
    public Optional<String> getString(String key) {
        try {
            return Optional.of(resourceBundle.get().getString(key));
        } catch (MissingResourceException e) {
            return Optional.empty();
        }
    }

    @Override
    public synchronized Optional<String> getString(String key, Object... args) {
        String value = "!empty!";
        try {
            value = resourceBundle.get().getString(key);
        } catch (MissingResourceException e) {
            value = '!' + key + '!';
        }
        if (value.startsWith("!") && value.endsWith("!"))
            return Optional.empty();
        return Optional.of(String.format(value, args));
    }

    @Override
    public synchronized Optional<Boolean> getBoolean(String key) {
        try {
            return Optional.of(Boolean.parseBoolean(resourceBundle.get().getString(key)));
        } catch (MissingResourceException e) {
            return Optional.empty();
        }
    }

    @Override
    public Boolean getBoolean(String key, Boolean defaultValue) {
        return getBoolean(key).orElse(defaultValue);
    }

    @Override
    public Optional<Integer> getInteger(String key) {
        try {
            return Optional.of(Integer.parseInt(resourceBundle.get().getString(key)));
        } catch (MissingResourceException | NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public Integer getInteger(String key, Integer defaultValue) {
        return getInteger(key).orElse(defaultValue);
    }

    @Override
    public synchronized Optional<Long> getLong(String key) {
        try {
            return Optional.of(Long.parseLong(resourceBundle.get().getString(key)));
        } catch (MissingResourceException | NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public Long getLong(String key, Long defaultValue) {
        return getLong(key).orElse(defaultValue);
    }

    @Override
    public synchronized Optional<Double> getDouble(String key) {
        try {
            return Optional.of(Double.parseDouble(resourceBundle.get().getString(key)));
        } catch (MissingResourceException | NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public Double getDouble(String key, Double defaultValue) {
        return getDouble(key).orElse(defaultValue);
    }

    @Override
    public Binding<String> getBinding(String key, Object... parameter) {
        return createStringBinding(key, null, parameter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Binding<T> getBinding(String key, T defaultValue, Object... parameter) {
        if (defaultValue == null)
            return (Binding<T>) getBinding(key, parameter);
        if (defaultValue instanceof String) {
            return (Binding<T>) createStringBinding(key, (String) defaultValue, parameter);
        } else if (defaultValue instanceof Boolean) {
            return (Binding<T>) createBooleanBinding(key, (Boolean) defaultValue);
        } else if (defaultValue instanceof Long) {
            return (Binding<T>) createLongBinding(key, (Long) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return (Binding<T>) createIntegerBinding(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Double) {
            return (Binding<T>) createDoubleBinding(key, (Double) defaultValue);
        }
        throw new IllegalArgumentException("Unknown type to create a binding for: " + defaultValue.getClass().getName());
    }

    @Override
    public Binding<String> getBinding(ObjectProperty<? extends BaseEnumType> type, Object... parameter) {
        return createStringBindingForBaseEnumType(type, null, null, parameter);
    }

    @Override
    public Binding<String> getBinding(ObjectProperty<? extends BaseEnumType> type, List<Observable> dependencies, Object... parameter) {
        return createStringBindingForBaseEnumType(type, null, dependencies, parameter);
    }

    @Override
    public Binding<String> getBinding(ObjectProperty<? extends BaseEnumType> type, String defaultValue, List<Observable> dependencies,
            Object... parameter) {
        return createStringBindingForBaseEnumType(type, defaultValue, dependencies, parameter);
    }

    private StringBinding createStringBinding(final String key, final String defaultValue, final Object... parameter) {
        if (parameter == null || parameter.length == 0) {
            StringBinding binding = keyStringBindings.get(key);
            if (binding != null)
                return binding;
            binding = Bindings
                    .createStringBinding(() -> defaultValue == null ? getGuranteedString(key) : getString(key).orElse(defaultValue), resourceBundle);
            keyStringBindings.put(key, binding);
            return binding;
        } else {
            return Bindings.createStringBinding(() -> defaultValue == null ?
                    getGuranteedString(key, parameter) :
                    String.format(getLocale(), getString(key).orElse(defaultValue), parameter), resourceBundle);
        }
    }

    private BooleanBinding createBooleanBinding(final String key, final Boolean defaultValue) {
        BooleanBinding binding = keyBooleanBindings.get(key);
        if (binding != null)
            return binding;
        binding = Bindings.createBooleanBinding(() -> getBoolean(key, defaultValue), resourceBundle);
        keyBooleanBindings.put(key, binding);
        return binding;
    }

    private IntegerBinding createIntegerBinding(final String key, final Integer defaultValue) {
        IntegerBinding binding = keyIntegerBindings.get(key);
        if (binding != null)
            return binding;
        binding = Bindings.createIntegerBinding(() -> getInteger(key, defaultValue), resourceBundle);
        keyIntegerBindings.put(key, binding);
        return binding;
    }

    private LongBinding createLongBinding(final String key, final Long defaultValue) {
        LongBinding binding = keyLongBindings.get(key);
        if (binding != null)
            return binding;
        binding = Bindings.createLongBinding(() -> getLong(key, defaultValue), resourceBundle);
        keyLongBindings.put(key, binding);
        return binding;
    }

    private DoubleBinding createDoubleBinding(final String key, final Double defaultValue) {
        DoubleBinding binding = keyDoubleBindings.get(key);
        if (binding != null)
            return binding;
        binding = Bindings.createDoubleBinding(() -> getDouble(key, defaultValue), resourceBundle);
        keyDoubleBindings.put(key, binding);
        return binding;
    }

    private StringBinding createStringBindingForBaseEnumType(final ObjectProperty<? extends BaseEnumType> type, final String defaultValue,
            final List<Observable> dependencies, final Object... parameter) {
        final List<Observable> finalDependencies = new ArrayList<>();
        finalDependencies.addAll(Arrays.asList(type, resourceBundle));
        if (dependencies != null && !dependencies.isEmpty())
            finalDependencies.addAll(dependencies);
        // @formatter:off
		return Bindings.createStringBinding(
				() -> defaultValue == null 
						? getGuranteedString(type.get().getKey())
						: String.format(getLocale(), getString(type.get().getKey()).orElse(defaultValue), parameter),
				finalDependencies.stream().toArray(size -> new Observable[size]));
		// @formatter:on
    }
}
