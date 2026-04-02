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
    protected void setup() {
        test("getCapabilities", this::testGetCapabilities);
        test("getDefaultCurrency", this::testGetDefaultCurrency);
        test("getCurrencies", this::testGetCurrencies);
        test("getCurrency", this::testGetCurrency);
        test("currencySymbol", this::testCurrencySymbol);
        test("currencyFractionalDigits", this::testCurrencyFractionalDigits);
        test("currencyDisplayNames", this::testCurrencyDisplayNames);
        test("currencyToData", this::testCurrencyToData);
        test("formatCurrency", this::testFormatCurrency);
        test("getAccounts", this::testGetAccounts);
        test("loadAccounts", this::testLoadAccounts);
        playerTest("accountLifecycle", this::testAccountLifecycle);
    }

    private void testGetCapabilities() {
        final var capabilities = controller.getCapabilities();
        pass("getCapabilities", capabilities.isEmpty() ? "no capabilities" : capabilities.toString());
    }

    private void testGetDefaultCurrency() {
        final var currency = controller.getCurrencyController().getDefaultCurrency();
        pass("getDefaultCurrency", currency.getName());
    }

    private void testGetCurrencies() {
        final var currencies = controller.getCurrencyController().getCurrencies();
        pass("getCurrencies", currencies.size() + " currency/currencies");
    }

    private void testGetCurrency() {
        final var currency = controller.getCurrencyController().getDefaultCurrency();
        final var lookup = controller.getCurrencyController().getCurrency(currency.getName());
        if (lookup.isPresent()) pass("getCurrency", "found '" + currency.getName() + "' by name");
        else fail("getCurrency", "could not find default currency by name '" + currency.getName() + "'");
    }

    private void testCurrencySymbol() {
        final var currency = controller.getCurrencyController().getDefaultCurrency();
        final var symbol = currency.getSymbol();
        source.getSender().sendMessage(Component.text(" ✓ ", NamedTextColor.GREEN)
                .append(Component.text("getSymbol() → ", NamedTextColor.GRAY))
                .append(symbol));
    }

    private void testCurrencyFractionalDigits() {
        final var currency = controller.getCurrencyController().getDefaultCurrency();
        final var digits = currency.getFractionalDigits();
        pass("getFractionalDigits", String.valueOf(digits));
    }

    private void testCurrencyDisplayNames() {
        final var currency = controller.getCurrencyController().getDefaultCurrency();
        final var audience = source.getSender();

        final var singular = currency.getDisplayNameSingular(audience);
        if (singular.isPresent()) {
            source.getSender().sendMessage(Component.text(" ✓ ", NamedTextColor.GREEN)
                    .append(Component.text("getDisplayNameSingular() → ", NamedTextColor.GRAY))
                    .append(singular.get()));
        } else {
            skip("getDisplayNameSingular", "not provided");
        }

        final var plural = currency.getDisplayNamePlural(audience);
        if (plural.isPresent()) {
            source.getSender().sendMessage(Component.text(" ✓ ", NamedTextColor.GREEN)
                    .append(Component.text("getDisplayNamePlural() → ", NamedTextColor.GRAY))
                    .append(plural.get()));
        } else {
            skip("getDisplayNamePlural", "not provided");
        }
    }

    private void testCurrencyToData() {
        final var currency = controller.getCurrencyController().getDefaultCurrency();
        final var data = currency.toData();
        if (data.name().equals(currency.getName())) pass("toData", "name matches: " + data.name());
        else fail("toData", "name mismatch: " + data.name() + " != " + currency.getName());
    }

    private void testFormatCurrency() {
        final var currency = controller.getCurrencyController().getDefaultCurrency();
        final var formatted = currency.format(1234.56, source.getSender());
        source.getSender().sendMessage(Component.text(" ✓ ", NamedTextColor.GREEN)
                .append(Component.text("format(1234.56) → ", NamedTextColor.GRAY))
                .append(formatted));
    }

    private void testGetAccounts() {
        final var accounts = controller.getAccounts();
        pass("getAccounts", accounts.size() + " account(s) cached");
    }

    private void testLoadAccounts() {
        controller.loadAccounts().thenAccept(accounts -> {
            pass("loadAccounts", accounts.size() + " account(s) loaded");
        }).exceptionally(throwable -> {
            fail("loadAccounts", throwable.getMessage());
            return null;
        }).join();
    }

    private void testAccountLifecycle(final Player player) {
        final var currency = controller.getCurrencyController().getDefaultCurrency();

        controller.createAccount(player).thenAccept(account -> {
            pass("createAccount", "created account for " + player.getName());

            assertAccountCached(player);
            assertGetAccounts();
            assertAccountOwner(account, player);
            assertAccountWorld(account);

            assertBalance(account.getBalance(currency), currency);
            assertCanHold(account.canHold(currency), currency);

            assertDeposit(account, 100, currency);
            assertWithdraw(account, 50, currency);
            assertSetBalance(account, 1000, currency);
            assertInsufficientFunds(account.withdraw(5000, currency), currency);

            assertLoadAccount(player);
            assertResolveAccount(player);

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

    private void assertAccountOwner(final net.thenextlvl.service.economy.Account account, final Player player) {
        if (account.getOwner().equals(player.getUniqueId())) pass("getOwner", "matches player UUID");
        else fail("getOwner", "expected " + player.getUniqueId() + " but got " + account.getOwner());
    }

    private void assertAccountWorld(final net.thenextlvl.service.economy.Account account) {
        final var world = account.getWorld();
        if (world.isPresent()) pass("getWorld", world.get().getName());
        else pass("getWorld", "global account (no world)");
    }

    private void assertBalance(final BigDecimal balance, final Currency currency) {
        pass("getBalance", balance.toPlainString() + " " + currency.getName());
    }

    private void assertCanHold(final boolean canHold, final Currency currency) {
        if (canHold) pass("canHold", "account can hold " + currency.getName());
        else fail("canHold", "account cannot hold default currency");
    }

    private void assertDeposit(final net.thenextlvl.service.economy.Account account, final Number amount, final Currency currency) {
        final var balanceBefore = account.getBalance(currency);
        final var result = account.deposit(amount, currency);
        final var balanceAfter = account.getBalance(currency);

        assertTransactionResult("deposit(" + amount + ")", result, currency);

        final var expected = balanceBefore.add(new BigDecimal(amount.toString()));
        if (balanceAfter.compareTo(expected) == 0) {
            pass("deposit(" + amount + ") balance", balanceBefore.toPlainString() + " → " + balanceAfter.toPlainString());
        } else {
            fail("deposit(" + amount + ") balance", "expected " + expected.toPlainString() + " but got " + balanceAfter.toPlainString());
        }
    }

    private void assertWithdraw(final net.thenextlvl.service.economy.Account account, final Number amount, final Currency currency) {
        final var balanceBefore = account.getBalance(currency);
        final var result = account.withdraw(amount, currency);
        final var balanceAfter = account.getBalance(currency);

        assertTransactionResult("withdraw(" + amount + ")", result, currency);

        final var expected = balanceBefore.subtract(new BigDecimal(amount.toString()));
        if (balanceAfter.compareTo(expected) == 0) {
            pass("withdraw(" + amount + ") balance", balanceBefore.toPlainString() + " → " + balanceAfter.toPlainString());
        } else {
            fail("withdraw(" + amount + ") balance", "expected " + expected.toPlainString() + " but got " + balanceAfter.toPlainString());
        }
    }

    private void assertSetBalance(final net.thenextlvl.service.economy.Account account, final Number balance, final Currency currency) {
        final var result = account.setBalance(balance, currency);
        final var balanceAfter = account.getBalance(currency);

        assertTransactionResult("setBalance(" + balance + ")", result, currency);

        final var expected = new BigDecimal(balance.toString());
        if (balanceAfter.compareTo(expected) == 0) {
            pass("setBalance(" + balance + ") balance", balanceAfter.toPlainString());
        } else {
            fail("setBalance(" + balance + ") balance", "expected " + expected.toPlainString() + " but got " + balanceAfter.toPlainString());
        }
    }

    private void assertTransactionResult(final String name, final TransactionResult result, final Currency currency) {
        if (!result.successful()) {
            fail(name, "status: " + result.status());
            return;
        }
        if (!result.currency().getName().equals(currency.getName())) {
            fail(name + " currency", "expected " + currency.getName() + " but got " + result.currency().getName());
            return;
        }
        pass(name, "balance: " + result.balance() + " " + currency.getName());
    }

    private void assertInsufficientFunds(final TransactionResult result, final Currency currency) {
        if (result.status() == TransactionResult.Status.INSUFFICIENT_FUNDS)
            pass("withdraw(5000) insufficient", "correctly returned INSUFFICIENT_FUNDS");
        else if (result.successful())
            fail("withdraw(5000) insufficient", "should have failed but succeeded with balance: " + result.balance());
        else pass("withdraw(5000) insufficient", "returned " + result.status());
    }

    private void assertLoadAccount(final Player player) {
        controller.loadAccount(player).thenAccept(account -> {
            if (account.isPresent()) pass("loadAccount", "loaded account for " + player.getName());
            else fail("loadAccount", "account not found in backing store");
        }).exceptionally(throwable -> {
            fail("loadAccount", throwable.getMessage());
            return null;
        }).join();
    }

    private void assertResolveAccount(final Player player) {
        controller.resolveAccount(player).thenAccept(account -> {
            if (account.isPresent()) pass("resolveAccount", "resolved account for " + player.getName());
            else fail("resolveAccount", "account not found");
        }).exceptionally(throwable -> {
            fail("resolveAccount", throwable.getMessage());
            return null;
        }).join();
    }
}
