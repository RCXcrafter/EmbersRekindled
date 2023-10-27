package com.rekindled.embers;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ConfigManager {

	public static ConfigValue<Integer> MAX_PROXY_DISTANCE;
	public static ConfigValue<Double> EMBER_BORE_SPEED_MOD;
	public static ConfigValue<Integer> EMBER_BORE_TIME;
	public static ConfigValue<Double> EMBER_BORE_FUEL_CONSUMPTION;
	public static ConfigValue<Integer> RESERVOIR_CAPACITY;
	public static ConfigValue<Integer> MINI_BOILER_CAPACITY;
	public static ConfigValue<Double> MINI_BOILER_HEAT_MULTIPLIER;
	public static ConfigValue<Boolean> MINI_BOILER_CAN_EXPLODE;
	public static ConfigValue<Integer> INJECTOR_MAX_DISTANCE;
	public static ConfigValue<Integer> MELTER_PROCESS_TIME;
	public static ConfigValue<Double> MELTER_EMBER_COST;
	public static ConfigValue<Integer> MELTER_CAPACITY;
	public static ConfigValue<Integer> GEO_SEPARATOR_CAPACITY;
	public static ConfigValue<Integer> STAMP_BASE_CAPACITY;
	public static ConfigValue<Double> CHARGER_MAX_TRANSFER;
	public static ConfigValue<Integer> FLUID_VESSEL_CAPACITY;
	public static ConfigValue<Double> BLAZING_RAY_COST;
	public static ConfigValue<Integer> BLAZING_RAY_COOLDOWN;
	public static ConfigValue<Integer> BLAZING_RAY_MAX_CHARGE;
	public static ConfigValue<Double> BLAZING_RAY_DAMAGE;
	public static ConfigValue<Double> BLAZING_RAY_MAX_SPREAD;
	public static ConfigValue<Double> BLAZING_RAY_MAX_DISTANCE;
	public static ConfigValue<Double> CINDER_STAFF_COST;
	public static ConfigValue<Integer> CINDER_STAFF_COOLDOWN;
	public static ConfigValue<Integer> CINDER_STAFF_MAX_CHARGE;
	public static ConfigValue<Double> CINDER_STAFF_DAMAGE;
	public static ConfigValue<Double> CINDER_STAFF_SIZE;
	public static ConfigValue<Double> CINDER_STAFF_AOE_SIZE;
	public static ConfigValue<Integer> CINDER_STAFF_LIFETIME;

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

		MAX_PROXY_DISTANCE = COMMON.comment("The maximum distance that mechanical cores can proxy capabilities and upgrades.").define("mechanical_core.max_distance", 3);

		EMBER_BORE_SPEED_MOD = COMMON.comment("The speed modifier of the Ember Bore before upgrades.").define("emberBore.speedMod", 1.0);
		EMBER_BORE_TIME = COMMON.comment("The time in ticks it takes to try one dig attempt.").define("emberBore.processTime", 200);
		EMBER_BORE_FUEL_CONSUMPTION = COMMON.comment("The amount of fuel consumed each tick.").define("emberBore.fuelCost", 3.0);

		RESERVOIR_CAPACITY = COMMON.comment("How much fluid (in mb) fits into each Caminite Ring on a Reservoir.").define("reservoir.capacity", FluidType.BUCKET_VOLUME * 40);

		MINI_BOILER_CAPACITY = COMMON.comment("How much fluid and gas (in mb) fits into a Mini Boiler.").define("mini_boiler.capacity", FluidType.BUCKET_VOLUME * 16);
		MINI_BOILER_HEAT_MULTIPLIER = COMMON.comment("How much fluid (in mb) a Mini Boiler boils for each ember used/generated.").define("mini_boiler.heat_multiplier", 1.0);
		MINI_BOILER_CAN_EXPLODE = COMMON.comment("Whether the Mini Boiler can explode when overfilled with gas").define("mini_boiler.can_explode", true);

		MELTER_PROCESS_TIME = COMMON.comment("The time in ticks it takes to process one recipe.").define("melter.processTime", 200);
		MELTER_EMBER_COST = COMMON.comment("The ember cost per tick.").define("melter.cost", 1.0);
		MELTER_CAPACITY = COMMON.comment("How much fluid (in mb) fits into a Melter.").define("melter.capacity", FluidType.BUCKET_VOLUME * 4);

		GEO_SEPARATOR_CAPACITY = COMMON.comment("How much fluid (in mb) fits into a Geologic Seperator.").define("geoSeparator.capacity", FluidType.BUCKET_VOLUME);

		STAMP_BASE_CAPACITY = COMMON.comment("How much fluid (in mb) fits into the Stamp Base.").define("stamper.capacity", (FluidType.BUCKET_VOLUME * 3) /2);

		CHARGER_MAX_TRANSFER = COMMON.comment("How much ember is transferred between item and charger per tick.").define("charger.transfer", 10.0);

		FLUID_VESSEL_CAPACITY = COMMON.comment("How much fluid (in mb) fits into the Fluid Vessel.").define("fluidVessel.capacity", FluidType.BUCKET_VOLUME * 16);

		INJECTOR_MAX_DISTANCE = COMMON.comment("The maximum distance that Ember Injectors can be placed from a crystal seed.").define("ember_injector.max_distance", 1);

		BLAZING_RAY_COST = COMMON.comment("Ember used up by each shot.").define("blazingRay.cost", 25.0);
		BLAZING_RAY_COOLDOWN = COMMON.comment("Cooldown in ticks between each shot.").define("blazingRay.cooldown", 10);
		BLAZING_RAY_MAX_CHARGE = COMMON.comment("Time in ticks to fully charge.").define("blazingRay.charge", 20);
		BLAZING_RAY_DAMAGE = COMMON.comment("Damage dealt by one shot.").define("blazingRay.damage", 7.0);
		BLAZING_RAY_MAX_SPREAD = COMMON.comment("Maximum spread.").define("blazingRay.spread", 30.0);
		BLAZING_RAY_MAX_DISTANCE = COMMON.comment("Maximum shot distance.").define("blazingRay.distance", 96.0);

		CINDER_STAFF_COST = COMMON.comment("Ember used up by each shot.").define("cinderStaff.cost", 25.0);
		CINDER_STAFF_COOLDOWN = COMMON.comment("Cooldown in ticks between each shot.").define("cinderStaff.cooldown", 10);
		CINDER_STAFF_MAX_CHARGE = COMMON.comment("Time in ticks to fully charge.").define("cinderStaff.charge", 60);
		CINDER_STAFF_DAMAGE = COMMON.comment("Damage dealt by one shot.").define("cinderStaff.damage", 17.0);
		CINDER_STAFF_SIZE = COMMON.comment("Size of the projectile.").define("cinderStaff.size", 17.0);
		CINDER_STAFF_AOE_SIZE = COMMON.comment("Area of Effect on impact.").define("cinderStaff.aoe", 17 * 0.125);
		CINDER_STAFF_LIFETIME = COMMON.comment("Maximum lifetime in ticks of projectile.").define("cinderStaff.lifetime", 160);

		COMMON.pop();


		COMMON.comment("Miscellaneous settings").push("misc");

		CODEX_PROGRESSION = COMMON.comment("Codex entries need to be completed before the next one unlocks.").define("codexProgression", true);

		List<String> preferences = new ArrayList<String>();
		preferences.add(0, "minecraft");
		preferences.add(1, Embers.MODID);
		TAG_PREFERENCES = COMMON.comment("Which domains are preferred for tag output recipes.").defineList("tagPreferences", preferences, a -> true);

		COMMON.pop();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON.build());
	}

	public static void registerServerConfigs() {
		ForgeConfigSpec.Builder SERVER = new ForgeConfigSpec.Builder();

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER.build());
	}
}
