package com.rekindled.embers.datagen;

import java.util.List;

import com.rekindled.embers.Embers;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class EmbersPlacedFeatures {

	public static final ResourceKey<PlacedFeature> ORE_LEAD_KEY = ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(Embers.MODID, "ore_lead_placer"));
	public static final List<PlacementModifier> ORE_LEAD_PLACEMENT = commonOrePlacement(8, HeightRangePlacement.triangle(VerticalAnchor.absolute(-28), VerticalAnchor.absolute(28)));

	public static final ResourceKey<PlacedFeature> ORE_SILVER_KEY = ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(Embers.MODID, "ore_silver_placer"));
	public static final List<PlacementModifier> ORE_SILVER_PLACEMENT = commonOrePlacement(4, HeightRangePlacement.triangle(VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(64)));

	public static void generate(BootstapContext<PlacedFeature> bootstrap) {
		HolderGetter<ConfiguredFeature<?, ?>> configured = bootstrap.lookup(Registries.CONFIGURED_FEATURE);
		bootstrap.register(ORE_LEAD_KEY, new PlacedFeature(configured.getOrThrow(EmbersConfiguredFeatures.ORE_LEAD_KEY), ORE_LEAD_PLACEMENT));
		bootstrap.register(ORE_SILVER_KEY, new PlacedFeature(configured.getOrThrow(EmbersConfiguredFeatures.ORE_SILVER_KEY), ORE_SILVER_PLACEMENT));
	}

	public static List<PlacementModifier> orePlacement(PlacementModifier p_195347_, PlacementModifier p_195348_) {
		return List.of(p_195347_, InSquarePlacement.spread(), p_195348_, BiomeFilter.biome());
	}

	public static List<PlacementModifier> commonOrePlacement(int pCount, PlacementModifier pHeightRange) {
		return orePlacement(CountPlacement.of(pCount), pHeightRange);
	}

	public static List<PlacementModifier> rareOrePlacement(int pChance, PlacementModifier pHeightRange) {
		return orePlacement(RarityFilter.onAverageOnceEvery(pChance), pHeightRange);
	}
}
