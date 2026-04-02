package net.thenextlvl.service.plugin.commands.test;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.service.Controller;
import net.thenextlvl.service.plugin.ServicePlugin;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class TestSuite<C extends Controller> {
    protected final CommandSourceStack source;
    protected final ServicePlugin plugin;
    protected final C controller;

    private final List<TestStep> steps = new ArrayList<>();

    protected TestSuite(final ServicePlugin plugin, final CommandSourceStack source, final C controller) {
        this.controller = controller;
        this.plugin = plugin;
        this.source = source;
    }

    protected abstract void setup();

    protected void test(final String name, final Runnable action) {
        steps.add(new TestStep(name, false, ignored -> action.run()));
    }

    protected void playerTest(final String name, final Consumer<Player> action) {
        steps.add(new TestStep(name, true, action));
    }

    public void execute() {
        steps.clear();
        setup();

        final var sender = source.getSender();
        final var player = sender instanceof final Player p ? p : null;

        var performed = 0;
        var skipped = 0;

        for (final var step : steps) {
            if (step.requiresPlayer()) {
                if (player != null) {
                    try {
                        step.action().accept(player);
                    } catch (final Exception e) {
                        final var cause = e.getCause() != null ? e.getCause() : e;
                        fail(step.name(), cause.getMessage());
                    }
                    performed++;
                } else {
                    skipped++;
                }
            } else {
                try {
                    step.action().accept(null);
                } catch (final Exception e) {
                    final var cause = e.getCause() != null ? e.getCause() : e;
                    fail(step.name(), cause.getMessage());
                }
                performed++;
            }
        }

        final var total = steps.size();
        sender.sendMessage(Component.text(" ■ ", NamedTextColor.DARK_GRAY)
                .append(Component.text(performed + "/" + total + " test(s) performed", NamedTextColor.GRAY))
                .append(skipped > 0
                        ? Component.text(", " + skipped + " skipped (requires player)", NamedTextColor.YELLOW)
                        : Component.empty()));
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

    private record TestStep(String name, boolean requiresPlayer, Consumer<@Nullable Player> action) {
    }
}
