package net.thenextlvl.service.plugin.commands.test;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.service.economy.EconomyController;
import net.thenextlvl.service.economy.TransactionResult;
import net.thenextlvl.service.economy.currency.Currency;
import net.thenextlvl.service.plugin.ServicePlugin;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public final class EconomyTestSuite extends TestSuite<EconomyController> {
    public EconomyTestSuite(final ServicePlugin plugin, final CommandSourceStack source, final EconomyController controller) {
        super(plugin, source, controller);
    }

    @Test(order = 1)
    private void testGetDefaultCurrency() {
        final var currency = controller.getCurrencyController().getDefaultCurrency();
        pass("getDefaultCurrency", currency.getName());
    }

    @Test(order = 2)
    private void testGetCurrencies() {
        final var currencies = controller.getCurrencyController().getCurrencies();
        pass("getCurrencies", currencies.size() + " currency/currencies");
    }

    @Test(order = 3)
    private void testFormatCurrency() {
        final var currency = controller.getCurrencyController().getDefaultCurrency();
        final var formatted = currency.format(1234.56, source.getSender());
        source.getSender().sendMessage(Component.text(" ✓ ", NamedTextColor.GREEN)
                .append(Component.text("format(1234.56) → ", NamedTextColor.GRAY))
                .append(formatted));
    }

    @Test(order = 4)
    private void testGetAccounts() {
        final var accounts = controller.getAccounts();
        pass("getAccounts", accounts.size() + " account(s) cached");
    }

    @Test(order = 5)
    private void testAccountLifecycle(final Player player) {
        final var currency = controller.getCurrencyController().getDefaultCurrency();

        controller.createAccount(player).thenAccept(account -> {
            pass("createAccount", "created account for " + player.getName());

            assertAccountCached(player);
            assertGetAccounts();

            assertBalance(account.getBalance(currency), currency);
            assertCanHold(account.canHold(currency), currency);

            assertTransaction("deposit(100)", account.deposit(100, currency), currency);
            assertBalance(account.getBalance(currency), currency);

            assertTransaction("withdraw(50)", account.withdraw(50, currency), currency);
            assertBalance(account.getBalance(currency), currency);

            assertTransaction("setBalance(1000)", account.setBalance(1000, currency), currency);
            assertBalance(account.getBalance(currency), currency);

            assertInsufficientFunds(account.withdraw(5000, currency), currency);

            controller.deleteAccount(account).thenAccept(deleted -> {
                if (deleted) pass("deleteAccount", "deleted account for " + player.getName());
                else fail("deleteAccount", "failed to delete account");

                assertAccountNotCached(player);
            }).exceptionally(throwable -> {
                fail("deleteAccount", throwable.getMessage());
                return null;
            });
        }).exceptionally(throwable -> {
            fail("createAccount", throwable.getMessage());
            return null;
        });
    }

    private void assertAccountCached(final Player player) {
        final var account = controller.getAccount(player);
        if (account.isPresent()) pass("getAccount", "found cached account");
        else fail("getAccount", "account not cached after creation");
    }

    private void assertAccountNotCached(final Player player) {
        final var account = controller.getAccount(player);
        if (account.isEmpty()) pass("getAccount (after delete)", "account no longer cached");
        else fail("getAccount (after delete)", "account still cached after deletion");
    }

    private void assertGetAccounts() {
        final var accounts = controller.getAccounts();
        pass("getAccounts", accounts.size() + " account(s) cached");
    }

    private void assertBalance(final BigDecimal balance, final Currency currency) {
        pass("getBalance", balance.toPlainString() + " " + currency.getName());
    }

    private void assertCanHold(final boolean canHold, final Currency currency) {
        if (canHold) pass("canHold", "account can hold " + currency.getName());
        else fail("canHold", "account cannot hold default currency");
    }

    private void assertTransaction(final String name, final TransactionResult result, final Currency currency) {
        if (result.successful()) pass(name, "balance: " + result.balance() + " " + currency.getName());
        else fail(name, "status: " + result.status());
    }

    private void assertInsufficientFunds(final TransactionResult result, final Currency currency) {
        if (result.status() == TransactionResult.Status.INSUFFICIENT_FUNDS)
            pass("withdraw(5000) insufficient", "correctly returned INSUFFICIENT_FUNDS");
        else if (result.successful())
            fail("withdraw(5000) insufficient", "should have failed but succeeded with balance: " + result.balance());
        else pass("withdraw(5000) insufficient", "returned " + result.status());
    }
}
