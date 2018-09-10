package net.runelite.client.plugins.earlydamage;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("earlydamage")
public interface EarlyDamageConfig extends Config
{
    @ConfigItem(
            keyName = "testOption",
            name = "Test Option",
            description = "I guess this works",
            position = 0
    )
    default boolean testOption() { return true; }
}
