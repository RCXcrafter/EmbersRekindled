package com.rekindled.embers.datagen;

import java.util.function.Consumer;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.recipe.BoringRecipeBuilder;
import com.rekindled.embers.recipe.EmberActivationRecipeBuilder;
import com.rekindled.embers.recipe.MeltingRecipeBuilder;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

public class EmbersRecipes extends RecipeProvider implements IConditionBuilder {

	public static final int INGOT_AMOUNT = 90;
	public static final int NUGGET_AMOUNT = INGOT_AMOUNT / 9;
	public static final int BLOCK_AMOUNT = INGOT_AMOUNT * 9;
	public static final int ORE_AMOUNT = INGOT_AMOUNT * 2;
	public static final int RAW_AMOUNT = INGOT_AMOUNT * 2;
	public static final int RAW_BLOCK_AMOUNT = RAW_AMOUNT * 9;
	public static final int PLATE_AMOUNT = INGOT_AMOUNT;

	public EmbersRecipes(DataGenerator gen) {
		super(gen);
	}

	@Override
	public void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		String boringFolder = "boring";
		BoringRecipeBuilder.create(RegistryManager.EMBER_CRYSTAL.get()).folder(boringFolder).weight(20).maxHeight(7).save(consumer);
		BoringRecipeBuilder.create(RegistryManager.EMBER_SHARD.get()).folder(boringFolder).weight(60).maxHeight(7).save(consumer);
		BoringRecipeBuilder.create(RegistryManager.EMBER_GRIT.get()).folder(boringFolder).weight(20).maxHeight(7).save(consumer);

		String activationFolder = "ember_activation";
		EmberActivationRecipeBuilder.create(RegistryManager.EMBER_CRYSTAL.get()).folder(activationFolder).ember(2400).save(consumer);
		EmberActivationRecipeBuilder.create(RegistryManager.EMBER_SHARD.get()).folder(activationFolder).ember(400).save(consumer);
		EmberActivationRecipeBuilder.create(RegistryManager.EMBER_GRIT.get()).folder(activationFolder).ember(0).save(consumer);

		String meltingFolder = "melting";
		MeltingRecipeBuilder.create(Tags.Items.INGOTS_IRON).domain(Embers.MODID).folder(meltingFolder).output(RegistryManager.MOLTEN_IRON.FLUID.get(), INGOT_AMOUNT).save(consumer);
	}

	public ResourceLocation getResource(String name) {
		return new ResourceLocation(Embers.MODID, name);
	}
}
