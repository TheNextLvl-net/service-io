package net.thenextlvl.services.api.permission;

import net.kyori.adventure.util.TriState;
import org.bukkit.World;

public interface PermissionHolder {
    TriState addPermission(String permission);

    TriState addPermission(World world, String permission);

    TriState checkPermission(String permission);

    TriState checkPermission(World world, String permission);

    TriState removePermission(String permission);

    TriState removePermission(World world, String permission);
}
