package net.thenextlvl.service.plugin;

import dev.faststats.core.ErrorTracker;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

public final class ServiceBootstrapper implements PluginBootstrap {
    public static final ErrorTracker ERROR_TRACKER = ErrorTracker.contextAware();
    public static final String ISSUES = "https://github.com/TheNextLvl-net/service-io/issues/new?template=bug_report.yml";
    public static final @Nullable String COMPATIBILITY_MODE = System.getenv("COMPATIBILITY_MODE");
    private static final Pattern SEMVER_PATTERN = Pattern.compile("^\\d+\\.\\d+\\.\\d+$");

    @Override
    public void bootstrap(final BootstrapContext context) {
    }

    @Override
    public JavaPlugin createPlugin(final PluginProviderContext context) {
        final var plugin = PluginBootstrap.super.createPlugin(context);
        resolveCompatibilityVersion(context).ifPresent(version -> enableCompatibilityMode(context, version));
        return plugin;
    }

    private Optional<String> resolveCompatibilityVersion(final PluginProviderContext context) {
        if (COMPATIBILITY_MODE == null) return Optional.empty();

        final var value = COMPATIBILITY_MODE.trim().toLowerCase(Locale.ROOT);
        switch (value) {
            case "", "false" -> {
                return Optional.empty();
            }
            case "1" -> {
                return Optional.of("1.7.3");
            }
            case "2", "true" -> {
                return Optional.of("2.19.0");
            }
        }
        if (SEMVER_PATTERN.matcher(value).matches()) return Optional.of(value);

        context.getLogger().warn("Ignoring COMPATIBILITY_MODE value '{}': expected false, 1, 2, true, or a version like 2.19.0", value);
        return Optional.empty();
    }

    private void enableCompatibilityMode(final PluginProviderContext context, final String version) {
        final var logger = context.getLogger();
        try {
            final var meta = context.getConfiguration();

            final var metaClass = meta.getClass();
            final var name = metaClass.getDeclaredField("name");
            final var provides = metaClass.getDeclaredField("provides");
            final var pluginVersion = metaClass.getDeclaredField("version");

            name.trySetAccessible();
            provides.trySetAccessible();
            pluginVersion.trySetAccessible();

            final var providedPlugins = new ArrayList<>(meta.getProvidedPlugins());
            providedPlugins.remove("Vault");
            providedPlugins.add(meta.getName());

            name.set(meta, "Vault");
            provides.set(meta, List.copyOf(providedPlugins));
            pluginVersion.set(meta, version);
            logger.info("Enabled compatibility mode with version {}, only use this if you really need to.", version);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            logger.warn("Failed to initialize compatibility mode", e);
            logger.warn("Please look for similar issues or report this on GitHub: {}", ISSUES);
            ERROR_TRACKER.trackError(e);
        }
    }
}
