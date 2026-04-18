package net.thenextlvl.service.plugin.listeners;

import net.thenextlvl.service.Controller;
import net.thenextlvl.service.DoNotWrap;
import net.thenextlvl.service.chat.ChatController;
import net.thenextlvl.service.economy.EconomyController;
import net.thenextlvl.service.economy.bank.BankController;
import net.thenextlvl.service.group.GroupController;
import net.thenextlvl.service.permission.PermissionController;
import net.thenextlvl.service.plugin.wrapper.Wrapper;
import net.thenextlvl.service.plugin.wrapper.service.BankServiceWrapper;
import net.thenextlvl.service.plugin.wrapper.service.ChatServiceWrapper;
import net.thenextlvl.service.plugin.wrapper.service.EconomyServiceWrapper;
import net.thenextlvl.service.plugin.wrapper.service.PermissionServiceWrapper;
import net.thenextlvl.service.plugin.wrapper.vault.VaultChatServiceWrapper;
import net.thenextlvl.service.plugin.wrapper.vault.VaultEconomyServiceWrapper;
import net.thenextlvl.service.plugin.wrapper.vault.VaultPermissionServiceWrapper;
import net.thenextlvl.service.plugin.wrapper.vaultunlocked.VaultUnlockedChatServiceWrapper;
import net.thenextlvl.service.plugin.wrapper.vaultunlocked.VaultUnlockedEconomyServiceWrapper;
import net.thenextlvl.service.plugin.wrapper.vaultunlocked.VaultUnlockedPermissionServiceWrapper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

public final class ServiceListener implements Listener {
    private final Plugin plugin;

    public ServiceListener(final Plugin plugin) {
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

    @SuppressWarnings({"unchecked", "IfCanBeSwitch"})
    private void loadWrapper(final RegisteredServiceProvider<?> registered) {
        final var provider = registered.getProvider();

        if (provider.getClass().isAnnotationPresent(DoNotWrap.class)) return;
        if (provider instanceof Wrapper) return;

        if (provider instanceof PermissionController) {
            loadVaultPermissionWrapper((RegisteredServiceProvider<PermissionController>) registered);
            loadVaultUnlockedPermissionWrapper((RegisteredServiceProvider<PermissionController>) registered);
        } else if (provider instanceof net.milkbowl.vault2.permission.Permission) {
            loadVaultUnlockedServicePermissionWrapper((RegisteredServiceProvider<net.milkbowl.vault2.permission.Permission>) registered);
        } else if (provider instanceof net.milkbowl.vault.permission.Permission) {
            loadServicePermissionWrapper((RegisteredServiceProvider<net.milkbowl.vault.permission.Permission>) registered);
            loadVaultUnlockedVaultPermissionWrapper((RegisteredServiceProvider<net.milkbowl.vault.permission.Permission>) registered);
        } else if (provider instanceof EconomyController) {
            loadVaultEconomyWrapper((RegisteredServiceProvider<EconomyController>) registered);
            loadVaultUnlockedEconomyWrapper((RegisteredServiceProvider<EconomyController>) registered);
        } else if (provider instanceof net.milkbowl.vault2.economy.Economy) {
            loadVaultUnlockedServiceEconomyWrapper((RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy>) registered);
        } else if (provider instanceof net.milkbowl.vault.economy.Economy) {
            loadServiceEconomyWrapper((RegisteredServiceProvider<net.milkbowl.vault.economy.Economy>) registered);
            loadVaultUnlockedVaultEconomyWrapper((RegisteredServiceProvider<net.milkbowl.vault.economy.Economy>) registered);
        } else if (provider instanceof ChatController) {
            loadVaultChatWrapper((RegisteredServiceProvider<ChatController>) registered);
            loadVaultUnlockedChatWrapper((RegisteredServiceProvider<ChatController>) registered);
        } else if (provider instanceof net.milkbowl.vault2.chat.Chat) {
            loadVaultUnlockedServiceChatWrapper((RegisteredServiceProvider<net.milkbowl.vault2.chat.Chat>) registered);
        } else if (provider instanceof net.milkbowl.vault.chat.Chat) {
            loadServiceChatWrapper((RegisteredServiceProvider<net.milkbowl.vault.chat.Chat>) registered);
            loadVaultUnlockedVaultChatWrapper((RegisteredServiceProvider<net.milkbowl.vault.chat.Chat>) registered);
        }
    }

    private void loadServicePermissionWrapper(final RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> provider) {
        final var wrapper = new PermissionServiceWrapper(provider.getProvider(), provider.getPlugin());
        final var priority = getWrapperPriority(provider);
        getServicesManager().register(PermissionController.class, wrapper, provider.getPlugin(), priority);
        plugin.getComponentLogger().info("Registered permission service wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), priority.name());
    }

    private void loadServiceEconomyWrapper(final RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> provider) {
        final var wrapper = new EconomyServiceWrapper(provider.getProvider(), provider.getPlugin());
        final var priority = getWrapperPriority(provider);
        getServicesManager().register(EconomyController.class, wrapper, provider.getPlugin(), priority);
        plugin.getComponentLogger().info("Registered economy service wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), priority.name());

        if (!provider.getProvider().hasBankSupport()) return;
        final var banks = new BankServiceWrapper(provider.getProvider(), provider.getPlugin(), wrapper);
        getServicesManager().register(BankController.class, banks, provider.getPlugin(), priority);
        plugin.getComponentLogger().info("Registered bank service wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), priority.name());
    }

    private void loadServiceChatWrapper(final RegisteredServiceProvider<net.milkbowl.vault.chat.Chat> provider) {
        final var wrapper = new ChatServiceWrapper(provider.getProvider(), provider.getPlugin());
        final var priority = getWrapperPriority(provider);
        getServicesManager().register(ChatController.class, wrapper, provider.getPlugin(), priority);
        plugin.getComponentLogger().info("Registered chat service wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), priority.name());
    }

    private void loadVaultPermissionWrapper(final RegisteredServiceProvider<PermissionController> provider) {
        final var groupController = getServicesManager().load(GroupController.class);
        final var wrapper = new VaultPermissionServiceWrapper(groupController, provider.getProvider(), provider.getPlugin());
        final var priority = getWrapperPriority(provider);
        getServicesManager().register(net.milkbowl.vault.permission.Permission.class, wrapper, provider.getPlugin(), priority);
        plugin.getComponentLogger().info("Registered vault permission wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), priority.name());
    }

    private void loadVaultEconomyWrapper(final RegisteredServiceProvider<EconomyController> provider) {
        final var wrapper = new VaultEconomyServiceWrapper(provider.getProvider(), provider.getPlugin());
        final var priority = getWrapperPriority(provider);
        getServicesManager().register(net.milkbowl.vault.economy.Economy.class, wrapper, provider.getPlugin(), priority);
        plugin.getComponentLogger().info("Registered vault economy wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), priority.name());
    }

    private void loadVaultChatWrapper(final RegisteredServiceProvider<ChatController> provider) {
        final var groupController = getServicesManager().load(GroupController.class);
        final var permission = getServicesManager().load(net.milkbowl.vault.permission.Permission.class);

        if (permission == null) {
            plugin.getComponentLogger().warn("Failed to register chat service wrapper, no permission service found");
            return;
        }

        final var wrapper = new VaultChatServiceWrapper(permission, groupController, provider.getProvider(), provider.getPlugin());
        final var priority = getWrapperPriority(provider);
        getServicesManager().register(net.milkbowl.vault.chat.Chat.class, wrapper, provider.getPlugin(), priority);
        plugin.getComponentLogger().info("Registered vault chat wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), priority.name());
    }

    private void loadVaultUnlockedPermissionWrapper(final RegisteredServiceProvider<PermissionController> provider) {
        final var groupController = getServicesManager().load(GroupController.class);
        final var wrapper = new VaultUnlockedPermissionServiceWrapper(groupController, provider.getProvider(), provider.getPlugin());
        final var priority = getWrapperPriority(provider);
        getServicesManager().register(net.milkbowl.vault2.permission.Permission.class, wrapper, provider.getPlugin(), priority);
        plugin.getComponentLogger().info("Registered vault unlocked permission wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), priority.name());
    }

    private void loadVaultUnlockedEconomyWrapper(final RegisteredServiceProvider<EconomyController> provider) {
        final var wrapper = new VaultUnlockedEconomyServiceWrapper(provider.getProvider(), provider.getPlugin());
        final var priority = getWrapperPriority(provider);
        getServicesManager().register(net.milkbowl.vault2.economy.Economy.class, wrapper, provider.getPlugin(), priority);
        plugin.getComponentLogger().info("Registered vault unlocked economy wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), priority.name());
    }

    private void loadVaultUnlockedChatWrapper(final RegisteredServiceProvider<ChatController> provider) {
        final var groupController = getServicesManager().load(GroupController.class);
        final var permission = getServicesManager().load(net.milkbowl.vault2.permission.Permission.class);

        if (permission == null) {
            plugin.getComponentLogger().warn("Failed to register vault unlocked chat wrapper, no permission service found");
            return;
        }

        final var wrapper = new VaultUnlockedChatServiceWrapper(permission, groupController, provider.getProvider(), provider.getPlugin());
        final var priority = getWrapperPriority(provider);
        getServicesManager().register(net.milkbowl.vault2.chat.Chat.class, wrapper, provider.getPlugin(), priority);
        plugin.getComponentLogger().info("Registered vault unlocked chat wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), priority.name());
    }

    private void loadVaultUnlockedServicePermissionWrapper(final RegisteredServiceProvider<net.milkbowl.vault2.permission.Permission> provider) {
        final var wrapper = new net.thenextlvl.service.plugin.wrapper.service.VaultUnlockedPermissionServiceWrapper(provider.getProvider(), provider.getPlugin());
        final var priority = getWrapperPriority(provider);
        getServicesManager().register(PermissionController.class, wrapper, provider.getPlugin(), priority);
        plugin.getComponentLogger().info("Registered vault unlocked permission service wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), priority.name());
    }

    private void loadVaultUnlockedVaultPermissionWrapper(final RegisteredServiceProvider<net.milkbowl.vault.permission.Permission> provider) {
        final var groupController = getServicesManager().load(GroupController.class);
        final var service = new PermissionServiceWrapper(provider.getProvider(), provider.getPlugin());
        final var wrapper = new VaultUnlockedPermissionServiceWrapper(groupController, service, provider.getPlugin());
        final var priority = getWrapperPriority(provider);
        getServicesManager().register(net.milkbowl.vault2.permission.Permission.class, wrapper, provider.getPlugin(), priority);
        plugin.getComponentLogger().info("Registered vault to vault unlocked permission wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), priority.name());
    }

    private void loadVaultUnlockedServiceEconomyWrapper(final RegisteredServiceProvider<net.milkbowl.vault2.economy.Economy> provider) {
        final var wrapper = new net.thenextlvl.service.plugin.wrapper.service.VaultUnlockedEconomyServiceWrapper(provider.getProvider(), provider.getPlugin());
        final var priority = getWrapperPriority(provider);
        getServicesManager().register(EconomyController.class, wrapper, provider.getPlugin(), priority);
        plugin.getComponentLogger().info("Registered vault unlocked economy service wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), priority.name());
    }

    private void loadVaultUnlockedVaultEconomyWrapper(final RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> provider) {
        final var service = new EconomyServiceWrapper(provider.getProvider(), provider.getPlugin());
        final var wrapper = new VaultUnlockedEconomyServiceWrapper(service, provider.getPlugin());
        final var priority = getWrapperPriority(provider);
        getServicesManager().register(net.milkbowl.vault2.economy.Economy.class, wrapper, provider.getPlugin(), priority);
        plugin.getComponentLogger().info("Registered vault to vault unlocked economy wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), priority.name());
    }

    private void loadVaultUnlockedServiceChatWrapper(final RegisteredServiceProvider<net.milkbowl.vault2.chat.Chat> provider) {
        final var wrapper = new net.thenextlvl.service.plugin.wrapper.service.VaultUnlockedChatServiceWrapper(provider.getProvider(), provider.getPlugin());
        final var priority = getWrapperPriority(provider);
        getServicesManager().register(ChatController.class, wrapper, provider.getPlugin(), priority);
        plugin.getComponentLogger().info("Registered vault unlocked chat service wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), priority.name());
    }

    private void loadVaultUnlockedVaultChatWrapper(final RegisteredServiceProvider<net.milkbowl.vault.chat.Chat> provider) {
        final var groupController = getServicesManager().load(GroupController.class);
        final var permission = getServicesManager().load(net.milkbowl.vault2.permission.Permission.class);

        if (permission == null) {
            plugin.getComponentLogger().warn("Failed to register vault to vault unlocked chat wrapper, no permission service found");
            return;
        }

        final var service = new ChatServiceWrapper(provider.getProvider(), provider.getPlugin());
        final var wrapper = new VaultUnlockedChatServiceWrapper(permission, groupController, service, provider.getPlugin());
        final var priority = getWrapperPriority(provider);
        getServicesManager().register(net.milkbowl.vault2.chat.Chat.class, wrapper, provider.getPlugin(), priority);
        plugin.getComponentLogger().info("Registered vault to vault unlocked chat wrapper for {} - {} ({})",
                provider.getPlugin().getName(), provider.getProvider().getName(), priority.name());
    }

    private ServicesManager getServicesManager() {
        return plugin.getServer().getServicesManager();
    }

    private ServicePriority getWrapperPriority(final RegisteredServiceProvider<?> provider) {
        return switch (provider.getPriority()) {
            case Highest -> ServicePriority.High;
            case High -> ServicePriority.Normal;
            case Normal -> ServicePriority.Low;
            case Low, Lowest -> ServicePriority.Lowest;
        };
    }
}
