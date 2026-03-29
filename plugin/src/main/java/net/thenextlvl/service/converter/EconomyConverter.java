package net.thenextlvl.service.converter;

import net.thenextlvl.service.api.economy.Account;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.currency.Currency;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@NullMarked
final class EconomyConverter extends Converter<EconomyController> {
    public EconomyConverter(final Plugin plugin, final EconomyController source, final EconomyController target) {
        super(plugin, source, target);
    }

    @Override
    public CompletableFuture<Void> convert() {
        final var sourceCurrencies = source.getCurrencyController().getCurrencies();
        final var targetCurrencies = target.getCurrencyController();

        return source.loadAccounts().thenCompose(accounts -> CompletableFuture.allOf(sourceCurrencies.stream()
                .map(currency -> {
                    final var targetCurrency = targetCurrencies.getCurrency(currency.getName())
                            .orElseGet(() -> targetCurrencies.createCurrency(currency.toData()));
                    return accounts.stream()
                            .map(account -> convert(account, currency, targetCurrency))
                            .toArray(CompletableFuture[]::new);
                })
                .flatMap(Arrays::stream)
                .toArray(CompletableFuture[]::new)));
    }

    private CompletableFuture<Void> convert(final Account account, final Currency sourceCurrency, final Currency targetCurrency) {
        return account.getWorld().map(world -> target.resolveAccount(account.getOwner(), world)
                        .thenCompose(existing -> existing.map(CompletableFuture::completedFuture)
                                .orElseGet(() -> target.createAccount(account.getOwner(), world)))
                        .thenAccept(target1 -> target1.setBalance(account.getBalance(sourceCurrency), targetCurrency)))
                .orElseGet(() -> target.resolveAccount(account.getOwner())
                        .thenCompose(existing -> existing.map(CompletableFuture::completedFuture)
                                .orElseGet(() -> target.createAccount(account.getOwner())))
                        .thenAccept(target1 -> target1.setBalance(account.getBalance(sourceCurrency), targetCurrency)));
    }
}
