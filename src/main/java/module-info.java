import org.jspecify.annotations.NullMarked;

@NullMarked
module net.thenextlvl.services {
    exports net.thenextlvl.service.capability;
    exports net.thenextlvl.service.character.event;
    exports net.thenextlvl.service.character;
    exports net.thenextlvl.service.chat;
    exports net.thenextlvl.service.economy.bank;
    exports net.thenextlvl.service.economy.currency;
    exports net.thenextlvl.service.economy;
    exports net.thenextlvl.service.group;
    exports net.thenextlvl.service.hologram.line;
    exports net.thenextlvl.service.hologram;
    exports net.thenextlvl.service.model;
    exports net.thenextlvl.service.permission;
    exports net.thenextlvl.service;

    requires com.google.common;
    requires net.kyori.adventure.key;
    requires net.kyori.adventure;
    requires net.kyori.examination.api;
    requires net.thenextlvl.vault;
    requires org.bukkit;
    requires org.joml;

    requires static org.jetbrains.annotations;
    requires static org.jspecify;
}
