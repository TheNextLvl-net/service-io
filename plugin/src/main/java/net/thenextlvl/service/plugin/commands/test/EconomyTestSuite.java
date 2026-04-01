package net.thenextlvl.service.plugin.commands.test;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.thenextlvl.service.economy.EconomyController;
import net.thenextlvl.service.economy.TransactionResult;
import net.thenextlvl.service.economy.currency.Currency;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public final class EconomyTestSuite implements TestSuite<EconomyController> {
    @Override
    public Class<EconomyController> controllerType() {
        return EconomyController.class;
    }

    @Override
    public void run(final CommandSender sender, final EconomyController controller) {
        if (!(sender instanceof final Player player)) {
            sender.sendMessage(Component.text("This test must be run by a player", NamedTextColor.RED));
            return;
        }

        final var currencies = controller.getCurrencyController();
        final var currency = currencies.getDefaultCurrency();

        testGetDefaultCurrency(sender, currency);
        testGetCurrencies(sender, controller);
        testFormatCurrency(sender, currency, player);

        controller.createAccount(player).thenAccept(account -> {
            pass(sender, "createAccount", "created account for " + player.getName());

            testGetAccount(sender, controller, player);
            testGetAccounts(sender, controller);

            testGetBalance(sender, account.getBalance(currency), currency);
            testCanHold(sender, account.canHold(currency), currency);

            testDeposit(sender, account.deposit(100, currency), currency);
            testGetBalance(sender, account.getBalance(currency), currency);

            testWithdraw(sender, account.withdraw(50, currency), currency);
            testGetBalance(sender, account.getBalance(currency), currency);

            testSetBalance(sender, account.setBalance(1000, currency), currency);
            testGetBalance(sender, account.getBalance(currency), currency);

            testWithdrawInsufficientFunds(sender, account.withdraw(5000, currency), currency);

            controller.deleteAccount(account).thenAccept(deleted -> {
                if (deleted) pass(sender, "deleteAccount", "deleted account for " + player.getName());
                else fail(sender, "deleteAccount", "failed to delete account");

                testGetAccountEmpty(sender, controller, player);
            }).exceptionally(throwable -> {
                fail(sender, "deleteAccount", throwable.getMessage());
                return null;
            });
        }).exceptionally(throwable -> {
            fail(sender, "createAccount", throwable.getMessage());
            return null;
        });
    }

    private void testGetDefaultCurrency(final CommandSender sender, final Currency currency) {
        pass(sender, "getDefaultCurrency", currency.getName());
    }

    private void testGetCurrencies(final CommandSender sender, final EconomyController controller) {
        final var currencies = controller.getCurrencyController().getCurrencies();
        pass(sender, "getCurrencies", currencies.size() + " currency/currencies");
    }

    private void testFormatCurrency(final CommandSender sender, final Currency currency, final Player player) {
        final var formatted = currency.format(1234.56, player);
        sender.sendMessage(Component.text(" ✓ ", NamedTextColor.GREEN)
                .append(Component.text("format(1234.56) → ", NamedTextColor.GRAY))
                .append(formatted));
    }

    private void testGetAccount(final CommandSender sender, final EconomyController controller, final Player player) {
        final var account = controller.getAccount(player);
        if (account.isPresent()) pass(sender, "getAccount", "found cached account");
        else fail(sender, "getAccount", "account not cached after creation");
    }

    private void testGetAccountEmpty(final CommandSender sender, final EconomyController controller, final Player player) {
        final var account = controller.getAccount(player);
        if (account.isEmpty()) pass(sender, "getAccount (after delete)", "account no longer cached");
        else fail(sender, "getAccount (after delete)", "account still cached after deletion");
    }

    private void testGetAccounts(final CommandSender sender, final EconomyController controller) {
        final var accounts = controller.getAccounts();
        pass(sender, "getAccounts", accounts.size() + " account(s) cached");
    }

    private void testGetBalance(final CommandSender sender, final BigDecimal balance, final Currency currency) {
        pass(sender, "getBalance", balance.toPlainString() + " " + currency.getName());
    }

    private void testCanHold(final CommandSender sender, final boolean canHold, final Currency currency) {
        if (canHold) pass(sender, "canHold", "account can hold " + currency.getName());
        else fail(sender, "canHold", "account cannot hold default currency");
    }

    private void testDeposit(final CommandSender sender, final TransactionResult result, final Currency currency) {
        if (result.successful()) pass(sender, "deposit(100)", "balance: " + result.balance() + " " + currency.getName());
        else fail(sender, "deposit(100)", "status: " + result.status());
    }

    private void testWithdraw(final CommandSender sender, final TransactionResult result, final Currency currency) {
        if (result.successful()) pass(sender, "withdraw(50)", "balance: " + result.balance() + " " + currency.getName());
        else fail(sender, "withdraw(50)", "status: " + result.status());
    }

    private void testSetBalance(final CommandSender sender, final TransactionResult result, final Currency currency) {
        if (result.successful()) pass(sender, "setBalance(1000)", "balance: " + result.balance() + " " + currency.getName());
        else fail(sender, "setBalance(1000)", "status: " + result.status());
    }

    private void testWithdrawInsufficientFunds(final CommandSender sender, final TransactionResult result, final Currency currency) {
        if (result.status() == TransactionResult.Status.INSUFFICIENT_FUNDS)
            pass(sender, "withdraw(5000) insufficient", "correctly returned INSUFFICIENT_FUNDS");
        else if (result.successful())
            fail(sender, "withdraw(5000) insufficient", "should have failed but succeeded with balance: " + result.balance());
        else pass(sender, "withdraw(5000) insufficient", "returned " + result.status());
    }
}
