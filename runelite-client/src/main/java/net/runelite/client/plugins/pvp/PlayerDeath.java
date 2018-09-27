package net.runelite.client.plugins.pvp;

import lombok.Getter;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

import javax.xml.stream.Location;
import java.time.Instant;

public class PlayerDeath
{
    @Getter
    private WorldPoint deathLocation;

    @Getter
    private Instant deathTime;

    PlayerDeath(WorldPoint deathLocation, Instant deathTime)
    {
        this.deathLocation = deathLocation;
        this.deathTime = deathTime;
    }
}
