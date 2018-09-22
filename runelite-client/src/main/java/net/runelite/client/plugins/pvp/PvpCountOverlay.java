package net.runelite.client.plugins.pvp;

import com.google.common.graph.Graph;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class PvpCountOverlay extends Overlay
{
    private final PvpPlugin plugin;
    private final Client client;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    public PvpCountOverlay(Client client, PvpPlugin plugin)
    {
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        this.plugin = plugin;
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        int numPlayers = client.getPlayers() == null ? 0 : client.getPlayers().size();
        String strPlayers = "Players: " + numPlayers;

        panelComponent.getChildren().clear();
        panelComponent.getChildren().add(TitleComponent.builder()
            .text(strPlayers)
            .color(Color.WHITE)
            .build());
        panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(strPlayers)+10,
            0));

        return panelComponent.render(graphics);
    }
}
