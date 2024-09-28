package net.thenextlvl.service;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.command.ServiceCommand;
import net.thenextlvl.service.controller.chat.GroupManagerChatController;
import net.thenextlvl.service.controller.chat.LuckPermsChatController;
import net.thenextlvl.service.controller.group.GroupManagerGroupController;
import net.thenextlvl.service.controller.group.LuckPermsGroupController;
import net.thenextlvl.service.controller.permission.GroupManagerPermissionController;
import net.thenextlvl.service.controller.permission.LuckPermsPermissionController;
import net.thenextlvl.service.controller.permission.SuperPermsPermissionController;
import net.thenextlvl.service.version.PluginVersionChecker;
import net.thenextlvl.service.wrapper.VaultChatServiceWrapper;
import net.thenextlvl.service.wrapper.VaultEconomyServiceWrapper;
import net.thenextlvl.service.wrapper.VaultPermissionServiceWrapper;
import net.thenextlvl.service.wrapper.service.ChatServiceWrapper;
import net.thenextlvl.service.wrapper.service.EconomyServiceWrapper;
import net.thenextlvl.service.wrapper.service.PermissionServiceWrapper;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Function;

@Getter
@Accessors(fluent = true)
public class ServicePlugin extends JavaPlugin {
    private final PluginVersionChecker versionChecker = new PluginVersionChecker(this);
    private final Metrics metrics = new Metrics(this, 23083);

    private @Nullable ChatController chatController = null;
    private @Nullable GroupController groupController = null;

    private @Nullable Permission vaultPermissionWrapper = null;

    private PermissionController permissionController = new SuperPermsPermissionController(this);

    @Override
    public void onLoad() {
        versionChecker().checkVersion();
        registerCommands();
    }

    @Override
    public void onEnable() {
        loadServicePermissionWrapper();
        loadServiceEconomyWrapper();
        loadServiceChatWrapper();

        loadPermissionServices();
        loadGroupServices();
        loadChatServices();

        printServices();

        loadVaultPermissionWrapper();
        loadVaultEconomyWrapper();
        loadVaultChatWrapper();

        addCustomCharts();
    }

    private void registerCommands() {
        new ServiceCommand(this).register();
    }

    @Override
    public void onDisable() {
        metrics().shutdown();
    }

    @SuppressWarnings("Convert2MethodRef")
    private void loadChatServices() {
        hookChatService("GroupManager", () -> new GroupManagerChatController(), ServicePriority.High);
        hookChatService("LuckPerms", () -> new LuckPermsChatController(), ServicePriority.Highest);
        this.chatController = getServer().getServicesManager().load(ChatController.class);
    }

    @SuppressWarnings("Convert2MethodRef")
    private void loadGroupServices() {
        hookGroupService("GroupManager", () -> new GroupManagerGroupController(), ServicePriority.High);
        hookGroupService("LuckPerms", () -> new LuckPermsGroupController(), ServicePriority.Highest);
        this.groupController = getServer().getServicesManager().load(GroupController.class);
    }

    @SuppressWarnings("Convert2MethodRef")
    private void loadPermissionServices() {
        hookPermissionService("GroupManager", () -> new GroupManagerPermissionController(), ServicePriority.High);
        hookPermissionService("LuckPerms", () -> new LuckPermsPermissionController(), ServicePriority.Highest);

        getServer().getServicesManager().register(PermissionController.class, permissionController, this, ServicePriority.Lowest);
        getComponentLogger().debug("Added SuperPerms as backup permission provider (Lowest)");

        var controller = getServer().getServicesManager().load(PermissionController.class);
        if (controller != null) this.permissionController = controller;
    }

    private void hookChatService(String name, Callable<? extends ChatController> callable, ServicePriority priority) {
        try {
            if (getServer().getPluginManager().getPlugin(name) == null) return;
            var hook = callable.call();
            getServer().getServicesManager().register(ChatController.class, hook, this, priority);
            getComponentLogger().debug("Added {} as chat provider ({})", name, priority.name());
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
            getComponentLogger().debug("Added {} as group provider ({})", name, priority.name());
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
            getComponentLogger().debug("Added {} as permission provider ({})", name, priority.name());
        } catch (Exception e) {
            getComponentLogger().error("Failed to register {} as permission provider - " +
                                       "check to make sure you're using a compatible version!", name, e);
        }
    }

    private void loadServicePermissionWrapper() {
        getServer().getServicesManager().getRegistrations(Permission.class).forEach(provider -> {
            var wrapper = new PermissionServiceWrapper(provider.getProvider(), this);
            getServer().getServicesManager().register(PermissionController.class, wrapper, provider.getPlugin(), provider.getPriority());
        });
    }

    private void loadServiceEconomyWrapper() {
        var services = getServer().getServicesManager();
        services.getRegistrations(Economy.class).forEach(economy -> {
            var wrapper = new EconomyServiceWrapper(economy.getProvider(), this);
            services.register(EconomyController.class, wrapper, economy.getPlugin(), economy.getPriority());
            wrapper.getBankController().ifPresent(controller ->
                    services.register(BankController.class, controller, economy.getPlugin(), economy.getPriority()));
        });
    }

    private void loadServiceChatWrapper() {
        getServer().getServicesManager().getRegistrations(Chat.class).forEach(chat -> {
            var wrapper = new ChatServiceWrapper(chat.getProvider(), this);
            getServer().getServicesManager().register(ChatController.class, wrapper, chat.getPlugin(), chat.getPriority());
        });
    }

    private void loadVaultPermissionWrapper() {
        getServer().getServicesManager().getRegistrations(PermissionController.class).forEach(provider -> {
            var wrapper = new VaultPermissionServiceWrapper(groupController(), provider.getProvider(), provider.getPlugin());
            getServer().getServicesManager().register(Permission.class, wrapper, provider.getPlugin(), provider.getPriority());
        });
        this.vaultPermissionWrapper = getServer().getServicesManager().load(Permission.class);
    }

    private void loadVaultEconomyWrapper() {
        getServer().getServicesManager().getRegistrations(EconomyController.class).forEach(provider -> {
            var wrapper = new VaultEconomyServiceWrapper(provider.getProvider(), provider.getPlugin());
            getServer().getServicesManager().register(Economy.class, wrapper, provider.getPlugin(), provider.getPriority());
        });
    }

    private void loadVaultChatWrapper() {
        if (chatController() == null) return;
        Preconditions.checkNotNull(vaultPermissionWrapper(), "vault permission wrapper cannot be null");
        var wrapper = new VaultChatServiceWrapper(vaultPermissionWrapper(), groupController(), chatController(), this);
        getServer().getServicesManager().register(Chat.class, wrapper, this, ServicePriority.Highest);
    }

    private void printServices() {
        var chat = chatController() != null ? chatController().getName() : null;
        var group = groupController() != null ? groupController().getName() : null;
        var permission = permissionController().getName();

        if (chat == null && group == null) {
            getComponentLogger().info("Found no chat and group provider");
        } else if (chat == null) {
            getComponentLogger().info("Found no chat provider");
        } else if (group == null) {
            getComponentLogger().info("Found no group provider");
        }

        if (Objects.equals(chat, group) && Objects.equals(chat, permission)) {
            getComponentLogger().info("Using {} as chat, group and permission provider", chat);
        } else if (chat != null && Objects.equals(chat, group)) {
            getComponentLogger().info("Using {} as chat and group provider", chat);
            getComponentLogger().info("Using {} as permission provider", permission);
        } else if (Objects.equals(chat, permission)) {
            getComponentLogger().info("Using {} as chat and permission provider", chat);
            if (group != null) getComponentLogger().info("Using {} as group provider", group);
        } else {
            if (chat != null) getComponentLogger().info("Using {} as chat provider", chat);
            if (group != null) getComponentLogger().info("Using {} as group provider", group);
            getComponentLogger().info("Using {} as permission provider", permission);
        }
    }

    private void addCustomCharts() {
        addCustomChart(BankController.class, BankController::getName, "bank_provider");
        addCustomChart(GroupController.class, GroupController::getName, "group_provider");
        addCustomChart(ChatController.class, ChatController::getName, "chat_provider");
        addCustomChart(EconomyController.class, EconomyController::getName, "economy_provider");
        addCustomChart(PermissionController.class, PermissionController::getName, "permission_provider");
    }

    private <T> void addCustomChart(Class<T> service, Function<T, String> function, String name) {
        var loaded = getServer().getServicesManager().load(service);
        metrics.addCustomChart(new SimplePie(name, () -> loaded != null ? function.apply(loaded) : "None"));
    }
}