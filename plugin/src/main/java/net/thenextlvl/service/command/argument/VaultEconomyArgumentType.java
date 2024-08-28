package net.thenextlvl.service.command.argument;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import core.paper.command.WrappedArgumentType;
import net.milkbowl.vault.economy.Economy;
import net.thenextlvl.service.ServicePlugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.function.BiPredicate;

public class VaultEconomyArgumentType extends WrappedArgumentType<String, Economy> {
    public VaultEconomyArgumentType(ServicePlugin plugin, BiPredicate<CommandContext<?>, Economy> filter) {
        super(StringArgumentType.string(), (reader, type) -> plugin.getServer().getServicesManager()
                .getRegistrations(Economy.class).stream()
                .map(RegisteredServiceProvider::getProvider)
                .filter(economy -> economy.getName().equals(type))
                .findAny().orElseThrow(), (context, builder) -> {
            plugin.getServer().getServicesManager()
                    .getRegistrations(Economy.class).stream()
                    .map(RegisteredServiceProvider::getProvider)
                    .filter(economy -> filter.test(context, economy))
                    .map(Economy::getName)
                    .map(StringArgumentType::escapeIfRequired)
                    .filter(name -> name.contains(context.getInput()))
                    .forEach(builder::suggest);
            return builder.buildFuture();
        });
    }
}
