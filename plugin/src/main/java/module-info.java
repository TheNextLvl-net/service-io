import org.jspecify.annotations.NullMarked;

@NullMarked
module net.thenextlvl.services.plugin {
    requires com.google.common;
    requires dev.faststats.bukkit;
    requires dev.faststats.core;
    requires net.kyori.adventure.key;
    requires net.kyori.adventure.text.minimessage;
    requires net.kyori.adventure.text.serializer.plain;
    requires net.kyori.adventure;
    requires net.thenextlvl.i18n;
    requires net.thenextlvl.services;
    requires net.thenextlvl.vault;
    requires net.thenextlvl.version.modrinth.paper;
    requires net.thenextlvl.version;
    requires org.bukkit;
    requires org.slf4j;

    requires static org.jetbrains.annotations;
    requires static org.jspecify;
}