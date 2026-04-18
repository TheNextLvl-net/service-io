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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class TestSuite<C extends Controller> {
    protected final CommandSourceStack source;
    protected final ServicePlugin plugin;
    protected final C controller;

    private final List<TestStep> steps = new ArrayList<>();
    private final SuiteCounters suiteCounters = new SuiteCounters();
    private final ConcurrentLinkedQueue<QueuedUpdate> updates = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean drainScheduled = new AtomicBoolean(false);

    private volatile @Nullable StepContext currentStep;

    protected TestSuite(final ServicePlugin plugin, final CommandSourceStack source, final C controller) {
        this.controller = controller;
        this.plugin = plugin;
        this.source = source;
    }

    protected abstract void setup();

    protected void test(final String name, final Runnable action) {
        asyncTest(name, () -> {
            action.run();
            return CompletableFuture.completedFuture(null);
        });
    }

    protected void playerTest(final String name, final Consumer<Player> action) {
        playerAsyncTest(name, player -> {
            action.accept(player);
            return CompletableFuture.completedFuture(null);
        });
    }

    protected void asyncTest(final String name, final Supplier<CompletableFuture<Void>> action) {
        steps.add(new TestStep(name, false, ignored -> action.get()));
    }

    protected void playerAsyncTest(final String name, final Function<Player, CompletableFuture<Void>> action) {
        steps.add(new TestStep(name, true, action));
    }

    public CompletableFuture<@Nullable Void> execute() {
        steps.clear();
        suiteCounters.reset();
        updates.clear();
        drainScheduled.set(false);
        currentStep = null;
        setup();

        final var sender = source.getSender();
        final var player = sender instanceof final Player p ? p : null;
        final var totalSteps = steps.size();

        var execution = CompletableFuture.<@Nullable Void>completedFuture(null);
        for (final var step : steps) {
            execution = execution.thenCompose(ignored -> {
                if (step.requiresPlayer() && player == null) {
                    return enqueueUpdate(() -> {
                        suiteCounters.stepsSkipped++;
                        sender.sendMessage(stepLine("⊘", NamedTextColor.YELLOW, step.name(),
                                "skipped (requires player)"));
                    });
                }

                final var context = new StepContext(step.name());
                currentStep = context;
                return enqueueUpdate(() -> sender.sendMessage(stepLine("•", NamedTextColor.AQUA, step.name(), "running")))
                        .thenCompose(unused -> {
                            try {
                                return step.action().apply(player);
                            } catch (final Exception exception) {
                                fail(step.name(), resolveMessage(exception));
                                return CompletableFuture.completedFuture(null);
                            }
                        }).handle((result, throwable) -> {
                            if (throwable != null) fail(step.name(), resolveMessage(throwable));
                            return result;
                        }).thenCompose(unused -> enqueueUpdate(() -> finishStep(context)))
                        .whenComplete((result, throwable) -> currentStep = null);
            });
        }

        return execution.thenCompose(ignored -> enqueueUpdate(() -> sender.sendMessage(summaryLine(totalSteps))));
    }

    private String resolveMessage(final Throwable throwable) {
        var cause = throwable;
        while (cause.getCause() != null) cause = cause.getCause();
        return cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName();
    }

    protected void pass(final String test, final String detail) {
        recordAssertion(AssertionResult.PASS, "✓", NamedTextColor.GREEN, test, detail);
    }

    protected void fail(final String test, final String detail) {
        recordAssertion(AssertionResult.FAIL, "✗", NamedTextColor.RED, test, detail);
    }

    protected void skip(final String test, final String reason) {
        recordAssertion(AssertionResult.SKIP, "⊘", NamedTextColor.YELLOW, test, reason);
    }

    private void recordAssertion(
            final AssertionResult result,
            final String symbol,
            final NamedTextColor color,
            final String test,
            final String detail
    ) {
        final var step = currentStep;
        enqueueUpdate(() -> {
            switch (result) {
                case PASS -> suiteCounters.assertionsPassed++;
                case FAIL -> suiteCounters.assertionsFailed++;
                case SKIP -> suiteCounters.assertionsSkipped++;
            }

            if (step != null) {
                switch (result) {
                    case PASS -> step.passed++;
                    case FAIL -> step.failed++;
                    case SKIP -> step.skipped++;
                }
            }

            source.getSender().sendMessage(assertionLine(symbol, color, test, detail));
        });
    }

    private CompletableFuture<@Nullable Void> enqueueUpdate(final Runnable action) {
        final var future = new CompletableFuture<Void>();
        updates.add(new QueuedUpdate(action, future));
        scheduleDrain();
        return future;
    }

    private void scheduleDrain() {
        if (!drainScheduled.compareAndSet(false, true)) return;
        plugin.getServer().getScheduler().runTask(plugin, this::drainUpdates);
    }

    private void drainUpdates() {
        QueuedUpdate update;
        while ((update = updates.poll()) != null) {
            try {
                update.action().run();
                update.future().complete(null);
            } catch (final Throwable throwable) {
                update.future().completeExceptionally(throwable);
            }
        }
        drainScheduled.set(false);
        if (!updates.isEmpty()) scheduleDrain();
    }

    private void finishStep(final StepContext step) {
        if (step.failed > 0) {
            suiteCounters.stepsFailed++;
            source.getSender().sendMessage(stepLine("✗", NamedTextColor.RED, step.name(), stepSummary(step, "failed")));
        } else {
            suiteCounters.stepsPassed++;
            source.getSender().sendMessage(stepLine("✓", NamedTextColor.GREEN, step.name(), stepSummary(step, "passed")));
        }
    }

    private Component stepLine(
            final String symbol,
            final NamedTextColor color,
            final String name,
            final String detail
    ) {
        return Component.text(" " + symbol + " ", color)
                .append(Component.text(name, NamedTextColor.WHITE))
                .append(Component.text(" → " + detail, NamedTextColor.DARK_GRAY));
    }

    private Component assertionLine(
            final String symbol,
            final NamedTextColor color,
            final String test,
            final String detail
    ) {
        return Component.text("   " + symbol + " ", color)
                .append(Component.text(test, NamedTextColor.GRAY))
                .append(Component.text(" → " + detail, NamedTextColor.DARK_GRAY));
    }

    private String stepSummary(final StepContext step, final String status) {
        final var totalAssertions = step.passed + step.failed + step.skipped;
        if (totalAssertions == 0) return status + ", no assertions recorded";

        final var summary = new StringBuilder(status)
                .append(", ")
                .append(totalAssertions)
                .append(" assertion(s): ")
                .append(step.passed)
                .append(" passed");
        if (step.failed > 0) summary.append(", ").append(step.failed).append(" failed");
        if (step.skipped > 0) summary.append(", ").append(step.skipped).append(" skipped");
        return summary.toString();
    }

    private Component summaryLine(final int totalSteps) {
        final var totalAssertions = suiteCounters.assertionsPassed + suiteCounters.assertionsFailed + suiteCounters.assertionsSkipped;
        final var stepSummary = new StringBuilder()
                .append(suiteCounters.stepsPassed)
                .append(" passed");
        if (suiteCounters.stepsFailed > 0) stepSummary.append(", ").append(suiteCounters.stepsFailed).append(" failed");
        if (suiteCounters.stepsSkipped > 0)
            stepSummary.append(", ").append(suiteCounters.stepsSkipped).append(" skipped");

        final var assertionSummary = new StringBuilder()
                .append(suiteCounters.assertionsPassed)
                .append(" passed");
        if (suiteCounters.assertionsFailed > 0)
            assertionSummary.append(", ").append(suiteCounters.assertionsFailed).append(" failed");
        if (suiteCounters.assertionsSkipped > 0)
            assertionSummary.append(", ").append(suiteCounters.assertionsSkipped).append(" skipped");

        return Component.text(" ■ ", NamedTextColor.DARK_GRAY)
                .append(Component.text("Steps: " + stepSummary + " (" + totalSteps + " total)", NamedTextColor.GRAY))
                .append(Component.text(" | Assertions: " + assertionSummary + " (" + totalAssertions + " total)", NamedTextColor.DARK_GRAY));
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

    private record TestStep(
            String name, boolean requiresPlayer,
            Function<@Nullable Player, CompletableFuture<Void>> action
    ) {
    }

    private static final class StepContext {
        private final String name;
        private int passed;
        private int failed;
        private int skipped;

        private StepContext(final String name) {
            this.name = name;
        }

        private String name() {
            return name;
        }
    }

    private enum AssertionResult {
        PASS, FAIL, SKIP
    }

    private static final class SuiteCounters {
        private int stepsPassed;
        private int stepsFailed;
        private int stepsSkipped;
        private int assertionsPassed;
        private int assertionsFailed;
        private int assertionsSkipped;

        private void reset() {
            stepsPassed = 0;
            stepsFailed = 0;
            stepsSkipped = 0;
            assertionsPassed = 0;
            assertionsFailed = 0;
            assertionsSkipped = 0;
        }
    }

    private record QueuedUpdate(Runnable action, CompletableFuture<@Nullable Void> future) {
    }
}
