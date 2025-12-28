package net.thenextlvl.service;

import dev.faststats.bukkit.BukkitMetrics;
import dev.faststats.core.chart.Chart;
import dev.faststats.core.chart.ChartId;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.key.Key;
import net.milkbowl.vault.Vault;
import net.thenextlvl.i18n.ComponentBundle;
import net.thenextlvl.service.api.character.CharacterController;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.hologram.HologramController;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.commands.ServiceCommand;
import net.thenextlvl.service.listeners.PluginListener;
import net.thenextlvl.service.listeners.ServiceListener;
import net.thenextlvl.service.providers.superperms.SuperPermsPermissionController;
import net.thenextlvl.service.version.PluginVersionChecker;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.jspecify.annotations.NullMarked;

import java.nio.file.Path;
import java.util.Locale;
import java.util.function.Function;

@NullMarked
public class ServicePlugin extends Vault {
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
            .addChart(createChart(BankController.class, BankController::getName, "bank_providers"))
            .addChart(createChart(GroupController.class, GroupController::getName, "group_providers"))
            .addChart(createChart(ChatController.class, ChatController::getName, "chat_providers"))
            .addChart(createChart(EconomyController.class, EconomyController::getName, "economy_providers"))
            .addChart(createChart(PermissionController.class, PermissionController::getName, "permission_providers"))
            .addChart(createChart(HologramController.class, HologramController::getName, "hologram_providers"))
            .addChart(createChart(CharacterController.class, CharacterController::getName, "npc_providers"))
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

    private <T> void addCustomChart(Class<T> service, Function<T, String> function, String chartId) {
        T loaded = getServer().getServicesManager().load(service);
        metrics.addCustomChart(new SimplePie(chartId, () -> loaded != null ? function.apply(loaded) : "None"));
    }

    private <T> Chart<String[]> createChart(Class<T> service, Function<T, String> function, @ChartId String chartId) {
        return Chart.stringArray(chartId, () -> {
            return getServer().getServicesManager().getRegistrations(service).stream()
                    .map(RegisteredServiceProvider::getProvider)
                    .map(function)
                    .toArray(String[]::new);
        });
    }
}