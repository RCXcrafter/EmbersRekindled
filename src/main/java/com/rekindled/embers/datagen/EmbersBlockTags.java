package com.rekindled.embers.datagen;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class EmbersBlockTags extends BlockTagsProvider {

	public static final TagKey<Block> EMITTER_CONNECTION = BlockTags.create(new ResourceLocation(Embers.MODID, "emitter_connection"));
	public static final TagKey<Block> EMITTER_CONNECTION_FLOOR = BlockTags.create(new ResourceLocation(Embers.MODID, "emitter_connection/floor"));
	public static final TagKey<Block> EMITTER_CONNECTION_CEILING = BlockTags.create(new ResourceLocation(Embers.MODID, "emitter_connection/ceiling"));

	public static final TagKey<Block> ITEM_PIPE_CONNECTION = BlockTags.create(new ResourceLocation(Embers.MODID, "item_pipe_connection"));
	public static final TagKey<Block> ITEM_PIPE_CONNECTION_TOGGLEABLE = BlockTags.create(new ResourceLocation(Embers.MODID, "item_pipe_connection/toggleable"));

	public static final TagKey<Block> FLUID_PIPE_CONNECTION = BlockTags.create(new ResourceLocation(Embers.MODID, "fluid_pipe_connection"));
	public static final TagKey<Block> FLUID_PIPE_CONNECTION_TOGGLEABLE = BlockTags.create(new ResourceLocation(Embers.MODID, "fluid_pipe_connection/toggleable"));

	public static final TagKey<Block> MECH_CORE_PROXYABLE = BlockTags.create(new ResourceLocation(Embers.MODID, "mech_core_proxyable"));

	public EmbersBlockTags(DataGenerator gen, ExistingFileHelper existingFileHelper) {
		super(gen, Embers.MODID, existingFileHelper);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addTags() {
		tag(EMITTER_CONNECTION).add(Blocks.LEVER, Blocks.LADDER, Blocks.IRON_BARS, Blocks.TRIPWIRE_HOOK, Blocks.WALL_TORCH, Blocks.SOUL_WALL_TORCH, Blocks.REDSTONE_WALL_TORCH, Blocks.BELL, Blocks.LANTERN, Blocks.SOUL_LANTERN, Blocks.END_ROD, Blocks.LIGHTNING_ROD, Blocks.CHAIN)
		.add(RegistryManager.EMBER_DIAL.get(), RegistryManager.CAMINITE_LEVER.get(), RegistryManager.EMBER_EMITTER.get(), RegistryManager.EMBER_RECEIVER.get())
		.addTags(Tags.Blocks.GLASS_PANES, BlockTags.BUTTONS, Tags.Blocks.FENCES, BlockTags.WALLS, BlockTags.WALL_SIGNS)
		.addTags(EMITTER_CONNECTION_FLOOR, EMITTER_CONNECTION_CEILING);
		tag(EMITTER_CONNECTION_FLOOR).add(Blocks.TORCH, Blocks.SOUL_TORCH, Blocks.REDSTONE_TORCH, Blocks.POINTED_DRIPSTONE)
		.addTags(BlockTags.SIGNS);
		tag(EMITTER_CONNECTION_CEILING).add(Blocks.POINTED_DRIPSTONE);

		tag(ITEM_PIPE_CONNECTION).addTag(ITEM_PIPE_CONNECTION_TOGGLEABLE);
		tag(ITEM_PIPE_CONNECTION_TOGGLEABLE).add(RegistryManager.ITEM_PIPE.get(), RegistryManager.ITEM_EXTRACTOR.get());

		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
				RegistryManager.LEAD_ORE.get(),
				RegistryManager.DEEPSLATE_LEAD_ORE.get(),
				RegistryManager.RAW_LEAD_BLOCK.get(),
				RegistryManager.LEAD_BLOCK.get(),
				RegistryManager.CAMINITE_BRICKS.get(),
				RegistryManager.COPPER_CELL.get(),
				RegistryManager.CREATIVE_EMBER.get(),
				RegistryManager.EMBER_DIAL.get(),
				RegistryManager.ITEM_DIAL.get(),
				RegistryManager.FLUID_DIAL.get(),
				RegistryManager.EMBER_EMITTER.get(),
				RegistryManager.EMBER_RECEIVER.get(),
				RegistryManager.ITEM_PIPE.get(),
				RegistryManager.ITEM_EXTRACTOR.get(),
				RegistryManager.EMBER_BORE.get(),
				RegistryManager.EMBER_BORE_EDGE.get(),
				RegistryManager.MECHANICAL_CORE.get(),
				RegistryManager.EMBER_ACTIVATOR.get(),
				RegistryManager.MELTER.get(),
				RegistryManager.FLUID_PIPE.get(),
				RegistryManager.FLUID_EXTRACTOR.get(),
				RegistryManager.FLUID_VESSEL.get(),
				RegistryManager.STAMPER.get(),
				RegistryManager.STAMP_BASE.get(),
				RegistryManager.BIN.get());

		tag(BlockTags.NEEDS_IRON_TOOL).add(
				RegistryManager.LEAD_ORE.get(),
				RegistryManager.DEEPSLATE_LEAD_ORE.get(),
				RegistryManager.RAW_LEAD_BLOCK.get(),
				RegistryManager.LEAD_BLOCK.get());

		tag(MECH_CORE_PROXYABLE).add(
				RegistryManager.EMBER_BORE.get(),
				RegistryManager.EMBER_ACTIVATOR.get());

		tag(FLUID_PIPE_CONNECTION).addTag(FLUID_PIPE_CONNECTION_TOGGLEABLE);
		tag(FLUID_PIPE_CONNECTION_TOGGLEABLE).add(RegistryManager.FLUID_PIPE.get(), RegistryManager.FLUID_EXTRACTOR.get());
	}
}
