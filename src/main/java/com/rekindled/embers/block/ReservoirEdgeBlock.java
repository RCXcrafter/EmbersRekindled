package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;

import net.minecraft.world.level.block.Block;

public class ReservoirEdgeBlock extends MechEdgeBlockBase {

	public ReservoirEdgeBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public Block getCenterBlock() {
		return RegistryManager.RESERVOIR.get();
	}
}
