package net.thenextlvl.service.version;

import net.thenextlvl.version.SemanticVersion;
import net.thenextlvl.version.modrinth.paper.PaperModrinthVersionChecker;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PluginVersionChecker extends PaperModrinthVersionChecker<SemanticVersion> {
    public PluginVersionChecker(final Plugin plugin) {
        super(plugin, "MNPyHOe7");
    }

    @Override
    public SemanticVersion parseVersion(final String version) {
        return SemanticVersion.parse(version);
    }
}
