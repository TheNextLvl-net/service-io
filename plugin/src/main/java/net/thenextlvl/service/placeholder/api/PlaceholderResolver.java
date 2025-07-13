package net.thenextlvl.service.placeholder.api;

import com.google.common.base.Preconditions;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.regex.Matcher;

@NullMarked
public interface PlaceholderResolver {
    @Nullable
    String resolve(OfflinePlayer player, Matcher matcher) throws RuntimeException;
    
    @SafeVarargs
    static PlaceholderResolver throwing(PlaceholderResolver resolver, Class<? extends RuntimeException>... ignored) {
        Preconditions.checkArgument(ignored.length > 0, "At least one exception class must be provided");
        return (player, matcher) -> {
            try {
                return resolver.resolve(player, matcher);
            } catch (RuntimeException exception) {
                for (var clazz : ignored) {
                    if (clazz.isInstance(exception)) return null;
                }
                throw exception;
            }
        };
    }
}
