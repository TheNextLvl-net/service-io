package net.thenextlvl.services.api.permission;

import org.bukkit.command.CommandSender;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PermissionController {
    CompletableFuture<PermissionHolder> getPermissionHolder(String name);

    CompletableFuture<PermissionHolder> getPermissionHolder(UUID uniqueId);

    CompletableFuture<PermissionHolder> getPermissionHolder(CommandSender uniqueId);
}
