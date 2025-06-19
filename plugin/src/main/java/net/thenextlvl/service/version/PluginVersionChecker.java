package net.thenextlvl.service.version;

import core.paper.version.PaperModrinthVersionChecker;
import core.version.SemanticVersion;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PluginVersionChecker extends PaperModrinthVersionChecker<SemanticVersion> {
    public PluginVersionChecker(Plugin plugin) {
        super(plugin, "MNPyHOe7");
    }

    @Override
    public SemanticVersion parseVersion(String version) {
        return SemanticVersion.parse(version);
    }
}
