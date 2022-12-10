package com.rekindled.embers.util;

import java.util.Random;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

public class Misc {
	public static Random random = new Random();

	public static void spawnInventoryInWorld(Level world, double x, double y, double z, IItemHandler inventory) {
		if (inventory != null && !world.isClientSide) {
			for (int i = 0; i < inventory.getSlots(); i++) {
				if (!inventory.getStackInSlot(i).isEmpty()) {
					world.addFreshEntity(new ItemEntity(world, x, y, z, inventory.getStackInSlot(i)));
				}
			}
		}
	}

	public static Direction readNullableFacing(int index) {
		return index > 0 ? Direction.from3DDataValue(index) : null;
	}

	public static int writeNullableFacing(Direction facing) {
		return facing != null ? facing.get3DDataValue() : -1;
	}
}
