package com.hitstats;

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

public class HitStatsOverlay extends Overlay {
    private final Client client;
    private final HitStatsPlugin plugin;
    private final HitStatsConfig config;
    private final PanelComponent panelComponent = new PanelComponent();


    @Inject
    public HitStatsOverlay(Client client, HitStatsPlugin plugin, HitStatsConfig config) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.BOTTOM_RIGHT);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setMovable(true);
        panelComponent.setPreferredSize(new Dimension(220, 0));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();

        if (config.showTotalHits()) {
            String totalHits = "Total Attacks: " + plugin.getTotalHits();
            panelComponent.getChildren().add(LineComponent.builder().left(totalHits).leftColor(Color.RED).build());
        }

        if (config.showNonZeroHits()) {
            String nonZeroHits = "Successful Attacks: " + plugin.getNonZeroHits();
            panelComponent.getChildren().add(LineComponent.builder().left(nonZeroHits).leftColor(Color.GREEN).build());
        }

        if (config.showTotalZeros()) {
            String totalZeros = "Missed Attacks: " + plugin.getTotalZeros();
            panelComponent.getChildren().add(LineComponent.builder().left(totalZeros).leftColor(Color.CYAN).build());
        }

        if (config.showAvgDamage()) {
            String avgDamage = String.format("Avg Dmg (incl. misses): %.2f", plugin.getAvgDamage());
            panelComponent.getChildren().add(LineComponent.builder().left(avgDamage).build());
        }

        if (config.showAvgDamageExclZeros()) {
            String avgDamageExclZeros = String.format("Avg Dmg (excl. misses): %.2f", plugin.getAvgDamageExcludingZeros());
            panelComponent.getChildren().add(LineComponent.builder().left(avgDamageExclZeros).build());
        }

        if (config.showTotalDamage()) {
            String totalDamage = "Total Dmg: " + plugin.getTotalDamage();
            panelComponent.getChildren().add(LineComponent.builder().left(totalDamage).build());
        }

        if (config.showDPS()) {
            String dps = String.format("DPS: %.2f", plugin.getDPS());
            panelComponent.getChildren().add(LineComponent.builder().left(dps).leftColor(Color.ORANGE).build());
        }

        if (config.showElapsedTime()) {
            String elapsedTime = "Time: " + plugin.getElapsedTime();
            panelComponent.getChildren().add(LineComponent.builder().left(elapsedTime).leftColor(Color.GRAY).build());
        }

        return panelComponent.render(graphics);
    }
}
