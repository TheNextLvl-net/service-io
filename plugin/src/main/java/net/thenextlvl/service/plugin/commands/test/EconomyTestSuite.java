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

    @Override
    public void run() {
        final var currencies = controller.getCurrencyController();
        final var currency = currencies.getDefaultCurrency();

        testGetDefaultCurrency(currency);
        testGetCurrencies();
        testFormatCurrency(currency);

        if (!(source.getSender() instanceof final Player player)) return;

        controller.createAccount(player).thenAccept(account -> {
            pass("createAccount", "created account for " + player.getName());

            testGetAccount(player);
            testGetAccounts();

            testGetBalance(account.getBalance(currency), currency);
            testCanHold(account.canHold(currency), currency);

            testDeposit(account.deposit(100, currency), currency);
            testGetBalance(account.getBalance(currency), currency);

            testWithdraw(account.withdraw(50, currency), currency);
            testGetBalance(account.getBalance(currency), currency);

            testSetBalance(account.setBalance(1000, currency), currency);
            testGetBalance(account.getBalance(currency), currency);

            testWithdrawInsufficientFunds(account.withdraw(5000, currency), currency);

            controller.deleteAccount(account).thenAccept(deleted -> {
                if (deleted) pass("deleteAccount", "deleted account for " + source.getSender().getName());
                else fail("deleteAccount", "failed to delete account");

                testGetAccountEmpty(player);
            }).exceptionally(throwable -> {
                fail("deleteAccount", throwable.getMessage());
                return null;
            });
        }).exceptionally(throwable -> {
            fail("createAccount", throwable.getMessage());
            return null;
        });
    }

    private void testGetDefaultCurrency(final Currency currency) {
        pass("getDefaultCurrency", currency.getName());
    }

    private void testGetCurrencies() {
        final var currencies = controller.getCurrencyController().getCurrencies();
        pass("getCurrencies", currencies.size() + " currency/currencies");
    }

    private void testFormatCurrency(final Currency currency) {
        final var formatted = currency.format(1234.56, source.getSender());
        source.getSender().sendMessage(Component.text(" ✓ ", NamedTextColor.GREEN)
                .append(Component.text("format(1234.56) → ", NamedTextColor.GRAY))
                .append(formatted));
    }

    private void testGetAccount(final Player player) {
        final var account = controller.getAccount(player);
        if (account.isPresent()) pass("getAccount", "found cached account");
        else fail("getAccount", "account not cached after creation");
    }

    private void testGetAccountEmpty(final Player player) {
        final var account = controller.getAccount(player);
        if (account.isEmpty()) pass("getAccount (after delete)", "account no longer cached");
        else fail("getAccount (after delete)", "account still cached after deletion");
    }

    private void testGetAccounts() {
        final var accounts = controller.getAccounts();
        pass("getAccounts", accounts.size() + " account(s) cached");
    }

    private void testGetBalance(final BigDecimal balance, final Currency currency) {
        pass("getBalance", balance.toPlainString() + " " + currency.getName());
    }

    private void testCanHold(final boolean canHold, final Currency currency) {
        if (canHold) pass("canHold", "account can hold " + currency.getName());
        else fail("canHold", "account cannot hold default currency");
    }

    private void testDeposit(final TransactionResult result, final Currency currency) {
        if (result.successful()) pass("deposit(100)", "balance: " + result.balance() + " " + currency.getName());
        else fail("deposit(100)", "status: " + result.status());
    }

    private void testWithdraw(final TransactionResult result, final Currency currency) {
        if (result.successful()) pass("withdraw(50)", "balance: " + result.balance() + " " + currency.getName());
        else fail("withdraw(50)", "status: " + result.status());
    }

    private void testSetBalance(final TransactionResult result, final Currency currency) {
        if (result.successful()) pass("setBalance(1000)", "balance: " + result.balance() + " " + currency.getName());
        else fail("setBalance(1000)", "status: " + result.status());
    }

    private void testWithdrawInsufficientFunds(final TransactionResult result, final Currency currency) {
        if (result.status() == TransactionResult.Status.INSUFFICIENT_FUNDS)
            pass("withdraw(5000) insufficient", "correctly returned INSUFFICIENT_FUNDS");
        else if (result.successful())
            fail("withdraw(5000) insufficient", "should have failed but succeeded with balance: " + result.balance());
        else pass("withdraw(5000) insufficient", "returned " + result.status());
    }
}
