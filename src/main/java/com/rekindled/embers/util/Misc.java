package com.rekindled.embers.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.rekindled.embers.Embers;

import net.minecraft.client.gui.Font;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class Misc {

	public static final double LOG_E = Math.log10(Math.exp(1));
	public static Random random = new Random();
	public static Direction[] horizontals = {
			Direction.NORTH,
			Direction.SOUTH,
			Direction.WEST,
			Direction.EAST
	};
	public static final List<BiPredicate<Player, InteractionHand>> IS_HOLDING_HAMMER = new ArrayList<BiPredicate<Player, InteractionHand>>();
	public static final List<Predicate<Player>> IS_WEARING_LENS = new ArrayList<Predicate<Player>>();

	public static void spawnInventoryInWorld(Level world, double x, double y, double z, IItemHandler inventory) {
		if (inventory != null && !world.isClientSide) {
			for (int i = 0; i < inventory.getSlots(); i++) {
				if (!inventory.getStackInSlot(i).isEmpty()) {
					world.addFreshEntity(new ItemEntity(world, x, y, z, inventory.getStackInSlot(i)));
				}
			}
		}
	}

	public static boolean isHoldingHammer(Player player, InteractionHand hand) {
		for (BiPredicate<Player, InteractionHand> predicate : IS_HOLDING_HAMMER) {
			if (predicate.test(player, hand)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isWearingLens(Player player) {
		for (Predicate<Player> predicate : IS_WEARING_LENS) {
			if (predicate.test(player)) {
				return true;
			}
		}
		return false;
	}

	public static Direction readNullableFacing(int index) {
		return index > 0 ? Direction.from3DDataValue(index) : null;
	}

	public static int writeNullableFacing(Direction facing) {
		return facing != null ? facing.get3DDataValue() : -1;
	}

	public static FluidStack deserializeFluidStack(JsonObject json) {
		String fluidName = GsonHelper.getAsString(json, "fluid");
		Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));
		if (fluid == null || fluid == Fluids.EMPTY) {
			throw new JsonSyntaxException("Unknown fluid " + fluidName);
		}
		int amount = GsonHelper.getAsInt(json, "amount");
		return new FluidStack(fluid, amount);
	}

	public static JsonObject serializeFluidStack(FluidStack stack) {
		JsonObject json = new JsonObject();
		json.addProperty("fluid", Objects.requireNonNull(ForgeRegistries.FLUIDS.getResourceKey(stack.getFluid()).get().location()).toString());
		json.addProperty("amount", stack.getAmount());
		return json;
	}

	public static boolean isGaseousFluid(FluidStack resource) {
		return resource != null && resource.getFluid().getFluidType().getDensity() <= 0;
	}

	public static double getDiminishedPower(double power, double softcap, double slope) {
		if (power > softcap)
			return softcap * slope + Math.log10(power - softcap + LOG_E / slope) - Math.log10(LOG_E / slope);
		else
			return power * slope;
	}

	public static void drawComponents(Font fontRenderer, PoseStack stack, int x, int y, Component... components) {
		for (Component component : components) {
			fontRenderer.drawShadow(stack, component, x, y, 0xFFFFFF);
			fontRenderer.draw(stack, component, x, y, 0xFFFFFF);
			y += fontRenderer.lineHeight + 2;
		}
	}
}
