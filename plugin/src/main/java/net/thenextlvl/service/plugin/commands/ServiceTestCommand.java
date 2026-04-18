package net.thenextlvl.service.plugin.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.service.Controller;
import net.thenextlvl.service.character.CharacterController;
import net.thenextlvl.service.chat.ChatController;
import net.thenextlvl.service.economy.EconomyController;
import net.thenextlvl.service.group.GroupController;
import net.thenextlvl.service.hologram.HologramController;
import net.thenextlvl.service.permission.PermissionController;
import net.thenextlvl.service.plugin.ServicePlugin;
import net.thenextlvl.service.plugin.commands.arguments.ControllerArgumentType;
import net.thenextlvl.service.plugin.commands.brigadier.BrigadierCommand;
import net.thenextlvl.service.plugin.commands.test.CharacterTestSuite;
import net.thenextlvl.service.plugin.commands.test.ChatTestSuite;
import net.thenextlvl.service.plugin.commands.test.EconomyTestSuite;
import net.thenextlvl.service.plugin.commands.test.GroupTestSuite;
import net.thenextlvl.service.plugin.commands.test.HologramTestSuite;
import net.thenextlvl.service.plugin.commands.test.PermissionTestSuite;
import net.thenextlvl.service.plugin.commands.test.TestSuite;

import java.util.Map;

final class ServiceTestCommand extends BrigadierCommand {
    private static final Map<String, TestSuite.Entry<?>> SUITES = Map.ofEntries(
            Map.entry("character", new TestSuite.Entry<>(CharacterController.class, CharacterTestSuite::new)),
            Map.entry("chat", new TestSuite.Entry<>(ChatController.class, ChatTestSuite::new)),
            Map.entry("economy", new TestSuite.Entry<>(EconomyController.class, EconomyTestSuite::new)),
            Map.entry("group", new TestSuite.Entry<>(GroupController.class, GroupTestSuite::new)),
            Map.entry("hologram", new TestSuite.Entry<>(HologramController.class, HologramTestSuite::new)),
            Map.entry("permission", new TestSuite.Entry<>(PermissionController.class, PermissionTestSuite::new))
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
        final var argument = new ControllerArgumentType<>(plugin, entry.controllerType(), (context, controller) -> true);
        final var provider = Commands.argument("provider", argument);
        return Commands.literal(name).then(provider.executes(context -> {
            final var controller = context.getArgument("provider", entry.controllerType());
            return runSuite(context.getSource(), entry, controller);
        })).executes(context -> {
            final var sender = context.getSource().getSender();
            final var controller = plugin.getServer().getServicesManager().load(entry.controllerType());
            final var displayName = ServiceInfoCommand.translate(plugin, sender, entry.controllerType());

            if (controller != null) return runSuite(context.getSource(), entry, controller);

            plugin.bundle().sendMessage(sender, "service.missing", Placeholder.component("service", displayName));
            return 0;

        });
    }

    private <C extends Controller> int runSuite(final CommandSourceStack source, final TestSuite.Entry<C> entry, final C controller) {
        final var displayName = ServiceInfoCommand.translate(plugin, source.getSender(), entry.controllerType());

        plugin.bundle().sendMessage(source.getSender(), "service.test.started",
                Placeholder.component("service", displayName),
                Placeholder.parsed("provider", controller.getName()));
        entry.factory().create(plugin, source, controller).execute().whenComplete((ignored, throwable) ->
                plugin.bundle().sendMessage(source.getSender(), "service.test.completed",
                        Placeholder.component("service", displayName),
                        Placeholder.parsed("provider", controller.getName())));
        return Command.SINGLE_SUCCESS;
    }
}
