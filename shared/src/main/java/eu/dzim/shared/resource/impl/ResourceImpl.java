package eu.dzim.shared.resource.impl;

import eu.dzim.shared.resource.BaseResource;

import java.util.Locale;

public class ResourceImpl extends BaseResource {

    public ResourceImpl() {
        // e.g. BaseResource.class.getPackage().getName()
        super("i18n", "strings", Locale.ENGLISH);
    }
}