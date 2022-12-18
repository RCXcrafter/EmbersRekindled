package com.rekindled.embers;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigManager {

	public static ConfigValue<Integer> EMBER_BORE_SPEED_MOD;

	public static void register() {
		//registerClientConfigs();
		registerCommonConfigs();
		//registerServerConfigs();
	}

	public static void registerClientConfigs() {
		ForgeConfigSpec.Builder CLIENT = new ForgeConfigSpec.Builder();

		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT.build());
	}

	public static void registerCommonConfigs() {
		ForgeConfigSpec.Builder COMMON = new ForgeConfigSpec.Builder();
		COMMON.comment("Settings for machine/item/misc parameters").push("parameters");

		EMBER_BORE_SPEED_MOD = COMMON.comment("The speed modifier of the Ember Bore before upgrades.").define("parameters.emberBore.speedMod", 1);

		COMMON.pop();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON.build());
	}

	public static void registerServerConfigs() {
		ForgeConfigSpec.Builder SERVER = new ForgeConfigSpec.Builder();

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER.build());
	}
}
