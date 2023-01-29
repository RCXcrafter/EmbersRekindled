package com.rekindled.embers.datagen;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;

import net.minecraft.data.DataGenerator;
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
	public static final TagKey<Fluid> MOLTEN_DAWNSTONE = FluidTags.create(new ResourceLocation("forge", "molten_dawnstone"));

	public EmbersFluidTags(DataGenerator gen, ExistingFileHelper existingFileHelper) {
		super(gen, Embers.MODID, existingFileHelper);
	}

	@Override
	public void addTags() {
		tag(MOLTEN_IRON).add(RegistryManager.MOLTEN_IRON.FLUID.get());
		tag(MOLTEN_GOLD).add(RegistryManager.MOLTEN_GOLD.FLUID.get());
		tag(MOLTEN_COPPER).add(RegistryManager.MOLTEN_COPPER.FLUID.get());
		tag(MOLTEN_LEAD).add(RegistryManager.MOLTEN_LEAD.FLUID.get());
		tag(MOLTEN_DAWNSTONE).add(RegistryManager.MOLTEN_DAWNSTONE.FLUID.get());
	}
}
