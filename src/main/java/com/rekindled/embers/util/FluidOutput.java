package com.rekindled.embers.util;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidOutput {

	public static final FluidOutput EMPTY = new FluidOutput(FluidTags.create(new ResourceLocation("empty")), -1);

	public FluidStack stack = FluidStack.EMPTY;
	public TagKey<Fluid> tag;
	public int amount = 0;

	public FluidOutput(FluidStack stack) {
		this.stack = stack;
	}

	public FluidOutput(TagKey<Fluid> tag, int amount) {
		this.tag = tag;
		this.amount = amount;
	}

	public boolean isEmpty() {
		return amount < 0;
	}

	public FluidStack getStack() {
		if (!stack.isEmpty())
			return stack;
		stack = new FluidStack(Misc.getTaggedFluid(tag), amount);
		return stack;
	}

	public static FluidOutput fromJson(JsonObject json) {
		if (json.has("tag")) {
			return new FluidOutput(FluidTags.create(new ResourceLocation(GsonHelper.getAsString(json, "tag"))), GsonHelper.getAsInt(json, "amount", 1));
		}
		return new FluidOutput(Misc.deserializeFluidStack(json));
	}

	public JsonObject toJson() {
		if (tag != null) {
			JsonObject json = new JsonObject();
			json.addProperty("tag", tag.location().toString());
			json.addProperty("amount", amount);
			return json;
		}
		return Misc.serializeFluidStack(stack);
	}

	public static FluidOutput fromNetwork(FriendlyByteBuf buffer) {
		if (buffer.readBoolean()) {
			return new FluidOutput(FluidTags.create(buffer.readResourceLocation()), buffer.readInt());
		}
		return new FluidOutput(FluidStack.readFromPacket(buffer));
	}

	public void toNetwork(FriendlyByteBuf buffer) {
		if (tag != null) {
			buffer.writeBoolean(true);
			buffer.writeResourceLocation(tag.location());
			buffer.writeInt(amount);
		} else {
			buffer.writeBoolean(false);
			stack.writeToPacket(buffer);
		}
	}
}
