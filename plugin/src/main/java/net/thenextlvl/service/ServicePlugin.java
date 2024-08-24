package net.thenextlvl.service;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.controller.chat.GroupManagerChatController;
import net.thenextlvl.service.controller.chat.LuckPermsChatController;
import net.thenextlvl.service.controller.group.GroupManagerGroupController;
import net.thenextlvl.service.controller.group.LuckPermsGroupController;
import net.thenextlvl.service.controller.permission.GroupManagerPermissionController;
import net.thenextlvl.service.controller.permission.LuckPermsPermissionController;
import net.thenextlvl.service.controller.permission.SuperPermsPermissionController;
import net.thenextlvl.service.version.PluginVersionChecker;
import net.thenextlvl.service.wrapper.VaultChatServiceWrapper;
import net.thenextlvl.service.wrapper.VaultPermissionServiceWrapper;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;
import java.util.function.Function;

@Getter
@Accessors(fluent = true)
public class ServicePlugin extends JavaPlugin {
    private final PluginVersionChecker versionChecker = new PluginVersionChecker(this);
    private final Metrics metrics = new Metrics(this, 23083);

    private @Nullable ChatController chatController = null;
    private @Nullable GroupController groupController = null;

    private @Nullable VaultPermissionServiceWrapper vaultPermissionWrapper = null;

    private PermissionController permissionController = new SuperPermsPermissionController();

    @Override
    public void onLoad() {
        versionChecker().checkVersion();
    }

    @Override
    public void onEnable() {
        loadPermissionServices();
        loadGroupServices();
        loadChatServices();

        loadVaultPermissionWrapper();
        loadVaultEconomyWrapper();
        loadVaultChatWrapper();

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

    private void loadChatServices() {
        hookChatService("GroupManager", GroupManagerChatController::new, ServicePriority.High);
        hookChatService("LuckPerms", LuckPermsChatController::new, ServicePriority.Highest);
        this.chatController = getServer().getServicesManager().load(ChatController.class);
    }

    private void loadGroupServices() {
        hookGroupService("GroupManager", GroupManagerGroupController::new, ServicePriority.High);
        hookGroupService("LuckPerms", LuckPermsGroupController::new, ServicePriority.Highest);
        this.groupController = getServer().getServicesManager().load(GroupController.class);
    }

    private void loadPermissionServices() {
        hookPermissionService("GroupManager", GroupManagerPermissionController::new, ServicePriority.High);
        hookPermissionService("LuckPerms", LuckPermsPermissionController::new, ServicePriority.Highest);

        getServer().getServicesManager().register(PermissionController.class, permissionController, this, ServicePriority.Lowest);
        getComponentLogger().info("Added SuperPerms as backup permission provider (Lowest)");

        var controller = getServer().getServicesManager().load(PermissionController.class);
        if (controller != null) this.permissionController = controller;
    }

    private void hookChatService(String name, Callable<? extends ChatController> callable, ServicePriority priority) {
        try {
            if (getServer().getPluginManager().getPlugin(name) == null) return;
            var hook = callable.call();
            getServer().getServicesManager().register(ChatController.class, hook, this, priority);
            getComponentLogger().info("Added {} as chat provider ({})", name, priority.name());
        } catch (Exception e) {
            getComponentLogger().error("Failed to register {} as chat provider - " +
                                       "check to make sure you're using a compatible version!", name, e);
        }
    }

    private void hookGroupService(String name, Callable<? extends GroupController> callable, ServicePriority priority) {
        try {
            if (getServer().getPluginManager().getPlugin(name) == null) return;
            var hook = callable.call();
            getServer().getServicesManager().register(GroupController.class, hook, this, priority);
            getComponentLogger().info("Added {} as group provider ({})", name, priority.name());
        } catch (Exception e) {
            getComponentLogger().error("Failed to register {} as group provider - " +
                                       "check to make sure you're using a compatible version!", name, e);
        }
    }

    private void hookPermissionService(String name, Callable<? extends PermissionController> callable, ServicePriority priority) {
        try {
            if (getServer().getPluginManager().getPlugin(name) == null) return;
            var hook = callable.call();
            getServer().getServicesManager().register(PermissionController.class, hook, this, priority);
            getComponentLogger().info("Added {} as permission provider ({})", name, priority.name());
        } catch (Exception e) {
            getComponentLogger().error("Failed to register {} as permission provider - " +
                                       "check to make sure you're using a compatible version!", name, e);
        }
    }

    private void loadVaultPermissionWrapper() {
        var wrapper = new VaultPermissionServiceWrapper(groupController(), permissionController(), this);
        getServer().getServicesManager().register(Permission.class, wrapper, this, ServicePriority.Highest);
        this.vaultPermissionWrapper = wrapper;
    }

    private void loadVaultEconomyWrapper() {
        var economyController = getServer().getServicesManager().load(EconomyController.class);
        if (economyController == null) return;
    }

    private void loadVaultChatWrapper() {
        var wrapper = new VaultChatServiceWrapper(vaultPermissionWrapper(), chatController(), this);
        getServer().getServicesManager().register(Chat.class, wrapper, this, ServicePriority.Highest);
    }
}