package com.rekindled.embers.datagen;

import java.util.concurrent.CompletableFuture;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.ExistingFileHelper;

public class EmbersFluidTags extends FluidTagsProvider {

	public static final TagKey<Fluid> MOLTEN_IRON = FluidTags.create(new ResourceLocation("forge", "molten_iron"));
	public static final TagKey<Fluid> MOLTEN_GOLD = FluidTags.create(new ResourceLocation("forge", "molten_gold"));
	public static final TagKey<Fluid> MOLTEN_COPPER = FluidTags.create(new ResourceLocation("forge", "molten_copper"));
	public static final TagKey<Fluid> MOLTEN_LEAD = FluidTags.create(new ResourceLocation("forge", "molten_lead"));
	public static final TagKey<Fluid> MOLTEN_SILVER = FluidTags.create(new ResourceLocation("forge", "molten_silver"));
	public static final TagKey<Fluid> MOLTEN_DAWNSTONE = FluidTags.create(new ResourceLocation("forge", "molten_dawnstone"));

	public static final TagKey<Fluid> STEAM = FluidTags.create(new ResourceLocation("forge", "steam"));

	//compatibility fluids
	public static final TagKey<Fluid> MOLTEN_NICKEL = FluidTags.create(new ResourceLocation("forge", "molten_nickel"));
	public static final TagKey<Fluid> MOLTEN_TIN = FluidTags.create(new ResourceLocation("forge", "molten_tin"));
	public static final TagKey<Fluid> MOLTEN_ALUMINUM = FluidTags.create(new ResourceLocation("forge", "molten_aluminum"));
	public static final TagKey<Fluid> MOLTEN_BRONZE = FluidTags.create(new ResourceLocation("forge", "molten_bronze"));
	public static final TagKey<Fluid> MOLTEN_ELECTRUM = FluidTags.create(new ResourceLocation("forge", "molten_electrum"));

	//ingot tooltips
	public static final TagKey<Fluid> INGOT_TOOLTIP = FluidTags.create(new ResourceLocation(Embers.MODID, "ingot_tooltip"));
	public static final TagKey<Fluid> METAL_TOOLTIPS = FluidTags.create(new ResourceLocation("tconstruct", "tooltips/metal"));

	public EmbersFluidTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, Embers.MODID, existingFileHelper);
	}

	@Override
	public void addTags(HolderLookup.Provider provider) {
		tag(MOLTEN_IRON).add(RegistryManager.MOLTEN_IRON.FLUID.get());
		tag(MOLTEN_GOLD).add(RegistryManager.MOLTEN_GOLD.FLUID.get());
		tag(MOLTEN_COPPER).add(RegistryManager.MOLTEN_COPPER.FLUID.get());
		tag(MOLTEN_LEAD).add(RegistryManager.MOLTEN_LEAD.FLUID.get());
		tag(MOLTEN_SILVER).add(RegistryManager.MOLTEN_SILVER.FLUID.get());
		tag(MOLTEN_DAWNSTONE).add(RegistryManager.MOLTEN_DAWNSTONE.FLUID.get());

		tag(STEAM).add(RegistryManager.STEAM.FLUID.get());

		tag(MOLTEN_NICKEL).add(RegistryManager.MOLTEN_NICKEL.FLUID.get());
		tag(MOLTEN_TIN).add(RegistryManager.MOLTEN_TIN.FLUID.get());
		tag(MOLTEN_ALUMINUM).add(RegistryManager.MOLTEN_ALUMINUM.FLUID.get());
		tag(MOLTEN_BRONZE).add(RegistryManager.MOLTEN_BRONZE.FLUID.get());
		tag(MOLTEN_ELECTRUM).add(RegistryManager.MOLTEN_ELECTRUM.FLUID.get());

		tag(INGOT_TOOLTIP)
		.add(RegistryManager.MOLTEN_IRON.FLUID.get())
		.add(RegistryManager.MOLTEN_GOLD.FLUID.get())
		.add(RegistryManager.MOLTEN_COPPER.FLUID.get())
		.add(RegistryManager.MOLTEN_LEAD.FLUID.get())
		.add(RegistryManager.MOLTEN_SILVER.FLUID.get())
		.add(RegistryManager.MOLTEN_DAWNSTONE.FLUID.get())
		.add(RegistryManager.MOLTEN_NICKEL.FLUID.get())
		.add(RegistryManager.MOLTEN_TIN.FLUID.get())
		.add(RegistryManager.MOLTEN_ALUMINUM.FLUID.get())
		.add(RegistryManager.MOLTEN_BRONZE.FLUID.get())
		.add(RegistryManager.MOLTEN_ELECTRUM.FLUID.get())
		.addOptionalTag(METAL_TOOLTIPS.location());
	}
}
