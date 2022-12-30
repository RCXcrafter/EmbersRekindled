package com.rekindled.embers.datagen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.rekindled.embers.Embers;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.AddFeaturesBiomeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EmbersBiomeModifiers extends JsonCodecProvider<BiomeModifier> {

	public EmbersBiomeModifiers(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, existingFileHelper, Embers.MODID, RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.builtinCopy()), PackType.SERVER_DATA, "forge/" + ForgeRegistries.Keys.BIOME_MODIFIERS.location().getPath(), BiomeModifier.DIRECT_CODEC, new HashMap<ResourceLocation, BiomeModifier>());
	}

	@Override
	public void run(final CachedOutput cache) throws IOException {
		Registry<Biome> biomeRegistry = ((RegistryOps<JsonElement>) dynamicOps).registry(ForgeRegistries.Keys.BIOMES).get();
		HolderSet.Named<Biome> overworldBiomes = new HolderSet.Named<>(biomeRegistry, BiomeTags.IS_OVERWORLD);

		this.entries.put(getResource("add_lead_ore"), new AddFeaturesBiomeModifier(overworldBiomes, HolderSet.direct(getHolder(EmbersPlacedFeatures.ORE_LEAD_PLACER)), Decoration.UNDERGROUND_ORES));
		super.run(cache);
	}

	public Holder<PlacedFeature> getHolder(RegistryObject<PlacedFeature> holder) {
		Optional<? extends Registry<PlacedFeature>> registryOptional = ((RegistryOps<JsonElement>) dynamicOps).registry(Registry.PLACED_FEATURE_REGISTRY);
		ResourceKey<PlacedFeature> key = holder.getKey();
		if (registryOptional.isPresent() && key != null) {
			return registryOptional.get().getOrCreateHolderOrThrow(key);
		}
		return null;
	}

	public ResourceLocation getResource(String name) {
		return new ResourceLocation(Embers.MODID, name);
	}
}
