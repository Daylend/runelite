package net.runelite.client.plugins.metronome;

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
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Experience;
import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.xptracker.XpTrackerService;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.ProgressBarComponent;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

public class MetronomeOverlay extends Overlay
{
    private static final double DIAMETER = 26D;
    private static final int OFFSET = 24;
    private static final int MS_PER_TICK = 600;
    private static final Color TICK_COLOR = brighter(0x1E95B0);


    private final Client client;
    private final MetronomePlugin plugin;
    private final MetronomePluginConfiguration config;
    private final PanelComponent panel = new PanelComponent();

    private static Color brighter(int color)
    {
        float[] hsv = new float[3];
        Color.RGBtoHSB(color >>> 16, (color >> 8) & 0xFF, color & 0xFF, hsv);
        return Color.getHSBColor(hsv[0], 1.f, 1.f);
    }

    @Inject
    private MetronomeOverlay(Client client, MetronomePlugin plugin, MetronomePluginConfiguration config)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
//        panel.getChildren().clear();
//        panel.getChildren().add(LineComponent.builder()
//            .left("Tick " + Integer.toString(plugin.getTickCounter()))
//            .build());
//        return panel.render(graphics);

        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        Instant lastTick = plugin.getLastTick();
        long timeSinceTick = Duration.between(lastTick, Instant.now()).toMillis();
        renderTick(graphics, WidgetInfo.MINIMAP_QUICK_PRAYER_ORB, ((double)timeSinceTick / MS_PER_TICK), TICK_COLOR);

        return null;
    }

    private void renderTick(Graphics2D graphics, WidgetInfo widgetInfo, double percent, Color color)
    {
        Widget widget = client.getWidget(widgetInfo);
        if (widget == null || widget.isHidden())
        {
            return;
        }
        Rectangle bounds = widget.getBounds();

        Arc2D.Double arc = new Arc2D.Double(bounds.x + OFFSET, bounds.y +
            (bounds.height / 2 - DIAMETER / 2) - 1, DIAMETER, DIAMETER, 88.d,
            -360.d * percent, Arc2D.OPEN);
        final Stroke STROKE = new BasicStroke(2f);
        graphics.setStroke(STROKE);
        graphics.setColor(color);
        graphics.draw(arc);
    }
}
