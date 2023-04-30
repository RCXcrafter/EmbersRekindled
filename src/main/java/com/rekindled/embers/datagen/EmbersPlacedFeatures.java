package com.rekindled.embers.datagen;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.rekindled.embers.Embers;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class EmbersPlacedFeatures extends JsonCodecProvider<PlacedFeature> {

	public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, Embers.MODID);

	public static final RegistryObject<PlacedFeature> ORE_LEAD_PLACER = PLACED_FEATURES.register("ore_lead_placer", () -> new PlacedFeature(Holder.hackyErase(EmbersConfiguredFeatures.ORE_LEAD.getHolder().get()),
			commonOrePlacement(8, HeightRangePlacement.triangle(VerticalAnchor.absolute(-28), VerticalAnchor.absolute(28)))));

	public static final RegistryObject<PlacedFeature> ORE_SILVER_PLACER = PLACED_FEATURES.register("ore_silver_placer", () -> new PlacedFeature(Holder.hackyErase(EmbersConfiguredFeatures.ORE_SILVER.getHolder().get()),
			commonOrePlacement(8, HeightRangePlacement.triangle(VerticalAnchor.absolute(-28), VerticalAnchor.absolute(28)))));

	public EmbersPlacedFeatures(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, existingFileHelper, Embers.MODID, RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.builtinCopy()), PackType.SERVER_DATA, Registry.PLACED_FEATURE_REGISTRY.location().getPath(), PlacedFeature.DIRECT_CODEC, new HashMap<ResourceLocation, PlacedFeature>());
	}

	@Override
	public void run(final CachedOutput cache) throws IOException {
		this.add(ORE_LEAD_PLACER);
		this.add(ORE_SILVER_PLACER);

		super.run(cache);
	}

	public void add(RegistryObject<PlacedFeature> holder) {
		Optional<? extends Registry<PlacedFeature>> registryOptional = ((RegistryOps<JsonElement>) dynamicOps).registry(Registry.PLACED_FEATURE_REGISTRY);
		ResourceKey<PlacedFeature> key = holder.getKey();
		if (registryOptional.isPresent() && key != null) {
			this.entries.put(key.location(), registryOptional.get().getOrCreateHolderOrThrow(key).value());
		}
	}

	public static ResourceLocation getResource(String name) {
		return new ResourceLocation(Embers.MODID, name);
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
