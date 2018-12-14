package eu.dzim.shared.model.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Attributes {

    private List<KeyValue> entries;

    public Attributes() {
        // sonar
    }

    public List<KeyValue> getEntries() {
        if (entries == null) {
            entries = new ArrayList<>();
        }
        return entries;
    }

    @JsonIgnore
    public <T> KeyValue addEntry(String key, T value) {
        final Class<?> clazz = value.getClass();
        final String stringValue;
        final KeyValueType type;
        if (Boolean.class.isAssignableFrom(clazz)) {
            stringValue = ((Boolean) value).toString();
            type = KeyValueType.BOOLEAN;
        } else if (Short.class.isAssignableFrom(clazz) || Integer.class.isAssignableFrom(clazz) || Long.class.isAssignableFrom(clazz)) {
            stringValue = "" + ((Number) value).longValue();
            type = KeyValueType.LONG;
        } else if (Float.class.isAssignableFrom(clazz) || Double.class.isAssignableFrom(clazz)) {
            stringValue = "" + ((Number) value).doubleValue();
            type = KeyValueType.DOUBLE;
        } else if (String.class.isAssignableFrom(clazz)) {
            stringValue = (String) value;
            type = KeyValueType.STRING;
        } else {
            stringValue = value.toString();
            type = KeyValueType.STRING;
        }
        Optional<KeyValue> oldEntry = findKeyValue(key);
        if (oldEntry.isPresent()) {
            KeyValue kvOld = oldEntry.get();
            kvOld.setValue(stringValue);
            kvOld.setType(type);
            return kvOld;
        } else {
            KeyValue kv = new KeyValue();
            kv.setKey(key);
            kv.setValue(stringValue);
            kv.setType(type);
            getEntries().add(kv);
            return kv;
        }
    }

    @JsonIgnore
    public <T> Optional<T> findValue(String key) {
        for (KeyValue kv : getEntries()) {
            if (!kv.getKey().equalsIgnoreCase(key))
                continue;
            return Optional.of(kv.getGenericValue());
        }
        return Optional.empty();
    }

    @JsonIgnore
    public Optional<KeyValue> findKeyValue(String key) {
        for (KeyValue kv : getEntries()) {
            if (!kv.getKey().equalsIgnoreCase(key))
                continue;
            return Optional.of(kv);
        }
        return Optional.empty();
    }

    @JsonIgnore
    public Optional<KeyValue> removeKeyValue(String key) {
        Optional<KeyValue> kv = findKeyValue(key);
        if (kv.isPresent()) {
            getEntries().remove(kv.get());
        }
        return kv;
    }
}
