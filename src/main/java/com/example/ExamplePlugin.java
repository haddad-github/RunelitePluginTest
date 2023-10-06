package com.example;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@PluginDescriptor(name = "Example")
public class ExamplePlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private ExampleConfig config;

	@Inject
	private OverlayManager overlayManager;

	private ExampleOverlay exampleOverlay;

	private int totalZeros = 0;
	private int totalDamage = 0;
	private int totalHits = 0;

	private Instant startTime;
	private Duration duration;

	private int nonZeroHits = 0;



	@Override
	protected void startUp() throws Exception {
		exampleOverlay = new ExampleOverlay(client, this);
		overlayManager.add(exampleOverlay);
		log.info("Example started!");
		startTime = Instant.now();
	}

	@Override
	protected void shutDown() throws Exception {
		overlayManager.remove(exampleOverlay);
		log.info("Example stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
		}
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		Actor localPlayer = client.getLocalPlayer();
		Actor target = localPlayer.getInteracting();

		if (target != null && target == event.getActor())
		{
			int damage = event.getHitsplat().getAmount();

			if (damage == 0) {
				totalZeros++;
			} else {
				nonZeroHits++;
			}

			totalHits++;
			totalDamage += damage;
		}
	}


	@Provides
	ExampleConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ExampleConfig.class);
	}

	// Getter methods for the overlay to access the stats
	public int getTotalZeros() {
		return totalZeros;
	}

	public int getTotalHits() {
		return totalHits;
	}


	public double getAvgDamage() {
		return totalHits == 0 ? 0 : (double) totalDamage / totalHits;
	}

	public int getTotalDamage() {
		return totalDamage;
	}

	public String getElapsedTime() {
		duration = Duration.between(startTime, Instant.now());
		long totalSeconds = duration.getSeconds();
		long hours = totalSeconds / 3600;
		long minutes = (totalSeconds % 3600) / 60;
		long seconds = totalSeconds % 60;
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	public double getDPS() {
		duration = Duration.between(startTime, Instant.now());
		long totalSeconds = duration.getSeconds();

		if (totalSeconds == 0) {
			return 0; // Avoid division by zero
		}

		return (double) totalDamage / totalSeconds;
	}

	public double getAvgDamageExcludingZeros() {
		return nonZeroHits == 0 ? 0 : (double) totalDamage / nonZeroHits;
	}



}