package net.thenextlvl.services;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.thenextlvl.services.api.ServiceProvider;
import net.thenextlvl.services.api.capability.CapabilityController;
import net.thenextlvl.services.api.chat.ChatController;
import net.thenextlvl.services.api.economy.EconomyController;
import net.thenextlvl.services.api.permission.GroupController;
import net.thenextlvl.services.capability.PaperCapabilityController;
import net.thenextlvl.services.chat.PaperChatController;
import net.thenextlvl.services.listener.PluginListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Accessors(fluent = true)
public class ServicePlugin extends JavaPlugin implements ServiceProvider {
    private final Map<Plugin, CapabilityController> capabilityControllers = new HashMap<>();

    @Override
    public void onLoad() {
        getServer().getServicesManager().register(ServiceProvider.class, this, this, ServicePriority.Highest);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PluginListener(this), this);
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
                PaperChatController::new
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
}