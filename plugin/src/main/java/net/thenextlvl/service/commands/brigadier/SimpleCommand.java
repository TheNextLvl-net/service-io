package net.thenextlvl.service.commands.brigadier;

import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.thenextlvl.service.ServicePlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class SimpleCommand extends BrigadierCommand implements Command<CommandSourceStack> {
    protected SimpleCommand(final ServicePlugin plugin, final String name, final String permission) {
        super(plugin, name, permission);
    }
}
