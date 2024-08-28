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
                .then(Commands.literal("chat"))
                .then(Commands.literal("economy"))
                .then(Commands.literal("groups"))
                .then(Commands.literal("permissions"))
                .requires(stack -> stack.getSender().hasPermission("service.info"))
                .executes(this::info);
    }

    @SuppressWarnings("DuplicatedCode")
    private int info(CommandContext<CommandSourceStack> context) {

        var sender = context.getSource().getSender();

        var bank = plugin.getServer().getServicesManager().load(BankController.class);
        var chat = plugin.getServer().getServicesManager().load(ChatController.class);
        var economy = plugin.getServer().getServicesManager().load(EconomyController.class);
        var group = plugin.getServer().getServicesManager().load(GroupController.class);
        var permission = plugin.getServer().getServicesManager().load(PermissionController.class);

        var banks = getRegistrations(BankController.class, bank, BankController::getName);
        var chats = getRegistrations(ChatController.class, chat, ChatController::getName);
        var economies = getRegistrations(EconomyController.class, economy, EconomyController::getName);
        var groups = getRegistrations(GroupController.class, group, GroupController::getName);
        var permissions = getRegistrations(PermissionController.class, permission, PermissionController::getName);

        // ED8106

        sender.sendRichMessage("ServiceIO Information (v<version>)",
                Placeholder.parsed("version", plugin.getPluginMeta().getVersion()));

        sendServiceInfo(sender, "Bank", bank != null ? bank.getName() : null, banks);
        sendServiceInfo(sender, "Chat", chat != null ? chat.getName() : null, chats);
        sendServiceInfo(sender, "Economy", economy != null ? economy.getName() : null, economies);
        sendServiceInfo(sender, "Group", group != null ? group.getName() : null, groups);
        sendServiceInfo(sender, "Permission", permission != null ? permission.getName() : null, permissions);

        return Command.SINGLE_SUCCESS;
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

    private void sendServiceInfo(CommandSender sender, String type, @Nullable String provider, List<TextComponent> registrations) {
        if (provider != null) sender.sendRichMessage("<#0288D1><type>: <provider>",
                Placeholder.parsed("provider", provider), Placeholder.parsed("type", type));
        if (!registrations.isEmpty()) sender.sendRichMessage("<green><registered>",
                Placeholder.component("registered", Component.join(separator, registrations)));
    }
}
