package net.thenextlvl.service.listener;

import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.Controller;
import net.thenextlvl.service.api.character.CharacterController;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.hologram.HologramController;
import net.thenextlvl.service.api.permission.PermissionController;
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
import net.thenextlvl.service.placeholder.api.PlaceholderExpansionBuilder;
import net.thenextlvl.service.placeholder.chat.ServiceChatPlaceholderStore;
import net.thenextlvl.service.placeholder.economy.ServiceBankPlaceholderStore;
import net.thenextlvl.service.placeholder.economy.ServiceEconomyPlaceholderStore;
import net.thenextlvl.service.placeholder.economy.UnlockedEconomyPlaceholderStore;
import net.thenextlvl.service.placeholder.group.ServiceGroupPlaceholderStore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Function;

@NullMarked
public class PluginListener implements Listener {
    private final ServicePlugin plugin;

    public PluginListener(ServicePlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("Convert2MethodRef")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPluginEnable(PluginEnableEvent event) {
        switch (event.getPlugin().getName()) {
            case "PlaceholderAPI" -> registerPlaceholders();

            case "Citizens" -> hookService(event.getPlugin(),
                    CharacterController.class,
                    plugin -> new CitizensCharacterController(this.plugin),
                    controller -> new CitizensListener(controller),
                    ServicePriority.Highest);
            case "FancyNpcs" -> hookService(event.getPlugin(), CharacterController.class,
                    plugin -> new FancyCharacterController(this.plugin),
                    controller -> new FancyNpcsListener(controller),
                    ServicePriority.High);

            case "GroupManager" -> {
                hookService(event.getPlugin(), ChatController.class, plugin -> new GroupManagerChatController(), ServicePriority.Low);
                hookService(event.getPlugin(), GroupController.class, plugin -> new GroupManagerGroupController(), ServicePriority.Low);
                hookService(event.getPlugin(), PermissionController.class, plugin -> new GroupManagerPermissionController(), ServicePriority.Low);
            }
            case "LuckPerms" -> {
                hookService(event.getPlugin(), ChatController.class, plugin -> new LuckPermsChatController(plugin), ServicePriority.Highest);
                hookService(event.getPlugin(), GroupController.class, plugin -> new LuckPermsGroupController(plugin), ServicePriority.Highest);
                hookService(event.getPlugin(), PermissionController.class, plugin -> new LuckPermsPermissionController(plugin), ServicePriority.Highest);
            }

            case "DecentHolograms" -> hookService(event.getPlugin(),
                    HologramController.class,
                    plugin -> new DecentHologramController(),
                    ServicePriority.Highest);
            case "FancyHolograms" -> hookService(event.getPlugin(),
                    HologramController.class,
                    plugin -> new FancyHologramController(),
                    ServicePriority.High);
        }
    }

    private void registerPlaceholders() {
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

    private <T extends Controller> void hookService(Plugin plugin, Class<T> type, Function<Plugin, ? extends T> controller,
                                                    Function<T, Listener> listener, ServicePriority priority) {
        T hook = hookService(plugin, type, controller, priority);
        if (hook != null) plugin.getServer().getPluginManager().registerEvents(listener.apply(hook), plugin);
    }

    private <T extends Controller> @Nullable T hookService(Plugin plugin, Class<T> type, Function<Plugin, ? extends T> controller, ServicePriority priority) {
        try {
            var provider = controller.apply(plugin);
            this.plugin.getServer().getServicesManager().register(type, provider, plugin, priority);
            this.plugin.getComponentLogger().info("Initialized support for {} as {} ({})", provider.getName(), type.getSimpleName(), priority.name());
            return provider;
        } catch (Exception e) {
            plugin.getComponentLogger().error("Failed to add {} for {} - make sure you're using a compatible version!",
                    type.getSimpleName(), plugin.getName(), e);
            return null;
        }
    }
}
