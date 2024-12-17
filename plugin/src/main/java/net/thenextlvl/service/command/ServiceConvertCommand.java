package net.thenextlvl.service.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.economy.Account;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.hologram.HologramController;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.command.argument.BankArgumentType;
import net.thenextlvl.service.command.argument.ChatArgumentType;
import net.thenextlvl.service.command.argument.EconomyArgumentType;
import net.thenextlvl.service.command.argument.GroupArgumentType;
import net.thenextlvl.service.command.argument.HologramArgumentType;
import net.thenextlvl.service.command.argument.PermissionArgumentType;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

@NullMarked
@RequiredArgsConstructor
class ServiceConvertCommand {
    private final AtomicBoolean conversionRunning = new AtomicBoolean(false);
    private final ServicePlugin plugin;

    LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("convert")
                .requires(stack -> stack.getSender().hasPermission("service.convert"))
                .then(Commands.literal("banks").then(banks()))
                .then(Commands.literal("chat").then(chat()))
                .then(Commands.literal("economy").then(economy()))
                .then(Commands.literal("groups").then(groups()))
                .then(Commands.literal("holograms").then(holograms()))
                .then(Commands.literal("permissions").then(permissions()));
    }

    private ArgumentBuilder<CommandSourceStack, ?> banks() {
        return Commands.argument("source", new BankArgumentType(plugin, (c, e) -> true))
                .then(Commands.argument("target", new BankArgumentType(plugin, (context, controller) ->
                                !context.getLastChild().getArgument("source", BankController.class).equals(controller)))
                        .executes(this::convertBanks));
    }

    private ArgumentBuilder<CommandSourceStack, ?> chat() {
        return Commands.argument("source", new ChatArgumentType(plugin, (c, e) -> true))
                .then(Commands.argument("target", new ChatArgumentType(plugin, (context, controller) ->
                                !context.getLastChild().getArgument("source", ChatController.class).equals(controller)))
                        .executes(this::convertChat));
    }

    private ArgumentBuilder<CommandSourceStack, ?> economy() {
        return Commands.argument("source", new EconomyArgumentType(plugin, (c, e) -> true))
                .then(Commands.argument("target", new EconomyArgumentType(plugin, (context, controller) ->
                                !context.getLastChild().getArgument("source", EconomyController.class).equals(controller)))
                        .executes(this::convertEconomy));
    }

    private ArgumentBuilder<CommandSourceStack, ?> groups() {
        return Commands.argument("source", new GroupArgumentType(plugin, (c, e) -> true))
                .then(Commands.argument("target", new GroupArgumentType(plugin, (context, controller) ->
                                !context.getLastChild().getArgument("source", GroupController.class).equals(controller)))
                        .executes(this::convertGroups));
    }

    private ArgumentBuilder<CommandSourceStack, ?> holograms() {
        return Commands.argument("source", new HologramArgumentType(plugin, (c, e) -> true))
                .then(Commands.argument("target", new HologramArgumentType(plugin, (context, controller) ->
                                !context.getLastChild().getArgument("source", HologramController.class).equals(controller)))
                        .executes(this::convertHolograms));
    }

    private ArgumentBuilder<CommandSourceStack, ?> permissions() {
        return Commands.argument("source", new PermissionArgumentType(plugin, (c, e) -> true))
                .then(Commands.argument("target", new PermissionArgumentType(plugin, (context, controller) ->
                                !context.getLastChild().getArgument("source", PermissionController.class).equals(controller)))
                        .executes(this::convertPermissions));
    }

    private int convertBanks(CommandContext<CommandSourceStack> context) {
        return convert(context, BankController.class, BankController::getName, new BankConverter());
    }

    private int convertChat(CommandContext<CommandSourceStack> context) {
        return convert(context, ChatController.class, ChatController::getName, new ChatConverter());
    }

    private int convertEconomy(CommandContext<CommandSourceStack> context) {
        return convert(context, EconomyController.class, EconomyController::getName, new EconomyConverter());
    }

    private int convertGroups(CommandContext<CommandSourceStack> context) {
        return convert(context, GroupController.class, GroupController::getName, new GroupConverter());
    }

    private int convertHolograms(CommandContext<CommandSourceStack> context) {
        return convert(context, HologramController.class, HologramController::getName, new HologramConverter());
    }

    private int convertPermissions(CommandContext<CommandSourceStack> context) {
        return convert(context, PermissionController.class, PermissionController::getName, new PermissionConverter());
    }

    private final class BankConverter extends PlayerConverter<BankController> {
        @Override
        public CompletableFuture<Void> convert(OfflinePlayer player, BankController source, BankController target) {
            return source.loadBanks().thenAccept(banks -> banks.forEach(bank ->
                    bank.getWorld().map(world -> target.createBank(bank.getOwner(), bank.getName(), world))
                            .orElseGet(() -> target.createBank(bank.getOwner(), bank.getName()))
                            .thenAccept(targetBank -> {
                                targetBank.setBalance(bank.getBalance());
                                bank.getMembers().forEach(targetBank::addMember);
                            })));
        }
    }

    private final class ChatConverter extends PlayerConverter<ChatController> {
        @Override
        public CompletableFuture<Void> convert(OfflinePlayer player, ChatController source, ChatController target) {
            return source.tryGetProfile(player).thenAccept(profile -> target.tryGetProfile(player)
                    .thenAccept(targetProfile -> {
                        profile.getPrefixes().forEach((priority, prefix) -> targetProfile.setPrefix(prefix, priority));
                        profile.getSuffixes().forEach((priority, suffix) -> targetProfile.setSuffix(suffix, priority));
                        profile.getDisplayName().ifPresent(targetProfile::setDisplayName);
                    }));
        }
    }

    private static final class EconomyConverter implements Converter<EconomyController> {

        @Override
        public CompletableFuture<Void> convert(EconomyController source, EconomyController target) {
            return source.loadAccounts().thenCompose(accounts -> CompletableFuture.allOf(accounts.stream()
                    .map(account -> convert(account, source, target))
                    .toArray(CompletableFuture[]::new)));
        }

        public CompletableFuture<Void> convert(Account account, EconomyController source, EconomyController target) {
            return account.getWorld().map(world -> target.tryGetAccount(account.getOwner(), world)
                            .thenCompose(account1 -> account1.map(CompletableFuture::completedFuture)
                                    .orElseGet(() -> target.createAccount(account.getOwner(), world)))
                            .thenAccept(account1 -> account1.setBalance(account.getBalance())))
                    .orElseGet(() -> target.tryGetAccount(account.getOwner())
                            .thenCompose(account1 -> account1.map(CompletableFuture::completedFuture)
                                    .orElseGet(() -> target.createAccount(account.getOwner())))
                            .thenAccept(account1 -> account1.setBalance(account.getBalance())));
        }
    }

    private final class GroupConverter extends PlayerConverter<GroupController> {
        @Override
        public CompletableFuture<Void> convert(OfflinePlayer player, GroupController source, GroupController target) {
            return source.tryGetGroupHolder(player).thenAccept(holder -> target.tryGetGroupHolder(player)
                    .thenAccept(targetHolder -> {
                        holder.getGroups().forEach(targetHolder::addGroup);
                        holder.getPermissions().forEach(targetHolder::setPermission);
                        targetHolder.setPrimaryGroup(holder.getPrimaryGroup());
                    }));
        }

        @Override
        public CompletableFuture<Void> convert(GroupController source, GroupController target) {
            source.loadGroups().thenAccept(groups -> groups.forEach(group -> group.getWorld()
                    .map(world -> target.createGroup(group.getName(), world))
                    .orElseGet(() -> target.createGroup(group.getName()))
                    .thenAccept(targetGroup -> {
                        group.getDisplayName().ifPresent(targetGroup::setDisplayName);
                        group.getPermissions().forEach(targetGroup::setPermission);
                        group.getPrefixes().forEach((priority, prefix) -> targetGroup.setPrefix(prefix, priority));
                        group.getSuffixes().forEach((priority, suffix) -> targetGroup.setSuffix(suffix, priority));
                        group.getWeight().ifPresent(targetGroup::setWeight);
                    })));
            return super.convert(source, target);
        }
    }

    private static final class HologramConverter implements Converter<HologramController> {
        @Override
        public CompletableFuture<Void> convert(HologramController source, HologramController target) {
            return CompletableFuture.runAsync(() -> source.getHolograms().forEach(hologram -> {
                var created = target.createHologram(hologram.getName(), hologram.getLocation(), hologram.getLines());
                created.addViewers(hologram.getViewers());
                created.setDisplayRange(hologram.getDisplayRange());
                created.setPersistent(hologram.isPersistent());
                created.setVisibleByDefault(hologram.isVisibleByDefault());
                created.persist();
            }));
        }
    }

    private final class PermissionConverter extends PlayerConverter<PermissionController> {
        @Override
        public CompletableFuture<Void> convert(OfflinePlayer player, PermissionController source, PermissionController target) {
            return source.tryGetPermissionHolder(player).thenAccept(holder -> target.tryGetPermissionHolder(player)
                    .thenAccept(targetHolder -> holder.getPermissions().forEach(targetHolder::setPermission)));
        }
    }

    private <T> int convert(CommandContext<CommandSourceStack> context, Class<T> controller, Function<T, String> name, Converter<T> converter) {
        var sender = context.getSource().getSender();

        if (conversionRunning.get()) {
            plugin.bundle().sendMessage(sender, "service.convert.running");
            return 0;
        }

        var source = context.getArgument("source", controller);
        var target = context.getArgument("target", controller);

        if (source.equals(target)) {
            plugin.bundle().sendMessage(sender, "service.convert.source-target");
            return 0;
        }

        plugin.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> {
            plugin.bundle().sendMessage(sender, "service.convert.start",
                    Placeholder.parsed("source", name.apply(source)),
                    Placeholder.parsed("target", name.apply(target)));

            var now = System.currentTimeMillis();
            conversionRunning.set(true);

            converter.convert(source, target).thenAccept(unused -> {
                conversionRunning.set(false);

                var time = new DecimalFormat("0.000").format((System.currentTimeMillis() - now) / 1000d);
                plugin.bundle().sendMessage(sender, "service.convert.done", Placeholder.parsed("time", time));

            }).exceptionally(throwable -> {
                conversionRunning.set(false);

                var time = new DecimalFormat("0.000").format((System.currentTimeMillis() - now) / 1000d);
                plugin.bundle().sendMessage(sender, "service.convert.failed", Placeholder.parsed("time", time));
                plugin.getComponentLogger().error("Data conversion failed after {} seconds", time, throwable);
                return null;
            });
        });
        return Command.SINGLE_SUCCESS;
    }

    @FunctionalInterface
    private interface Converter<T> {
        CompletableFuture<Void> convert(T source, T target);
    }

    @RequiredArgsConstructor
    private abstract class PlayerConverter<T> implements Converter<T> {
        public abstract CompletableFuture<Void> convert(OfflinePlayer player, T source, T target);

        @Override
        public CompletableFuture<Void> convert(T source, T target) {
            return CompletableFuture.allOf(Arrays.stream(plugin.getServer().getOfflinePlayers())
                    .map(player -> convert(player, source, target))
                    .toArray(CompletableFuture[]::new));
        }
    }
}
