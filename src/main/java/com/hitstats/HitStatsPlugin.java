package com.hitstats;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@PluginDescriptor(name = "Hit Stats")
public class HitStatsPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private HitStatsConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ConfigManager configManager;

	private HitStatsOverlay hitStatsOverlay;

	private boolean justLoggedIn = false;

	private String lastKnownWeapon = "Unknown";

	private int totalZeros = 0;
	private int totalDamage = 0;
	private int totalHits = 0;

	private Instant startTime;
	private Duration duration;

	private int nonZeroHits = 0;

	private ScheduledFuture<?> scheduledTask;

	private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

	@Override
	protected void startUp() throws Exception {
		hitStatsOverlay = new HitStatsOverlay(client, this, config);
		overlayManager.add(hitStatsOverlay);
		log.info("Hit Stats started!");
		startTime = Instant.now();

		saveStatsToFile();  // Immediate file save/check on startup

		// If executor is terminated, reinitialize it
		if (executorService.isTerminated()) {
			executorService = Executors.newSingleThreadScheduledExecutor();
		}

		// Check if executor is shut down
		if (!executorService.isShutdown()) {
			scheduledTask = executorService.scheduleAtFixedRate(this::saveStatsToFile, 1, 1, TimeUnit.MINUTES);
		}
	}


	@Override
	protected void shutDown() throws Exception {
		overlayManager.remove(hitStatsOverlay);
		log.info("Hit Stats stopped!");

		if (scheduledTask != null) {
			scheduledTask.cancel(false);  // Cancel the scheduled task without interrupting
		}

		executorService.shutdown();
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event) {
		Actor localPlayer = client.getLocalPlayer();
		Actor target = localPlayer.getInteracting();

		if (target != null && target == event.getActor() && event.getHitsplat().isMine()) {
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

	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (!"com.hitstats".equals(event.getGroup())) {
			return;
		}

		if ("resetStats".equals(event.getKey())) {
			resetAllStats();
			configManager.setConfiguration("com.hitstats", "resetStats", false); // Reset the dummy button
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		if (event.getGameState() == GameState.LOGGED_IN) {
			justLoggedIn = true;  // Set the flag when the user logs in
		}
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		if (justLoggedIn) {
			getWeaponName();  // Fetch the weapon name on the tick following login
			justLoggedIn = false;  // Reset the flag
		}
	}

	@Provides
	HitStatsConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(HitStatsConfig.class);
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

	public int getNonZeroHits() {
		return nonZeroHits;
	}

	public double getAvgDamageExcludingZeros() {
		return nonZeroHits == 0 ? 0 : (double) totalDamage / nonZeroHits;
	}

	private String getWeaponName() {
		if (client == null || client.getLocalPlayer() == null) {
			log.info("Client or local player is not initialized.");
			return null;
		}

		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment == null) {
			log.info("Equipment container is null.");
			return null;
		}

		Item weapon = equipment.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
		if (weapon == null) {
			log.info("No weapon equipped.");
			lastKnownWeapon = "None";
			return "None";
		}

		ItemComposition weaponComposition = client.getItemDefinition(weapon.getId());
		log.info("Player is wielding: {}", weaponComposition.getName());
		lastKnownWeapon = weaponComposition.getName();
		return weaponComposition.getName();
	}

	public String getLastKnownWeapon() {
		return lastKnownWeapon;
	}

	private void saveStatsToFile() {
		if (!config.enableLogging()) {
			return;
		}

		if (client == null || client.getLocalPlayer() == null) {
			log.error("Client or local player is not initialized.");
			return;
		}

		String playerName = client.getLocalPlayer().getName();
		if (playerName == null) {
			log.error("Player name is null.");
			return;
		}

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		String currentDate = LocalDate.now().format(dtf);
		String fileName = playerName + "_log_" + currentDate + "." + config.fileExtension().toString();

		File logDirectory = new File(config.logFilePath());
		if (!logDirectory.exists() || !logDirectory.isDirectory()) {
			log.error("The specified log directory does not exist: {}", config.logFilePath());
			return;
		}

		File logFile = new File(logDirectory, fileName);


		try (FileWriter writer = new FileWriter(logFile, true)) {
			if (logFile.length() == 0) {
				// Add the header if the file is empty
				writer.append("Timestamp,Total Zeros,Average Damage,Total Damage,Total Hits,Time Elapsed,DPS,Average Damage (excluding 0's),Weapon name\n");
			}

			// Write data
			writer.append(getElapsedTime()).append(",");
			writer.append(Integer.toString(getTotalZeros())).append(",");
			writer.append(String.format("%.2f", getAvgDamage())).append(",");
			writer.append(Integer.toString(getTotalDamage())).append(",");
			writer.append(Integer.toString(getTotalHits())).append(",");
			writer.append(getElapsedTime()).append(",");
			writer.append(String.format("%.2f", getDPS())).append(",");
			writer.append(String.format("%.2f", getAvgDamageExcludingZeros())).append(",");
			writer.append(getLastKnownWeapon()).append("\n");


		} catch (IOException e) {
			log.error("Error writing to log.txt", e);
		}
	}

	private void resetAllStats() {
		totalZeros = 0;
		totalDamage = 0;
		totalHits = 0;
		nonZeroHits = 0;
		startTime = Instant.now();
	}


}