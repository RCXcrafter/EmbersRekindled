package com.rekindled.embers.datagen;

import com.rekindled.embers.Embers;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class EmbersItemTags extends ItemTagsProvider {

	public static final TagKey<Item> PIPE_UNCLOGGER = ItemTags.create(new ResourceLocation(Embers.MODID, "pipe_uncloggers"));

	public EmbersItemTags(DataGenerator gen, BlockTagsProvider blockTags, ExistingFileHelper existingFileHelper) {
		super(gen, blockTags, Embers.MODID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		tag(PIPE_UNCLOGGER).addTag(Tags.Items.RODS);
	}
}
