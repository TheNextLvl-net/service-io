package net.thenextlvl.service.plugin.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.service.Controller;
import net.thenextlvl.service.economy.EconomyController;
import net.thenextlvl.service.hologram.HologramController;
import net.thenextlvl.service.plugin.ServicePlugin;
import net.thenextlvl.service.plugin.commands.brigadier.BrigadierCommand;
import net.thenextlvl.service.plugin.commands.test.EconomyTestSuite;
import net.thenextlvl.service.plugin.commands.test.HologramTestSuite;
import net.thenextlvl.service.plugin.commands.test.TestSuite;

import java.util.Map;

final class ServiceTestCommand extends BrigadierCommand {
    private static final Map<String, TestSuite.Entry<?>> SUITES = Map.ofEntries(
            Map.entry("economy", new TestSuite.Entry<>(EconomyController.class, EconomyTestSuite::new)),
            Map.entry("hologram", new TestSuite.Entry<>(HologramController.class, HologramTestSuite::new))
    );

    private ServiceTestCommand(final ServicePlugin plugin) {
        super(plugin, "test", "service.admin");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final ServicePlugin plugin) {
        final var command = new ServiceTestCommand(plugin);
        var builder = command.create();
        for (final var entry : SUITES.entrySet()) {
            builder = builder.then(command.buildSuite(entry.getKey(), entry.getValue()));
        }
        return builder;
    }

    private <C extends Controller> LiteralArgumentBuilder<CommandSourceStack> buildSuite(final String name, final TestSuite.Entry<C> entry) {
        return Commands.literal(name).executes(context -> {
            final var sender = context.getSource().getSender();
            final var controller = plugin.getServer().getServicesManager().load(entry.controllerType());
            final var displayName = ServiceInfoCommand.translate(plugin, sender, entry.controllerType());

            if (controller == null) {
                plugin.bundle().sendMessage(sender, "service.missing", Placeholder.component("service", displayName));
                return 0;
            }

            plugin.bundle().sendMessage(sender, "service.test.started",
                    Placeholder.component("service", displayName),
                    Placeholder.parsed("provider", controller.getName()));
            entry.factory().create(plugin, context.getSource(), controller).run();
            plugin.bundle().sendMessage(sender, "service.test.completed",
                    Placeholder.component("service", displayName),
                    Placeholder.parsed("provider", controller.getName()));
            return Command.SINGLE_SUCCESS;
        });
    }
}
