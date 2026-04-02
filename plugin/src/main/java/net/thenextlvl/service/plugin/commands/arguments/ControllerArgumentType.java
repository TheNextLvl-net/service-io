package net.thenextlvl.service.plugin.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.thenextlvl.service.Controller;
import net.thenextlvl.service.plugin.ServicePlugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;

public final class ControllerArgumentType<C extends Controller> implements CustomArgumentType.Converted<C, String> {
    private final ServicePlugin plugin;
    private final BiPredicate<CommandContext<?>, C> filter;
    private final Class<C> type;

    public ControllerArgumentType(final ServicePlugin plugin, final Class<C> type, final BiPredicate<CommandContext<?>, C> filter) {
        this.filter = filter;
        this.plugin = plugin;
        this.type = type;
    }

    @Override
    public C convert(final String nativeType) {
        return plugin.getServer().getServicesManager()
                .getRegistrations(type).stream()
                .map(RegisteredServiceProvider::getProvider)
                .filter(controller -> controller.getName().equals(nativeType))
                .findAny().orElseThrow(() -> new IllegalArgumentException("Controller not found: " + nativeType));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        plugin.getServer().getServicesManager()
                .getRegistrations(type).stream()
                .map(RegisteredServiceProvider::getProvider)
                .filter(controller -> filter.test(context, controller))
                .map(Controller::getName)
                .map(StringArgumentType::escapeIfRequired)
                .filter(name -> name.contains(builder.getRemaining()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }
}
