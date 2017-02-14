package eu.dzim.poc.fx.resource.impl;

import java.util.Locale;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import eu.dzim.poc.fx.resource.BaseResource;
import eu.dzim.poc.fx.resource.Resource;

@Component
@Scope("singleton")
public class ResourceImpl extends BaseResource implements Resource {
	
	public ResourceImpl() {
		// e.g. BaseResource.class.getPackage().getName()
		super("i18n", "strings", Locale.ENGLISH);
	}
}