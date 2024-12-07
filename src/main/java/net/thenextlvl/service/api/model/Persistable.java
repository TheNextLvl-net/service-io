package net.thenextlvl.service.api.model;

public interface Persistable {
    String getName();

    boolean isPersistent();

    boolean persist();

    void setPersistent(boolean persistent);
}
