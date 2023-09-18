package com.rekindled.embers;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigManager {

	public static ConfigValue<Double> EMBER_BORE_SPEED_MOD;
	public static ConfigValue<Integer> RESERVOIR_CAPACITY;
	public static ConfigValue<Integer> MINI_BOILER_CAPACITY;
	public static ConfigValue<Double> MINI_BOILER_HEAT_MULTIPLIER;
	public static ConfigValue<Boolean> MINI_BOILER_CAN_EXPLODE;
	public static ConfigValue<Boolean> CODEX_PROGRESSION;
	public static ConfigValue<List<? extends String>> TAG_PREFERENCES;

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

		EMBER_BORE_SPEED_MOD = COMMON.comment("The speed modifier of the Ember Bore before upgrades.").define("parameters.emberBore.speedMod", 1.0);

		RESERVOIR_CAPACITY = COMMON.comment("How much fluid (in mb) fits into each Caminite Ring on a Reservoir.").define("parameters.reservoir.capacity", FluidType.BUCKET_VOLUME * 40);

		MINI_BOILER_CAPACITY = COMMON.comment("How much fluid and gas (in mb) fits into a Mini Boiler.").define("parameters.mini_boiler.capacity", FluidType.BUCKET_VOLUME * 16);

		MINI_BOILER_HEAT_MULTIPLIER = COMMON.comment("How much fluid (in mb) a Mini Boiler boils for each ember used/generated.").define("parameters.mini_boiler.heat_multiplier", 1.0);

		MINI_BOILER_CAN_EXPLODE = COMMON.comment("Whether the Mini Boiler can explode when overfilled with gas").define("parameters.mini_boiler.can_explode", true);

		COMMON.pop();


		COMMON.comment("Miscellaneous settings").push("misc");

		CODEX_PROGRESSION = COMMON.comment("Codex entries need to be completed before the next one unlocks.").define("misc.codexProgression", true);

		List<String> preferences = new ArrayList<String>();
		preferences.add(0, "minecraft");
		preferences.add(1, Embers.MODID);
		TAG_PREFERENCES = COMMON.comment("Which domains are preferred for tag output recipes.").defineList("misc.tagPreferences", preferences, a -> true);

		COMMON.pop();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON.build());
	}

	public static void registerServerConfigs() {
		ForgeConfigSpec.Builder SERVER = new ForgeConfigSpec.Builder();

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER.build());
	}
}
