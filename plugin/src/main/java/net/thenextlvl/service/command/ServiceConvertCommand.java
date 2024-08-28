package net.thenextlvl.service.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.command.argument.*;
import org.bukkit.OfflinePlayer;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class ServiceConvertCommand {
    private final AtomicBoolean conversionRunning = new AtomicBoolean(false);
    private final ServicePlugin plugin;

    LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("convert")
                .requires(stack -> stack.getSender().hasPermission("service.convert"))
                .then(Commands.literal("banks").then(banks()))
                .then(Commands.literal("chat").then(chat()))
                .then(Commands.literal("economy").then(economy()))
                .then(Commands.literal("groups").then(groups()))
                .then(Commands.literal("permissions").then(permissions()));
    }

    private ArgumentBuilder<CommandSourceStack, ?> banks() {
        return Commands.argument("source", new BankArgumentType(plugin, (c, e) -> true))
                .then(Commands.argument("target", new BankArgumentType(plugin, (context, controller) ->
                                !context.getLastChild().getArgument("source", BankController.class).equals(controller)))
                        .executes(this::convertBanks));
    }

    private ArgumentBuilder<CommandSourceStack, ?> chat() {
        return Commands.argument("source", new ChatArgumentType(plugin, (c, e) -> true))
                .then(Commands.argument("target", new ChatArgumentType(plugin, (context, controller) ->
                                !context.getLastChild().getArgument("source", ChatController.class).equals(controller)))
                        .executes(this::convertChat));
    }

    private ArgumentBuilder<CommandSourceStack, ?> economy() {
        return Commands.argument("source", new EconomyArgumentType(plugin, (c, e) -> true))
                .then(Commands.argument("target", new EconomyArgumentType(plugin, (context, controller) ->
                                !context.getLastChild().getArgument("source", EconomyController.class).equals(controller)))
                        .executes(this::convertEconomy));
    }

    private ArgumentBuilder<CommandSourceStack, ?> groups() {
        return Commands.argument("source", new GroupArgumentType(plugin, (c, e) -> true))
                .then(Commands.argument("target", new GroupArgumentType(plugin, (context, controller) ->
                                !context.getLastChild().getArgument("source", GroupController.class).equals(controller)))
                        .executes(this::convertGroups));
    }

    private ArgumentBuilder<CommandSourceStack, ?> permissions() {
        return Commands.argument("source", new PermissionArgumentType(plugin, (c, e) -> true))
                .then(Commands.argument("target", new PermissionArgumentType(plugin, (context, controller) ->
                                !context.getLastChild().getArgument("source", PermissionController.class).equals(controller)))
                        .executes(this::convertPermissions));
    }

    private int convertBanks(CommandContext<CommandSourceStack> context) {
        return Command.SINGLE_SUCCESS;
    }

    private int convertChat(CommandContext<CommandSourceStack> context) {
        return Command.SINGLE_SUCCESS;
    }

    private int convertEconomy(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();

        if (conversionRunning.get()) {
            sender.sendRichMessage("A conversion is already running.");
            return 0;
        }

        var source = context.getArgument("source", EconomyController.class);
        var target = context.getArgument("target", EconomyController.class);

        if (source.equals(target)) {
            sender.sendRichMessage("Source and target economy cannot be the same.");
            return 0;
        }

        plugin.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> {
            sender.sendRichMessage("Start converting economy from <source> to <target>. This may take a while.");

            var now = System.currentTimeMillis();
            conversionRunning.set(true);

            Arrays.stream(plugin.getServer().getOfflinePlayers())
                    .forEach(player -> migratePlayer(player, source, target));

            conversionRunning.set(false);

            var time = new DecimalFormat("0.000").format((System.currentTimeMillis() - now) / 1000d);
            sender.sendRichMessage("Completed conversion in <time> seconds, please verify the data before using it.",
                    Placeholder.parsed("time", time));

        });
        return Command.SINGLE_SUCCESS;
    }

    private int convertGroups(CommandContext<CommandSourceStack> context) {
        return Command.SINGLE_SUCCESS;
    }

    private int convertPermissions(CommandContext<CommandSourceStack> context) {
        return Command.SINGLE_SUCCESS;
    }

    private void migratePlayer(OfflinePlayer player, EconomyController source, EconomyController target) {
        source.tryGetAccount(player).thenAccept(sourceAccount -> sourceAccount.ifPresent(account ->
                account.getWorld().ifPresentOrElse(world -> target.createAccount(player, world)
                                .thenAccept(account1 -> account1.setBalance(account.getBalance())),
                        () -> target.createAccount(player)
                                .thenAccept(account1 -> account1.setBalance(account.getBalance())))));
    }
}
