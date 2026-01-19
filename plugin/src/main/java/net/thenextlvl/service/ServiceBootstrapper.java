package net.thenextlvl.service;

import dev.faststats.core.ErrorTracker;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class ServiceBootstrapper implements PluginBootstrap {
    public static final ErrorTracker ERROR_TRACKER = ErrorTracker.contextAware();
    public static final String ISSUES = "https://github.com/TheNextLvl-net/service-io/issues/new?template=bug_report.yml";
    public static final boolean COMPATIBILITY_MODE = Boolean.parseBoolean(System.getenv("COMPATIBILITY_MODE"));

    @Override
    public void bootstrap(BootstrapContext context) {
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        var plugin = PluginBootstrap.super.createPlugin(context);
        if (COMPATIBILITY_MODE) enableCompatibilityMode(context);
        return plugin;
    }

    private void enableCompatibilityMode(PluginProviderContext context) {
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
            ERROR_TRACKER.trackError(e);
        }
    }
}
