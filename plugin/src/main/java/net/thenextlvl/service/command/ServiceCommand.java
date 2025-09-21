package net.thenextlvl.service.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.command.brigadier.BrigadierCommand;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ServiceCommand extends BrigadierCommand {
    private ServiceCommand(ServicePlugin plugin) {
        super(plugin, "service", "service.command");
    }

    public static LiteralCommandNode<CommandSourceStack> create(ServicePlugin plugin) {
        var command = new ServiceCommand(plugin);
        return command.create()
                .then(ServiceInfoCommand.create(plugin))
                .then(ServiceConvertCommand.create(plugin))
                .build();
    }
}
