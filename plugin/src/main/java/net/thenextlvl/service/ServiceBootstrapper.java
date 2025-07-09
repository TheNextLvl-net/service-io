package net.thenextlvl.service;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class ServiceBootstrapper implements PluginBootstrap {
    public static final String ISSUES = "https://github.com/TheNextLvl-net/service-io/issues/new?template=bug_report.yml";
    public static final boolean COMPATIBILITY_MODE = Boolean.parseBoolean(System.getenv("COMPATIBILITY_MODE"));

    @Override
    public void bootstrap(BootstrapContext context) {
        if (COMPATIBILITY_MODE) enableCompatibilityMode(context);
    }

    private void enableCompatibilityMode(BootstrapContext context) {
        var logger = context.getLogger();
        try {
            var meta = context.getConfiguration();

            var metaClass = meta.getClass();
            var name = metaClass.getDeclaredField("name");
            var provides = metaClass.getDeclaredField("provides");

            name.trySetAccessible();
            provides.trySetAccessible();

            var providedPlugins = new ArrayList<>(meta.getProvidedPlugins());
            providedPlugins.remove("Vault");
            providedPlugins.add(meta.getName());

            name.set(meta, "Vault");
            provides.set(meta, List.copyOf(providedPlugins));
            logger.info("Enabled compatibility mode, only use this if you really need to.");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.warn("Failed to initialize compatibility mode", e);
            logger.warn("Please look for similar issues or report this on GitHub: {}", ISSUES);
        }
    }
}
