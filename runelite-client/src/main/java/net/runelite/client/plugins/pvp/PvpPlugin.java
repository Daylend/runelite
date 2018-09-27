package net.runelite.client.plugins.pvp;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@PluginDescriptor(
    name = "PVP Plugin",
    tags = {"pvp"},
    enabledByDefault = false
)
@Slf4j
public class PvpPlugin extends Plugin
{
    @Getter
    private final Set<PlayerDeath> playerDeaths = new HashSet<>();

    @Getter
    private final int secondsToLoot = 60;

    @Inject
    private Client client;

    @Inject
    private PvpPlayerOverlay overlay;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private PvpCountOverlay countOverlay;

    @Inject
    private PvpConfig pvpConfig;

    @Provides
    PvpConfig pvpConfig(ConfigManager configManager) { return configManager.getConfig(PvpConfig.class); }

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

    @Subscribe
    public void onPlayerDespawned(PlayerDespawned playerDespawned)
    {
        final Player player = playerDespawned.getPlayer();
        // Only care about dead Players
        if (player.getHealthRatio() != 0)
        {
            return;
        }

        final WorldPoint location = player.getWorldLocation();
        final PlayerDeath death = new PlayerDeath(location, Instant.now());
        if (location == null || playerDeaths.stream().anyMatch(d -> d.getDeathLocation().equals(location)))
        {
            return;
        }

        playerDeaths.add(new PlayerDeath(location, Instant.now()));
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        removeExpiredDeaths();
    }

    private void removeExpiredDeaths()
    {
        final Instant now = Instant.now();
        playerDeaths.removeIf(d -> Duration.between(d.getDeathTime(),now).getSeconds()>secondsToLoot);
    }

}
