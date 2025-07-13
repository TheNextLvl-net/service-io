package net.thenextlvl.service;

import com.google.common.base.Preconditions;
import core.i18n.file.ComponentBundle;
import net.kyori.adventure.key.Key;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.api.Controller;
import net.thenextlvl.service.api.character.CharacterController;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.hologram.HologramController;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.command.ServiceCommand;
import net.thenextlvl.service.controller.character.CitizensCharacterController;
import net.thenextlvl.service.controller.character.FancyCharacterController;
import net.thenextlvl.service.controller.chat.GroupManagerChatController;
import net.thenextlvl.service.controller.chat.LuckPermsChatController;
import net.thenextlvl.service.controller.group.GroupManagerGroupController;
import net.thenextlvl.service.controller.group.LuckPermsGroupController;
import net.thenextlvl.service.controller.hologram.DecentHologramController;
import net.thenextlvl.service.controller.hologram.FancyHologramController;
import net.thenextlvl.service.controller.permission.GroupManagerPermissionController;
import net.thenextlvl.service.controller.permission.LuckPermsPermissionController;
import net.thenextlvl.service.controller.permission.SuperPermsPermissionController;
import net.thenextlvl.service.listener.CitizensListener;
import net.thenextlvl.service.listener.FancyNpcsListener;
import net.thenextlvl.service.placeholder.chat.ServiceChatPlaceholderExpansion;
import net.thenextlvl.service.placeholder.economy.ServiceEconomyPlaceholderExpansion;
import net.thenextlvl.service.placeholder.group.ServiceGroupPlaceholderExpansion;
import net.thenextlvl.service.version.PluginVersionChecker;
import net.thenextlvl.service.wrapper.VaultChatServiceWrapper;
import net.thenextlvl.service.wrapper.VaultEconomyServiceWrapper;
import net.thenextlvl.service.wrapper.VaultPermissionServiceWrapper;
import net.thenextlvl.service.wrapper.service.BankServiceWrapper;
import net.thenextlvl.service.wrapper.service.ChatServiceWrapper;
import net.thenextlvl.service.wrapper.service.EconomyServiceWrapper;
import net.thenextlvl.service.wrapper.service.PermissionServiceWrapper;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;

@NullMarked
public class ServicePlugin extends Vault {
    private final PluginVersionChecker versionChecker = new PluginVersionChecker(this);
    private final Metrics metrics = new Metrics(this, 23083);

    private @Nullable ChatController chatController = null;
    private @Nullable GroupController groupController = null;

    private @Nullable Permission vaultPermissionWrapper = null;

    private PermissionController permissionController = new SuperPermsPermissionController(this);

    private final Key key = Key.key("service_io", "translations");
    private final Path translations = getDataPath().resolve("translations");
    private final ComponentBundle bundle = ComponentBundle.builder(key, translations)
            .placeholder("prefix", "prefix")
            .resource("service-io.properties", Locale.US)
            .resource("service-io_german.properties", Locale.GERMANY)
            .build();

    @Override
    public void onLoad() {
        versionChecker.checkVersion();
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
        loadHologramServices();
        loadNpcServices();

        loadVaultPermissionWrapper();
        loadVaultEconomyWrapper();
        loadVaultChatWrapper();

        registerPlaceholders();

        addCustomCharts();
    }

    private void registerPlaceholders() {
        if (!getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) return;
        new ServiceChatPlaceholderExpansion(this).register();
        new ServiceEconomyPlaceholderExpansion(this).register();
        new ServiceGroupPlaceholderExpansion(this).register();
    }

    public ComponentBundle bundle() {
        return bundle;
    }

    private void registerCommands() {
        new ServiceCommand().register(this);
    }

    @Override
    public void onDisable() {
        metrics.shutdown();
    }

    @SuppressWarnings("Convert2MethodRef")
    private void loadChatServices() {
        hookService("GroupManager", ChatController.class, plugin -> new GroupManagerChatController(), ServicePriority.Low);
        hookService("LuckPerms", ChatController.class, plugin -> new LuckPermsChatController(plugin), ServicePriority.Highest);
        this.chatController = getServer().getServicesManager().load(ChatController.class);
    }

    @SuppressWarnings("Convert2MethodRef")
    private void loadGroupServices() {
        hookService("GroupManager", GroupController.class, plugin -> new GroupManagerGroupController(), ServicePriority.Low);
        hookService("LuckPerms", GroupController.class, plugin -> new LuckPermsGroupController(plugin), ServicePriority.Highest);
        this.groupController = getServer().getServicesManager().load(GroupController.class);
    }

    @SuppressWarnings("Convert2MethodRef")
    private void loadPermissionServices() {
        hookService("GroupManager", PermissionController.class, plugin -> new GroupManagerPermissionController(), ServicePriority.Low);
        hookService("LuckPerms", PermissionController.class, plugin -> new LuckPermsPermissionController(plugin), ServicePriority.Highest);

        getServer().getServicesManager().register(PermissionController.class, permissionController, this, ServicePriority.Lowest);
        getComponentLogger().debug("Added SuperPerms as backup permission provider (Lowest)");

        var controller = getServer().getServicesManager().load(PermissionController.class);
        if (controller != null) this.permissionController = controller;
    }

    private void loadHologramServices() {
        hookService("DecentHolograms", HologramController.class, plugin -> new DecentHologramController(), ServicePriority.Highest);
        hookService("FancyHolograms", HologramController.class, plugin -> new FancyHologramController(), ServicePriority.High);
    }

    @SuppressWarnings("Convert2MethodRef")
    private void loadNpcServices() {
        hookService("Citizens", CharacterController.class, plugin -> new CitizensCharacterController(this),
                controller -> new CitizensListener(controller), ServicePriority.Highest);
        hookService("FancyNpcs", CharacterController.class, plugin -> new FancyCharacterController(this),
                controller -> new FancyNpcsListener(controller), ServicePriority.High);
    }

    private <T extends Controller> void hookService(String plugin, Class<T> type, Function<Plugin, ? extends T> controller,
                                                    Function<T, Listener> listener, ServicePriority priority) {
        var hook = hookService(plugin, type, controller, priority);
        if (hook != null) getServer().getPluginManager().registerEvents(listener.apply(hook), this);
    }

    private <T extends Controller> @Nullable T hookService(String name, Class<T> type, Function<Plugin, ? extends T> controller, ServicePriority priority) {
        try {
            var plugin = getServer().getPluginManager().getPlugin(name);
            if (plugin == null) return null;
            var provider = controller.apply(plugin);
            getServer().getServicesManager().register(type, provider, this, priority);
            getComponentLogger().info("Initialized support for {} as {} ({})", provider.getName(), type.getSimpleName(), priority.name());
            return provider;
        } catch (Exception e) {
            getComponentLogger().error("Failed to add {} for {} - make sure you're using a compatible version!",
                    type.getSimpleName(), name, e);
            return null;
        }
    }

    private void loadServicePermissionWrapper() {
        getServer().getServicesManager().getRegistrations(Permission.class).forEach(provider -> {
            var wrapper = new PermissionServiceWrapper(provider.getProvider(), provider.getPlugin(), this);
            getServer().getServicesManager().register(PermissionController.class, wrapper, provider.getPlugin(), provider.getPriority());
        });
    }

    private void loadServiceEconomyWrapper() {
        var services = getServer().getServicesManager();
        services.getRegistrations(Economy.class).forEach(economy -> {
            var wrapper = new EconomyServiceWrapper(economy.getProvider(), economy.getPlugin(), this);
            services.register(EconomyController.class, wrapper, economy.getPlugin(), economy.getPriority());

            if (!economy.getProvider().hasBankSupport()) return;
            var banks = new BankServiceWrapper(economy.getProvider(), economy.getPlugin(), this);
            services.register(BankController.class, banks, economy.getPlugin(), economy.getPriority());
        });
    }

    private void loadServiceChatWrapper() {
        getServer().getServicesManager().getRegistrations(Chat.class).forEach(chat -> {
            var wrapper = new ChatServiceWrapper(chat.getProvider(), chat.getPlugin(), this);
            getServer().getServicesManager().register(ChatController.class, wrapper, chat.getPlugin(), chat.getPriority());
        });
    }

    private void loadVaultPermissionWrapper() {
        getServer().getServicesManager().getRegistrations(PermissionController.class).forEach(provider -> {
            var wrapper = new VaultPermissionServiceWrapper(groupController, provider.getProvider(), provider.getPlugin());
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
        if (chatController == null) return;
        Preconditions.checkNotNull(vaultPermissionWrapper, "vault permission wrapper cannot be null");
        var wrapper = new VaultChatServiceWrapper(vaultPermissionWrapper, groupController, chatController, this);
        getServer().getServicesManager().register(Chat.class, wrapper, this, ServicePriority.Highest);
    }

    private void addCustomCharts() {
        addCustomChart(BankController.class, BankController::getName, "bank_provider");
        addCustomChart(GroupController.class, GroupController::getName, "group_provider");
        addCustomChart(ChatController.class, ChatController::getName, "chat_provider");
        addCustomChart(EconomyController.class, EconomyController::getName, "economy_provider");
        addCustomChart(PermissionController.class, PermissionController::getName, "permission_provider");
        addCustomChart(HologramController.class, HologramController::getName, "hologram_provider");
        addCustomChart(CharacterController.class, CharacterController::getName, "npc_provider");
    }

    private <T> void addCustomChart(Class<T> service, Function<T, String> function, String chartId) {
        var loaded = getServer().getServicesManager().load(service);
        metrics.addCustomChart(new SimplePie(chartId, () -> loaded != null ? function.apply(loaded) : "None"));
    }

    public EntityType getEntityTypeByClass(Class<? extends Entity> type) {
        return Arrays.stream(EntityType.values())
                .filter(entityType -> type.equals(entityType.getEntityClass()))
                .findAny().orElseThrow();
    }
}