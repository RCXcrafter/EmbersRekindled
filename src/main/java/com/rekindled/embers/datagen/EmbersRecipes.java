package com.rekindled.embers.datagen;

import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.recipe.BoringRecipeBuilder;
import com.rekindled.embers.recipe.EmberActivationRecipeBuilder;
import com.rekindled.embers.recipe.MeltingRecipeBuilder;
import com.rekindled.embers.recipe.StampingRecipeBuilder;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

public class EmbersRecipes extends RecipeProvider implements IConditionBuilder {

	public static final int INGOT_AMOUNT = 90;
	public static final int NUGGET_AMOUNT = INGOT_AMOUNT / 9;
	public static final int BLOCK_AMOUNT = INGOT_AMOUNT * 9;
	public static final int RAW_AMOUNT = NUGGET_AMOUNT * 12;
	public static final int ORE_AMOUNT = RAW_AMOUNT * 2;
	public static final int RAW_BLOCK_AMOUNT = RAW_AMOUNT * 9;
	public static final int PLATE_AMOUNT = INGOT_AMOUNT;

	public static String boringFolder = "boring";
	public static String activationFolder = "ember_activation";
	public static String meltingFolder = "melting";
	public static String stampingFolder = "stamping";

	public EmbersRecipes(DataGenerator gen) {
		super(gen);
	}

	@Override
	public void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		//boring
		BoringRecipeBuilder.create(RegistryManager.EMBER_CRYSTAL.get()).folder(boringFolder).weight(20).maxHeight(7).save(consumer);
		BoringRecipeBuilder.create(RegistryManager.EMBER_SHARD.get()).folder(boringFolder).weight(60).maxHeight(7).save(consumer);
		BoringRecipeBuilder.create(RegistryManager.EMBER_GRIT.get()).folder(boringFolder).weight(20).maxHeight(7).save(consumer);

		//activation
		EmberActivationRecipeBuilder.create(RegistryManager.EMBER_CRYSTAL.get()).folder(activationFolder).ember(2400).save(consumer);
		EmberActivationRecipeBuilder.create(RegistryManager.EMBER_SHARD.get()).folder(activationFolder).ember(400).save(consumer);
		EmberActivationRecipeBuilder.create(RegistryManager.EMBER_GRIT.get()).folder(activationFolder).ember(0).save(consumer);

		//metals
		fullOreRecipes("lead", ImmutableList.of(RegistryManager.LEAD_ORE_ITEM.get(), RegistryManager.DEEPSLATE_LEAD_ORE_ITEM.get(), RegistryManager.RAW_LEAD.get()), RegistryManager.MOLTEN_LEAD.FLUID.get(), RegistryManager.RAW_LEAD.get(), RegistryManager.RAW_LEAD_BLOCK_ITEM.get(), RegistryManager.LEAD_BLOCK_ITEM.get(), RegistryManager.LEAD_INGOT.get(), RegistryManager.LEAD_NUGGET.get(), RegistryManager.LEAD_PLATE.get(), consumer);

		ShapedRecipeBuilder.shaped(Items.COPPER_INGOT)
		.pattern("XXX")
		.pattern("XXX")
		.pattern("XXX")
		.define('X', itemTag("forge", "nuggets/copper"))
		.unlockedBy("has_nugget", has(itemTag("forge", "nuggets/copper")))
		.save(consumer, getResource("copper_nugget_to_ingot"));

		ShapelessRecipeBuilder.shapeless(RegistryManager.COPPER_NUGGET.get(), 9)
		.requires(itemTag("forge", "ingots/copper"))
		.group("")
		.unlockedBy("has_ingot", has(itemTag("forge", "ingots/copper")))
		.save(consumer, getResource("copper_ingot_to_nugget"));

		plateHammerRecipe("iron", RegistryManager.IRON_PLATE.get(), consumer);
		plateHammerRecipe("gold", RegistryManager.GOLD_PLATE.get(), consumer);
		plateHammerRecipe("copper", RegistryManager.COPPER_PLATE.get(), consumer);

		//melting and stamping
		fullOreMeltingStampingRecipes("iron", RegistryManager.MOLTEN_IRON.FLUID.get(), Items.IRON_INGOT, Items.IRON_NUGGET, RegistryManager.IRON_PLATE.get(), consumer);
		fullOreMeltingStampingRecipes("gold", RegistryManager.MOLTEN_GOLD.FLUID.get(), Items.GOLD_INGOT, Items.GOLD_NUGGET, RegistryManager.GOLD_PLATE.get(), consumer);
		fullOreMeltingStampingRecipes("copper", RegistryManager.MOLTEN_COPPER.FLUID.get(), Items.COPPER_INGOT, RegistryManager.COPPER_NUGGET.get(), RegistryManager.COPPER_PLATE.get(), consumer);

		//crafting
		ShapedRecipeBuilder.shaped(RegistryManager.EMBER_CRYSTAL.get())
		.pattern("XXX")
		.pattern("XXX")
		.define('X', RegistryManager.EMBER_SHARD.get())
		.unlockedBy("has_shard", has(RegistryManager.EMBER_SHARD.get()))
		.save(consumer, getResource("ember_shard_to_crystal"));
		ShapelessRecipeBuilder.shapeless(RegistryManager.EMBER_SHARD.get(), 9)
		.requires(RegistryManager.EMBER_CRYSTAL.get())
		.unlockedBy("has_crystal", has(RegistryManager.EMBER_CRYSTAL.get()))
		.save(consumer, getResource("ember_crystal_to_shard"));

		ShapelessRecipeBuilder.shapeless(RegistryManager.CAMINITE_BLEND.get(), 8)
		.requires(Items.CLAY_BALL)
		.requires(Items.CLAY_BALL)
		.requires(Items.CLAY_BALL)
		.requires(Items.CLAY_BALL)
		.requires(Tags.Items.SAND)
		.unlockedBy("has_clay", has(Items.CLAY_BALL))
		.save(consumer, getResource("caminite_blend"));
		ShapedRecipeBuilder.shaped(RegistryManager.RAW_CAMINITE_PLATE.get())
		.pattern("XX")
		.pattern("XX")
		.define('X', RegistryManager.CAMINITE_BLEND.get())
		.unlockedBy("has_caminite", has(RegistryManager.CAMINITE_BLEND.get()))
		.save(consumer, getResource("raw_caminite_plate"));
		ShapedRecipeBuilder.shaped(RegistryManager.RAW_INGOT_STAMP.get())
		.pattern(" X ")
		.pattern("X X")
		.pattern(" X ")
		.define('X', RegistryManager.CAMINITE_BLEND.get())
		.unlockedBy("has_caminite", has(RegistryManager.CAMINITE_BLEND.get()))
		.save(consumer, getResource("raw_ingot_stamp"));
		ShapedRecipeBuilder.shaped(RegistryManager.RAW_NUGGET_STAMP.get())
		.pattern("X X")
		.pattern("X X")
		.define('X', RegistryManager.CAMINITE_BLEND.get())
		.unlockedBy("has_caminite", has(RegistryManager.CAMINITE_BLEND.get()))
		.save(consumer, getResource("raw_nugget_stamp"));
		ShapedRecipeBuilder.shaped(RegistryManager.RAW_PLATE_STAMP.get())
		.pattern("X X")
		.pattern("   ")
		.pattern("X X")
		.define('X', RegistryManager.CAMINITE_BLEND.get())
		.unlockedBy("has_caminite", has(RegistryManager.CAMINITE_BLEND.get()))
		.save(consumer, getResource("raw_plate_stamp"));
		
		ShapedRecipeBuilder.shaped(RegistryManager.CAMINITE_BRICKS.get())
		.pattern("XX")
		.pattern("XX")
		.define('X', RegistryManager.CAMINITE_BRICK.get())
		.unlockedBy("has_brick", has(RegistryManager.CAMINITE_BRICK.get()))
		.save(consumer, getResource("caminite_bricks"));

		SimpleCookingRecipeBuilder.smelting(Ingredient.of(RegistryManager.CAMINITE_BLEND.get()), RegistryManager.CAMINITE_BRICK.get(), 0.1F, 200)
		.unlockedBy("has_caminite", has(RegistryManager.CAMINITE_BLEND.get())).save(consumer, getResource("caminite_brick"));
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(RegistryManager.RAW_CAMINITE_PLATE.get()), RegistryManager.CAMINITE_PLATE.get(), 0.1F, 200)
		.unlockedBy("has_raw_plate", has(RegistryManager.RAW_CAMINITE_PLATE.get())).save(consumer, getResource("caminite_plate"));
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(RegistryManager.RAW_INGOT_STAMP.get()), RegistryManager.INGOT_STAMP.get(), 0.1F, 200)
		.unlockedBy("has_raw_ingot_stamp", has(RegistryManager.RAW_INGOT_STAMP.get())).save(consumer, getResource("ingot_stamp"));
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(RegistryManager.RAW_NUGGET_STAMP.get()), RegistryManager.NUGGET_STAMP.get(), 0.1F, 200)
		.unlockedBy("has_raw_nugget_stamp", has(RegistryManager.RAW_NUGGET_STAMP.get())).save(consumer, getResource("nugget_stamp"));
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(RegistryManager.RAW_PLATE_STAMP.get()), RegistryManager.PLATE_STAMP.get(), 0.1F, 200)
		.unlockedBy("has_raw_plate_stamp", has(RegistryManager.RAW_PLATE_STAMP.get())).save(consumer, getResource("plate_stamp"));

		ShapedRecipeBuilder.shaped(RegistryManager.TINKER_HAMMER.get())
		.pattern("IBI")
		.pattern("ISI")
		.pattern(" S ")
		.define('B', itemTag("forge", "ingots/lead"))
		.define('I', itemTag("forge", "ingots/iron"))
		.define('S', Tags.Items.RODS_WOODEN)
		.unlockedBy("has_lead", has(itemTag("forge", "ingots/lead")))
		.save(consumer, getResource("tinker_hammer"));

		ShapedRecipeBuilder.shaped(RegistryManager.MECHANICAL_CORE.get())
		.pattern("IBI")
		.pattern(" P ")
		.pattern("I I")
		.define('B', itemTag("forge", "plates/lead"))
		.define('I', itemTag("forge", "ingots/iron"))
		.define('P', ItemTags.COMPASSES)
		.unlockedBy("has_lead_plate", has(itemTag("forge", "plates/lead")))
		.save(consumer, getResource("mechanical_core"));

		ShapedRecipeBuilder.shaped(RegistryManager.EMBER_BORE.get())
		.pattern("YCY")
		.pattern("YBY")
		.pattern("III")
		.define('B', RegistryManager.MECHANICAL_CORE.get())
		.define('I', itemTag("forge", "ingots/iron"))
		.define('C', itemTag("forge", "ingots/copper"))
		.define('Y', RegistryManager.CAMINITE_BRICKS.get())
		.unlockedBy("has_mech_core", has(RegistryManager.MECHANICAL_CORE.get()))
		.save(consumer, getResource("ember_bore"));

		ShapedRecipeBuilder.shaped(RegistryManager.CAMINITE_LEVER.get(), 4)
		.pattern("S")
		.pattern("P")
		.define('S', Tags.Items.RODS_WOODEN)
		.define('P', RegistryManager.CAMINITE_PLATE.get())
		.unlockedBy("has_caminite_plate", has(RegistryManager.CAMINITE_PLATE.get()))
		.save(consumer, getResource("caminite_lever"));

		ShapedRecipeBuilder.shaped(RegistryManager.EMBER_EMITTER.get(), 4)
		.pattern(" C ")
		.pattern(" C ")
		.pattern("IPI")
		.define('I', itemTag("forge", "ingots/iron"))
		.define('C', itemTag("forge", "ingots/copper"))
		.define('P', RegistryManager.CAMINITE_PLATE.get())
		.unlockedBy("has_caminite_plate", has(RegistryManager.CAMINITE_PLATE.get()))
		.save(consumer, getResource("ember_emitter"));

		ShapedRecipeBuilder.shaped(RegistryManager.EMBER_RECEIVER.get(), 4)
		.pattern("I I")
		.pattern("CPC")
		.define('I', itemTag("forge", "ingots/iron"))
		.define('C', itemTag("forge", "ingots/copper"))
		.define('P', RegistryManager.CAMINITE_PLATE.get())
		.unlockedBy("has_caminite_plate", has(RegistryManager.CAMINITE_PLATE.get()))
		.save(consumer, getResource("ember_receiver"));

		ShapedRecipeBuilder.shaped(RegistryManager.EMBER_ACTIVATOR.get())
		.pattern("CCC")
		.pattern("CCC")
		.pattern("IFI")
		.define('I', itemTag("forge", "plates/iron"))
		.define('C', itemTag("forge", "ingots/copper"))
		.define('F', Items.FURNACE)
		.unlockedBy("has_iron_plate", has(itemTag("forge", "plates/iron")))
		.save(consumer, getResource("ember_activator"));

		ShapedRecipeBuilder.shaped(RegistryManager.COPPER_CELL.get())
		.pattern("BIB")
		.pattern("ICI")
		.pattern("BIB")
		.define('I', itemTag("forge", "ingots/iron"))
		.define('C', itemTag("forge", "storage_blocks/copper"))
		.define('B', RegistryManager.CAMINITE_BRICKS.get())
		.unlockedBy("has_caminite_bricks", has(RegistryManager.CAMINITE_BRICKS.get()))
		.save(consumer, getResource("copper_cell"));

		ShapedRecipeBuilder.shaped(RegistryManager.MELTER.get())
		.pattern("BPB")
		.pattern("BCB")
		.pattern("IFI")
		.define('I', itemTag("forge", "ingots/iron"))
		.define('C', itemTag("forge", "ingots/copper"))
		.define('P', RegistryManager.CAMINITE_PLATE.get())
		.define('B', RegistryManager.CAMINITE_BRICKS.get())
		.define('F', Items.FURNACE)
		.unlockedBy("has_caminite_bricks", has(RegistryManager.CAMINITE_BRICKS.get()))
		.save(consumer, getResource("melter"));

		ShapedRecipeBuilder.shaped(RegistryManager.FLUID_VESSEL.get())
		.pattern("B B")
		.pattern("P P")
		.pattern("BIB")
		.define('I', itemTag("forge", "ingots/iron"))
		.define('P', itemTag("forge", "plates/iron"))
		.define('B', RegistryManager.CAMINITE_BRICK.get())
		.unlockedBy("has_caminite_brick", has(RegistryManager.CAMINITE_BRICK.get()))
		.save(consumer, getResource("fluid_vessel"));

		ShapedRecipeBuilder.shaped(RegistryManager.STAMPER.get())
		.pattern("XCX")
		.pattern("XBX")
		.pattern("X X")
		.define('B', itemTag("forge", "storage_blocks/iron"))
		.define('C', itemTag("forge", "ingots/copper"))
		.define('X', RegistryManager.CAMINITE_BRICK.get())
		.unlockedBy("has_iron_block", has(itemTag("forge", "storage_blocks/iron")))
		.save(consumer, getResource("stamper"));

		ShapedRecipeBuilder.shaped(RegistryManager.STAMP_BASE.get())
		.pattern("I I")
		.pattern("XBX")
		.define('I', itemTag("forge", "ingots/iron"))
		.define('B', Items.BUCKET)
		.define('X', RegistryManager.CAMINITE_BRICK.get())
		.unlockedBy("has_bucket", has(Items.BUCKET))
		.save(consumer, getResource("stamp_base"));

		ShapedRecipeBuilder.shaped(RegistryManager.EMBER_DIAL.get())
		.pattern("P")
		.pattern("C")
		.define('P', Items.PAPER)
		.define('C', itemTag("forge", "plates/copper"))
		.unlockedBy("has_copper_plate", has(itemTag("forge", "plates/copper")))
		.save(consumer, getResource("ember_dial"));
		ShapedRecipeBuilder.shaped(RegistryManager.ITEM_DIAL.get())
		.pattern("P")
		.pattern("L")
		.define('P', Items.PAPER)
		.define('L', itemTag("forge", "plates/lead"))
		.unlockedBy("has_lead_plate", has(itemTag("forge", "plates/lead")))
		.save(consumer, getResource("item_dial"));
		ShapedRecipeBuilder.shaped(RegistryManager.FLUID_DIAL.get())
		.pattern("P")
		.pattern("I")
		.define('P', Items.PAPER)
		.define('I', itemTag("forge", "plates/iron"))
		.unlockedBy("has_iron_plate", has(itemTag("forge", "plates/iron")))
		.save(consumer, getResource("fluid_dial"));

		ShapedRecipeBuilder.shaped(RegistryManager.FLUID_PIPE.get(), 8)
		.pattern("IPI")
		.define('I', itemTag("forge", "ingots/iron"))
		.define('P', itemTag("forge", "plates/iron"))
		.unlockedBy("has_iron_plate", has(itemTag("forge", "plates/iron")))
		.save(consumer, getResource("fluid_pipe"));
		ShapedRecipeBuilder.shaped(RegistryManager.ITEM_PIPE.get(), 8)
		.pattern("IPI")
		.define('I', itemTag("forge", "ingots/lead"))
		.define('P', itemTag("forge", "plates/lead"))
		.unlockedBy("has_lead_plate", has(itemTag("forge", "plates/lead")))
		.save(consumer, getResource("item_pipe"));

		ShapedRecipeBuilder.shaped(RegistryManager.FLUID_EXTRACTOR.get())
		.pattern(" R ")
		.pattern("PBP")
		.pattern(" R ")
		.define('P', RegistryManager.FLUID_PIPE.get())
		.define('B', RegistryManager.CAMINITE_PLATE.get())
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_fluid_pipe", has(RegistryManager.FLUID_PIPE.get()))
		.save(consumer, getResource("fluid_extractor"));
		ShapedRecipeBuilder.shaped(RegistryManager.ITEM_EXTRACTOR.get())
		.pattern(" R ")
		.pattern("PBP")
		.pattern(" R ")
		.define('P', RegistryManager.ITEM_PIPE.get())
		.define('B', RegistryManager.CAMINITE_PLATE.get())
		.define('R', Tags.Items.DUSTS_REDSTONE)
		.unlockedBy("has_item_pipe", has(RegistryManager.ITEM_PIPE.get()))
		.save(consumer, getResource("item_extractor"));
		
		ShapedRecipeBuilder.shaped(RegistryManager.BIN.get())
		.pattern("I I")
		.pattern("I I")
		.pattern("IPI")
		.define('I', itemTag("forge", "ingots/lead"))
		.define('P', itemTag("forge", "plates/lead"))
		.unlockedBy("has_lead_plate", has(itemTag("forge", "plates/lead")))
		.save(consumer, getResource("bin"));
	}

	public void fullOreRecipes(String name, ImmutableList<ItemLike> ores, Fluid fluid, Item raw, Item rawBlock, Item block, Item ingot, Item nugget, Item plate, Consumer<FinishedRecipe> consumer) {
		fullMetalRecipes(name, fluid, block, ingot, nugget, plate, consumer);

		ShapedRecipeBuilder.shaped(rawBlock)
		.pattern("XXX")
		.pattern("XYX")
		.pattern("XXX")
		.define('X', itemTag("forge", "raw_materials/" + name))
		.define('Y', raw)
		.unlockedBy("has_raw", has(raw))
		.save(consumer, getResource(name + "_raw_to_raw_block"));

		ShapelessRecipeBuilder.shapeless(raw, 9)
		.requires(rawBlock)
		.unlockedBy("has_block", has(rawBlock))
		.save(consumer, getResource(name + "_raw_block_to_raw"));

		oreSmelting(consumer, ores, ingot, 0.7F, 200, name + "_ingot");
		oreBlasting(consumer, ores, ingot, 0.7F, 100, name + "_ingot");
	}

	public void fullMetalRecipes(String name, Fluid fluid, Item block, Item ingot, Item nugget, Item plate, Consumer<FinishedRecipe> consumer) {
		fullMeltingStampingRecipes(name, fluid, ingot, nugget, plate, consumer);
		blockIngotNuggetCompression(name, block, ingot, nugget, consumer);
		plateHammerRecipe(name, plate, consumer);
	}

	public void plateHammerRecipe(String name, Item plate, Consumer<FinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapeless(plate)
		.requires(itemTag("forge", "ingots/" + name))
		.requires(itemTag("forge", "ingots/" + name))
		.requires(EmbersItemTags.TINKER_HAMMER)
		.unlockedBy("has_ingot", has(itemTag("forge", "ingots/" + name)))
		.save(consumer, getResource(name + "_plate_hammering"));
	}

	public void fullOreMeltingStampingRecipes(String name, Fluid fluid, Item ingot, Item nugget, Item plate, Consumer<FinishedRecipe> consumer) {
		OreMeltingRecipes(name, fluid, consumer);
		fullMeltingStampingRecipes(name, fluid, ingot, nugget, plate, consumer);
	}

	public void OreMeltingRecipes(String name, Fluid fluid, Consumer<FinishedRecipe> consumer) {
		MeltingRecipeBuilder.create(itemTag("forge", "raw_materials/" + name)).domain(Embers.MODID).folder(meltingFolder).output(fluid, RAW_AMOUNT).save(consumer);
		MeltingRecipeBuilder.create(itemTag("forge", "ores/" + name)).domain(Embers.MODID).folder(meltingFolder).output(fluid, ORE_AMOUNT).save(consumer);
		MeltingRecipeBuilder.create(itemTag("forge", "storage_blocks/raw_" + name)).domain(Embers.MODID).folder(meltingFolder).output(fluid, RAW_BLOCK_AMOUNT).save(consumer);
	}

	public void fullMeltingStampingRecipes(String name, Fluid fluid, Item ingot, Item nugget, Item plate, Consumer<FinishedRecipe> consumer) {
		//melting
		MeltingRecipeBuilder.create(itemTag("forge", "ingots/" + name)).domain(Embers.MODID).folder(meltingFolder).output(fluid, INGOT_AMOUNT).save(consumer);
		MeltingRecipeBuilder.create(itemTag("forge", "nuggets/" + name)).domain(Embers.MODID).folder(meltingFolder).output(fluid, NUGGET_AMOUNT).save(consumer);
		MeltingRecipeBuilder.create(itemTag("forge", "storage_blocks/" + name)).domain(Embers.MODID).folder(meltingFolder).output(fluid, BLOCK_AMOUNT).save(consumer);
		MeltingRecipeBuilder.create(itemTag("forge", "plates/" + name)).domain(Embers.MODID).folder(meltingFolder).output(fluid, PLATE_AMOUNT).save(consumer);
		//stamping
		StampingRecipeBuilder.create(ingot).domain(Embers.MODID).folder(stampingFolder).stamp(RegistryManager.INGOT_STAMP.get()).fluid(fluidTag("forge", "molten_" + name), INGOT_AMOUNT).save(consumer);
		StampingRecipeBuilder.create(nugget).domain(Embers.MODID).folder(stampingFolder).stamp(RegistryManager.NUGGET_STAMP.get()).fluid(fluidTag("forge", "molten_" + name), NUGGET_AMOUNT).save(consumer);
		StampingRecipeBuilder.create(plate).domain(Embers.MODID).folder(stampingFolder).stamp(RegistryManager.PLATE_STAMP.get()).fluid(fluidTag("forge", "molten_" + name), PLATE_AMOUNT).save(consumer);
	}

	public void blockIngotNuggetCompression(String name, Item block, Item ingot, Item nugget, Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(block)
		.pattern("XXX")
		.pattern("XYX")
		.pattern("XXX")
		.define('X', itemTag("forge", "ingots/" + name))
		.define('Y', ingot)
		.unlockedBy("has_ingot", has(itemTag("forge", "ingots/" + name)))
		.save(consumer, getResource(name + "_ingot_to_block"));

		ShapelessRecipeBuilder.shapeless(ingot, 9)
		.requires(block)
		.unlockedBy("has_block", has(block))
		.save(consumer, getResource(name + "_block_to_ingot"));

		ShapedRecipeBuilder.shaped(ingot)
		.pattern("XXX")
		.pattern("XYX")
		.pattern("XXX")
		.define('X', itemTag("forge", "nuggets/" + name))
		.define('Y', nugget)
		.unlockedBy("has_nugget", has(itemTag("forge", "nuggets/" + name)))
		.save(consumer, getResource(name + "_nugget_to_ingot"));

		ShapelessRecipeBuilder.shapeless(nugget, 9)
		.requires(ingot)
		.unlockedBy("has_ingot", has(ingot))
		.save(consumer, getResource(name + "_ingot_to_nugget"));
	}

	public TagKey<Item> itemTag(String modId, String name) {
		return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(modId, name));
	}

	public TagKey<Fluid> fluidTag(String modId, String name) {
		return TagKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(modId, name));
	}

	public ResourceLocation getResource(String name) {
		return new ResourceLocation(Embers.MODID, name);
	}
}
