package net.thenextlvl.service.converter;

import net.thenextlvl.service.api.Controller;
import net.thenextlvl.service.api.character.CharacterController;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.hologram.HologramController;
import net.thenextlvl.service.api.permission.PermissionController;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class Converters {

    private Converters() {
    }

    public static Entry<BankController> banks() {
        return new Entry<>(BankController.class, BankConverter::new);
    }

    public static Entry<CharacterController> characters() {
        return new Entry<>(CharacterController.class, CharacterConverter::new);
    }

    public static Entry<ChatController> chat() {
        return new Entry<>(ChatController.class, ChatConverter::new);
    }

    public static Entry<EconomyController> economy() {
        return new Entry<>(EconomyController.class, EconomyConverter::new);
    }

    public static Entry<GroupController> groups() {
        return new Entry<>(GroupController.class, GroupConverter::new);
    }

    public static Entry<HologramController> holograms() {
        return new Entry<>(HologramController.class, HologramConverter::new);
    }

    public static Entry<PermissionController> permissions() {
        return new Entry<>(PermissionController.class, PermissionConverter::new);
    }

    public record Entry<C extends Controller>(Class<C> type, ConverterFactory<C> factory) {
    }

    @FunctionalInterface
    public interface ConverterFactory<C extends Controller> {
        Converter<C> create(Plugin plugin, C source, C target);
    }
}
