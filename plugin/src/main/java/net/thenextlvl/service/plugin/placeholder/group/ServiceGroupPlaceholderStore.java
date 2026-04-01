package net.thenextlvl.service.plugin.placeholder.group;

import net.thenextlvl.service.group.Group;
import net.thenextlvl.service.group.GroupController;
import net.thenextlvl.service.group.GroupHolder;
import net.thenextlvl.service.plugin.placeholder.api.PlaceholderStore;
import org.bukkit.plugin.Plugin;

import java.util.stream.Collectors;

public class ServiceGroupPlaceholderStore extends PlaceholderStore<GroupController> {
    public ServiceGroupPlaceholderStore(final Plugin plugin) {
        super(plugin, GroupController.class);
    }

    @Override
    protected void registerResolvers() {
        // %serviceio_group%
        registerResolver("group", (provider, player, matcher) -> {
            return provider.getGroupHolder(player).map(GroupHolder::getPrimaryGroup).orElse("");
        });

        // %serviceio_groups%
        registerResolver("groups", (provider, player, matcher) -> {
            return provider.getGroupHolder(player).map(GroupHolder::getGroups).map(groups ->
                    groups.stream().map(Group::getName).collect(Collectors.joining(", "))
            ).orElse("");
        });
    }
}
