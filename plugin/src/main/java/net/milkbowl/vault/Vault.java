package net.milkbowl.vault;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

/**
 * A compatibility class used to mitigate issues caused by certain plugins that check the instance
 * of the plugin for validation, potentially leading to class not found errors.
 */
@ApiStatus.Internal
public class Vault extends JavaPlugin {
    public double updateCheck(double currentVersion) {
        return currentVersion;
    }
}
