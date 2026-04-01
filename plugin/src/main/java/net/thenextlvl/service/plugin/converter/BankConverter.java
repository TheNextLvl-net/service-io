package net.thenextlvl.service.plugin.converter;

import net.thenextlvl.service.economy.bank.Bank;
import net.thenextlvl.service.economy.bank.BankController;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;

public final class BankConverter extends Converter<BankController> {
    public BankConverter(final Plugin plugin, final BankController source, final BankController target) {
        super(plugin, source, target);
    }

    @Override
    public CompletableFuture<Void> convert() {
        return source.loadBanks().thenCompose(banks -> CompletableFuture.allOf(banks.stream()
                .map(this::convert)
                .toArray(CompletableFuture[]::new)));
    }

    private CompletableFuture<Void> convert(final Bank bank) {
        return bank.getWorld().map(world -> target.createBank(bank.getOwner(), bank.getName(), world))
                .orElseGet(() -> target.createBank(bank.getOwner(), bank.getName()))
                .thenAccept(targetBank -> {
                    final var currency = source.getCurrencyController().getDefaultCurrency();
                    final var targetCurrency = target.getCurrencyController().getDefaultCurrency();
                    targetBank.setBalance(bank.getBalance(currency), targetCurrency);
                    bank.getMembers().forEach(targetBank::addMember);
                });
    }
}
