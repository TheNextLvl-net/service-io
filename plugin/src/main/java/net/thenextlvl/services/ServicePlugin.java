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
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@Getter
@Accessors(fluent = true)
public class ServicePlugin extends JavaPlugin implements ServiceProvider {
    private final CapabilityController capabilityController = new PaperCapabilityController();

    @Override
    public void onLoad() {
        getServer().getServicesManager().register(ServiceProvider.class, this, this, ServicePriority.Highest);
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
        return null;
    }
}