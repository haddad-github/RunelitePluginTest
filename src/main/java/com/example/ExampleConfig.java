package com.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("com.example")
public interface ExampleConfig extends Config {
	@ConfigItem(
			keyName = "showTotalZeros",
			name = "Show Total Zeros",
			description = "Toggle displaying the total number of 0's"
	)
	default boolean showTotalZeros() {
		return true;
	}

	@ConfigItem(
			keyName = "showAvgDamage",
			name = "Show Average Damage",
			description = "Toggle displaying the average damage"
	)
	default boolean showAvgDamage() {
		return true;
	}

	@ConfigItem(
			keyName = "showTotalDamage",
			name = "Show Total Damage",
			description = "Toggle displaying the total damage"
	)
	default boolean showTotalDamage() {
		return true;
	}

	@ConfigItem(
			keyName = "showTotalHits",
			name = "Show Total Hits",
			description = "Toggle displaying the total hits"
	)
	default boolean showTotalHits() {
		return true;
	}

	@ConfigItem(
			keyName = "showElapsedTime",
			name = "Show Elapsed Time",
			description = "Toggle displaying the elapsed time"
	)
	default boolean showElapsedTime() {
		return true;
	}

	@ConfigItem(
			keyName = "showDPS",
			name = "Show DPS",
			description = "Toggle displaying the DPS"
	)
	default boolean showDPS() {
		return true;
	}

	@ConfigItem(
			keyName = "dpsTextColor",
			name = "DPS Text Color",
			description = "Color of the DPS text in the overlay"
	)
	default Color dpsTextColor() {
		return Color.ORANGE; // default color
	}

	@ConfigItem(
			keyName = "showAvgDamageExclZeros",
			name = "Show Avg Damage Excluding Zeros",
			description = "Toggle displaying the average damage excluding zeros"
	)
	default boolean showAvgDamageExclZeros() {
		return true;
	}

	@ConfigItem(
			keyName = "showNonZeroHits",
			name = "Show Non-0 Hits",
			description = "Toggle displaying the number of non-zero hits"
	)
	default boolean showNonZeroHits() {
		return true; // on by default
	}


	@ConfigItem(
			keyName = "enableLogging",
			name = "Enable Logging",
			description = "Toggle saving the stats to a .txt file"
	)
	default boolean enableLogging() {
		return false; // off by default
	}

	@ConfigItem(
			keyName = "logFilePath",
			name = "Log File Path",
			description = "Path to save the log.txt file"
	)
	default String logFilePath() {
		return System.getProperty("user.home") + "/Desktop"; // default path
	}

	@ConfigItem(
			keyName = "resetStats",
			name = "Reset Stats",
			description = "Reset all recorded stats"
	)
	default boolean resetStats() {
		return false; // This value doesn't matter, as it's only for triggering
	}

	@ConfigItem(
			keyName = "resetStats",
			name = "",
			description = ""
	)
	void resetStats(boolean set);

}


