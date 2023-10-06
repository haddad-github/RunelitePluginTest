package com.example;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class ExampleOverlay extends Overlay {
    private final Client client;
    private final ExamplePlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    public ExampleOverlay(Client client, ExamplePlugin plugin) {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.BOTTOM_RIGHT);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }


    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();

        String totalZeros = "Number of 0's: " + plugin.getTotalZeros();
        String avgDamage = String.format("Average Damage: %.2f", plugin.getAvgDamage());
        String totalDamage = "Total Damage: " + plugin.getTotalDamage();
        String totalHits = "Total Hits: " + plugin.getTotalHits();
        String elapsedTime = "Time: " + plugin.getElapsedTime();
        String dps = String.format("DPS: %.2f", plugin.getDPS());
        String avgDamageExclZeros = String.format("Average Damage (excluding 0's): %.2f", plugin.getAvgDamageExcludingZeros());

        panelComponent.getChildren().add(LineComponent.builder().left(totalZeros).leftColor(Color.CYAN).build());
        panelComponent.getChildren().add(LineComponent.builder().left(avgDamage).build());
        panelComponent.getChildren().add(LineComponent.builder().left(totalDamage).build());
        panelComponent.getChildren().add(LineComponent.builder().left(totalHits).leftColor(Color.RED).build());
        panelComponent.getChildren().add(LineComponent.builder().left(elapsedTime).build());
        panelComponent.getChildren().add(LineComponent.builder().left(dps).leftColor(Color.ORANGE).build());
        panelComponent.getChildren().add(LineComponent.builder().left(avgDamageExclZeros).build());


        return panelComponent.render(graphics);
    }
}