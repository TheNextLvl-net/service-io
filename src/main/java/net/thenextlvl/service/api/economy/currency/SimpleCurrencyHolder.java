package net.thenextlvl.service.api.economy.currency;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

@NullMarked
@ApiStatus.Internal
public final class SimpleCurrencyHolder implements CurrencyHolder {
    public static final SimpleCurrencyHolder INSTANCE = new SimpleCurrencyHolder();

    private SimpleCurrencyHolder() {
    }

    @Override
    public Currency getDefaultCurrency() {
        return SimpleCurrency.INSTANCE;
    }
}
