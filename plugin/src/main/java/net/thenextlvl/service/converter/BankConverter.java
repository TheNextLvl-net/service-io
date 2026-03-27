package net.thenextlvl.service.converter;

import net.thenextlvl.service.api.economy.bank.Bank;
import net.thenextlvl.service.api.economy.bank.BankController;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
final class BankConverter implements Converter<BankController> {
    @Override
    public CompletableFuture<Void> convert(final BankController source, final BankController target) {
        return source.resolveBanks().thenCompose(banks -> CompletableFuture.allOf(banks.stream()
                .map(bank -> convert(bank, source, target))
                .toArray(CompletableFuture[]::new)));
    }

    private CompletableFuture<Void> convert(final Bank bank, final BankController source, final BankController target) {
        return bank.getWorld().map(world -> target.createBank(bank.getOwner(), bank.getName(), world))
                .orElseGet(() -> target.createBank(bank.getOwner(), bank.getName()))
                .thenAccept(targetBank -> {
                    var currency = source.getCurrencyController().getDefaultCurrency();
                    var targetCurrency = target.getCurrencyController().getDefaultCurrency();
                    targetBank.setBalance(bank.getBalance(currency), targetCurrency);
                    bank.getMembers().forEach(targetBank::addMember);
                });
    }
}
