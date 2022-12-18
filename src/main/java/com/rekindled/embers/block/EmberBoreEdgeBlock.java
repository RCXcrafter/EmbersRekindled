package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class EmberBoreEdgeBlock extends MechEdgeBlockBase {

	public EmberBoreEdgeBlock(Properties pProperties) {
		super(pProperties);
		this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_AXIS, Axis.Z));
	}

	@Override
	public Block getCenterBlock() {
		return RegistryManager.EMBER_BORE.get();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(BlockStateProperties.HORIZONTAL_AXIS);
	}
}
