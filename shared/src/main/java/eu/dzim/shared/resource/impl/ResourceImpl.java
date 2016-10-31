package eu.dzim.shared.resource.impl;

import java.util.Locale;

import eu.dzim.shared.resource.BaseResource;

public class ResourceImpl extends BaseResource {
	
	public ResourceImpl() {
		// e.g. BaseResource.class.getPackage().getName()
		super("i18n", "strings", Locale.ENGLISH);
	}
}