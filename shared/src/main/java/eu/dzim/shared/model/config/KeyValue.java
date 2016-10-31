package eu.dzim.shared.model.config;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class KeyValue {
	
	private String key;
	private String value;
	private KeyValueType type;
	
	public KeyValue() {
		// sonar
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public KeyValueType getType() {
		return type;
	}
	
	public void setType(KeyValueType type) {
		this.type = type;
	}
	
	@SuppressWarnings("unchecked")
	@JsonIgnore
	public <T> T getGenericValue() {
		return (T) getGenericValueFromString(type, value);
	}
	
	@JsonIgnore
	private Object getGenericValueFromString(KeyValueType type, String valueString) {
		Object value;
		if (KeyValueType.BOOLEAN == type) {
			value = Boolean.parseBoolean(valueString);
		} else if (KeyValueType.LONG == type) {
			value = Long.parseLong(valueString);
		} else if (KeyValueType.DOUBLE == type) {
			value = Double.parseDouble(valueString);
		} else {
			value = valueString;
		}
		return value;
	}
}
