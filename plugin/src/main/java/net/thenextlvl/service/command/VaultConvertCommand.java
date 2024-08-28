package net.thenextlvl.service.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.command.argument.VaultEconomyArgumentType;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
class VaultConvertCommand {
    private final AtomicBoolean conversionRunning = new AtomicBoolean(false);
    private final ServicePlugin plugin;

    LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("convert")
                .requires(stack -> stack.getSender().hasPermission("service.convert"))
                .then(Commands.argument("source", new VaultEconomyArgumentType(plugin, (c, e) -> true))
                        .then(Commands.argument("target", new VaultEconomyArgumentType(plugin, (context, economy) ->
                                        !context.getLastChild().getArgument("source", Economy.class).equals(economy)))
                                .executes(this::convert)));
    }

    private int convert(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();

        if (conversionRunning.get()) {
            sender.sendRichMessage("A conversion is already running.");
            return 0;
        }

        var source = context.getArgument("source", Economy.class);
        var target = context.getArgument("target", Economy.class);

        if (source.equals(target)) {
            sender.sendRichMessage("Source and target economy cannot be the same.");
            return 0;
        }

        plugin.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> {
            sender.sendRichMessage("Start converting economy from <source> to <target>. This may take a while.");

            var now = System.currentTimeMillis();
            conversionRunning.set(true);

            Arrays.stream(plugin.getServer().getOfflinePlayers())
                    .filter(source::hasAccount)
                    .filter(player -> !target.hasAccount(player))
                    .filter(target::createPlayerAccount)
                    .forEach(player -> {
                        var difference = source.getBalance(player) - target.getBalance(player);
                        if (difference > 0) {
                            target.depositPlayer(player, difference);
                        } else if (difference < 0) {
                            target.withdrawPlayer(player, -difference);
                        }
                    });

            conversionRunning.set(false);

            var time = new DecimalFormat("0.000").format((System.currentTimeMillis() - now) / 1000d);
            sender.sendRichMessage("Completed conversion in <time> seconds, please verify the data before using it.",
                    Placeholder.parsed("time", time));

        });
        return Command.SINGLE_SUCCESS;
    }
}
