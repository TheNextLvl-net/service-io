package net.thenextlvl.service.plugin.commands.test;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.service.Controller;
import net.thenextlvl.service.plugin.ServicePlugin;

public abstract class TestSuite<C extends Controller> {
    protected final CommandSourceStack source;
    protected final ServicePlugin plugin;
    protected final C controller;

    protected TestSuite(final ServicePlugin plugin, final CommandSourceStack source, final C controller) {
        this.controller = controller;
        this.plugin = plugin;
        this.source = source;
    }

    public abstract void run();

    protected void pass(final String test, final String detail) {
        source.getSender().sendMessage(Component.text(" ✓ ", NamedTextColor.GREEN)
                .append(Component.text(test, NamedTextColor.GRAY))
                .append(Component.text(" → " + detail, NamedTextColor.DARK_GRAY)));
    }

    protected void fail(final String test, final String detail) {
        source.getSender().sendMessage(Component.text(" ✗ ", NamedTextColor.RED)
                .append(Component.text(test, NamedTextColor.GRAY))
                .append(Component.text(" → " + detail, NamedTextColor.DARK_GRAY)));
    }

    protected void skip(final String test, final String reason) {
        source.getSender().sendMessage(Component.text(" ⊘ ", NamedTextColor.YELLOW)
                .append(Component.text(test, NamedTextColor.GRAY))
                .append(Component.text(" → " + reason, NamedTextColor.DARK_GRAY)));
    }

    public record Entry<C extends Controller>(
            Class<C> controllerType,
            Factory<C> factory
    ) {
    }

    @FunctionalInterface
    public interface Factory<C extends Controller> {
        TestSuite<C> create(ServicePlugin plugin, CommandSourceStack source, C controller);
    }
}
