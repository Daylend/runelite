package net.runelite.client.plugins.earlydamage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.Point;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.xptracker.XpTrackerService;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.ProgressBarComponent;

public class EarlyDamageOverlay extends Overlay
{
    private final Client client;
    private final EarlyDamagePlugin plugin;
    private final EarlyDamageConfig config;
    private final XpTrackerService xpTrackerService;
    private final PanelComponent panel = new PanelComponent();

    @Inject
    private EarlyDamageOverlay(
            Client client,
            EarlyDamagePlugin plugin,
            EarlyDamageConfig config,
            XpTrackerService xpTrackerService)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.xpTrackerService = xpTrackerService;
        setPosition(OverlayPosition.TOP_CENTER);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        panel.getChildren().clear();
        panel.getChildren().add(LineComponent.builder()
            .left("Damage: ")
            .right("Test")
            .build());

        return panel.render(graphics);
    }
}
