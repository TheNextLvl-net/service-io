package net.thenextlvl.service.api.economy;

import net.thenextlvl.service.api.capability.Capability;

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
     * The provider supports per-world economies.
     */
    MULTI_WORLD
}
