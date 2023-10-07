package com.rekindled.embers.datagen;

import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.AddFeaturesBiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers.AddSpawnsBiomeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.holdersets.AndHolderSet;
import net.minecraftforge.registries.holdersets.NotHolderSet;
import net.minecraftforge.registries.holdersets.OrHolderSet;

public class EmbersBiomeModifiers {

	public static final ResourceKey<BiomeModifier> ORE_LEAD_KEY = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(Embers.MODID, "add_lead_ore"));
	public static final ResourceKey<BiomeModifier> ORE_SILVER_KEY = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(Embers.MODID, "add_silver_ore"));

	public static final ResourceKey<BiomeModifier> GOLEM_SPAWN = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(Embers.MODID, "add_golem_spawn"));

	public static void generate(BootstapContext<BiomeModifier> bootstrap) {
		HolderGetter<PlacedFeature> placed = bootstrap.lookup(Registries.PLACED_FEATURE);
		HolderGetter<Biome> biome = bootstrap.lookup(ForgeRegistries.Keys.BIOMES);
		HolderSet<Biome> overworldBiomes = biome.getOrThrow(BiomeTags.IS_OVERWORLD);
		List<HolderSet<Biome>> biomeBlackList = List.of(biome.getOrThrow(Tags.Biomes.IS_MUSHROOM), HolderSet.direct(biome.getOrThrow(Biomes.DEEP_DARK)));
		HolderSet<Biome> hostileSpawns = new AndHolderSet<Biome>(List.of(overworldBiomes, new NotHolderSetWrapper<Biome>(new OrHolderSet<Biome>(biomeBlackList))));

		bootstrap.register(ORE_LEAD_KEY, new AddFeaturesBiomeModifier(overworldBiomes, HolderSet.direct(placed.getOrThrow(EmbersPlacedFeatures.ORE_LEAD_KEY)), Decoration.UNDERGROUND_ORES));
		bootstrap.register(ORE_SILVER_KEY, new AddFeaturesBiomeModifier(overworldBiomes, HolderSet.direct(placed.getOrThrow(EmbersPlacedFeatures.ORE_SILVER_KEY)), Decoration.UNDERGROUND_ORES));

		bootstrap.register(GOLEM_SPAWN, new AddSpawnsBiomeModifier(hostileSpawns, List.of(new MobSpawnSettings.SpawnerData(RegistryManager.ANCIENT_GOLEM.get(), 15, 1, 1))));
	}

	//wow this is stupid
	public static class NotHolderSetWrapper<T> extends NotHolderSet<T> {
		public NotHolderSetWrapper(HolderSet<T> value) {
			super(null, value);
		}

		@Override
		public boolean canSerializeIn(HolderOwner<T> holderOwner) {
			return true;
		}
	}
}
