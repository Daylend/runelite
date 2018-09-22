package net.runelite.client.plugins.pvp;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor(
    name = "PVP Plugin",
    tags = {"pvp"},
    enabledByDefault = false
)
@Slf4j
public class PvpPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private PvpPlayerOverlay overlay;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private PvpCountOverlay countOverlay;

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
        overlayManager.add(countOverlay);

    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
        overlayManager.remove(countOverlay);
    }

}
