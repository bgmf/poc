package eu.dzim.guice.fx.resource.impl;

import java.util.Locale;

import eu.dzim.guice.fx.resource.BaseResource;

public class ResourceImpl extends BaseResource {
	
	public ResourceImpl() {
		// e.g. BaseResource.class.getPackage().getName()
		super("i18n", "strings", Locale.ENGLISH);
	}
}