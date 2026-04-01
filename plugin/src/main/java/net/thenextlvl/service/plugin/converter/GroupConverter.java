package net.thenextlvl.service.plugin.converter;

import net.thenextlvl.service.group.GroupController;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.CompletableFuture;

final class GroupConverter extends PlayerConverter<GroupController> {
    public GroupConverter(final Plugin plugin, final GroupController source, final GroupController target) {
        super(plugin, source, target);
    }

    @Override
    public CompletableFuture<Void> convert(final OfflinePlayer player) {
        return source.resolveGroupHolder(player).thenAccept(holder -> target.resolveGroupHolder(player)
                .thenAccept(targetHolder -> {
                    holder.getGroups().forEach(targetHolder::addGroup);
                    holder.getPermissions().forEach(targetHolder::setPermission);
                    targetHolder.setPrimaryGroup(holder.getPrimaryGroup());
                }));
    }

    @Override
    public CompletableFuture<Void> convert() {
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
        return super.convert();
    }
}
