package net.thenextlvl.service.converter;

import net.thenextlvl.service.api.group.GroupController;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;

import java.util.concurrent.CompletableFuture;

@NullMarked
final class GroupConverter extends PlayerConverter<GroupController> {
    @Override
    public CompletableFuture<Void> convert(final OfflinePlayer player, final GroupController source, final GroupController target) {
        return source.resolveGroupHolder(player).thenAccept(holder -> target.resolveGroupHolder(player)
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
