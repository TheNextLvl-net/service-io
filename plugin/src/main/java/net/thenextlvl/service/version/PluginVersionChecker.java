package net.thenextlvl.service.version;

import core.paper.version.PaperHangarVersionChecker;
import core.version.SemanticVersion;
import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class PluginVersionChecker extends PaperHangarVersionChecker<SemanticVersion> {
    private final SemanticVersion versionRunning;
    private final Plugin plugin;

    public PluginVersionChecker(Plugin plugin) {
        super("ServiceIO");
        this.plugin = plugin;
        this.versionRunning = Objects.requireNonNull(parseVersion(plugin.getPluginMeta().getVersion()));
    }

    @Override
    public @Nullable SemanticVersion parseVersion(String version) {
        return SemanticVersion.parse(version);
    }

    public void checkVersion() {
        retrieveLatestSupportedVersion(latest -> latest.ifPresentOrElse(version -> {
            if (version.equals(getVersionRunning())) {
                plugin.getComponentLogger().info("You are running the latest version of ServiceIO");
            } else if (version.compareTo(getVersionRunning()) > 0) {
                plugin.getComponentLogger().warn("An update for ServiceIO is available");
                plugin.getComponentLogger().warn("You are running version {}, the latest supported version is {}", getVersionRunning(), version);
                plugin.getComponentLogger().warn("Update at https://hangar.papermc.io/TheNextLvl/ServiceIO");
            } else {
                plugin.getComponentLogger().warn("You are running a snapshot version of ServiceIO");
            }
        }, () -> plugin.getComponentLogger().error("Version check failed")));
    }
}
