package net.thenextlvl.service.api.npc;

import net.thenextlvl.service.api.model.Persistable;
import net.thenextlvl.service.api.model.Viewable;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface Character extends Persistable, Viewable {
    Entity getEntity();
}
