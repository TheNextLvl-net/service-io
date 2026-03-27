package net.thenextlvl.service.api.economy;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import net.thenextlvl.service.api.capability.Capability;
import org.jetbrains.annotations.Contract;

/**
 * Represents capabilities that an economy provider may or may not support.
 *
 * @since 3.0.0
 */
public enum EconomyCapability implements Capability {
    /**
     * The provider supports multiple currencies beyond the default.
     */
    MULTI_CURRENCY("multi_currency"),
    /**
     * The provider supports per-world economies.
     */
    MULTI_WORLD("multi_world");

    private final Key key;

    EconomyCapability(@KeyPattern.Value final String value) {
        this.key = Key.key("service-io", value);
    }

    @Override
    @Contract(pure = true)
    public Key key() {
        return key;
    }
}
