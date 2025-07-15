package net.thenextlvl.service.controller.chat;

import net.thenextlvl.service.api.chat.ChatController;
import net.thenextlvl.service.api.chat.ChatProfile;
import net.thenextlvl.service.model.chat.GroupManagerChatProfile;
import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.anjocaido.groupmanager.dataholder.WorldDataHolder;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@NullMarked
public class GroupManagerChatController implements ChatController {
    private final GroupManager groupManager = JavaPlugin.getPlugin(GroupManager.class);

    @Override
    public CompletableFuture<ChatProfile> loadProfile(UUID uuid, @Nullable World world) {
        return getProfile(uuid, world)
                .map(CompletableFuture::completedFuture)
                .orElseGet(() -> CompletableFuture.completedFuture(null));
    }

    @Override
    public Optional<ChatProfile> getProfile(UUID uuid, @Nullable World world) {
        return getProfile(getHolder(world), uuid, null);
    }

    private @Nullable OverloadedWorldHolder getHolder(@Nullable World world) {
        if (world == null) return groupManager.getWorldsHolder().getDefaultWorld();
        return groupManager.getWorldsHolder().getWorldData(world.getName());
    }

    private Optional<ChatProfile> getProfile(@Nullable WorldDataHolder holder, UUID uuid, @Nullable String name) {
        if (holder == null) return Optional.empty();
        var user = name != null ? holder.getUser(uuid.toString(), name) : holder.getUser(uuid.toString());
        return user != null ? Optional.of(new GroupManagerChatProfile(user, holder)) : Optional.empty();
    }

    @Override
    public Plugin getPlugin() {
        return groupManager;
    }

    @Override
    public String getName() {
        return "GroupManager Chat";
    }
}
