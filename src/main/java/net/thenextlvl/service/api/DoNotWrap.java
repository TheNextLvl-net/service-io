package net.thenextlvl.service.api;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.permission.PermissionController;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Annotating a service class with {@code DoNotWrap} indicates that no wrapper will be created for it.
 * <p>
 * This can be applied to any {@link PermissionController}, {@link EconomyController}, {@link ChatController},
 * {@link Economy}, {@link Chat}, or {@link Permission} class.
 *
 * @since 2.5.0
 */
@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DoNotWrap {
}
