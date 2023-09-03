package com.rekindled.embers.datagen;

import java.util.concurrent.CompletableFuture;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.RegistryManager.ToolSet;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class EmbersItemTags extends ItemTagsProvider {

	public static final TagKey<Item> PIPE_UNCLOGGER = ItemTags.create(new ResourceLocation(Embers.MODID, "pipe_uncloggers"));

	//this tag is only for recipes
	public static final TagKey<Item> TINKER_HAMMER = ItemTags.create(new ResourceLocation(Embers.MODID, "tinker_hammer"));

	public static final TagKey<Item> TOOLS_HAMMERS = ItemTags.create(new ResourceLocation("forge", "tools/hammers"));

	public static final TagKey<Item> ASPECTUS = ItemTags.create(new ResourceLocation(Embers.MODID, "aspectus"));
	public static final TagKey<Item> IRON_ASPECTUS = ItemTags.create(new ResourceLocation(Embers.MODID, "aspectus/iron"));
	public static final TagKey<Item> COPPER_ASPECTUS = ItemTags.create(new ResourceLocation(Embers.MODID, "aspectus/copper"));
	public static final TagKey<Item> LEAD_ASPECTUS = ItemTags.create(new ResourceLocation(Embers.MODID, "aspectus/lead"));
	public static final TagKey<Item> SILVER_ASPECTUS = ItemTags.create(new ResourceLocation(Embers.MODID, "aspectus/silver"));
	public static final TagKey<Item> DAWNSTONE_ASPECTUS = ItemTags.create(new ResourceLocation(Embers.MODID, "aspectus/dawnstone"));

	public static final TagKey<Item> PLATES = ItemTags.create(new ResourceLocation("forge", "plates"));
	public static final TagKey<Item> IRON_PLATE = ItemTags.create(new ResourceLocation("forge", "plates/iron"));
	//public static final TagKey<Item> GOLD_PLATE = ItemTags.create(new ResourceLocation("forge", "plates/gold"));
	public static final TagKey<Item> COPPER_PLATE = ItemTags.create(new ResourceLocation("forge", "plates/copper"));

	public static final TagKey<Item> COPPER_NUGGET = ItemTags.create(new ResourceLocation("forge", "nuggets/copper"));

	public static final TagKey<Item> LEAD_ORE = ItemTags.create(new ResourceLocation("forge", "ores/lead"));
	public static final TagKey<Item> RAW_LEAD_BLOCK = ItemTags.create(new ResourceLocation("forge", "storage_blocks/raw_lead"));
	public static final TagKey<Item> LEAD_BLOCK = ItemTags.create(new ResourceLocation("forge", "storage_blocks/lead"));

	public static final TagKey<Item> RAW_LEAD = ItemTags.create(new ResourceLocation("forge", "raw_materials/lead"));
	public static final TagKey<Item> LEAD_INGOT = ItemTags.create(new ResourceLocation("forge", "ingots/lead"));
	public static final TagKey<Item> LEAD_NUGGET = ItemTags.create(new ResourceLocation("forge", "nuggets/lead"));
	public static final TagKey<Item> LEAD_PLATE = ItemTags.create(new ResourceLocation("forge", "plates/lead"));

	public static final TagKey<Item> SILVER_ORE = ItemTags.create(new ResourceLocation("forge", "ores/silver"));
	public static final TagKey<Item> RAW_SILVER_BLOCK = ItemTags.create(new ResourceLocation("forge", "storage_blocks/raw_silver"));
	public static final TagKey<Item> SILVER_BLOCK = ItemTags.create(new ResourceLocation("forge", "storage_blocks/silver"));

	public static final TagKey<Item> RAW_SILVER = ItemTags.create(new ResourceLocation("forge", "raw_materials/silver"));
	public static final TagKey<Item> SILVER_INGOT = ItemTags.create(new ResourceLocation("forge", "ingots/silver"));
	public static final TagKey<Item> SILVER_NUGGET = ItemTags.create(new ResourceLocation("forge", "nuggets/silver"));
	public static final TagKey<Item> SILVER_PLATE = ItemTags.create(new ResourceLocation("forge", "plates/silver"));

	public static final TagKey<Item> DAWNSTONE_BLOCK = ItemTags.create(new ResourceLocation("forge", "storage_blocks/dawnstone"));
	public static final TagKey<Item> DAWNSTONE_INGOT = ItemTags.create(new ResourceLocation("forge", "ingots/dawnstone"));
	public static final TagKey<Item> DAWNSTONE_NUGGET = ItemTags.create(new ResourceLocation("forge", "nuggets/dawnstone"));
	public static final TagKey<Item> DAWNSTONE_PLATE = ItemTags.create(new ResourceLocation("forge", "plates/dawnstone"));

	public static final TagKey<Item> CAMINITE_BRICK = ItemTags.create(new ResourceLocation("forge", "ingots/caminite_brick"));
	public static final TagKey<Item> ARCHAIC_BRICK = ItemTags.create(new ResourceLocation("forge", "ingots/archaic_brick"));
	public static final TagKey<Item> ASH_DUST = ItemTags.create(new ResourceLocation("forge", "dusts/ash"));

	public static final TagKey<Item> WORLD_BOTTOM = ItemTags.create(new ResourceLocation(Embers.MODID, "world_bottom"));

	public static final TagKey<Item> PRISTINE_COPPER = ItemTags.create(new ResourceLocation(Embers.MODID, "pristine_copper"));
	public static final TagKey<Item> EXPOSED_COPPER = ItemTags.create(new ResourceLocation(Embers.MODID, "exposed_copper"));
	public static final TagKey<Item> WEATHERED_COPPER = ItemTags.create(new ResourceLocation(Embers.MODID, "weathered_copper"));
	public static final TagKey<Item> OXIDIZED_COPPER = ItemTags.create(new ResourceLocation(Embers.MODID, "oxidized_copper"));

	public EmbersItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, blockTagProvider, Embers.MODID, existingFileHelper);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(PIPE_UNCLOGGER).addTag(Tags.Items.RODS);
		tag(TINKER_HAMMER).add(RegistryManager.TINKER_HAMMER.get());

		tag(ASPECTUS).addTags(IRON_ASPECTUS, COPPER_ASPECTUS, LEAD_ASPECTUS, SILVER_ASPECTUS, DAWNSTONE_ASPECTUS);
		tag(IRON_ASPECTUS).add(RegistryManager.IRON_ASPECTUS.get());
		tag(COPPER_ASPECTUS).add(RegistryManager.COPPER_ASPECTUS.get());
		tag(LEAD_ASPECTUS).add(RegistryManager.LEAD_ASPECTUS.get());
		tag(SILVER_ASPECTUS).add(RegistryManager.SILVER_ASPECTUS.get());
		tag(DAWNSTONE_ASPECTUS).add(RegistryManager.DAWNSTONE_ASPECTUS.get());

		toolTags(RegistryManager.LEAD_TOOLS);
		toolTags(RegistryManager.SILVER_TOOLS);
		toolTags(RegistryManager.DAWNSTONE_TOOLS);
		tag(ItemTags.SWORDS).add(RegistryManager.TYRFING.get());

		tag(Tags.Items.ORES).addTags(LEAD_ORE, SILVER_ORE);
		tag(LEAD_ORE).add(RegistryManager.LEAD_ORE_ITEM.get()).add(RegistryManager.DEEPSLATE_LEAD_ORE_ITEM.get());
		tag(SILVER_ORE).add(RegistryManager.SILVER_ORE_ITEM.get()).add(RegistryManager.DEEPSLATE_SILVER_ORE_ITEM.get());

		tag(Tags.Items.ORES_IN_GROUND_STONE).add(RegistryManager.LEAD_ORE_ITEM.get());
		tag(Tags.Items.ORES_IN_GROUND_DEEPSLATE).add(RegistryManager.DEEPSLATE_LEAD_ORE_ITEM.get());
		tag(Tags.Items.ORES_IN_GROUND_STONE).add(RegistryManager.SILVER_ORE_ITEM.get());
		tag(Tags.Items.ORES_IN_GROUND_DEEPSLATE).add(RegistryManager.DEEPSLATE_SILVER_ORE_ITEM.get());

		tag(Tags.Items.STORAGE_BLOCKS).addTags(RAW_LEAD_BLOCK, RAW_SILVER_BLOCK);
		tag(RAW_LEAD_BLOCK).add(RegistryManager.RAW_LEAD_BLOCK_ITEM.get());
		tag(RAW_SILVER_BLOCK).add(RegistryManager.RAW_SILVER_BLOCK_ITEM.get());

		tag(Tags.Items.STORAGE_BLOCKS).addTags(LEAD_BLOCK, SILVER_BLOCK, DAWNSTONE_BLOCK);
		tag(LEAD_BLOCK).add(RegistryManager.LEAD_BLOCK_ITEM.get());
		tag(SILVER_BLOCK).add(RegistryManager.SILVER_BLOCK_ITEM.get());
		tag(DAWNSTONE_BLOCK).add(RegistryManager.DAWNSTONE_BLOCK_ITEM.get());

		tag(Tags.Items.RAW_MATERIALS).addTags(RAW_LEAD, RAW_SILVER);
		tag(RAW_LEAD).add(RegistryManager.RAW_LEAD.get());
		tag(RAW_SILVER).add(RegistryManager.RAW_SILVER.get());

		tag(Tags.Items.INGOTS).addTags(LEAD_INGOT, SILVER_INGOT, DAWNSTONE_INGOT);
		tag(LEAD_INGOT).add(RegistryManager.LEAD_INGOT.get());
		tag(SILVER_INGOT).add(RegistryManager.SILVER_INGOT.get());
		tag(DAWNSTONE_INGOT).add(RegistryManager.DAWNSTONE_INGOT.get());

		tag(Tags.Items.NUGGETS).addTags(COPPER_NUGGET, LEAD_NUGGET, SILVER_NUGGET, DAWNSTONE_NUGGET);
		tag(COPPER_NUGGET).add(RegistryManager.COPPER_NUGGET.get());
		tag(LEAD_NUGGET).add(RegistryManager.LEAD_NUGGET.get());
		tag(SILVER_NUGGET).add(RegistryManager.SILVER_NUGGET.get());
		tag(DAWNSTONE_NUGGET).add(RegistryManager.DAWNSTONE_NUGGET.get());

		tag(PLATES).addTags(IRON_PLATE, COPPER_PLATE, LEAD_PLATE, SILVER_PLATE, DAWNSTONE_PLATE);
		tag(IRON_PLATE).add(RegistryManager.IRON_PLATE.get());
		//tag(GOLD_PLATE).add(RegistryManager.GOLD_PLATE.get());
		tag(COPPER_PLATE).add(RegistryManager.COPPER_PLATE.get());
		tag(LEAD_PLATE).add(RegistryManager.LEAD_PLATE.get());
		tag(SILVER_PLATE).add(RegistryManager.SILVER_PLATE.get());
		tag(DAWNSTONE_PLATE).add(RegistryManager.DAWNSTONE_PLATE.get());

		tag(Tags.Items.INGOTS).addTags(CAMINITE_BRICK);
		tag(CAMINITE_BRICK).add(RegistryManager.CAMINITE_BRICK.get());
		tag(Tags.Items.INGOTS).addTags(ARCHAIC_BRICK);
		tag(ARCHAIC_BRICK).add(RegistryManager.ARCHAIC_BRICK.get());

		tag(Tags.Items.DUSTS).addTag(ASH_DUST);
		tag(ASH_DUST).add(RegistryManager.ASH.get());

		tag(ItemTags.PICKAXES).add(RegistryManager.CLOCKWORK_PICKAXE.get());
		tag(ItemTags.AXES).add(RegistryManager.CLOCKWORK_AXE.get());
		tag(Tags.Items.TOOLS).addTag(TOOLS_HAMMERS);
		tag(TOOLS_HAMMERS).add(RegistryManager.TINKER_HAMMER.get(), RegistryManager.GRANDHAMMER.get());

		copy(EmbersBlockTags.WORLD_BOTTOM, WORLD_BOTTOM);

		copy(EmbersBlockTags.PRISTINE_COPPER, PRISTINE_COPPER);
		copy(EmbersBlockTags.EXPOSED_COPPER, EXPOSED_COPPER);
		copy(EmbersBlockTags.WEATHERED_COPPER, WEATHERED_COPPER);
		copy(EmbersBlockTags.OXIDIZED_COPPER, OXIDIZED_COPPER);
	}

	public void toolTags(ToolSet set) {
		tag(ItemTags.SWORDS).add(set.SWORD.get());
		tag(ItemTags.SHOVELS).add(set.SHOVEL.get());
		tag(ItemTags.PICKAXES).add(set.PICKAXE.get());
		tag(ItemTags.AXES).add(set.AXE.get());
		tag(ItemTags.HOES).add(set.HOE.get());
		tag(ItemTags.TOOLS).add(set.SWORD.get(), set.SHOVEL.get(), set.PICKAXE.get(), set.AXE.get(), set.HOE.get());
	}
}
