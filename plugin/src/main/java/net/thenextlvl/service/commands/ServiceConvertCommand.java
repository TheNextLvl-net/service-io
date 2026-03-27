package net.thenextlvl.service.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.Controller;
import net.thenextlvl.service.commands.arguments.ControllerArgumentType;
import net.thenextlvl.service.commands.brigadier.BrigadierCommand;
import net.thenextlvl.service.converter.Converter;
import net.thenextlvl.service.converter.Converters;
import org.jspecify.annotations.NullMarked;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicBoolean;

@NullMarked
final class ServiceConvertCommand extends BrigadierCommand {
    private final AtomicBoolean conversionRunning = new AtomicBoolean(false);

    private ServiceConvertCommand(final ServicePlugin plugin) {
        super(plugin, "convert", "service.convert");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final ServicePlugin plugin) {
        final var command = new ServiceConvertCommand(plugin);
        return command.create()
                .then(command.converter("banks", Converters.banks()))
                .then(command.converter("characters", Converters.characters()))
                .then(command.converter("chat", Converters.chat()))
                .then(command.converter("economy", Converters.economy()))
                .then(command.converter("groups", Converters.groups()))
                .then(command.converter("holograms", Converters.holograms()))
                .then(command.converter("permissions", Converters.permissions()));
    }

    private <C extends Controller> LiteralArgumentBuilder<CommandSourceStack> converter(final String name, final Converters.Entry<C> entry) {
        return Commands.literal(name).then(converter(entry.type(), entry.converter()));
    }

    private <C extends Controller> ArgumentBuilder<CommandSourceStack, ?> converter(final Class<C> type, final Converter<C> converter) {
        return Commands.argument("source", new ControllerArgumentType<>(plugin, type, (c, e) -> true))
                .then(Commands.argument("target", new ControllerArgumentType<>(plugin, type, (context, controller) ->
                                !context.getLastChild().getArgument("source", type).equals(controller)))
                        .executes(context -> convert(context, type, converter)));
    }

    private <C extends Controller> int convert(final CommandContext<CommandSourceStack> context, final Class<C> type, final Converter<C> converter) {
        final var sender = context.getSource().getSender();

        final var source = context.getArgument("source", type);
        final var target = context.getArgument("target", type);

        if (source.equals(target)) {
            plugin.bundle().sendMessage(sender, "service.convert.source-target");
            return 0;
        }

        if (!conversionRunning.compareAndSet(false, true)) {
            plugin.bundle().sendMessage(sender, "service.convert.running");
            return 0;
        }

        plugin.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> {
            plugin.bundle().sendMessage(sender, "service.convert.start",
                    Placeholder.parsed("source", source.getName()),
                    Placeholder.parsed("target", target.getName()));

            final var now = System.currentTimeMillis();

            converter.convert(source, target).thenAccept(unused -> {
                conversionRunning.set(false);

                final var time = new DecimalFormat("0.000").format((System.currentTimeMillis() - now) / 1000d);
                plugin.bundle().sendMessage(sender, "service.convert.done", Placeholder.parsed("time", time));

            }).exceptionally(throwable -> {
                conversionRunning.set(false);

                final var time = new DecimalFormat("0.000").format((System.currentTimeMillis() - now) / 1000d);
                plugin.bundle().sendMessage(sender, "service.convert.failed", Placeholder.parsed("time", time));
                plugin.getComponentLogger().error("Data conversion failed after {} seconds", time, throwable);
                return null;
            });
        });
        return Command.SINGLE_SUCCESS;
    }
}
