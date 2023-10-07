package com.hitstats;

import net.runelite.client.ui.PluginPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HitStatsPluginPanel extends PluginPanel  {
    private final JLabel zerosLabel = new JLabel();
    private final JLabel avgDamageLabel = new JLabel();
    private final JLabel totalDamageLabel = new JLabel();

    public HitStatsPluginPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(0, 2));

        statsPanel.add(new JLabel("Number of 0's:"));
        statsPanel.add(zerosLabel);

        statsPanel.add(new JLabel("Average Damage:"));
        statsPanel.add(avgDamageLabel);

        statsPanel.add(new JLabel("Total Damage:"));
        statsPanel.add(totalDamageLabel);

        add(statsPanel, BorderLayout.NORTH);
    }

    void updateZeros(int zeros) {
        zerosLabel.setText(String.valueOf(zeros));
    }

    void updateAvgDamage(double avgDamage) {
        avgDamageLabel.setText(String.format("%.2f", avgDamage));
    }

    void updateTotalDamage(int totalDamage) {
        totalDamageLabel.setText(String.valueOf(totalDamage));
    }
}
