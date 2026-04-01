package net.thenextlvl.service.plugin;

import dev.faststats.bukkit.BukkitMetrics;
import dev.faststats.core.data.Metric;
import dev.faststats.core.data.SourceId;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.key.Key;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.i18n.ComponentBundle;
import net.thenextlvl.service.character.CharacterController;
import net.thenextlvl.service.chat.ChatController;
import net.thenextlvl.service.economy.EconomyController;
import net.thenextlvl.service.economy.bank.BankController;
import net.thenextlvl.service.group.GroupController;
import net.thenextlvl.service.hologram.HologramController;
import net.thenextlvl.service.permission.PermissionController;
import net.thenextlvl.service.plugin.commands.ServiceCommand;
import net.thenextlvl.service.plugin.listeners.PluginListener;
import net.thenextlvl.service.plugin.listeners.ServiceListener;
import net.thenextlvl.service.plugin.version.PluginVersionChecker;
import net.thenextlvl.service.providers.superperms.SuperPermsPermission;
import net.thenextlvl.service.providers.superperms.SuperPermsPermissionController;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import java.nio.file.Path;
import java.util.Locale;
import java.util.function.Function;

public final class ServicePlugin extends Vault {
    private final PluginVersionChecker versionChecker = new PluginVersionChecker(this);
    private final Metrics metrics = new Metrics(this, 23083);

    private final Key key = Key.key("service_io", "translations");
    private final Path translations = getDataPath().resolve("translations");
    private final ComponentBundle bundle = ComponentBundle.builder(key, translations)
            .placeholder("prefix", "prefix")
            .resource("service-io.properties", Locale.US)
            .resource("service-io_german.properties", Locale.GERMANY)
            .build();

    private final dev.faststats.core.Metrics fastStats = BukkitMetrics.factory()
            .addMetric(createChart(BankController.class, BankController::getName, "bank_providers"))
            .addMetric(createChart(GroupController.class, GroupController::getName, "group_providers"))
            .addMetric(createChart(ChatController.class, ChatController::getName, "chat_providers"))
            .addMetric(createChart(EconomyController.class, EconomyController::getName, "economy_providers"))
            .addMetric(createChart(PermissionController.class, PermissionController::getName, "permission_providers"))
            .addMetric(createChart(HologramController.class, HologramController::getName, "hologram_providers"))
            .addMetric(createChart(CharacterController.class, CharacterController::getName, "npc_providers"))
            .errorTracker(ServiceBootstrapper.ERROR_TRACKER)
            .token("f7e1aef24e2f8fe48abfb84ccfae5163")
            .create(this);

    @Override
    public void onLoad() {
        versionChecker.checkVersion();
        registerServices();
        registerCommands();
    }

    @Override
    public void onEnable() {
        registerListeners();
        addCustomCharts();
    }

    @Override
    public void onDisable() {
        metrics.shutdown();
        fastStats.shutdown();
    }

    private void registerServices() {
        getServer().getServicesManager().register(Permission.class, new SuperPermsPermission(this), this, ServicePriority.Lowest);
        getServer().getServicesManager().register(PermissionController.class, new SuperPermsPermissionController(this), this, ServicePriority.Lowest);
        getComponentLogger().debug("Added SuperPerms as backup permission provider (Lowest)");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ServiceListener(this), this);
        getServer().getPluginManager().registerEvents(new PluginListener(this), this);
    }

    private void registerCommands() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event -> {
            event.registrar().register(ServiceCommand.create(this));
        }));
    }

    public ComponentBundle bundle() {
        return bundle;
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

    private <T> void addCustomChart(final Class<T> service, final Function<T, String> function, final String chartId) {
        final T loaded = getServer().getServicesManager().load(service);
        metrics.addCustomChart(new SimplePie(chartId, () -> loaded != null ? function.apply(loaded) : "None"));
    }

    private <T> Metric<String[]> createChart(final Class<T> service, final Function<T, String> function, @SourceId final String chartId) {
        return Metric.stringArray(chartId, () -> {
            return getServer().getServicesManager().getRegistrations(service).stream()
                    .map(RegisteredServiceProvider::getProvider)
                    .map(function)
                    .toArray(String[]::new);
        });
    }
}