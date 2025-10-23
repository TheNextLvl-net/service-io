package net.thenextlvl.service.placeholder.api;

import com.google.common.base.Preconditions;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.regex.Matcher;

@NullMarked
public interface PlaceholderResolver<T> {
    @Nullable
    String resolve(T provider, OfflinePlayer player, Matcher matcher) throws RuntimeException;

    @SafeVarargs
    static <T> PlaceholderResolver<T> throwing(PlaceholderResolver<T> resolver, Class<? extends RuntimeException>... ignored) {
        Preconditions.checkArgument(ignored.length > 0, "At least one exception class must be provided");
        return (provider, player, matcher) -> {
            try {
                return resolver.resolve(provider, player, matcher);
            } catch (RuntimeException exception) {
                for (var clazz : ignored) {
                    if (clazz.isInstance(exception)) return null;
                }
                throw exception;
            }
        };
    }
}
