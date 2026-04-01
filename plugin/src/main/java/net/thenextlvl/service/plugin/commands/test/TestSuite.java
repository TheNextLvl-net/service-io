package net.thenextlvl.service.plugin.commands.test;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.service.Controller;
import net.thenextlvl.service.plugin.ServicePlugin;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

public abstract class TestSuite<C extends Controller> {
    protected final CommandSourceStack source;
    protected final ServicePlugin plugin;
    protected final C controller;

    protected TestSuite(final ServicePlugin plugin, final CommandSourceStack source, final C controller) {
        this.controller = controller;
        this.plugin = plugin;
        this.source = source;
    }

    public void execute() {
        final var sender = source.getSender();
        final var player = sender instanceof final Player p ? p : null;

        final var tests = Arrays.stream(getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Test.class))
                .sorted(Comparator.comparingInt(method -> method.getAnnotation(Test.class).order()))
                .toList();

        var performed = 0;
        var skipped = 0;

        for (final var test : tests) {
            final var params = test.getParameterTypes();

            if (params.length == 0) {
                invoke(test);
                performed++;
            } else if (params.length == 1 && params[0] == Player.class) {
                if (player != null) {
                    invoke(test, player);
                    performed++;
                } else {
                    skipped++;
                }
            }
        }

        final var total = tests.size();
        sender.sendMessage(Component.text(" ■ ", NamedTextColor.DARK_GRAY)
                .append(Component.text(performed + "/" + total + " test(s) performed", NamedTextColor.GRAY))
                .append(skipped > 0
                        ? Component.text(", " + skipped + " skipped (requires player)", NamedTextColor.YELLOW)
                        : Component.empty()));
    }

    private void invoke(final Method method, final Object... args) {
        try {
            method.setAccessible(true);
            method.invoke(this, args);
        } catch (final Exception e) {
            final var cause = e.getCause() != null ? e.getCause() : e;
            fail(method.getName(), cause.getMessage());
        }
    }

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
