package net.thenextlvl.services.capability;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.thenextlvl.services.api.ServiceProvider;
import net.thenextlvl.services.api.capability.CapabilityController;
import org.bukkit.plugin.Plugin;

@Getter
@RequiredArgsConstructor
@Accessors(fluent = true)
public class PaperCapabilityController implements CapabilityController {
    private final PaperCapabilityLoader loader = new PaperCapabilityLoader(this);
    private final PaperCapabilityRegistry registry = new PaperCapabilityRegistry(this);

    private final Plugin owner;

    private final ServiceProvider provider;
}
