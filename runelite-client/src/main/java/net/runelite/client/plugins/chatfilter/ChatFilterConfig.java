package net.runelite.client.plugins.chatfilter;

import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Config;

@ConfigGroup("chatfilter")
public interface ChatFilterConfig extends Config
{
    @ConfigItem(
        keyName = "blacklistedStrings",
        name = "Blacklisted Phrases",
        description = "Removes chat messages based on blacklisted phrases, like: (phrase1), (phrase2)",
        position = 0
    )
    default String getBlacklistedStrings()
    {
        return "";
    }

    @ConfigItem(
        keyName = "blacklistedStrings",
        name = "",
        description = ""
    )
    void setBlacklistedStrings(String key);
}
