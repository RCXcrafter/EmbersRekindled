package com.rekindled.embers.compat.jei;

import java.util.ArrayList;
import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.recipe.AlchemyRecipe;
import com.rekindled.embers.recipe.BoilingRecipe;
import com.rekindled.embers.recipe.BoringRecipe;
import com.rekindled.embers.recipe.CatalysisCombustionRecipe;
import com.rekindled.embers.recipe.EmberActivationRecipe;
import com.rekindled.embers.recipe.GaseousFuelRecipe;
import com.rekindled.embers.recipe.MeltingRecipe;
import com.rekindled.embers.recipe.MetalCoefficientRecipe;
import com.rekindled.embers.recipe.MixingRecipe;
import com.rekindled.embers.recipe.StampingRecipe;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

	public static ResourceLocation pluginID = new ResourceLocation(Embers.MODID, "jei_plugin");

	public static final RecipeType<BoringRecipe> BORING = RecipeType.create(Embers.MODID, "boring", BoringRecipe.class);
	public static final RecipeType<EmberActivationRecipe> EMBER_ACTIVATION = RecipeType.create(Embers.MODID, "ember_activation", EmberActivationRecipe.class);
	public static final RecipeType<MeltingRecipe> MELTING = RecipeType.create(Embers.MODID, "melting", MeltingRecipe.class);
	public static final RecipeType<MeltingRecipe> MELTING_BONUS = RecipeType.create(Embers.MODID, "melting_bonus", MeltingRecipe.class);
	public static final RecipeType<StampingRecipe> STAMPING = RecipeType.create(Embers.MODID, "stamping", StampingRecipe.class);
	public static final RecipeType<MixingRecipe> MIXING = RecipeType.create(Embers.MODID, "mixing", MixingRecipe.class);
	public static final RecipeType<MetalCoefficientRecipe> METAL_COEFFICIENT = RecipeType.create(Embers.MODID, "metal_coefficient", MetalCoefficientRecipe.class);
	public static final RecipeType<AlchemyRecipe> ALCHEMY = RecipeType.create(Embers.MODID, "alchemy", AlchemyRecipe.class);
	public static final RecipeType<BoilingRecipe> BOILING = RecipeType.create(Embers.MODID, "boiling", BoilingRecipe.class);
	public static final RecipeType<GaseousFuelRecipe> GASEOUS_FUEL = RecipeType.create(Embers.MODID, "gaseous_fuel", GaseousFuelRecipe.class);
	public static final RecipeType<CatalysisCombustionRecipe> CATALYSIS_COMBUSTION = RecipeType.create(Embers.MODID, "catalysis_combustion", CatalysisCombustionRecipe.class);

	@Override
	public ResourceLocation getPluginUid() {
		return pluginID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

		registry.addRecipeCategories(new BoringCategory(guiHelper));
		registry.addRecipeCategories(new EmberActivationCategory(guiHelper));
		registry.addRecipeCategories(new MeltingCategory(guiHelper));
		registry.addRecipeCategories(new MeltingBonusCategory(guiHelper));
		registry.addRecipeCategories(new StampingCategory(guiHelper));
		registry.addRecipeCategories(new MixingCategory(guiHelper));
		registry.addRecipeCategories(new MetalCoefficientCategory(guiHelper));
		registry.addRecipeCategories(new AlchemyCategory(guiHelper));
		registry.addRecipeCategories(new BoilingCategory(guiHelper));
		registry.addRecipeCategories(new GaseousFuelCategory(guiHelper));
		registry.addRecipeCategories(new CatalysisCombustionCategory(guiHelper));
	}


	@SuppressWarnings("resource")
	@Override
	public void registerRecipes(IRecipeRegistration register) {
		assert Minecraft.getInstance().level != null;
		RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();

		List<BoringRecipe> boringRecipes = manager.getAllRecipesFor(RegistryManager.BORING.get());
		register.addRecipes(BORING, boringRecipes);

		List<EmberActivationRecipe> activationRecipes = manager.getAllRecipesFor(RegistryManager.EMBER_ACTIVATION.get());
		register.addRecipes(EMBER_ACTIVATION, activationRecipes);

		List<MeltingRecipe> meltingRecipes = manager.getAllRecipesFor(RegistryManager.MELTING.get());
		register.addRecipes(MELTING, meltingRecipes);

		List<MeltingRecipe> meltingBonusRecipes = new ArrayList<MeltingRecipe>();
		for (MeltingRecipe recipe : meltingRecipes) {
			if (!recipe.getBonus().isEmpty())
				meltingBonusRecipes.add(recipe);
		}
		register.addRecipes(MELTING_BONUS, meltingBonusRecipes);

		List<StampingRecipe> stampingRecipes = manager.getAllRecipesFor(RegistryManager.STAMPING.get());
		register.addRecipes(STAMPING, stampingRecipes);

		List<MixingRecipe> mixingRecipes = manager.getAllRecipesFor(RegistryManager.MIXING.get());
		register.addRecipes(MIXING, mixingRecipes);

		List<MetalCoefficientRecipe> coefficientRecipes = manager.getAllRecipesFor(RegistryManager.METAL_COEFFICIENT.get());
		register.addRecipes(METAL_COEFFICIENT, coefficientRecipes);

		List<AlchemyRecipe> alchemyRecipes = manager.getAllRecipesFor(RegistryManager.ALCHEMY.get());
		register.addRecipes(ALCHEMY, alchemyRecipes);

		List<BoilingRecipe> boilingRecipes = manager.getAllRecipesFor(RegistryManager.BOILING.get());
		register.addRecipes(BOILING, boilingRecipes);

		List<GaseousFuelRecipe> gaseousFuelRecipes = manager.getAllRecipesFor(RegistryManager.GASEOUS_FUEL.get());
		register.addRecipes(GASEOUS_FUEL, gaseousFuelRecipes);

		List<CatalysisCombustionRecipe> catalysisCombustionRecipes = manager.getAllRecipesFor(RegistryManager.CATALYSIS_COMBUSTION.get());
		register.addRecipes(CATALYSIS_COMBUSTION, catalysisCombustionRecipes);
	}


	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
		registry.addRecipeCatalyst(new ItemStack(RegistryManager.EMBER_BORE_ITEM.get()), BORING);
		registry.addRecipeCatalyst(new ItemStack(RegistryManager.EMBER_ACTIVATOR_ITEM.get()), EMBER_ACTIVATION);
		registry.addRecipeCatalyst(new ItemStack(RegistryManager.PRESSURE_REFINERY_ITEM.get()), EMBER_ACTIVATION);
		registry.addRecipeCatalyst(new ItemStack(RegistryManager.IGNEM_REACTOR_ITEM.get()), EMBER_ACTIVATION);
		registry.addRecipeCatalyst(new ItemStack(RegistryManager.MELTER_ITEM.get()), MELTING, MELTING_BONUS);
		registry.addRecipeCatalyst(new ItemStack(RegistryManager.GEOLOGIC_SEPARATOR_ITEM.get()), MELTING_BONUS);
		registry.addRecipeCatalyst(new ItemStack(RegistryManager.STAMPER_ITEM.get()), STAMPING);
		registry.addRecipeCatalyst(new ItemStack(RegistryManager.STAMP_BASE_ITEM.get()), STAMPING);
		registry.addRecipeCatalyst(new ItemStack(RegistryManager.MIXER_CENTRIFUGE_ITEM.get()), MIXING);
		registry.addRecipeCatalyst(new ItemStack(RegistryManager.PRESSURE_REFINERY_ITEM.get()), METAL_COEFFICIENT);
		registry.addRecipeCatalyst(new ItemStack(RegistryManager.HEARTH_COIL_ITEM.get()), RecipeTypes.SMELTING);
		registry.addRecipeCatalyst(new ItemStack(RegistryManager.ALCHEMY_PEDESTAL_ITEM.get()), ALCHEMY);
		registry.addRecipeCatalyst(new ItemStack(RegistryManager.ALCHEMY_TABLET_ITEM.get()), ALCHEMY);
		registry.addRecipeCatalyst(new ItemStack(RegistryManager.BEAM_CANNON_ITEM.get()), ALCHEMY);
		registry.addRecipeCatalyst(new ItemStack(RegistryManager.MINI_BOILER_ITEM.get()), BOILING);
		registry.addRecipeCatalyst(new ItemStack(RegistryManager.CATALYTIC_PLUG_ITEM.get()), GASEOUS_FUEL);
		registry.addRecipeCatalyst(new ItemStack(RegistryManager.WILDFIRE_STIRLING_ITEM.get()), GASEOUS_FUEL);
	}
}
