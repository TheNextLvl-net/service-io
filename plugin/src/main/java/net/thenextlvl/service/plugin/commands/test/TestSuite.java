package net.thenextlvl.service.plugin.commands.test;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.service.Controller;
import net.thenextlvl.service.plugin.ServicePlugin;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.PrintFormat;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class TestSuite<C extends Controller> {
    protected final CommandSourceStack source;
    protected final ServicePlugin plugin;
    protected final C controller;

    private final AtomicBoolean executing = new AtomicBoolean(false);
    private final ConcurrentLinkedQueue<QueuedMessage> queuedMessages = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean drainScheduled = new AtomicBoolean(false);

    private final List<TestStep> steps = new ArrayList<>();
    private volatile @Nullable AssertionSink assertions;

    protected TestSuite(final ServicePlugin plugin, final CommandSourceStack source, final C controller) {
        this.plugin = plugin;
        this.source = source;
        this.controller = controller;
    }

    protected abstract void setup();

    protected final void test(final String name, final Runnable action) {
        asyncTest(name, () -> {
            action.run();
            return CompletableFuture.completedFuture(null);
        });
    }

    protected final void playerTest(final String name, final Consumer<Player> action) {
        playerAsyncTest(name, player -> {
            action.accept(player);
            return CompletableFuture.completedFuture(null);
        });
    }

    protected final void asyncTest(final String name, final Supplier<CompletableFuture<Void>> action) {
        steps.add(new TestStep(name, false, ignored -> action.get()));
    }

    protected final void playerAsyncTest(final String name, final Function<Player, CompletableFuture<Void>> action) {
        steps.add(new TestStep(name, true, player -> player == null
                ? CompletableFuture.failedFuture(new IllegalStateException("player step invoked without a player"))
                : action.apply(player)));
    }

    public final CompletableFuture<@Nullable Void> execute() {
        if (!executing.compareAndSet(false, true)) {
            return CompletableFuture.failedFuture(new IllegalStateException("suite execution already in progress"));
        }

        steps.clear();
        queuedMessages.clear();
        drainScheduled.set(false);
        assertions = null;

        setup();

        final var sender = source.getExecutor() != null ?  source.getExecutor() : null;
        final var player = sender instanceof final Player current ? current : null;
        final var totalSteps = steps.size();
        final var summary = new SuiteSummary();

        var chain = CompletableFuture.completedFuture((Void) null);
        for (final var step : List.copyOf(steps)) {
            chain = chain.thenCompose(ignored -> executeStep(step, player, summary));
        }

        return chain.thenCompose(ignored -> enqueueMessage(() -> source.getSender().sendMessage(renderSummary(summary, totalSteps))))
                .handle((result, throwable) -> {
                    if (throwable != null) throw unwrap(throwable);
                    return (@Nullable Void) null;
                })
                .whenComplete((ignored, throwable) -> {
                    assertions = null;
                    executing.set(false);
                });
    }

    protected <T> void assertEquals(@Nullable final T expectation, @Nullable final T actual, final String test) {
        if (Objects.equals(expectation, actual)) pass(test, String.valueOf(actual));
        else throw assertionFailure(test, "Expected '%s' but got '%s'", expectation, actual);
    }

    protected void assertTrue(final boolean condition, final String test) {
        assertEquals(true, condition, test);
    }

    protected void assertFalse(final boolean condition, final String test) {
        assertEquals(false, condition, test);
    }

    @SafeVarargs
    protected final CompletableFuture<Void> lifecycleAsync(final CheckedSupplier<CompletableFuture<Void>>... actions) {
        var chain = CompletableFuture.completedFuture((Void) null);
        for (final var action : actions) {
            chain = chain.thenCompose(ignored -> guard(action));
        }
        return chain;
    }

    protected final CompletableFuture<Void> lifecycle(final CheckedRunnable... actions) {
        var chain = CompletableFuture.completedFuture((Void) null);
        for (final var action : actions) {
            chain = chain.thenCompose(ignored -> guard(() -> {
                action.run();
                return CompletableFuture.completedFuture(null);
            }));
        }
        return chain;
    }

    protected final void pass(final String test, @Nullable final String detail) {
        record(AssertionState.PASS, test, detail);
    }

    protected final void fail(final String test, @Nullable final String detail) {
        record(AssertionState.FAIL, test, detail);
    }

    protected final void skip(final String test, @Nullable final String detail) {
        record(AssertionState.SKIP, test, detail);
    }

    protected final void assertState(
            final String test,
            final boolean condition,
            final String successDetail,
            final String failureDetail
    ) {
        if (condition) pass(test, successDetail);
        else fail(test, failureDetail);
    }

    protected final void assertRequiredStateChange(
            final String test,
            final boolean changed,
            final boolean stateVerified,
            final String successDetail,
            final String failureDetail,
            final String verificationFailureDetail
    ) {
        if (!changed) {
            fail(test, failureDetail);
            return;
        }
        if (stateVerified) pass(test, successDetail);
        else fail(test, verificationFailureDetail);
    }

    protected final <T> void assertChangedValue(
            final String test,
            final boolean changed,
            final @Nullable T before,
            final @Nullable T after,
            final @Nullable T expected,
            final String successDetail
    ) {
        if (changed) {
            if (Objects.equals(after, expected)) pass(test, successDetail);
            else fail(test, "returned true but expected " + formatValue(expected) + " and got " + formatValue(after));
            return;
        }
        if (Objects.equals(after, before)) pass(test, "returned false, value unchanged");
        else fail(test, "returned false but value changed from " + formatValue(before) + " to " + formatValue(after));
    }

    private String formatValue(final @Nullable Object value) {
        return value == null ? "(not set)" : String.valueOf(value);
    }

    private RecordedAssertionError assertionFailure(final String test, @PrintFormat final String detail, final @Nullable Object... args) {
        final var formatted = detail.formatted(args);
        fail(test, formatted);
        return new RecordedAssertionError(formatted);
    }

    private CompletableFuture<Void> executeStep(
            final TestStep step,
            final @Nullable Player player,
            final SuiteSummary summary
    ) {
        if (step.requiresPlayer && player == null) {
            summary.stepsSkipped.increment();
            return enqueueMessage(() -> source.getSender().sendMessage(renderStep(
                    "⊘",
                    NamedTextColor.YELLOW,
                    step.name,
                    "skipped, player context required"
            )));
        }

        final var stepAssertions = new AssertionSink(step.name);
        assertions = stepAssertions;

        return enqueueMessage(() -> source.getSender().sendMessage(renderStep("•", NamedTextColor.AQUA, step.name, "running")))
                .thenCompose(ignored -> invokeStep(step, player))
                .exceptionally(throwable -> {
                    stepAssertions.record(AssertionState.FAIL, step.name, rootMessage(throwable));
                    return null;
                })
                .thenCompose(ignored -> {
                    final var outcome = stepAssertions.finish();
                    summary.accept(outcome);
                    return enqueueMessage(() -> source.getSender().sendMessage(renderOutcome(outcome)));
                })
                .whenComplete((ignored, throwable) -> {
                    if (assertions == stepAssertions) assertions = null;
                });
    }

    private CompletableFuture<@Nullable Void> invokeStep(final TestStep step, final @Nullable Player player) {
        try {
            return step.action.apply(player);
        } catch (final Throwable throwable) {
            return CompletableFuture.failedFuture(throwable);
        }
    }

    private CompletableFuture<@Nullable Void> guard(final CheckedSupplier<CompletableFuture<Void>> action) {
        try {
            return action.get().handle((ignored, throwable) -> {
                handleLifecycleThrowable(throwable);
                return null;
            });
        } catch (final Throwable throwable) {
            handleLifecycleThrowable(throwable);
            return CompletableFuture.completedFuture(null);
        }
    }

    private void handleLifecycleThrowable(@Nullable final Throwable throwable) {
        if (throwable == null) return;

        final var cause = unwrapThrowable(throwable);
        if (cause instanceof RecordedAssertionError) return;
        fail(currentStepName(), rootMessage(cause));
    }

    private void record(final AssertionState state, final String test, @Nullable final String detail) {
        final var current = assertions;
        if (current == null || current.isClosed()) {
            enqueueMessage(() -> source.getSender().sendMessage(renderAssertion(state.marker, state.color, test, detail)));
            return;
        }
        current.record(state, test, detail);
    }

    private CompletableFuture<Void> enqueueMessage(final Runnable action) {
        final var future = new CompletableFuture<Void>();
        queuedMessages.add(new QueuedMessage(action, future));
        scheduleDrain();
        return future;
    }

    private void scheduleDrain() {
        if (!drainScheduled.compareAndSet(false, true)) return;
        plugin.getServer().getGlobalRegionScheduler().execute(plugin, this::drainMessages);
    }

    private void drainMessages() {
        QueuedMessage message;
        while ((message = queuedMessages.poll()) != null) {
            try {
                message.action.run();
                message.future.complete(null);
            } catch (final Throwable throwable) {
                message.future.completeExceptionally(throwable);
            }
        }
        drainScheduled.set(false);
        if (!queuedMessages.isEmpty()) scheduleDrain();
    }

    private RuntimeException unwrap(final Throwable throwable) {
        if (throwable instanceof final RuntimeException runtime) return runtime;
        return new RuntimeException(throwable);
    }

    private Throwable unwrapThrowable(final Throwable throwable) {
        if (throwable instanceof CompletionException && throwable.getCause() != null) {
            return unwrapThrowable(throwable.getCause());
        }
        return throwable;
    }

    private String currentStepName() {
        final var current = assertions;
        return current != null ? current.name() : "test";
    }

    private String rootMessage(final Throwable throwable) {
        var cause = throwable;
        while (cause.getCause() != null) cause = cause.getCause();
        final var message = cause.getMessage();
        return message == null || message.isBlank() ? cause.getClass().getSimpleName() : message;
    }

    private Component renderStep(
            final String marker,
            final NamedTextColor color,
            final String name,
            final String detail
    ) {
        return Component.text(" " + marker + " ", color)
                .append(Component.text(name, NamedTextColor.WHITE))
                .append(Component.text(" | " + detail, NamedTextColor.DARK_GRAY));
    }

    private Component renderAssertion(
            final String marker,
            final NamedTextColor color,
            final String test,
            @Nullable final String detail
    ) {
        final var rendered = Component.text("   " + marker + " ", color)
                .append(Component.text(test, NamedTextColor.GRAY));
        if (detail == null) return rendered;
        return rendered.append(Component.text(" | " + detail, NamedTextColor.DARK_GRAY));
    }

    private Component renderOutcome(final StepOutcome outcome) {
        final var totalAssertions = outcome.passed + outcome.failed + outcome.skipped;
        final var detail = totalAssertions == 0
                ? (outcome.failed > 0 ? "failed, no assertions recorded" : "passed, no assertions recorded")
                : (outcome.failed > 0 ? "failed" : "passed") +
                  ", " +
                  totalAssertions +
                  " assertion(s): " +
                  outcome.passed +
                  " passed" +
                  (outcome.failed > 0 ? ", " + outcome.failed + " failed" : "") +
                  (outcome.skipped > 0 ? ", " + outcome.skipped + " skipped" : "");

        return renderStep(outcome.failed > 0 ? "✗" : "✓",
                outcome.failed > 0 ? NamedTextColor.RED : NamedTextColor.GREEN,
                outcome.name,
                detail);
    }

    private Component renderSummary(final SuiteSummary summary, final int totalSteps) {
        final var totalAssertions = summary.assertionsPassed.intValue()
                + summary.assertionsFailed.intValue()
                + summary.assertionsSkipped.intValue();

        final var stepSummary = summary.stepsPassed.intValue() +
                " passed" +
                (summary.stepsFailed.intValue() > 0 ? ", " + summary.stepsFailed.intValue() + " failed" : "") +
                (summary.stepsSkipped.intValue() > 0 ? ", " + summary.stepsSkipped.intValue() + " skipped" : "");

        final var assertionSummary = summary.assertionsPassed.intValue() +
                " passed" +
                (summary.assertionsFailed.intValue() > 0 ? ", " + summary.assertionsFailed.intValue() + " failed" : "") +
                (summary.assertionsSkipped.intValue() > 0 ? ", " + summary.assertionsSkipped.intValue() + " skipped" : "");

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
            String name,
            boolean requiresPlayer,
            Function<@Nullable Player, CompletableFuture<Void>> action
    ) {
    }

    private record QueuedMessage(
            Runnable action,
            CompletableFuture<@Nullable Void> future
    ) {
    }

    private enum AssertionState {
        PASS("✓", NamedTextColor.GREEN),
        FAIL("✗", NamedTextColor.RED),
        SKIP("⊘", NamedTextColor.YELLOW);

        private final String marker;
        private final NamedTextColor color;

        AssertionState(final String marker, final NamedTextColor color) {
            this.marker = marker;
            this.color = color;
        }
    }

    private record StepOutcome(String name, int passed, int failed, int skipped) {
    }

    private final class AssertionSink {
        private final String name;
        private final LongAdder passed = new LongAdder();
        private final LongAdder failed = new LongAdder();
        private final LongAdder skipped = new LongAdder();
        private final AtomicBoolean closed = new AtomicBoolean(false);

        private AssertionSink(final String name) {
            this.name = name;
        }

        private void record(final AssertionState state, final String test, @Nullable final String detail) {
            if (closed.get()) return;

            switch (state) {
                case PASS -> passed.increment();
                case FAIL -> failed.increment();
                case SKIP -> skipped.increment();
            }

            enqueueMessage(() -> source.getSender().sendMessage(renderAssertion(state.marker, state.color, test, detail)));
        }

        private boolean isClosed() {
            return closed.get();
        }

        private String name() {
            return name;
        }

        private StepOutcome finish() {
            closed.set(true);
            return new StepOutcome(name, passed.intValue(), failed.intValue(), skipped.intValue());
        }
    }

    @FunctionalInterface
    protected interface CheckedRunnable {
        void run() throws Exception;
    }

    @FunctionalInterface
    protected interface CheckedSupplier<T> {
        T get() throws Exception;
    }

    private static final class RecordedAssertionError extends AssertionError {
        private RecordedAssertionError(final String message) {
            super(message);
        }
    }

    private static final class SuiteSummary {
        private final LongAdder stepsPassed = new LongAdder();
        private final LongAdder stepsFailed = new LongAdder();
        private final LongAdder stepsSkipped = new LongAdder();
        private final LongAdder assertionsPassed = new LongAdder();
        private final LongAdder assertionsFailed = new LongAdder();
        private final LongAdder assertionsSkipped = new LongAdder();

        private void accept(final StepOutcome outcome) {
            assertionsPassed.add(outcome.passed);
            assertionsFailed.add(outcome.failed);
            assertionsSkipped.add(outcome.skipped);

            if (outcome.failed > 0) stepsFailed.increment();
            else stepsPassed.increment();
        }
    }
}
