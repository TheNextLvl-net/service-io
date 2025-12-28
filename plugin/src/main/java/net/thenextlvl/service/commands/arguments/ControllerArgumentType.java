package net.thenextlvl.service.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.Controller;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;

@NullMarked
public final class ControllerArgumentType<C extends Controller> implements CustomArgumentType.Converted<C, String> {
    private final ServicePlugin plugin;
    private final BiPredicate<CommandContext<?>, C> filter;
    private final Class<C> type;

    public ControllerArgumentType(ServicePlugin plugin, Class<C> type, BiPredicate<CommandContext<?>, C> filter) {
        this.filter = filter;
        this.plugin = plugin;
        this.type = type;
    }

    @Override
    public C convert(String nativeType) {
        return plugin.getServer().getServicesManager()
                .getRegistrations(type).stream()
                .map(RegisteredServiceProvider::getProvider)
                .filter(controller -> controller.getName().equals(nativeType))
                .findAny().orElseThrow();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
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
