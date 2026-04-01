package net.thenextlvl.service.economy;

import net.thenextlvl.service.capability.Capability;

/**
 * Represents capabilities that an economy provider may or may not support.
 *
 * @since 3.0.0
 */
public enum EconomyCapability implements Capability {
    /**
     * The provider supports multiple currencies beyond the default.
     */
    MULTI_CURRENCY,
    /**
     * The provider supports creating new currencies.
     * <p>
     * If a provider supports currency creation, {@link #MULTI_CURRENCY} is also supported.
     *
     * @since 3.0.0
     */
    CURRENCY_CREATION,
    /**
     * The provider supports per-world economies.
     */
    MULTI_WORLD
}
