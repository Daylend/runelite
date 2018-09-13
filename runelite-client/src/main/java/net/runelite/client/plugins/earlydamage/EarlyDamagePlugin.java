package net.runelite.client.plugins.earlydamage;


import com.google.common.eventbus.Subscribe;
import com.google.inject.Provides;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.Skill;
import net.runelite.api.events.ExperienceChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.xptracker.XpTrackerPlugin;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
        name = "Early Damage",
        description = "Show damage before it hits",
        tags = {"damage"},
        enabledByDefault = false
)
@PluginDependency(XpTrackerPlugin.class)
public class EarlyDamagePlugin extends Plugin
{
    private static final int MAXIMUM_SHOWN_GLOBES = 5;

    @Inject
    private Client client;

    @Inject
    private EarlyDamageConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private EarlyDamageOverlay overlay;

    @Provides
    EarlyDamageConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(EarlyDamageConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onExperienceChanged(ExperienceChanged event)
    {
        Skill skill = event.getSkill();
        int currentXp = client.getSkillExperience(skill);
        int currentLevel = Experience.getLevelForXp(currentXp);
        int skillIdx = skill.ordinal();
        //XpGlobe cachedGlobe = globeCache[skillIdx];


        int startingXp = Experience.getXpForLevel(currentLevel);
        int goalXp = currentLevel + 1 <= Experience.MAX_VIRT_LEVEL ? Experience.getXpForLevel(currentLevel + 1) : -1;

    }


    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        switch (event.getGameState())
        {
            case HOPPING:
            case LOGGING_IN:
                break;
        }
    }

}
