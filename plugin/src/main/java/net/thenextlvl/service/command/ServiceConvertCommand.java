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
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.command.argument.*;
import org.bukkit.OfflinePlayer;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

@RequiredArgsConstructor
@SuppressWarnings("UnstableApiUsage")
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

    private int convertPermissions(CommandContext<CommandSourceStack> context) {
        return convert(context, PermissionController.class, PermissionController::getName, new PermissionConverter());
    }

    private final class BankConverter extends PlayerConverter<BankController> {
        @Override
        public void convert(OfflinePlayer player, BankController source, BankController target) {
            source.loadBanks().thenAccept(banks -> banks.forEach(bank ->
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
        public void convert(OfflinePlayer player, ChatController source, ChatController target) {
            source.tryGetProfile(player).thenAccept(sourceProfile -> target.tryGetProfile(player)
                    .thenAccept(targetProfile -> {
                        sourceProfile.getPrefix().ifPresent(targetProfile::setPrefix);
                        sourceProfile.getSuffix().ifPresent(targetProfile::setSuffix);
                        sourceProfile.getDisplayName().ifPresent(targetProfile::setDisplayName);
                    }));
        }
    }

    private final class EconomyConverter extends PlayerConverter<EconomyController> {
        @Override
        public void convert(OfflinePlayer player, EconomyController source, EconomyController target) {
            source.tryGetAccount(player).thenAccept(sourceAccount -> sourceAccount.ifPresent(account ->
                    account.getWorld().ifPresentOrElse(world -> target.tryGetAccount(player, world)
                                    .thenCompose(account1 -> account1.map(CompletableFuture::completedFuture)
                                            .orElseGet(() -> target.createAccount(player, world)))
                                    .thenAccept(account1 -> account1.setBalance(account.getBalance())),
                            () -> target.tryGetAccount(player)
                                    .thenCompose(account1 -> account1.map(CompletableFuture::completedFuture)
                                            .orElseGet(() -> target.createAccount(player)))
                                    .thenAccept(account1 -> account1.setBalance(account.getBalance())))));
        }
    }

    private final class GroupConverter extends PlayerConverter<GroupController> {
        @Override
        public void convert(OfflinePlayer player, GroupController source, GroupController target) {
            source.tryGetGroupHolder(player).thenAccept(holder -> target.tryGetGroupHolder(player)
                    .thenAccept(targetHolder -> {
                        holder.getGroups().forEach(targetHolder::addGroup);
                        holder.getPermissions().forEach(targetHolder::setPermission);
                        targetHolder.setPrimaryGroup(holder.getPrimaryGroup());
                    }));
        }

        @Override
        public void convert(GroupController source, GroupController target) {
            source.loadGroups().thenAccept(groups -> groups.forEach(group -> group.getWorld()
                    .map(world -> target.createGroup(group.getName(), world))
                    .orElseGet(() -> target.createGroup(group.getName()))
                    .thenAccept(targetGroup -> {
                        group.getDisplayName().ifPresent(targetGroup::setDisplayName);
                        group.getPermissions().forEach(targetGroup::setPermission);
                        group.getPrefix().ifPresent(targetGroup::setPrefix);
                        group.getSuffix().ifPresent(targetGroup::setSuffix);
                        group.getWeight().ifPresent(targetGroup::setWeight);
                    })));
            super.convert(source, target);
        }
    }

    private final class PermissionConverter extends PlayerConverter<PermissionController> {
        @Override
        public void convert(OfflinePlayer player, PermissionController source, PermissionController target) {
            source.tryGetPermissionHolder(player).thenAccept(holder -> target.tryGetPermissionHolder(player)
                    .thenAccept(targetHolder -> holder.getPermissions().forEach(targetHolder::setPermission)));
        }
    }

    private <T> int convert(CommandContext<CommandSourceStack> context, Class<T> controller, Function<T, String> name, Converter<T> converter) {
        var sender = context.getSource().getSender();

        if (conversionRunning.get()) {
            sender.sendRichMessage("A conversion is already running.");
            return 0;
        }

        var source = context.getArgument("source", controller);
        var target = context.getArgument("target", controller);

        if (source.equals(target)) {
            sender.sendRichMessage("Source and target service cannot be the same.");
            return 0;
        }

        plugin.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> {
            sender.sendRichMessage("Start converting data from <source> to <target>. This may take a while.",
                    Placeholder.parsed("source", name.apply(source)),
                    Placeholder.parsed("target", name.apply(target)));

            var now = System.currentTimeMillis();
            conversionRunning.set(true);

            converter.convert(source, target);

            conversionRunning.set(false);

            var time = new DecimalFormat("0.000").format((System.currentTimeMillis() - now) / 1000d);
            sender.sendRichMessage("Completed conversion in <time> seconds, please verify the data before using it.",
                    Placeholder.parsed("time", time));

        });
        return Command.SINGLE_SUCCESS;
    }

    @FunctionalInterface
    private interface Converter<T> {
        void convert(T source, T target);
    }

    @RequiredArgsConstructor
    private abstract class PlayerConverter<T> implements Converter<T> {
        public abstract void convert(OfflinePlayer player, T source, T target);

        @Override
        public void convert(T source, T target) {
            Arrays.stream(plugin.getServer().getOfflinePlayers())
                    .forEach(player -> convert(player, source, target));
        }
    }
}
