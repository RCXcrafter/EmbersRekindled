package com.rekindled.embers.datagen;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;

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

	//this tag is only for recipes
	public static final TagKey<Item> TINKER_HAMMER = ItemTags.create(new ResourceLocation(Embers.MODID, "tinker_hammer"));

	public static final TagKey<Item> PLATES = ItemTags.create(new ResourceLocation("forge", "plates"));
	public static final TagKey<Item> IRON_PLATE = ItemTags.create(new ResourceLocation("forge", "plates/iron"));
	public static final TagKey<Item> GOLD_PLATE = ItemTags.create(new ResourceLocation("forge", "plates/gold"));
	public static final TagKey<Item> COPPER_PLATE = ItemTags.create(new ResourceLocation("forge", "plates/copper"));

	public static final TagKey<Item> COPPER_NUGGET = ItemTags.create(new ResourceLocation("forge", "nuggets/copper"));

	public static final TagKey<Item> LEAD_ORE = ItemTags.create(new ResourceLocation("forge", "ores/lead"));
	public static final TagKey<Item> RAW_LEAD_BLOCK = ItemTags.create(new ResourceLocation("forge", "storage_blocks/raw_lead"));
	public static final TagKey<Item> LEAD_BLOCK = ItemTags.create(new ResourceLocation("forge", "storage_blocks/lead"));

	public static final TagKey<Item> RAW_LEAD = ItemTags.create(new ResourceLocation("forge", "raw_materials/lead"));
	public static final TagKey<Item> LEAD_INGOT = ItemTags.create(new ResourceLocation("forge", "ingots/lead"));
	public static final TagKey<Item> LEAD_NUGGET = ItemTags.create(new ResourceLocation("forge", "nuggets/lead"));
	public static final TagKey<Item> LEAD_PLATE = ItemTags.create(new ResourceLocation("forge", "plates/lead"));

	public static final TagKey<Item> CAMINITE_BRICK = ItemTags.create(new ResourceLocation("forge", "ingots/caminite_brick"));
	public static final TagKey<Item> ARCHAIC_BRICK = ItemTags.create(new ResourceLocation("forge", "ingots/archaic_brick"));

	public EmbersItemTags(DataGenerator gen, BlockTagsProvider blockTags, ExistingFileHelper existingFileHelper) {
		super(gen, blockTags, Embers.MODID, existingFileHelper);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addTags() {
		tag(PIPE_UNCLOGGER).addTag(Tags.Items.RODS);
		tag(TINKER_HAMMER).add(RegistryManager.TINKER_HAMMER.get());

		tag(Tags.Items.ORES).addTags(LEAD_ORE);
		tag(LEAD_ORE).add(RegistryManager.LEAD_ORE_ITEM.get()).add(RegistryManager.DEEPSLATE_LEAD_ORE_ITEM.get());

		tag(Tags.Items.ORES_IN_GROUND_STONE).add(RegistryManager.LEAD_ORE_ITEM.get());
		tag(Tags.Items.ORES_IN_GROUND_DEEPSLATE).add(RegistryManager.DEEPSLATE_LEAD_ORE_ITEM.get());

		tag(Tags.Items.STORAGE_BLOCKS).addTags(RAW_LEAD_BLOCK);
		tag(RAW_LEAD_BLOCK).add(RegistryManager.RAW_LEAD_BLOCK_ITEM.get());

		tag(Tags.Items.STORAGE_BLOCKS).addTags(LEAD_BLOCK);
		tag(LEAD_BLOCK).add(RegistryManager.LEAD_BLOCK_ITEM.get());

		tag(Tags.Items.RAW_MATERIALS).addTags(RAW_LEAD);
		tag(RAW_LEAD).add(RegistryManager.RAW_LEAD.get());

		tag(Tags.Items.INGOTS).addTags(LEAD_INGOT);
		tag(LEAD_INGOT).add(RegistryManager.LEAD_INGOT.get());

		tag(Tags.Items.NUGGETS).addTags(COPPER_NUGGET, LEAD_NUGGET);
		tag(COPPER_NUGGET).add(RegistryManager.COPPER_NUGGET.get());
		tag(LEAD_NUGGET).add(RegistryManager.LEAD_NUGGET.get());

		tag(PLATES).addTags(IRON_PLATE, GOLD_PLATE, COPPER_PLATE, LEAD_PLATE);
		tag(IRON_PLATE).add(RegistryManager.IRON_PLATE.get());
		tag(GOLD_PLATE).add(RegistryManager.GOLD_PLATE.get());
		tag(COPPER_PLATE).add(RegistryManager.COPPER_PLATE.get());
		tag(LEAD_PLATE).add(RegistryManager.LEAD_PLATE.get());

		tag(Tags.Items.INGOTS).addTags(CAMINITE_BRICK);
		tag(CAMINITE_BRICK).add(RegistryManager.CAMINITE_BRICK.get());
		tag(Tags.Items.INGOTS).addTags(ARCHAIC_BRICK);
		tag(ARCHAIC_BRICK).add(RegistryManager.ARCHAIC_BRICK.get());
	}
}
