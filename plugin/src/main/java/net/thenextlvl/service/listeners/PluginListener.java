package net.thenextlvl.service.listeners;

import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.ServiceBootstrapper;
import net.thenextlvl.service.api.Controller;
import net.thenextlvl.service.api.character.CharacterController;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.hologram.HologramController;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.placeholder.api.PlaceholderExpansionBuilder;
import net.thenextlvl.service.placeholder.chat.ServiceChatPlaceholderStore;
import net.thenextlvl.service.placeholder.economy.ServiceBankPlaceholderStore;
import net.thenextlvl.service.placeholder.economy.ServiceEconomyPlaceholderStore;
import net.thenextlvl.service.placeholder.economy.UnlockedEconomyPlaceholderStore;
import net.thenextlvl.service.placeholder.group.ServiceGroupPlaceholderStore;
import net.thenextlvl.service.providers.citizens.CitizensCharacterController;
import net.thenextlvl.service.providers.citizens.CitizensListener;
import net.thenextlvl.service.providers.decentholograms.DecentHologramController;
import net.thenextlvl.service.providers.fancyholograms.v3.FancyHologramController;
import net.thenextlvl.service.providers.fancynpcs.FancyCharacterController;
import net.thenextlvl.service.providers.groupmanager.GroupManagerChatController;
import net.thenextlvl.service.providers.groupmanager.GroupManagerGroupController;
import net.thenextlvl.service.providers.groupmanager.GroupManagerPermission;
import net.thenextlvl.service.providers.groupmanager.GroupManagerPermissionController;
import net.thenextlvl.service.providers.luckperms.LuckPermsChatController;
import net.thenextlvl.service.providers.luckperms.LuckPermsGroupController;
import net.thenextlvl.service.providers.luckperms.LuckPermsPermissionController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@NullMarked
public class PluginListener implements Listener {
    private final Map<String, Consumer<Plugin>> registrations;
    private final Logger logger;

    @SuppressWarnings("Convert2MethodRef")
    public PluginListener(Plugin provider) {
        this.logger = provider.getComponentLogger();
        this.registrations = Map.ofEntries(
                Map.entry("PlaceholderAPI", plugin -> {
                    registerPlaceholders(provider);
                }),
                Map.entry("Citizens", plugin -> {
                    hookService(plugin, CharacterController.class, () -> new CitizensCharacterController(plugin),
                            controller -> new CitizensListener(controller), ServicePriority.Highest);
                }),
                Map.entry("DecentHolograms", plugin -> {
                    hookService(plugin, HologramController.class, () -> new DecentHologramController(), ServicePriority.Highest);
                }),
                Map.entry("FancyNpcs", plugin -> {
                    hookService(plugin, CharacterController.class, () -> new FancyCharacterController(plugin),
                            controller -> new net.thenextlvl.service.providers.fancynpcs.FancyNpcsListener(controller), ServicePriority.High);
                }),
                Map.entry("GroupManager", plugin -> {
                    hookService(plugin, ChatController.class, () -> new GroupManagerChatController(), ServicePriority.Low);
                    hookService(plugin, GroupController.class, () -> new GroupManagerGroupController(), ServicePriority.Low);
                    hookService(plugin, PermissionController.class, () -> new GroupManagerPermissionController(), ServicePriority.Low);
                    hook(plugin, Permission.class, () -> new GroupManagerPermission(), Permission::getName, ServicePriority.Low);
                }),
                Map.entry("FancyHolograms", plugin -> {
                    var version = plugin.getPluginMeta().getVersion();
                    if (version.startsWith("2")) hookService(plugin, HologramController.class,
                            () -> new net.thenextlvl.service.providers.fancyholograms.v2.FancyHologramController(),
                            ServicePriority.High);
                    else if (version.startsWith("3")) hookService(plugin, HologramController.class,
                            () -> new FancyHologramController(),
                            ServicePriority.High);
                    else logger.warn("Unsupported FancyHolograms version {}", version);
                }),
                Map.entry("LuckPerms", plugin -> {
                    hookService(plugin, ChatController.class, () -> new LuckPermsChatController(plugin), ServicePriority.Highest);
                    hookService(plugin, GroupController.class, () -> new LuckPermsGroupController(plugin), ServicePriority.Highest);
                    hookService(plugin, PermissionController.class, () -> new LuckPermsPermissionController(plugin), ServicePriority.Highest);
                })
        );
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPluginEnable(PluginEnableEvent event) {
        var consumer = this.registrations.get(event.getPlugin().getName());
        if (consumer != null) consumer.accept(event.getPlugin());
    }

    private void registerPlaceholders(Plugin plugin) {
        new PlaceholderExpansionBuilder(plugin)
                .registerStore(new ServiceBankPlaceholderStore(plugin))
                .registerStore(new ServiceChatPlaceholderStore(plugin))
                .registerStore(new ServiceEconomyPlaceholderStore(plugin))
                .registerStore(new ServiceGroupPlaceholderStore(plugin))
                .register();

        var authors = new ArrayList<>(plugin.getPluginMeta().getAuthors());
        authors.add("creatorfromhell");

        new PlaceholderExpansionBuilder(plugin, "vaultunlocked")
                .setAuthors(authors)
                .setVersion("2.13.1")
                .registerStore(new UnlockedEconomyPlaceholderStore(plugin))
                .register();
    }

    private <T extends Controller> void hookService(Plugin plugin, Class<T> type, Supplier<? extends T> controller,
                                                    Function<T, Listener> listener, ServicePriority priority) {
        T hook = hookService(plugin, type, controller, priority);
        if (hook != null) plugin.getServer().getPluginManager().registerEvents(listener.apply(hook), plugin);
    }

    private <T extends Controller> @Nullable T hookService(Plugin plugin, Class<T> type, Supplier<? extends T> controller, ServicePriority priority) {
        return hook(plugin, type, controller, Controller::getName, priority);
    }

    private <T> @Nullable T hook(Plugin plugin, Class<T> type, Supplier<? extends T> controller, Function<T, String> name, ServicePriority priority) {
        try {
            var provider = controller.get();
            plugin.getServer().getServicesManager().register(type, provider, plugin, priority);
            logger.info("Initialized support for {} as {} ({})", name.apply(provider), type.getSimpleName(), priority.name());
            return provider;
        } catch (Exception e) {
            logger.error("Failed to add {} for {} - make sure you're using a compatible version!",
                    type.getSimpleName(), plugin.getName(), e);
            ServiceBootstrapper.ERROR_TRACKER.trackError(e);
            return null;
        }
    }
}
