package net.thenextlvl.service.plugin.wrapper;

public interface Wrapper {
    enum Type {
        SERVICE_IO("ServiceIO"),
        VAULT("Vault"),
        VAULT_UNLOCKED("VaultUnlocked");

        private final String name;

        Type(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
