package net.thenextlvl.service.converter;

import net.thenextlvl.service.api.Controller;
import net.thenextlvl.service.api.character.CharacterController;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.hologram.HologramController;
import net.thenextlvl.service.api.permission.PermissionController;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class Converters {

    private Converters() {
    }

    public static Entry<BankController> banks() {
        return new Entry<>(BankController.class, new BankConverter());
    }

    public static Entry<CharacterController> characters() {
        return new Entry<>(CharacterController.class, new CharacterConverter());
    }

    public static Entry<ChatController> chat() {
        return new Entry<>(ChatController.class, new ChatConverter());
    }

    public static Entry<EconomyController> economy() {
        return new Entry<>(EconomyController.class, new EconomyConverter());
    }

    public static Entry<GroupController> groups() {
        return new Entry<>(GroupController.class, new GroupConverter());
    }

    public static Entry<HologramController> holograms() {
        return new Entry<>(HologramController.class, new HologramConverter());
    }

    public static Entry<PermissionController> permissions() {
        return new Entry<>(PermissionController.class, new PermissionConverter());
    }

    public record Entry<C extends Controller>(Class<C> type, Converter<C> converter) {
    }
}
