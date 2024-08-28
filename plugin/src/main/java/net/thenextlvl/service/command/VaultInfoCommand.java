package net.thenextlvl.service.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.ServicePlugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
public class VaultInfoCommand {
    private final ServicePlugin plugin;

    LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("info")
                .requires(stack -> stack.getSender().hasPermission("service.info"))
                .executes(this::info);
    }

    @SuppressWarnings("DuplicatedCode")
    private int info(CommandContext<CommandSourceStack> context) {
        var chats = plugin.getServer().getServicesManager().getRegistrations(Chat.class).stream()
                .map(RegisteredServiceProvider::getProvider)
                .map(Chat::getName)
                .collect(Collectors.joining(", "));

        var economies = plugin.getServer().getServicesManager().getRegistrations(Economy.class).stream()
                .map(RegisteredServiceProvider::getProvider)
                .map(Economy::getName)
                .collect(Collectors.joining(", "));

        var permissions = plugin.getServer().getServicesManager().getRegistrations(Permission.class).stream()
                .map(RegisteredServiceProvider::getProvider)
                .map(Permission::getName)
                .collect(Collectors.joining(", "));

        var chat = plugin.getServer().getServicesManager().load(Chat.class);
        var economy = plugin.getServer().getServicesManager().load(Economy.class);
        var permission = plugin.getServer().getServicesManager().load(Permission.class);

        context.getSource().getSender().sendRichMessage("ServiceIO v<version> - Vault Information",
                Placeholder.parsed("version", plugin.getPluginMeta().getVersion()));

        context.getSource().getSender().sendRichMessage("Chat: <provider> [<registered>]",
                Placeholder.parsed("provider", chat != null ? chat.getName() : "None"),
                Placeholder.parsed("registered", chats));
        context.getSource().getSender().sendRichMessage("Economy: <provider> [<registered>]",
                Placeholder.parsed("provider", economy != null ? economy.getName() : "None"),
                Placeholder.parsed("registered", economies));
        context.getSource().getSender().sendRichMessage("Permission: <provider> [<registered>]",
                Placeholder.parsed("provider", permission != null ? permission.getName() : "None"),
                Placeholder.parsed("registered", permissions));
        return Command.SINGLE_SUCCESS;
    }
}
