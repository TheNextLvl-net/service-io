package net.thenextlvl.service;

import com.google.common.base.Preconditions;
import core.i18n.file.ComponentBundle;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.hologram.HologramController;
import net.thenextlvl.service.api.character.CharacterController;
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
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.function.Function;

@Getter
@NullMarked
@Accessors(fluent = true)
public class ServicePlugin extends JavaPlugin {
    private final PluginVersionChecker versionChecker = new PluginVersionChecker(this);
    private final Metrics metrics = new Metrics(this, 23083);

    private @Nullable ChatController chatController = null;
    private @Nullable EconomyController economyController = null;
    private @Nullable GroupController groupController = null;
    private @Nullable HologramController hologramController = null;
    private @Nullable CharacterController characterController = null;

    private @Nullable Permission vaultPermissionWrapper = null;

    private PermissionController permissionController = new SuperPermsPermissionController(this);

    private final ComponentBundle bundle = new ComponentBundle(new File(getDataFolder(), "translations"),
            audience -> audience instanceof Player player ? player.locale() : Locale.US)
            .register("service-io", Locale.US)
            .register("service-io_german", Locale.GERMANY)
            .miniMessage(bundle -> MiniMessage.builder().tags(TagResolver.resolver(
                    Placeholder.component("prefix", bundle.component(Locale.US, "prefix")),
                    TagResolver.standard()
            )).build());

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
        loadHologramServices();
        loadNpcServices();
        loadEconomyServices();

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

    @SuppressWarnings("Convert2MethodRef")
    private void loadHologramServices() {
        hookHologramService("DecentHolograms", () -> new DecentHologramController(), ServicePriority.Highest);
        hookHologramService("FancyHolograms", () -> new FancyHologramController(), ServicePriority.High);

        var controller = getServer().getServicesManager().load(HologramController.class);
        if (controller != null) this.hologramController = controller;
    }

    private void loadNpcServices() {
        hookNpcService("Citizens", () -> new CitizensCharacterController(this), CitizensListener::new, ServicePriority.Highest);
        hookNpcService("FancyNpcs", () -> new FancyCharacterController(this), FancyNpcsListener::new, ServicePriority.High);

        var controller = getServer().getServicesManager().load(CharacterController.class);
        if (controller != null) this.characterController = controller;
    }

    private void loadEconomyServices() {
        var controller = getServer().getServicesManager().load(EconomyController.class);
        if (controller != null) this.economyController = controller;
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

    private void hookHologramService(String name, Callable<? extends HologramController> callable, ServicePriority priority) {
        try {
            if (getServer().getPluginManager().getPlugin(name) == null) return;
            var hook = callable.call();
            getServer().getServicesManager().register(HologramController.class, hook, this, priority);
            getComponentLogger().debug("Added {} as hologram provider ({})", name, priority.name());
        } catch (Exception e) {
            getComponentLogger().error("Failed to register {} as hologram provider - " +
                                       "check to make sure you're using a compatible version!", name, e);
        }
    }

    private void hookNpcService(String name, Callable<? extends CharacterController> controller, Function<CharacterController, Listener> listener, ServicePriority priority) {
        try {
            if (getServer().getPluginManager().getPlugin(name) == null) return;
            var provider = controller.call();
            getServer().getPluginManager().registerEvents(listener.apply(provider), this);
            getServer().getServicesManager().register(CharacterController.class, provider, this, priority);
            getComponentLogger().debug("Added {} as npc provider ({})", name, priority.name());
        } catch (Exception e) {
            getComponentLogger().error("Failed to register {} as npc provider - " +
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
            if (economy.getProvider().hasBankSupport()) {
                var banks = new BankServiceWrapper(economy.getProvider(), this);
                services.register(BankController.class, banks, economy.getPlugin(), economy.getPriority());
            }
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
        var economy = economyController() != null ? economyController().getName() : null;
        var group = groupController() != null ? groupController().getName() : null;
        var hologram = hologramController() != null ? hologramController().getName() : null;
        var character = characterController() != null ? characterController().getName() : null;

        var permission = permissionController().getName();

        getComponentLogger().info("Using {} as permission provider", permission);

        if (chat != null) getComponentLogger().info("Using {} as chat provider", chat);
        else getComponentLogger().info("Found no chat provider");

        if (economy != null) getComponentLogger().info("Using {} as economy provider", economy);
        else getComponentLogger().info("Found no economy provider");

        if (group != null) getComponentLogger().info("Using {} as group provider", group);
        else getComponentLogger().info("Found no group provider");

        if (hologram != null) getComponentLogger().info("Using {} as hologram provider", hologram);
        else getComponentLogger().info("Found no hologram provider");

        if (character != null) getComponentLogger().info("Using {} as npc provider", character);
        else getComponentLogger().info("Found no npc provider");
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