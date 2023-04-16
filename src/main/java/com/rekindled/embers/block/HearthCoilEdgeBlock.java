package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;

import net.minecraft.world.level.block.Block;

public class HearthCoilEdgeBlock extends MechEdgeBlockBase {

	public HearthCoilEdgeBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public Block getCenterBlock() {
		return RegistryManager.HEARTH_COIL.get();
	}
}
