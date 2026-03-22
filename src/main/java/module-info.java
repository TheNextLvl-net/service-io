import org.jspecify.annotations.NullMarked;

@NullMarked
module net.thenextlvl.services {
    exports net.thenextlvl.service.api.capability;
    exports net.thenextlvl.service.api.character.event;
    exports net.thenextlvl.service.api.character;
    exports net.thenextlvl.service.api.chat;
    exports net.thenextlvl.service.api.economy.bank;
    exports net.thenextlvl.service.api.economy;
    exports net.thenextlvl.service.api.group;
    exports net.thenextlvl.service.api.hologram;
    exports net.thenextlvl.service.api.model;
    exports net.thenextlvl.service.api.permission;
    exports net.thenextlvl.service.api;

    requires net.kyori.adventure.key;
    requires net.kyori.adventure;
    requires net.kyori.examination.api;
    requires net.thenextlvl.vault;
    requires org.bukkit;
    requires org.joml;

    requires static org.jetbrains.annotations;
    requires static org.jspecify;
}