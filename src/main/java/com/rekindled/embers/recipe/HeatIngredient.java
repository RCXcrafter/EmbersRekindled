package com.rekindled.embers.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rekindled.embers.api.augment.AugmentUtil;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public class HeatIngredient extends AbstractIngredient {

	private final Ingredient base;
	private ItemStack[] heatedMatchingStacks;
	private IntList packedMatchingStacks;
	private boolean inverted;

	public HeatIngredient(Ingredient base, boolean inverted) {
		this.base = base;
		this.inverted = inverted;
	}

	public static HeatIngredient of(Ingredient base, boolean inverted) {
		return new HeatIngredient(base, inverted);
	}

	public static HeatIngredient of(Ingredient base) {
		return new HeatIngredient(base, false);
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		if (stack == null || stack.isEmpty())
			return false;
		return base.test(stack) && (inverted ^ AugmentUtil.hasHeat(stack));
	}

	@Override
	public ItemStack[] getItems() {
		if (!inverted) {
			if (this.heatedMatchingStacks == null) {
				ItemStack[] items = base.getItems();
				this.heatedMatchingStacks = new ItemStack[items.length];
				for (int i = 0; i < items.length; i ++) {
					ItemStack stack = items[i].copy();
					AugmentUtil.setHeat(stack, 0);
					this.heatedMatchingStacks[i] = stack;
				}
			}
			return heatedMatchingStacks;
		}
		return base.getItems();
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
		this.heatedMatchingStacks = null;
		this.packedMatchingStacks = null;
	}

	@Override
	public IntList getStackingIds() {
		if (!inverted) {
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
		return base.getStackingIds();
	}

	@Override
	public JsonElement toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", CraftingHelper.getID(Serializer.INSTANCE).toString());
		json.add("base", base.toJson());
		if (inverted)
			json.addProperty("inverted", inverted);
		return json;
	}

	@Override
	public IIngredientSerializer<HeatIngredient> getSerializer() {
		return Serializer.INSTANCE;
	}

	public static class Serializer implements IIngredientSerializer<HeatIngredient> {
		public static final IIngredientSerializer<HeatIngredient> INSTANCE = new Serializer();

		@Override
		public HeatIngredient parse(JsonObject json) {
			Ingredient base = Ingredient.fromJson(json.get("base"), false);
			boolean inverted = false;
			if (json.has("inverted"))
				inverted = GsonHelper.getAsBoolean(json, "inverted");
			return new HeatIngredient(base, inverted);
		}

		@Override
		public HeatIngredient parse(FriendlyByteBuf buffer) {
			Ingredient base = Ingredient.fromNetwork(buffer);
			boolean inverted = buffer.readBoolean();
			return new HeatIngredient(base, inverted);
		}

		@Override
		public void write(FriendlyByteBuf buffer, HeatIngredient ingredient) {
			ingredient.base.toNetwork(buffer);
			buffer.writeBoolean(ingredient.inverted);
		}
	}
}
