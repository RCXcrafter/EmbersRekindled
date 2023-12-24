package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.api.augment.IAugment;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public class AugmentIngredient extends AbstractIngredient {

	private final Ingredient base;
	private final IAugment augment;
	private final int level;
	private ItemStack[] augmentedMatchingStacks;
	private IntList packedMatchingStacks;
	private boolean inverted;

	public AugmentIngredient(Ingredient base, IAugment augment, int level, boolean inverted) {
		this.base = base;
		this.augment = augment;
		this.level = level;
		this.inverted = inverted;
	}

	public static AugmentIngredient of(Ingredient base, IAugment augment, int level, boolean inverted) {
		return new AugmentIngredient(base, augment, level, inverted);
	}

	public static AugmentIngredient of(Ingredient base, IAugment augment, boolean inverted) {
		return new AugmentIngredient(base, augment, 1, inverted);
	}

	public static AugmentIngredient of(Ingredient base, IAugment augment, int level) {
		return new AugmentIngredient(base, augment, level, false);
	}

	public static AugmentIngredient of(Ingredient base, IAugment augment) {
		return new AugmentIngredient(base, augment, 1, false);
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		if (stack == null || stack.isEmpty())
			return false;
		return base.test(stack) && AugmentUtil.hasHeat(stack) && (inverted ^ AugmentUtil.getAugmentLevel(stack, augment) >= level);
	}

	@Override
	public ItemStack[] getItems() {
		if (this.augmentedMatchingStacks == null) {
			ItemStack[] items = base.getItems();
			this.augmentedMatchingStacks = new ItemStack[items.length];
			for (int i = 0; i < items.length; i ++) {
				ItemStack stack = items[i].copy();
				if (inverted)
					AugmentUtil.setHeat(stack, 0);
				else {
					AugmentUtil.setLevel(stack, AugmentUtil.getLevel(stack) + level);
					AugmentUtil.addAugment(stack, ItemStack.EMPTY, augment);
					AugmentUtil.setAugmentLevel(stack, augment, level);
				}
				this.augmentedMatchingStacks[i] = stack;
			}
		}
		return augmentedMatchingStacks;
	}

	@Override
	public boolean isEmpty() {
		return base.isEmpty();
	}

	@Override
	public boolean isSimple() {
		return base.isSimple();
	}

	@Override
	protected void invalidate() {
		super.invalidate();
		this.augmentedMatchingStacks = null;
		this.packedMatchingStacks = null;
	}

	@Override
	public IntList getStackingIds() {
		if (this.packedMatchingStacks == null || checkInvalidation()) {
			markValid();
			ItemStack[] matchingStacks = getItems();
			this.packedMatchingStacks = new IntArrayList(matchingStacks.length);
			for (ItemStack stack : matchingStacks)
				this.packedMatchingStacks.add(StackedContents.getStackingIndex(stack));

			this.packedMatchingStacks.sort(IntComparators.NATURAL_COMPARATOR);
		}
		return packedMatchingStacks;
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", CraftingHelper.getID(Serializer.INSTANCE).toString());
		json.add("base", base.toJson());
		json.addProperty("augment", augment.getName().toString());
		json.addProperty("level", level);
		if (inverted)
			json.addProperty("inverted", inverted);
		return json;
	}

	@Override
	public IIngredientSerializer<AugmentIngredient> getSerializer() {
		return Serializer.INSTANCE;
	}

	public static class Serializer implements IIngredientSerializer<AugmentIngredient> {
		public static final IIngredientSerializer<AugmentIngredient> INSTANCE = new Serializer();

		@Override
		public AugmentIngredient parse(JsonObject json) {
			Ingredient base = Ingredient.fromJson(json.get("base"), false);
			IAugment augment = AugmentUtil.getAugment(new ResourceLocation(GsonHelper.getAsString(json, "augment")));
			int level = GsonHelper.getAsInt(json, "level");
			boolean inverted = false;
			if (json.has("inverted"))
				inverted = GsonHelper.getAsBoolean(json, "inverted");
			return new AugmentIngredient(base, augment, level, inverted);
		}

		@Override
		public AugmentIngredient parse(FriendlyByteBuf buffer) {
			Ingredient base = Ingredient.fromNetwork(buffer);
			IAugment augment = AugmentUtil.getAugment(buffer.readResourceLocation());
			int level = buffer.readInt();
			boolean inverted = buffer.readBoolean();
			return new AugmentIngredient(base, augment, level, inverted);
		}

		@Override
		public void write(FriendlyByteBuf buffer, AugmentIngredient ingredient) {
			ingredient.base.toNetwork(buffer);
			buffer.writeResourceLocation(ingredient.augment.getName());
			buffer.writeInt(ingredient.level);
			buffer.writeBoolean(ingredient.inverted);
		}
	}
}
