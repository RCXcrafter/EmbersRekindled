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

	public EmbersFluidTags(DataGenerator gen, ExistingFileHelper existingFileHelper) {
		super(gen, Embers.MODID, existingFileHelper);
	}

	@Override
	public void addTags() {
		tag(MOLTEN_IRON).add(RegistryManager.MOLTEN_IRON.FLUID.get());
	}
}
