package net.thenextlvl.service.api.model;

import net.kyori.adventure.key.Keyed;

public interface Persistable extends Keyed {
    boolean isPersistent();

    boolean persist();

    void setPersistent(boolean persistent);
}
