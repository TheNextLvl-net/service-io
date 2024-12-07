package net.thenextlvl.service.api.model;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface Positioned {
    Location getLocation();

    Server getServer();

    World getWorld();

    double getX();

    double getY();

    double getZ();

    float getPitch();

    float getYaw();
}
