package eu.dzim.shared.fx.integration;

import eu.dzim.shared.fx.integration.impl.PluginServiceLoader;

public class PluginIntegratorFactory {

    private static PluginIntegrator integrator;

    public static PluginIntegrator getInstance() {
        if (integrator == null)
            integrator = new PluginServiceLoader();
        return integrator;
    }
}
