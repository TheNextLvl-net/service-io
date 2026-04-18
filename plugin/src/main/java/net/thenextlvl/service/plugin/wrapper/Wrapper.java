package net.thenextlvl.service.plugin.wrapper;

public interface Wrapper {
    Type type();
    
    enum Type {
        VAULT, VAULT_UNLOCKED
    }
}
