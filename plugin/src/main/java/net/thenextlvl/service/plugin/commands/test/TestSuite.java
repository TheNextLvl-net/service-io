package net.thenextlvl.service.plugin.commands.test;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.service.Controller;
import org.bukkit.command.CommandSender;

public interface TestSuite<C extends Controller> {
    Class<C> controllerType();

    void run(CommandSender sender, C controller);

    default void pass(final CommandSender sender, final String test, final String detail) {
        sender.sendMessage(Component.text(" ✓ ", NamedTextColor.GREEN)
                .append(Component.text(test, NamedTextColor.GRAY))
                .append(Component.text(" → " + detail, NamedTextColor.DARK_GRAY)));
    }

    default void fail(final CommandSender sender, final String test, final String detail) {
        sender.sendMessage(Component.text(" ✗ ", NamedTextColor.RED)
                .append(Component.text(test, NamedTextColor.GRAY))
                .append(Component.text(" → " + detail, NamedTextColor.DARK_GRAY)));
    }

    default void skip(final CommandSender sender, final String test, final String reason) {
        sender.sendMessage(Component.text(" ⊘ ", NamedTextColor.YELLOW)
                .append(Component.text(test, NamedTextColor.GRAY))
                .append(Component.text(" → " + reason, NamedTextColor.DARK_GRAY)));
    }
}
