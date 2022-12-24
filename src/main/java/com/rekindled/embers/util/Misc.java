package com.rekindled.embers.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiPredicate;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.core.Direction;
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

	public static Random random = new Random();
	public static Direction[] horizontals = {
			Direction.NORTH,
			Direction.SOUTH,
			Direction.WEST,
			Direction.EAST
	};
	public static final List<BiPredicate<Player, InteractionHand>> IS_HOLDING_HAMMER = new ArrayList<BiPredicate<Player, InteractionHand>>();

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
}
