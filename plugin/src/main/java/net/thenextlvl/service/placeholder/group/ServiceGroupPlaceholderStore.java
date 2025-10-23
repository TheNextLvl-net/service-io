package net.thenextlvl.service.placeholder.group;

import net.thenextlvl.service.ServicePlugin;
import net.thenextlvl.service.api.group.Group;
import net.thenextlvl.service.api.group.GroupController;
import net.thenextlvl.service.api.group.GroupHolder;
import net.thenextlvl.service.placeholder.api.PlaceholderStore;
import org.jspecify.annotations.NullMarked;

import java.util.stream.Collectors;

@NullMarked
public class ServiceGroupPlaceholderStore extends PlaceholderStore<GroupController> {
    public ServiceGroupPlaceholderStore(ServicePlugin plugin) {
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
