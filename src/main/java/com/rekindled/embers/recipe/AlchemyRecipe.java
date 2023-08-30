package com.rekindled.embers.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.misc.AlchemyResult;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public class AlchemyRecipe implements Recipe<AlchemyContext> {

	public static final Serializer SERIALIZER = new Serializer();

	public final ResourceLocation id;

	public final Ingredient tablet;
	public final ArrayList<Ingredient> aspects;
	public final ArrayList<Ingredient> inputs;

	public final ItemStack output;
	public final ItemStack failure;

	public Long cachedSeed = null;
	public ArrayList<Ingredient> code = new ArrayList<Ingredient>();

	public AlchemyRecipe(ResourceLocation id, Ingredient tablet, ArrayList<Ingredient> aspects, ArrayList<Ingredient> inputs, ItemStack output, ItemStack failure) {
		this.id = id;
		this.tablet = tablet;
		this.aspects = aspects;
		this.inputs = inputs;
		this.output = output;
		this.failure = failure;
	}

	public ArrayList<Ingredient> getCode(long seed) {
		if (cachedSeed == null || cachedSeed != seed) {
			code.clear();
			Random rand = new Random(seed - id.getPath().hashCode());
			for (int i = 0; i < inputs.size(); i++) {
				code.add(aspects.get(rand.nextInt(aspects.size())));
			}
			cachedSeed = seed;
		}
		return code;
	}

	@Override
	public boolean matches(AlchemyContext context, Level pLevel) {
		if (!tablet.test(context.tablet) || inputs.size() != context.contents.size())
			return false;

		ArrayList<PedestalContents> remaining = new ArrayList<PedestalContents>(context.contents);
		for (int i = 0; i < inputs.size(); i++) {
			boolean matched = false;
			for (int j = 0; j < remaining.size(); j++) {
				if (inputs.get(i).test(remaining.get(j).input)) {
					matched = true;
					remaining.remove(j);
					break;
				}
			}
			if (!matched)
				return false;
		}
		return true;
	}

	public boolean matchesCorrect(AlchemyContext context, Level pLevel) {
		getCode(context.seed);
		if (!tablet.test(context.tablet) || code.size() != context.contents.size())
			return false;

		ArrayList<PedestalContents> remaining = new ArrayList<PedestalContents>(context.contents);
		for (int i = 0; i < inputs.size(); i++) {
			boolean matched = false;
			for (int j = 0; j < remaining.size(); j++) {
				if (code.get(i).test(remaining.get(j).aspect) && inputs.get(i).test(remaining.get(j).input)) {
					matched = true;
					remaining.remove(j);
					break;
				}
			}
			if (!matched)
				return false;
		}
		return true;
	}

	@Override
	public ItemStack assemble(AlchemyContext context, RegistryAccess registry) {
		getCode(context.seed);
		int blackPins = 0;
		int whitePins = 0;

		ArrayList<Ingredient> remainingCode = new ArrayList<Ingredient>(code);
		for (int i = 0; i < context.contents.size(); i++) {
			for (int j = 0; j < remainingCode.size(); j++) {
				if (remainingCode.get(j).test(context.contents.get(i).aspect)) {
					whitePins++;
					remainingCode.remove(j);
					break;
				}
			}
		}

		ArrayList<PedestalContents> remaining = new ArrayList<PedestalContents>(context.contents);
		for (int i = 0; i < inputs.size(); i++) {
			for (int j = 0; j < remaining.size(); j++) {
				if (code.get(i).test(remaining.get(j).aspect) && inputs.get(i).test(remaining.get(j).input)) {
					blackPins++;
					remaining.remove(j);
					break;
				}
			}
		}
		whitePins -= blackPins;

		if (blackPins < code.size()) {
			ItemStack waste = failure.copy();
			CompoundTag nbt = new CompoundTag();
			nbt.putInt("blackPins", blackPins);
			nbt.putInt("whitePins", whitePins);

			ListTag aspectNBT = new ListTag();
			ListTag inputNBT = new ListTag();
			for (PedestalContents contents : context.contents) {
				aspectNBT.add(contents.aspect.serializeNBT());
				inputNBT.add(contents.input.serializeNBT());
			}
			nbt.put("aspects", aspectNBT);
			nbt.put("inputs", inputNBT);

			waste.setTag(nbt);
			return waste;
		}
		return output;
	}

	public AlchemyResult getResult(AlchemyContext context) {
		getCode(context.seed);
		int blackPins = 0;
		int whitePins = 0;

		ArrayList<Ingredient> remainingCode = new ArrayList<Ingredient>(code);
		for (int i = 0; i < context.contents.size(); i++) {
			for (int j = 0; j < remainingCode.size(); j++) {
				if (remainingCode.get(j).test(context.contents.get(i).aspect)) {
					whitePins++;
					remainingCode.remove(j);
					break;
				}
			}
		}

		ArrayList<PedestalContents> remaining = new ArrayList<PedestalContents>(context.contents);
		for (int i = 0; i < inputs.size(); i++) {
			for (int j = 0; j < remaining.size(); j++) {
				if (code.get(i).test(remaining.get(j).aspect) && inputs.get(i).test(remaining.get(j).input)) {
					blackPins++;
					remaining.remove(j);
					break;
				}
			}
		}
		whitePins -= blackPins;

		//ensure that the ingredient order matches the recipe
		List<PedestalContents> contents = new ArrayList<PedestalContents>(context.contents);
		List<PedestalContents> sortedContents = new ArrayList<PedestalContents>();
		for (Ingredient input : inputs) {
			for (PedestalContents pedestal : contents) {
				if (input.test(pedestal.input)) {
					sortedContents.add(pedestal);
					contents.remove(pedestal);
					break;
				}
			}
		}

		return new AlchemyResult(sortedContents, blackPins, whitePins);
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack(RegistryManager.ALCHEMY_TABLET_ITEM.get());
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return RegistryManager.ALCHEMY.get();
	}

	@Override
	public ItemStack getResultItem(RegistryAccess registry) {
		return getResultItem();
	}

	public ItemStack getResultItem() {
		return output;
	}

	public ItemStack getfailureItem() {
		return failure;
	}

	@Override
	@Deprecated
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	public static class PedestalContents {
		public ItemStack aspect;
		public ItemStack input;

		public PedestalContents(ItemStack aspect, ItemStack input) {
			this.aspect = aspect;
			this.input = input;
		}
	}

	public static class Serializer implements RecipeSerializer<AlchemyRecipe> {

		@Override
		public AlchemyRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			Ingredient tablet = Ingredient.fromJson(json.get("tablet"));

			ArrayList<Ingredient> inputs = new ArrayList<>();
			JsonArray inputJson = GsonHelper.getAsJsonArray(json, "inputs", null);
			if (inputJson != null) {
				for (JsonElement element : inputJson) {
					inputs.add(Ingredient.fromJson(element));
				}
			}
			ArrayList<Ingredient> aspects = new ArrayList<>();
			JsonArray aspectJson = GsonHelper.getAsJsonArray(json, "aspects", null);
			if (aspectJson != null) {
				for (JsonElement element : aspectJson) {
					aspects.add(Ingredient.fromJson(element));
				}
			}
			ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
			ItemStack failure;
			if (json.has("failure")) {
				failure = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "failure"));
			} else {
				failure = new ItemStack(RegistryManager.ALCHEMICAL_WASTE.get());
			}
			return new AlchemyRecipe(recipeId, tablet, aspects, inputs, output, failure);
		}

		@Override
		public @Nullable AlchemyRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			Ingredient tablet = Ingredient.fromNetwork(buffer);
			ArrayList<Ingredient> aspects = buffer.readCollection((i) -> new ArrayList<>(), (buf) -> Ingredient.fromNetwork(buf));
			ArrayList<Ingredient> inputs = buffer.readCollection((i) -> new ArrayList<>(), (buf) -> Ingredient.fromNetwork(buf));
			ItemStack output = buffer.readItem();
			ItemStack failure = buffer.readItem();

			return new AlchemyRecipe(recipeId, tablet, aspects, inputs, output, failure);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, AlchemyRecipe recipe) {
			recipe.tablet.toNetwork(buffer);
			buffer.writeCollection(recipe.aspects, (buf, input) -> input.toNetwork(buf));
			buffer.writeCollection(recipe.inputs, (buf, input) -> input.toNetwork(buf));
			buffer.writeItemStack(recipe.output, false);
			buffer.writeItemStack(recipe.failure, false);
		}
	}
}
