package net.thenextlvl.service.listener;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.Controller;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.wrapper.VaultChatServiceWrapper;
import net.thenextlvl.service.wrapper.VaultEconomyServiceWrapper;
import net.thenextlvl.service.wrapper.VaultPermissionServiceWrapper;
import net.thenextlvl.service.wrapper.Wrapper;
import net.thenextlvl.service.wrapper.service.BankServiceWrapper;
import net.thenextlvl.service.wrapper.service.ChatServiceWrapper;
import net.thenextlvl.service.wrapper.service.EconomyServiceWrapper;
import net.thenextlvl.service.wrapper.service.PermissionServiceWrapper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ServiceListener implements Listener {
    private final ServicePlugin plugin;

    public ServiceListener(final ServicePlugin plugin) {
        this.plugin = plugin;
        getServicesManager().getKnownServices().forEach(aClass ->
                getServicesManager().getRegistrations(aClass).forEach(this::loadWrapper));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServiceUnregister(final ServiceUnregisterEvent event) {
        final var provider = event.getProvider().getProvider();
        final var type = provider instanceof Controller ? "controller"
                : provider instanceof Wrapper ? "service wrapper" : null;
        if (type != null) plugin.getComponentLogger().info("Unregistered {} for {} - {} ({})", type,
                event.getProvider().getPlugin().getName(),
                event.getProvider().getProvider().getClass().getName(),
                event.getProvider().getPriority().name());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServiceRegister(final ServiceRegisterEvent event) {
        loadWrapper(event.getProvider());
    }

    @SuppressWarnings("unchecked")
    private void loadWrapper(final RegisteredServiceProvider<?> provider) {
        if (provider.getProvider() instanceof Wrapper) return;
        if (provider.getProvider() instanceof PermissionController) {
            loadVaultPermissionWrapper((RegisteredServiceProvider<PermissionController>) provider);
        } else if (provider.getProvider() instanceof Permission) {
            loadServicePermissionWrapper((RegisteredServiceProvider<Permission>) provider);
        } else if (provider.getProvider() instanceof EconomyController) {
            loadVaultEconomyWrapper((RegisteredServiceProvider<EconomyController>) provider);
        } else if (provider.getProvider() instanceof Economy) {
            loadServiceEconomyWrapper((RegisteredServiceProvider<Economy>) provider);
        } else if (provider.getProvider() instanceof ChatController) {
            loadVaultChatWrapper((RegisteredServiceProvider<ChatController>) provider);
        } else if (provider.getProvider() instanceof Chat) {
            loadServiceChatWrapper((RegisteredServiceProvider<Chat>) provider);
        }
    }

    private void loadServicePermissionWrapper(final RegisteredServiceProvider<Permission> provider) {
        final var wrapper = new PermissionServiceWrapper(provider.getProvider(), provider.getPlugin());
        getServicesManager().register(PermissionController.class, wrapper, provider.getPlugin(), getWrapperPriority(provider));
        plugin.getComponentLogger().info("Registered permission service wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), provider.getPriority().name());
    }

    private void loadServiceEconomyWrapper(final RegisteredServiceProvider<Economy> provider) {
        final var wrapper = new EconomyServiceWrapper(provider.getProvider(), provider.getPlugin());
        getServicesManager().register(EconomyController.class, wrapper, provider.getPlugin(), getWrapperPriority(provider));
        plugin.getComponentLogger().info("Registered economy service wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), provider.getPriority().name());

        if (!provider.getProvider().hasBankSupport()) return;
        final var banks = new BankServiceWrapper(wrapper.getCurrencyHolder(), provider.getProvider(), provider.getPlugin());
        getServicesManager().register(BankController.class, banks, provider.getPlugin(), getWrapperPriority(provider));
        plugin.getComponentLogger().info("Registered bank service wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), provider.getPriority().name());
    }

    private void loadServiceChatWrapper(final RegisteredServiceProvider<Chat> provider) {
        final var wrapper = new ChatServiceWrapper(provider.getProvider(), provider.getPlugin());
        getServicesManager().register(ChatController.class, wrapper, provider.getPlugin(), getWrapperPriority(provider));
        plugin.getComponentLogger().info("Registered chat service wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), provider.getPriority().name());
    }

    private void loadVaultPermissionWrapper(final RegisteredServiceProvider<PermissionController> provider) {
        final var groupController = getServicesManager().load(GroupController.class);
        final var wrapper = new VaultPermissionServiceWrapper(groupController, provider.getProvider(), provider.getPlugin());
        getServicesManager().register(Permission.class, wrapper, provider.getPlugin(), getWrapperPriority(provider));
        plugin.getComponentLogger().info("Registered vault permission wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), provider.getPriority().name());
    }

    private void loadVaultEconomyWrapper(final RegisteredServiceProvider<EconomyController> provider) {
        final var wrapper = new VaultEconomyServiceWrapper(provider.getProvider(), provider.getPlugin());
        getServicesManager().register(Economy.class, wrapper, provider.getPlugin(), getWrapperPriority(provider));
        plugin.getComponentLogger().info("Registered vault economy wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), provider.getPriority().name());
    }

    private void loadVaultChatWrapper(final RegisteredServiceProvider<ChatController> provider) {
        final var groupController = getServicesManager().load(GroupController.class);
        final var permission = getServicesManager().load(Permission.class);

        if (permission == null) {
            plugin.getComponentLogger().warn("Failed to register chat service wrapper, no permission service found");
            return;
        }

        final var wrapper = new VaultChatServiceWrapper(permission, groupController, provider.getProvider(), provider.getPlugin());
        getServicesManager().register(Chat.class, wrapper, provider.getPlugin(), getWrapperPriority(provider));
        plugin.getComponentLogger().info("Registered vault chat wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), provider.getPriority().name());
    }

    private ServicePriority getWrapperPriority(final RegisteredServiceProvider<?> provider) {
        return switch (provider.getPriority()) {
            case Highest -> ServicePriority.High;
            case High -> ServicePriority.Normal;
            case Normal -> ServicePriority.Low;
            case Low, Lowest -> ServicePriority.Lowest;
        };
    }

    private ServicesManager getServicesManager() {
        return plugin.getServer().getServicesManager();
    }
}
