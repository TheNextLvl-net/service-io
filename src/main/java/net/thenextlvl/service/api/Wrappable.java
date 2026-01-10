package net.thenextlvl.service.api;

import org.jetbrains.annotations.Contract;

public interface Wrappable {
    /**
     * Determines if a wrapper for the controller should automatically be created.
     *
     * @return {@code true} if a wrapper should be created, {@code false} otherwise.
     */
    @Contract(pure = true)
    boolean createWrapper();
}
