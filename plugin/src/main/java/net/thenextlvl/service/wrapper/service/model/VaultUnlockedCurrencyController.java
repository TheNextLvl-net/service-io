package net.thenextlvl.service.wrapper.service.model;

import net.milkbowl.vault2.economy.Economy;
import net.thenextlvl.service.api.capability.CapabilityException;
import net.thenextlvl.service.api.economy.EconomyCapability;
import net.thenextlvl.service.api.economy.currency.Currency;
import net.thenextlvl.service.api.economy.currency.CurrencyController;
import net.thenextlvl.service.api.economy.currency.CurrencyData;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class VaultUnlockedCurrencyController implements CurrencyController {
    private final VaultUnlockedCurrency defaultCurrency;
    private final Plugin plugin;

    public VaultUnlockedCurrencyController(final Economy economy, final Plugin plugin) {
        this.defaultCurrency = new VaultUnlockedCurrency(economy, plugin.getName());
        this.plugin = plugin;
    }

    @Override
    public Currency getDefaultCurrency() {
        return defaultCurrency;
    }

    @Override
    public Currency createCurrency(final CurrencyData data) throws IllegalArgumentException {
        throw new CapabilityException(plugin, EconomyCapability.CURRENCY_CREATION);
    }

    @Override
    public boolean deleteCurrency(final String name) {
        return false;
    }
}
