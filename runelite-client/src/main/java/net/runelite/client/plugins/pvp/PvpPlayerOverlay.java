package net.runelite.client.plugins.pvp;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;


public class PvpPlayerOverlay extends Overlay
{
    private final Client client;
    private final PvpPlugin plugin;
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
    private static final Color WARNING_COLOR = new Color(ORANGE.getRed(), ORANGE.getGreen(), ORANGE.getBlue(), 100);
    private static final Color ALERT_COLOR = new Color(RED.getRed(), RED.getGreen(), RED.getBlue(), 100);
    private static final Color EASY_COLOR = new Color(GREEN.getRed(), GREEN.getGreen(), GREEN.getBlue(), 100);

    @Inject
    public PvpPlayerOverlay(Client client, PvpPlugin plugin)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        renderPlayers(graphics);

        return null;
    }

    private void renderPlayers(Graphics2D graphics)
    {
        List<Player> players = client.getPlayers();
        Player local = client.getLocalPlayer();

        for (Player p : players)
        {
            if(p != local)
            {
                final int levelDifference = local.getCombatLevel()-p.getCombatLevel();
                String text = p.getCombatLevel() + "  [" + p.getName() + "]";
                Polygon playerClickbox = p.getConvexHull();

                if(levelDifference<=-5) // If enemy is greater clvl by 5
                {
                    OverlayUtil.renderActorOverlay(graphics, p, text, ALERT_COLOR);
                    graphics.setColor(ALERT_COLOR);
                    graphics.setStroke(new BasicStroke(2));
                    graphics.draw(playerClickbox);
                    graphics.setColor(ALERT_COLOR);
                    graphics.fill(playerClickbox);
                }
                else if(levelDifference>-5 && levelDifference<5) //within 5 clvls
                {
                    OverlayUtil.renderActorOverlay(graphics, p, text, ALERT_COLOR);
                    graphics.setColor(WARNING_COLOR);
                    graphics.setStroke(new BasicStroke(2));
                    graphics.draw(playerClickbox);
                    graphics.setColor(WARNING_COLOR);
                    graphics.fill(playerClickbox);
                }
                else
                {
                    OverlayUtil.renderActorOverlay(graphics, p, text, EASY_COLOR);
                    graphics.setColor(EASY_COLOR);
                    graphics.setStroke(new BasicStroke(2));
                    graphics.draw(playerClickbox);
                    graphics.setColor(EASY_COLOR);
                    graphics.fill(playerClickbox);
                }
            }
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
}
