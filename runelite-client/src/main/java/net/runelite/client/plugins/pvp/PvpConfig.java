package net.runelite.client.plugins.pvp;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("pvp")
public interface PvpConfig extends Config
{
    @ConfigItem(
        keyName = "showNames",
        name = "Show names & tiles",
        description = "Show names and tile highlighting")
    default boolean showNames() { return true; }

    @ConfigItem(
        keyName = "showHull",
        name = "Show hull highlighting",
        description = "Show hull highlighting")
    default boolean showHull() { return true; }
}
