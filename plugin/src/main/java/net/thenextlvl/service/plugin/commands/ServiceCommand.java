package net.thenextlvl.service.plugin.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.service.plugin.ServicePlugin;
import net.thenextlvl.service.plugin.commands.brigadier.BrigadierCommand;

public final class ServiceCommand extends BrigadierCommand {
    private ServiceCommand(final ServicePlugin plugin) {
        super(plugin, "service", "service.command");
    }

    public static LiteralCommandNode<CommandSourceStack> create(final ServicePlugin plugin) {
        final var command = new ServiceCommand(plugin);
        return command.create()
                .then(ServiceInfoCommand.create(plugin))
                .then(ServiceConvertCommand.create(plugin))
                .then(ServiceTestCommand.create(plugin))
                .build();
    }
}
