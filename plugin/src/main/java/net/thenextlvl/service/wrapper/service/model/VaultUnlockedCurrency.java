package net.thenextlvl.service.wrapper.service.model;

import net.kyori.adventure.text.Component;
import net.milkbowl.vault2.economy.Economy;
import net.thenextlvl.service.api.economy.currency.Currency;
import net.thenextlvl.service.api.economy.currency.CurrencyData;
import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@NullMarked
public record VaultUnlockedCurrency(Economy economy, String pluginName) implements Currency {
    @Override
    public String getName() {
        return economy.getDefaultCurrency(pluginName);
    }

    @Override
    public Optional<Component> getDisplayNameSingular(final Locale locale) {
        return Optional.of(economy.defaultCurrencyNameSingular(pluginName))
                .filter(name -> !name.isEmpty())
                .map(Component::text);
    }

    @Override
    public Optional<Component> getDisplayNamePlural(final Locale locale) {
        return Optional.of(economy.defaultCurrencyNamePlural(pluginName))
                .filter(name -> !name.isEmpty())
                .map(Component::text);
    }

    @Override
    public CurrencyData toData() {
        return CurrencyData.of(getName(), getSymbol(), getFractionalDigits(),
                getDisplayNameSingular(Locale.US).map(c -> Map.of(Locale.US, c)).orElseGet(Map::of),
                getDisplayNamePlural(Locale.US).map(c -> Map.of(Locale.US, c)).orElseGet(Map::of));
    }

    @Override
    public Component getSymbol() {
        return Component.empty();
    }

    @Override
    public Component format(final Number amount, final Locale locale) {
        return Component.text(economy.format(pluginName, new BigDecimal(amount.toString())));
    }

    @Override
    public int getFractionalDigits() {
        return economy.fractionalDigits(pluginName);
    }
}
