package com.rekindled.embers.datagen;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import com.mojang.serialization.JsonOps;
import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class EmbersConfiguredFeatures extends JsonCodecProvider<ConfiguredFeature<?, ?>> {

	public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, Embers.MODID);

	public static final Supplier<List<OreConfiguration.TargetBlockState>> ORE_LEAD_TARGET_LIST = () -> List.of(
			OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, RegistryManager.LEAD_ORE.get().defaultBlockState()),
			OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, RegistryManager.DEEPSLATE_LEAD_ORE.get().defaultBlockState()));
	public static final RegistryObject<ConfiguredFeature<OreConfiguration, ?>> ORE_LEAD = CONFIGURED_FEATURES.register("ore_lead", () -> new ConfiguredFeature<OreConfiguration, Feature<OreConfiguration>>(Feature.ORE, new OreConfiguration(ORE_LEAD_TARGET_LIST.get(), 8)));

	public static final Supplier<List<OreConfiguration.TargetBlockState>> ORE_SILVER_TARGET_LIST = () -> List.of(
			OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, RegistryManager.SILVER_ORE.get().defaultBlockState()),
			OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, RegistryManager.DEEPSLATE_SILVER_ORE.get().defaultBlockState()));
	public static final RegistryObject<ConfiguredFeature<OreConfiguration, ?>> ORE_SILVER = CONFIGURED_FEATURES.register("ore_silver", () -> new ConfiguredFeature<OreConfiguration, Feature<OreConfiguration>>(Feature.ORE, new OreConfiguration(ORE_SILVER_TARGET_LIST.get(), 8)));

	public EmbersConfiguredFeatures(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, existingFileHelper, Embers.MODID, RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.builtinCopy()), PackType.SERVER_DATA, Registry.CONFIGURED_FEATURE_REGISTRY.location().getPath(), ConfiguredFeature.DIRECT_CODEC, new HashMap<ResourceLocation, ConfiguredFeature<?, ?>>());
	}

	@Override
	public void run(final CachedOutput cache) throws IOException {
		this.entries.put(ORE_LEAD.getId(), ORE_LEAD.get());
		this.entries.put(ORE_SILVER.getId(), ORE_SILVER.get());
		super.run(cache);
	}

	public ResourceLocation getResource(String name) {
		return new ResourceLocation(Embers.MODID, name);
	}
}
