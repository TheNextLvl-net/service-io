package net.thenextlvl.services;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.services.api.ServiceProvider;
import net.thenextlvl.services.api.capability.CapabilityController;
import net.thenextlvl.services.api.chat.ChatController;
import net.thenextlvl.services.api.economy.EconomyController;
import net.thenextlvl.services.api.permission.GroupController;
import net.thenextlvl.services.capability.PaperCapabilityController;
import net.thenextlvl.services.chat.PaperChatController;
import net.thenextlvl.services.hook.vault.chat.VaultChatGroupManager;
import net.thenextlvl.services.hook.vault.chat.VaultChatLuckPerms;
import net.thenextlvl.services.hook.vault.permission.VaultGroupManager;
import net.thenextlvl.services.hook.vault.permission.VaultLuckPerms;
import net.thenextlvl.services.hook.vault.permission.VaultSuperPerms;
import net.thenextlvl.services.listener.PluginListener;
import net.thenextlvl.services.version.PluginVersionChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

@Getter
@Accessors(fluent = true)
public class ServicePlugin extends JavaPlugin implements ServiceProvider {
    private final Map<Plugin, CapabilityController> capabilityControllers = new HashMap<>();
    private final PluginVersionChecker versionChecker = new PluginVersionChecker(this);
    private final Metrics metrics = new Metrics(this, 23083);

    private @Nullable Permission permissions;

    @Override
    public void onLoad() {
        versionChecker().checkVersion();
        registerServices();
    }

    @Override
    public void onEnable() {
        loadPermissionControllers();
        loadChatControllers();

        loadVaultPermission();
        loadVaultChat();
    }

    @Override
    public void onDisable() {
        metrics().shutdown();
    }

    private void registerServices() {
        getServer().getServicesManager().register(ServiceProvider.class, this, this, ServicePriority.Highest);
    }

    @Override
    public CapabilityController capabilityController(Plugin plugin) {
        return capabilityControllers().computeIfAbsent(plugin, owner ->
                new PaperCapabilityController(owner, this));
    }

    @Override
    public ChatController chatController() {
        return Objects.requireNonNullElseGet(
                getServer().getServicesManager().load(ChatController.class),
                () -> new PaperChatController(this)
        );
    }

    @Override
    public EconomyController economyController() {
        return Preconditions.checkNotNull(
                getServer().getServicesManager().load(EconomyController.class),
                "No EconomyController available"
        );
    }

    @Override
    public GroupController groupController() {
        return Preconditions.checkNotNull(
                getServer().getServicesManager().load(GroupController.class),
                "No GroupController available"
        );
    }

    private void loadPermissionControllers() {
    }

    private void loadChatControllers() {
    }

    private void loadVaultChat() {
        hookVaultChat("GroupManager", () -> new VaultChatGroupManager(this, permissions), ServicePriority.High);
        hookVaultChat("LuckPerms", () -> new VaultChatLuckPerms(this, permissions), ServicePriority.Highest);
    }

    private void loadVaultPermission() {
        hookVaultPermission("GroupManager", () -> new VaultGroupManager(this), ServicePriority.High);
        hookVaultPermission("LuckPerms", () -> new VaultLuckPerms(this), ServicePriority.Highest);

        var permissions = new VaultSuperPerms(this);
        getServer().getServicesManager().register(Permission.class, permissions, this, ServicePriority.Lowest);
        getComponentLogger().info("[Permission] SuperPermissions loaded as backup permission system.");

        this.permissions = getServer().getServicesManager().load(Permission.class);
    }

    private void hookVaultChat(String name, Callable<? extends Chat> callable, ServicePriority priority) {
        try {
            if (getServer().getPluginManager().getPlugin(name) == null) return;
            var hook = callable.call();
            getServer().getServicesManager().register(Chat.class, hook, this, priority);
            getComponentLogger().info("[Chat] Registered Vault support for {}", name);
        } catch (Exception e) {
            getComponentLogger().error("[Chat] Failed to register Vault support for {}" +
                                       " - check to make sure you're using a compatible version!", name, e);
        }
    }

    private void hookVaultPermission(String name, Callable<? extends Permission> callable, ServicePriority priority) {
        try {
            if (getServer().getPluginManager().getPlugin(name) == null) return;
            var hook = callable.call();
            getServer().getServicesManager().register(Permission.class, hook, this, priority);
            getComponentLogger().info("[Permission] Registered Vault support for {}", name);
        } catch (Exception e) {
            getComponentLogger().error("[Permission] Failed to register Vault support for {}" +
                                       " - check to make sure you're using a compatible version!", name, e);
        }
    }
}