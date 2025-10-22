package net.thenextlvl.service.command;

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
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.Controller;
import net.thenextlvl.service.api.character.CharacterController;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.hologram.HologramController;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.command.brigadier.SimpleCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@NullMarked
final class ServiceInfoCommand extends SimpleCommand {
    private ServiceInfoCommand(ServicePlugin plugin) {
        super(plugin, "info", "service.info");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(ServicePlugin plugin) {
        var command = new ServiceInfoCommand(plugin);
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
    public int run(CommandContext<CommandSourceStack> context) {
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

    private <C extends Controller, V> int info(CommandContext<CommandSourceStack> context, Class<C> type, @Nullable Class<V> vault, @Nullable Function<V, String> mapper, String name, String none) {
        var sender = context.getSource().getSender();
        C service = plugin.getServer().getServicesManager().load(type);
        var registrations = getRegistrations(type, service, vault, mapper);
        if (sendServiceInfo(sender, name, service != null ? service.getName() : null, registrations))
            return SINGLE_SUCCESS;
        plugin.bundle().sendMessage(sender, none);
        return 0;
    }

    private int infoBanks(CommandContext<CommandSourceStack> context) {
        return info(context, BankController.class, null, null, "Bank", "service.bank.none");
    }

    private int infoCharacters(CommandContext<CommandSourceStack> context) {
        return info(context, CharacterController.class, null, null, "Character", "service.character.none");
    }

    private int infoChat(CommandContext<CommandSourceStack> context) {
        return info(context, ChatController.class, Chat.class, Chat::getName, "Chat", "service.chat.none");
    }

    private int infoEconomy(CommandContext<CommandSourceStack> context) {
        return info(context, EconomyController.class, Economy.class, Economy::getName, "Economy", "service.economy.none");
    }

    private int infoGroups(CommandContext<CommandSourceStack> context) {
        return info(context, GroupController.class, null, null, "Group", "service.group.none");
    }

    private int infoHolograms(CommandContext<CommandSourceStack> context) {
        return info(context, HologramController.class, null, null, "Hologram", "service.hologram.none");
    }

    private int infoPermissions(CommandContext<CommandSourceStack> context) {
        return info(context, PermissionController.class, Permission.class, Permission::getName, "Permission", "service.permission.none");
    }

    private final JoinConfiguration separator = JoinConfiguration.builder()
            .separator(Component.text(", ", NamedTextColor.WHITE))
            .prefix(Component.text(" - ", NamedTextColor.DARK_GRAY))
            .build();

    private <C extends Controller, V> List<TextComponent> getRegistrations(Class<C> registration, @Nullable C loaded, @Nullable Class<V> vault, @Nullable Function<V, String> mapper) {
        var name = loaded != null ? loaded.getName() : null;
        var registrations = plugin.getServer().getServicesManager().getRegistrations(registration).stream()
                .map(RegisteredServiceProvider::getProvider)
                .map(Controller::getName);
        var vaultRegistrations = vault != null && mapper != null ? plugin.getServer().getServicesManager().getRegistrations(vault).stream()
                .map(RegisteredServiceProvider::getProvider)
                .map(mapper) : Stream.<String>empty();
        return Stream.concat(registrations, vaultRegistrations)
                .filter(provider -> !provider.equals(name))
                .distinct()
                .map(Component::text)
                .toList();
    }

    private boolean sendServiceInfo(CommandSender sender, String type, @Nullable String provider, List<TextComponent> registrations) {
        if (provider != null) plugin.bundle().sendMessage(sender, "service.provider.name",
                Placeholder.parsed("provider", provider), Placeholder.parsed("type", type));
        if (!registrations.isEmpty()) plugin.bundle().sendMessage(sender, "service.provider.registrations",
                Placeholder.component("registered", Component.join(separator, registrations)));
        return provider != null || !registrations.isEmpty();
    }
}
