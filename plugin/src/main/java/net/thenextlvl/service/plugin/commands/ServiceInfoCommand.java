package net.thenextlvl.service.plugin.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.Controller;
import net.thenextlvl.service.character.CharacterController;
import net.thenextlvl.service.chat.ChatController;
import net.thenextlvl.service.economy.EconomyController;
import net.thenextlvl.service.economy.bank.BankController;
import net.thenextlvl.service.group.GroupController;
import net.thenextlvl.service.hologram.HologramController;
import net.thenextlvl.service.permission.PermissionController;
import net.thenextlvl.service.plugin.ServicePlugin;
import net.thenextlvl.service.plugin.commands.brigadier.SimpleCommand;
import net.thenextlvl.service.plugin.wrapper.Wrapper;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class ServiceInfoCommand extends SimpleCommand {
    private static final Comparator<ServiceRegistration> registrationOrder = (left, right) -> {
        final var active = Boolean.compare(right.active(), left.active());
        if (active != 0) return active;
        final var priority = Integer.compare(right.priority().ordinal() + 1, left.priority().ordinal() + 1);
        if (priority != 0) return priority;
        final var source = left.source().compareTo(right.source());
        if (source != 0) return source;
        return left.name().compareTo(right.name());
    };
    private static final Comparator<ServiceRegistration> representativeOrder = (left, right) -> {
        final var wrapper = Boolean.compare(left.wrapper(), right.wrapper());
        if (wrapper != 0) return wrapper;
        return registrationOrder.compare(left, right);
    };
    private static final Comparator<ServiceGroup> groupOrder = Comparator
            .comparing(ServiceGroup::active).reversed()
            .thenComparing(ServiceGroup::name);

    private ServiceInfoCommand(final ServicePlugin plugin) {
        super(plugin, "info", "service.info");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final ServicePlugin plugin) {
        final var command = new ServiceInfoCommand(plugin);
        return command.create()
                .then(Commands.literal("banks").executes(command::infoBanks))
                .then(Commands.literal("characters").executes(command::infoCharacters))
                .then(Commands.literal("chat").executes(command::infoChat))
                .then(Commands.literal("economy").executes(command::infoEconomy))
                .then(Commands.literal("groups").executes(command::infoGroups))
                .then(Commands.literal("holograms").executes(command::infoHolograms))
                .then(Commands.literal("permissions").executes(command::infoPermissions))
                .executes(command);
    }

    @Override
    public int run(final CommandContext<CommandSourceStack> context) {
        plugin.bundle().sendMessage(context.getSource().getSender(), "service.version",
                Placeholder.parsed("version", plugin.getPluginMeta().getVersion()));

        infoBanks(context);
        infoCharacters(context);
        infoChat(context);
        infoEconomy(context);
        infoGroups(context);
        infoHolograms(context);
        infoPermissions(context);

        return SINGLE_SUCCESS;
    }

    private <C extends Controller, V, U> int info(
            final CommandContext<CommandSourceStack> context, final Class<C> type,
            @Nullable final Class<V> vault, @Nullable final Class<U> vaultUnlocked,
            @Nullable final Function<V, String> vaultMapper,
            @Nullable final Function<U, String> vaultUnlockedMapper
    ) {
        final var sender = context.getSource().getSender();
        final var sources = new ArrayList<ServiceSource>();

        sources.add(source(type, "ServiceIO", Controller::getName));
        if (vault != null && vaultMapper != null)
            sources.add(source(vault, "Vault", vaultMapper));
        if (vaultUnlocked != null && vaultUnlockedMapper != null)
            sources.add(source(vaultUnlocked, "VaultUnlocked", vaultUnlockedMapper));

        sendServiceInfo(sender, buildSection(type, sources));
        return SINGLE_SUCCESS;
    }

    private static final Map<Class<? extends Controller>, String> translations = Map.ofEntries(
            Map.entry(BankController.class, "service.name.bank"),
            Map.entry(CharacterController.class, "service.name.character"),
            Map.entry(ChatController.class, "service.name.chat"),
            Map.entry(EconomyController.class, "service.name.economy"),
            Map.entry(GroupController.class, "service.name.group"),
            Map.entry(HologramController.class, "service.name.hologram"),
            Map.entry(PermissionController.class, "service.name.permission")
    );
    private static final Map<Class<? extends Controller>, String> sectionTranslations = Map.ofEntries(
            Map.entry(BankController.class, "service.section.type.bank"),
            Map.entry(CharacterController.class, "service.section.type.character"),
            Map.entry(ChatController.class, "service.section.type.chat"),
            Map.entry(EconomyController.class, "service.section.type.economy"),
            Map.entry(GroupController.class, "service.section.type.group"),
            Map.entry(HologramController.class, "service.section.type.hologram"),
            Map.entry(PermissionController.class, "service.section.type.permission")
    );

    static Component translate(final ServicePlugin plugin, final Audience audience, final Class<? extends Controller> type) {
        final var translation = translations.get(type);
        if (translation != null) return plugin.bundle().component(translation, audience);
        throw new IllegalStateException("Unexpected controller: " + type);
    }

    private static Component translateSection(final ServicePlugin plugin, final Audience audience, final Class<? extends Controller> type) {
        final var translation = sectionTranslations.get(type);
        if (translation != null) return plugin.bundle().component(translation, audience);
        throw new IllegalStateException("Unexpected controller: " + type);
    }

    private int infoBanks(final CommandContext<CommandSourceStack> context) {
        return info(context, BankController.class, null, null, null, null);
    }

    private int infoCharacters(final CommandContext<CommandSourceStack> context) {
        return info(context, CharacterController.class, null, null, null, null);
    }

    private int infoChat(final CommandContext<CommandSourceStack> context) {
        return info(context, ChatController.class,
                Chat.class, net.milkbowl.vault2.chat.Chat.class,
                Chat::getName, net.milkbowl.vault2.chat.Chat::getName);
    }

    private int infoEconomy(final CommandContext<CommandSourceStack> context) {
        return info(context, EconomyController.class,
                Economy.class, net.milkbowl.vault2.economy.Economy.class,
                Economy::getName, net.milkbowl.vault2.economy.Economy::getName);
    }

    private int infoGroups(final CommandContext<CommandSourceStack> context) {
        return info(context, GroupController.class, null, null, null, null);
    }

    private int infoHolograms(final CommandContext<CommandSourceStack> context) {
        return info(context, HologramController.class, null, null, null, null);
    }

    private int infoPermissions(final CommandContext<CommandSourceStack> context) {
        return info(context, PermissionController.class,
                Permission.class, net.milkbowl.vault2.permission.Permission.class,
                Permission::getName, net.milkbowl.vault2.permission.Permission::getName);
    }

    private <C extends Controller> ServiceSection buildSection(
            final Class<C> type, final List<ServiceSource> sources
    ) {
        final var activeRegistration = plugin.getServer().getServicesManager().getRegistration(type);
        final var activeProvider = activeRegistration != null ? activeRegistration.getProvider() : null;
        final var entries = sources.stream()
                .flatMap(source -> getRegistrations(source, activeProvider))
                .toList();
        final var groups = entries.stream()
                .collect(Collectors.groupingBy(ServiceRegistration::group))
                .values().stream()
                .map(this::buildGroup)
                .sorted(groupOrder)
                .toList();
        return new ServiceSection(type, groups);
    }

    @SuppressWarnings("unchecked")
    private Stream<ServiceRegistration> getRegistrations(
            final ServiceSource source, @Nullable final Object activeProvider
    ) {
        return plugin.getServer().getServicesManager().getRegistrations((Class<Object>) source.type())
                .stream()
                .map(registration -> createRegistration(source, registration, registration.getProvider() == activeProvider));
    }

    private ServiceGroup buildGroup(final List<ServiceRegistration> registrations) {
        final var entries = registrations.stream()
                .sorted(registrationOrder)
                .toList();
        final var representative = entries.stream().min(representativeOrder)
                .orElseThrow();
        final var active = entries.stream().anyMatch(ServiceRegistration::active);
        return new ServiceGroup(representative.group(), active, entries);
    }

    private ServiceRegistration createRegistration(
            final ServiceSource source, final RegisteredServiceProvider<?> registration, final boolean active
    ) {
        final var provider = registration.getProvider();
        final var name = source.nameMapper().apply(provider);
        return new ServiceRegistration(
                registration.getPlugin().getName(),
                name,
                source.name(),
                registration.getPriority(),
                active,
                provider instanceof Wrapper
        );
    }

    private void sendServiceInfo(final CommandSender sender, final ServiceSection section) {
        plugin.bundle().sendMessage(sender, "service.section.header",
                Placeholder.component("type", translateSection(plugin, sender, section.type())),
                Placeholder.parsed("count", Integer.toString(section.groups().size())));
        if (section.groups().isEmpty()) {
            plugin.bundle().sendMessage(sender, "service.section.empty");
            return;
        }
        section.groups().forEach(group -> sendGroup(sender, group));
    }

    private void sendGroup(final CommandSender sender, final ServiceGroup group) {
        plugin.bundle().sendMessage(sender, "service.section.group",
                Placeholder.parsed("provider", group.name()));
        for (var i = 0; i < group.registrations().size(); i++) {
            final var registration = group.registrations().get(i);
            plugin.bundle().sendMessage(sender, i + 1 == group.registrations().size()
                            ? "service.section.entry.last"
                            : "service.section.entry.branch",
                    Placeholder.parsed("tree", i + 1 == group.registrations().size() ? "└" : "├"),
                    Placeholder.parsed("name", registration.name()),
                    Placeholder.parsed("priority", registration.priority().name()),
                    Placeholder.parsed("source", registration.source()));
        }
    }

    private static <T> ServiceSource source(final Class<T> type, final String source, final Function<T, String> nameMapper) {
        return new ServiceSource(type, source, value -> nameMapper.apply(type.cast(value)));
    }

    private record ServiceSource(Class<?> type, String name, Function<Object, String> nameMapper) {
    }

    private record ServiceSection(Class<? extends Controller> type, List<ServiceGroup> groups) {
    }

    private record ServiceGroup(String name, boolean active, List<ServiceRegistration> registrations) {
    }

    private record ServiceRegistration(
            String group, String name, String source, ServicePriority priority, boolean active, boolean wrapper
    ) {
    }
}
