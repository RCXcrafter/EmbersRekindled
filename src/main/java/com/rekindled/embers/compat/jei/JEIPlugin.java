package com.rekindled.embers.compat.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.recipe.AlchemyRecipe;
import com.rekindled.embers.recipe.BoilingRecipe;
import com.rekindled.embers.recipe.BoringRecipe;
import com.rekindled.embers.recipe.CatalysisCombustionRecipe;
import com.rekindled.embers.recipe.EmberActivationRecipe;
import com.rekindled.embers.recipe.GaseousFuelRecipe;
import com.rekindled.embers.recipe.IDawnstoneAnvilRecipe;
import com.rekindled.embers.recipe.IVisuallySplitRecipe;
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
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
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
	public static final RecipeType<IDawnstoneAnvilRecipe> DAWNSTONE_ANVIL = RecipeType.create(Embers.MODID, "dawnstone_anvil", IDawnstoneAnvilRecipe.class);

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
		registry.addRecipeCategories(new DawnstoneAnvilCategory(guiHelper));
	}

	public static <C extends Container, T extends Recipe<C>> void addRecipes(IRecipeRegistration register, RecipeManager manager, RecipeType<T> jeiType, net.minecraft.world.item.crafting.RecipeType<T> type) {
		List<T> recipes = manager.getAllRecipesFor(type);
		List<T> visualRecipes = new ArrayList<T>();
		for (T recipe : recipes) {
			if (recipe instanceof IVisuallySplitRecipe splitter) {
				visualRecipes.addAll(splitter.getVisualRecipes());
			} else {
				visualRecipes.add(recipe);
			}
		}
		register.addRecipes(jeiType, visualRecipes);
	}

	@SuppressWarnings("resource")
	@Override
	public void registerRecipes(IRecipeRegistration register) {
		assert Minecraft.getInstance().level != null;
		RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();

		addRecipes(register, manager, BORING, RegistryManager.BORING.get());

		addRecipes(register, manager, EMBER_ACTIVATION, RegistryManager.EMBER_ACTIVATION.get());

		List<MeltingRecipe> meltingRecipes = manager.getAllRecipesFor(RegistryManager.MELTING.get());
		register.addRecipes(MELTING, meltingRecipes);

		List<MeltingRecipe> meltingBonusRecipes = new ArrayList<MeltingRecipe>();
		for (MeltingRecipe recipe : meltingRecipes) {
			if (!recipe.getBonus().isEmpty())
				meltingBonusRecipes.add(recipe);
		}
		register.addRecipes(MELTING_BONUS, meltingBonusRecipes);

		addRecipes(register, manager, STAMPING, RegistryManager.STAMPING.get());

		addRecipes(register, manager, MIXING, RegistryManager.MIXING.get());

		addRecipes(register, manager, METAL_COEFFICIENT, RegistryManager.METAL_COEFFICIENT.get());

		addRecipes(register, manager, ALCHEMY, RegistryManager.ALCHEMY.get());

		addRecipes(register, manager, BOILING, RegistryManager.BOILING.get());

		addRecipes(register, manager, GASEOUS_FUEL, RegistryManager.GASEOUS_FUEL.get());

		addRecipes(register, manager, CATALYSIS_COMBUSTION, RegistryManager.CATALYSIS_COMBUSTION.get());

		addRecipes(register, manager, DAWNSTONE_ANVIL, RegistryManager.DAWNSTONE_ANVIL_RECIPE.get());
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
		registry.addRecipeCatalyst(new ItemStack(RegistryManager.DAWNSTONE_ANVIL.get()), DAWNSTONE_ANVIL);
	}
}
