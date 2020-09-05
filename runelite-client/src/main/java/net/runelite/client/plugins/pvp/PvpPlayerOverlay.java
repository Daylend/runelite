package net.runelite.client.plugins.pvp;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;


public class PvpPlayerOverlay extends Overlay
{
    private final Client client;
    private final PvpPlugin plugin;
    private final PvpConfig config;
    private final PanelComponent panelComponent = new PanelComponent();

    private static final Color RED = new Color(221, 44, 0);
    private static final Color GREEN = new Color(0, 200, 83);
    private static final Color ORANGE = new Color(255, 109, 0);
    private static final Color YELLOW = new Color(255, 214, 0);
    private static final Color CYAN = new Color(0, 184, 212);
    private static final Color BLUE = new Color(41, 98, 255);
    private static final Color DEEP_PURPLE = new Color(98, 0, 234);
    private static final Color PURPLE = new Color(170, 0, 255);
    private static final Color GRAY = new Color(158, 158, 158);
    private static final Color MID_LEVEL_COLOR = new Color(ORANGE.getRed(), ORANGE.getGreen(), ORANGE.getBlue(), 100);
    private static final Color HIGH_LEVEL_COLOR = new Color(RED.getRed(), RED.getGreen(), RED.getBlue(), 100);
    private static final Color LOW_LEVEL_COLOR = new Color(GREEN.getRed(), GREEN.getGreen(), GREEN.getBlue(), 100);
    private static final Color WILD_WARNING_COLOR = new Color(YELLOW.getRed(), YELLOW.getGreen(), YELLOW.getBlue(), 50);
    private static final Color WILD_ALERT_COLOR = new Color(RED.getRed(), RED.getGreen(), RED.getBlue(), 100);

    private static final NumberFormat TIME_LEFT_FORMATTER = DecimalFormat.getInstance(Locale.US);

    static
    {
        ((DecimalFormat)TIME_LEFT_FORMATTER).applyPattern("#0.0");
    }

    @Inject
    public PvpPlayerOverlay(Client client, PvpPlugin plugin, PvpConfig config)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        renderPlayers(graphics);
        plugin.getPlayerDeaths().forEach(d -> renderPlayerDeath(graphics, d));

        return null;
    }

    private void renderPlayers(Graphics2D graphics)
    {
        List<Player> players = client.getPlayers();
        Player local = client.getLocalPlayer();

        Widget wildWidget = client.getWidget(WidgetInfo.PVP_WILDERNESS_LEVEL);

        // Death animation = 836

        if(wildWidget!=null && !wildWidget.isHidden()) // If in wild and widget exists
        {
            String levelString = wildWidget.getText();
            int wildLevel = levelString!=null && levelString.contains("Level: ") ?
                Integer.parseInt(levelString.split("Level: ")[1]) : 1;

            for (Player p : players)
            {
                if (p != local)
                {
                    final int levelDifference = Math.abs(local.getCombatLevel()-p.getCombatLevel());

                    if(levelDifference<=wildLevel) // Enemy within wildy difference
                    {
                        renderPlayer(graphics, p, WILD_ALERT_COLOR);
                    }
                    else // Enemy outside wildy difference
                    {
                        renderPlayer(graphics, p, WILD_WARNING_COLOR);
                    }
                }
            }
        }
        else
        {
            for (Player p : players)
            {
                if(p != local)
                {
                    final int levelDifference = local.getCombatLevel()-p.getCombatLevel();

                    if(levelDifference<=-5) // If enemy is greater clvl by 5
                    {
                        renderPlayer(graphics, p, HIGH_LEVEL_COLOR);
                    }
                    else if(levelDifference>-5 && levelDifference<5) //within 5 clvls
                    {
                        renderPlayer(graphics, p, MID_LEVEL_COLOR);
                    }
                    else
                    {
                        renderPlayer(graphics, p, LOW_LEVEL_COLOR);
                    }
                }
            }
        }
    }

    private void renderPlayer(Graphics2D graphics, Player player, Color color)
    {
        String text = player.getCombatLevel() + "  [" + player.getName() + "]";
        Shape playerClickbox = player.getConvexHull();

        Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, player.getLocalLocation(),1);

        if(tilePoly!=null && config.showNames())
        {
            OverlayUtil.renderActorOverlay(graphics, player, text, color);
        }
        if(playerClickbox!=null && config.showHull())
        {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(playerClickbox);
            graphics.setColor(color);
            graphics.fill(playerClickbox);
        }

    }

    private void renderPlayerWireframe(Graphics2D graphics, Player player, Color color)
    {
        Polygon[] polys = player.getPolygons();

        if (polys == null)
        {
            return;
        }

        graphics.setColor(color);

        for (Polygon p : polys)
        {
            graphics.drawPolygon(p);
        }
    }

    private void renderPlayerDeath(Graphics2D graphics, PlayerDeath death)
    {
        final WorldPoint location = death.getDeathLocation();
        final LocalPoint deathPoint = LocalPoint.fromWorld(client, location.getX(), location.getY());

        if (deathPoint == null)
        {
            return;
        }

        final Color color = CYAN;

        final Polygon poly = Perspective.getCanvasTilePoly(client, deathPoint);

        if (poly != null)
        {
            OverlayUtil.renderPolygon(graphics, poly, color);
        }

        final String timeLeftStr = Long.toString(plugin.getSecondsToLoot() -
            Duration.between(death.getDeathTime(), Instant.now()).getSeconds());

        final int textWidth = graphics.getFontMetrics().stringWidth(timeLeftStr);
        final int textHeight = graphics.getFontMetrics().getAscent();
        final Point canvasPoint = Perspective.localToCanvas(client, deathPoint.getX(), deathPoint.getY(),
            location.getPlane());

        if (canvasPoint != null)
        {
            final Point canvasCenterPoint = new Point(
                canvasPoint.getX() - textWidth / 2,
                canvasPoint.getY() + textHeight / 2);

            OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, timeLeftStr, color);
        }
    }
}
