package net.thenextlvl.service.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.permission.PermissionController;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public class ServiceInfoCommand {
    private final ServicePlugin plugin;

    LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("info")
                .then(Commands.literal("bank").executes(this::infoBank))
                .then(Commands.literal("chat").executes(this::infoChat))
                .then(Commands.literal("economy").executes(this::infoEconomy))
                .then(Commands.literal("groups").executes(this::infoGroups))
                .then(Commands.literal("permissions").executes(this::infoPermissions))
                .requires(stack -> stack.getSender().hasPermission("service.info"))
                .executes(this::info);
    }

    @SuppressWarnings("DuplicatedCode")
    private int info(CommandContext<CommandSourceStack> context) {

        // ED8106

        context.getSource().getSender().sendRichMessage("ServiceIO Information (v<version>)",
                Placeholder.parsed("version", plugin.getPluginMeta().getVersion()));

        infoBank(context);
        infoChat(context);
        infoEconomy(context);
        infoGroups(context);
        infoPermissions(context);

        return Command.SINGLE_SUCCESS;
    }

    private int infoBank(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var bank = plugin.getServer().getServicesManager().load(BankController.class);
        var banks = getRegistrations(BankController.class, bank, BankController::getName);
        if (sendServiceInfo(sender, "Bank", bank != null ? bank.getName() : null, banks))
            return Command.SINGLE_SUCCESS;
        sender.sendRichMessage("<red>No bank service found.");
        return 0;
    }

    private int infoChat(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var chat = plugin.getServer().getServicesManager().load(ChatController.class);
        var chats = getRegistrations(ChatController.class, chat, ChatController::getName);
        if (sendServiceInfo(sender, "Chat", chat != null ? chat.getName() : null, chats))
            return Command.SINGLE_SUCCESS;
        sender.sendRichMessage("<red>No chat service found.");
        return 0;
    }

    private int infoEconomy(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var economy = plugin.getServer().getServicesManager().load(EconomyController.class);
        var economies = getRegistrations(EconomyController.class, economy, EconomyController::getName);
        if (sendServiceInfo(sender, "Economy", economy != null ? economy.getName() : null, economies))
            return Command.SINGLE_SUCCESS;
        sender.sendRichMessage("<red>No economy service found.");
        return 0;
    }

    private int infoGroups(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var group = plugin.getServer().getServicesManager().load(GroupController.class);
        var groups = getRegistrations(GroupController.class, group, GroupController::getName);
        if (sendServiceInfo(sender, "Group", group != null ? group.getName() : null, groups))
            return Command.SINGLE_SUCCESS;
        sender.sendRichMessage("<red>No group service found.");
        return 0;
    }

    private int infoPermissions(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        var permission = plugin.getServer().getServicesManager().load(PermissionController.class);
        var permissions = getRegistrations(PermissionController.class, permission, PermissionController::getName);
        if (sendServiceInfo(sender, "Permission", permission != null ? permission.getName() : null, permissions))
            return Command.SINGLE_SUCCESS;
        sender.sendRichMessage("<red>No permission service found.");
        return 0;
    }

    private final JoinConfiguration separator = JoinConfiguration.builder()
            .separator(Component.text(", ", NamedTextColor.WHITE))
            .prefix(Component.text(" - ", NamedTextColor.DARK_GRAY))
            .build();

    private <T> List<TextComponent> getRegistrations(Class<T> registration, @Nullable T loaded, Function<T, String> mapper) {
        return plugin.getServer().getServicesManager().getRegistrations(registration).stream()
                .map(RegisteredServiceProvider::getProvider)
                .filter(controller -> controller != loaded)
                .map(mapper)
                .map(Component::text)
                .toList();
    }

    private boolean sendServiceInfo(CommandSender sender, String type, @Nullable String provider, List<TextComponent> registrations) {
        if (provider != null) sender.sendRichMessage("<#0288D1><type>: <provider>",
                Placeholder.parsed("provider", provider), Placeholder.parsed("type", type));
        if (!registrations.isEmpty()) sender.sendRichMessage("<green><registered>",
                Placeholder.component("registered", Component.join(separator, registrations)));
        return provider != null || !registrations.isEmpty();
    }
}
