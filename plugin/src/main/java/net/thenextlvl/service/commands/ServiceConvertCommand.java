package net.thenextlvl.service.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.Controller;
import net.thenextlvl.service.api.character.Character;
import net.thenextlvl.service.api.character.CharacterController;
import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.economy.Account;
import net.thenextlvl.service.api.economy.EconomyController;
import net.thenextlvl.service.api.economy.bank.BankController;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.hologram.HologramController;
import net.thenextlvl.service.api.permission.PermissionController;
import net.thenextlvl.service.commands.arguments.ControllerArgumentType;
import net.thenextlvl.service.commands.brigadier.BrigadierCommand;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@NullMarked
final class ServiceConvertCommand extends BrigadierCommand {
    private final AtomicBoolean conversionRunning = new AtomicBoolean(false);

    private ServiceConvertCommand(final ServicePlugin plugin) {
        super(plugin, "convert", "service.convert");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> create(final ServicePlugin plugin) {
        final var command = new ServiceConvertCommand(plugin);
        final var banks = command.converter("banks", BankController.class, new BankConverter());
        final var characters = command.converter("characters", CharacterController.class, new CharacterConverter());
        final var chat = command.converter("chat", ChatController.class, new ChatConverter());
        final var economy = command.converter("economy", EconomyController.class, new EconomyConverter());
        final var groups = command.converter("groups", GroupController.class, new GroupConverter());
        final var holograms = command.converter("holograms", HologramController.class, new HologramConverter());
        final var permissions = command.converter("permissions", PermissionController.class, new PermissionConverter());
        return command.create().then(banks).then(characters).then(chat).then(economy).then(groups).then(holograms).then(permissions);
    }

    private <C extends Controller> LiteralArgumentBuilder<CommandSourceStack> converter(final String name, final Class<C> type, final Converter<C> converter) {
        return Commands.literal(name).then(converter(type, converter));
    }

    private <C extends Controller> ArgumentBuilder<CommandSourceStack, ?> converter(final Class<C> type, final Converter<C> converter) {
        return Commands.argument("source", new ControllerArgumentType<>(plugin, type, (c, e) -> true))
                .then(Commands.argument("target", new ControllerArgumentType<>(plugin, type, (context, controller) ->
                                !context.getLastChild().getArgument("source", type).equals(controller)))
                        .executes(context -> convert(context, type, converter)));
    }

    private static final class BankConverter extends PlayerConverter<BankController> {
        @Override
        public CompletableFuture<Void> convert(final OfflinePlayer player, final BankController source, final BankController target) {
            return source.loadBanks().thenAccept(banks -> banks.forEach(bank ->
                    bank.getWorld().map(world -> target.createBank(bank.getOwner(), bank.getName(), world))
                            .orElseGet(() -> target.createBank(bank.getOwner(), bank.getName()))
                            .thenAccept(targetBank -> {
                                targetBank.setBalance(bank.getBalance());
                                bank.getMembers().forEach(targetBank::addMember);
                            })));
        }
    }

    private static final class CharacterConverter implements Converter<CharacterController> {

        @Override
        public CompletableFuture<Void> convert(final CharacterController source, final CharacterController target) {
            return CompletableFuture.runAsync(() -> source.getNPCs().forEach(
                    character -> convert(character, source, target)
            ));
        }

        public CompletableFuture<Void> convert(final Character<?> character, final CharacterController source, final CharacterController target) {
            return CompletableFuture.runAsync(() -> target.getNPC(character.getName()).orElseGet(() -> {
                final var npc = target.createNPC(character.getName(), character.getType());
                npc.setDisplayName(character.getDisplayName());
                npc.setDisplayRange(character.getDisplayRange());
                npc.setInvulnerable(character.isInvulnerable());
                npc.setPersistent(character.isPersistent());
                npc.setTablistEntryHidden(character.isTablistEntryHidden());
                npc.setVisibleByDefault(character.isVisibleByDefault());
                if (character.getLocation() != null) npc.spawn(character.getLocation());
                character.addViewers(npc.getViewers());
                return npc;
            }));
        }
    }

    private static final class ChatConverter extends PlayerConverter<ChatController> {
        @Override
        public CompletableFuture<Void> convert(final OfflinePlayer player, final ChatController source, final ChatController target) {
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
        public CompletableFuture<Void> convert(final EconomyController source, final EconomyController target) {
            return source.loadAccounts().thenCompose(accounts -> CompletableFuture.allOf(accounts.stream()
                    .map(account -> convert(account, source, target))
                    .toArray(CompletableFuture[]::new)));
        }

        public CompletableFuture<Void> convert(final Account account, final EconomyController source, final EconomyController target) {
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

    private static final class GroupConverter extends PlayerConverter<GroupController> {
        @Override
        public CompletableFuture<Void> convert(final OfflinePlayer player, final GroupController source, final GroupController target) {
            return source.tryGetGroupHolder(player).thenAccept(holder -> target.tryGetGroupHolder(player)
                    .thenAccept(targetHolder -> {
                        holder.getGroups().forEach(targetHolder::addGroup);
                        holder.getPermissions().forEach(targetHolder::setPermission);
                        targetHolder.setPrimaryGroup(holder.getPrimaryGroup());
                    }));
        }

        @Override
        public CompletableFuture<Void> convert(final GroupController source, final GroupController target) {
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
        public CompletableFuture<Void> convert(final HologramController source, final HologramController target) {
            return CompletableFuture.runAsync(() -> source.getHolograms().forEach(hologram -> {
                final var created = target.createHologram(hologram.getName(), hologram.getLocation(), hologram.getLines());
                created.addViewers(hologram.getViewers());
                created.setDisplayRange(hologram.getDisplayRange());
                created.setPersistent(hologram.isPersistent());
                created.setVisibleByDefault(hologram.isVisibleByDefault());
                created.persist();
            }));
        }
    }

    private static final class PermissionConverter extends PlayerConverter<PermissionController> {
        @Override
        public CompletableFuture<Void> convert(final OfflinePlayer player, final PermissionController source, final PermissionController target) {
            return source.tryGetPermissionHolder(player).thenAccept(holder -> target.tryGetPermissionHolder(player)
                    .thenAccept(targetHolder -> holder.getPermissions().forEach(targetHolder::setPermission)));
        }
    }

    private <C extends Controller> int convert(final CommandContext<CommandSourceStack> context, final Class<C> type, final Converter<C> converter) {
        final var sender = context.getSource().getSender();

        if (conversionRunning.get()) {
            plugin.bundle().sendMessage(sender, "service.convert.running");
            return 0;
        }

        final var source = context.getArgument("source", type);
        final var target = context.getArgument("target", type);

        if (source.equals(target)) {
            plugin.bundle().sendMessage(sender, "service.convert.source-target");
            return 0;
        }

        plugin.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> {
            plugin.bundle().sendMessage(sender, "service.convert.start",
                    Placeholder.parsed("source", source.getName()),
                    Placeholder.parsed("target", target.getName()));

            final var now = System.currentTimeMillis();
            conversionRunning.set(true);

            converter.convert(source, target).thenAccept(unused -> {
                conversionRunning.set(false);

                final var time = new DecimalFormat("0.000").format((System.currentTimeMillis() - now) / 1000d);
                plugin.bundle().sendMessage(sender, "service.convert.done", Placeholder.parsed("time", time));

            }).exceptionally(throwable -> {
                conversionRunning.set(false);

                final var time = new DecimalFormat("0.000").format((System.currentTimeMillis() - now) / 1000d);
                plugin.bundle().sendMessage(sender, "service.convert.failed", Placeholder.parsed("time", time));
                plugin.getComponentLogger().error("Data conversion failed after {} seconds", time, throwable);
                return null;
            });
        });
        return Command.SINGLE_SUCCESS;
    }

    @FunctionalInterface
    private interface Converter<C extends Controller> {
        CompletableFuture<Void> convert(C source, C target);
    }

    private static abstract class PlayerConverter<C extends Controller> implements Converter<C> {
        public abstract CompletableFuture<Void> convert(OfflinePlayer player, C source, C target);

        @Override
        public CompletableFuture<Void> convert(final C source, final C target) {
            return CompletableFuture.allOf(Arrays.stream(source.getPlugin().getServer().getOfflinePlayers())
                    .map(player -> convert(player, source, target))
                    .toArray(CompletableFuture[]::new));
        }
    }
}
