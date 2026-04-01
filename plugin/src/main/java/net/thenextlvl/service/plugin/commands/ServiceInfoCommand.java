package net.thenextlvl.service.plugin.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
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
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

final class ServiceInfoCommand extends SimpleCommand {
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

    private <C extends Controller, V> int info(final CommandContext<CommandSourceStack> context, final Class<C> type, @Nullable final Class<V> vault, @Nullable final Function<V, String> mapper, final String name, final String none) {
        final var sender = context.getSource().getSender();
        final C service = plugin.getServer().getServicesManager().load(type);
        final var registrations = getRegistrations(type, service, vault, mapper);
        if (sendServiceInfo(sender, name, service != null ? service.getName() : null, registrations))
            return SINGLE_SUCCESS;
        plugin.bundle().sendMessage(sender, none);
        return 0;
    }

    private int infoBanks(final CommandContext<CommandSourceStack> context) {
        return info(context, BankController.class, null, null, "Bank", "service.bank.none");
    }

    private int infoCharacters(final CommandContext<CommandSourceStack> context) {
        return info(context, CharacterController.class, null, null, "Character", "service.character.none");
    }

    private int infoChat(final CommandContext<CommandSourceStack> context) {
        return info(context, ChatController.class, Chat.class, Chat::getName, "Chat", "service.chat.none");
    }

    private int infoEconomy(final CommandContext<CommandSourceStack> context) {
        return info(context, EconomyController.class, Economy.class, Economy::getName, "Economy", "service.economy.none");
    }

    private int infoGroups(final CommandContext<CommandSourceStack> context) {
        return info(context, GroupController.class, null, null, "Group", "service.group.none");
    }

    private int infoHolograms(final CommandContext<CommandSourceStack> context) {
        return info(context, HologramController.class, null, null, "Hologram", "service.hologram.none");
    }

    private int infoPermissions(final CommandContext<CommandSourceStack> context) {
        return info(context, PermissionController.class, Permission.class, Permission::getName, "Permission", "service.permission.none");
    }

    private final JoinConfiguration separator = JoinConfiguration.builder()
            .separator(Component.text(", ", NamedTextColor.WHITE))
            .prefix(Component.text(" - ", NamedTextColor.DARK_GRAY))
            .build();

    private <C extends Controller, V> List<TextComponent> getRegistrations(final Class<C> registration, @Nullable final C loaded, @Nullable final Class<V> vault, @Nullable final Function<V, String> mapper) {
        final var name = loaded != null ? loaded.getName() : null;
        final var registrations = plugin.getServer().getServicesManager().getRegistrations(registration).stream()
                .map(RegisteredServiceProvider::getProvider)
                .map(Controller::getName);
        final var vaultRegistrations = vault != null && mapper != null
                ? plugin.getServer().getServicesManager().getRegistrations(vault).stream()
                  .map(RegisteredServiceProvider::getProvider)
                  .map(mapper) : Stream.<String>empty();
        return Stream.concat(registrations, vaultRegistrations)
                .filter(provider -> !provider.equals(name))
                .distinct()
                .map(Component::text)
                .toList();
    }

    private boolean sendServiceInfo(final CommandSender sender, final String type, @Nullable final String provider, final List<TextComponent> registrations) {
        if (provider != null) plugin.bundle().sendMessage(sender, "service.provider.name",
                Placeholder.parsed("provider", provider), Placeholder.parsed("type", type));
        if (!registrations.isEmpty()) plugin.bundle().sendMessage(sender, "service.provider.registrations",
                Placeholder.component("registered", Component.join(separator, registrations)));
        return provider != null || !registrations.isEmpty();
    }
}
