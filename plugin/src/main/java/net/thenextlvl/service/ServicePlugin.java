package net.thenextlvl.service;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.vault.chat.GroupManagerVaultChat;
import net.thenextlvl.service.vault.chat.LuckPermsVaultChat;
import net.thenextlvl.service.vault.permission.VaultGroupManager;
import net.thenextlvl.service.vault.permission.VaultLuckPerms;
import net.thenextlvl.service.vault.permission.VaultSuperPerms;
import net.thenextlvl.service.version.PluginVersionChecker;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Callable;
import java.util.function.Function;

@Getter
@Accessors(fluent = true)
public class ServicePlugin extends JavaPlugin {
    private final PluginVersionChecker versionChecker = new PluginVersionChecker(this);
    private final Metrics metrics = new Metrics(this, 23083);

    private Permission permissions = new VaultSuperPerms(this);

    @Override
    public void onLoad() {
        versionChecker().checkVersion();
    }

    @Override
    public void onEnable() {
        loadPermissionControllers();
        loadChatControllers();

        loadVaultPermission();
        loadVaultChat();

        addCustomCharts();
    }

    @Override
    public void onDisable() {
        metrics().shutdown();
    }

    private void addCustomCharts() {
        addCustomChart(Chat.class, Chat::getName, "chat");
        addCustomChart(Economy.class, Economy::getName, "economy");
        addCustomChart(Permission.class, Permission::getName, "permission");
    }

    private <T> void addCustomChart(Class<T> service, Function<T, String> function, String name) {
        var loaded = getServer().getServicesManager().load(service);
        metrics.addCustomChart(new SimplePie(name, () -> loaded != null ? function.apply(loaded) : "None"));
    }

    private void loadPermissionControllers() {
    }

    private void loadChatControllers() {
    }

    private void loadVaultChat() {
        hookVaultChat("GroupManager", () -> new GroupManagerVaultChat(this, permissions), ServicePriority.High);
        hookVaultChat("LuckPerms", () -> new LuckPermsVaultChat(this, permissions), ServicePriority.Highest);
    }

    private void loadVaultPermission() {
        hookVaultPermission("GroupManager", () -> new VaultGroupManager(this), ServicePriority.High);
        hookVaultPermission("LuckPerms", () -> new VaultLuckPerms(this), ServicePriority.Highest);

        getServer().getServicesManager().register(Permission.class, this.permissions, this, ServicePriority.Lowest);
        getComponentLogger().info("[Permission] Registered SuperPerms as backup provider");

        var permissions = getServer().getServicesManager().load(Permission.class);
        if (permissions != null) this.permissions = permissions;
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