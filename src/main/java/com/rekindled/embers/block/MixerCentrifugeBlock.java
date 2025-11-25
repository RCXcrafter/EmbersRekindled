package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.MixerCentrifugeBottomBlockEntity;
import com.rekindled.embers.blockentity.MixerCentrifugeTopBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MixerCentrifugeBlock extends DoubleTallMachineBlock {

	protected static final VoxelShape BASE_AABB = Shapes.or(Block.box(2,15,2,14,16,14),Block.box(0,0,0,16,4,16),Block.box(2,4,9,7,15,14),Block.box(9,4,9,14,15,14),Block.box(2,4,2,7,15,7),Block.box(9,4,2,14,15,7),Block.box(14,4,4,16,12,12),Block.box(0,4,4,2,12,12),Block.box(4,4,0,12,12,2),Block.box(4,4,14,12,12,16),Block.box(1,5,11,5,9,15),Block.box(1,11,11,5,15,15),Block.box(11,11,11,15,15,15),Block.box(11,5,11,15,9,15),Block.box(11,5,1,15,9,5),Block.box(11,11,1,15,15,5),Block.box(1,11,1,5,15,5),Block.box(1,5,1,5,9,5),Block.box(2,11,2,14,12,14),Block.box(4,12,4,12,15,12));
	protected static final VoxelShape TOP_AABB = Shapes.or(Block.box(9,2,2,14,5,7),Block.box(2,5,2,14,12,4),Block.box(2,5,12,14,12,14),Block.box(2,5,4,4,12,12),Block.box(12,5,4,14,12,12),Block.box(6,6,14,10,10,16),Block.box(1,1,11,5,5,15),Block.box(1,7,11,5,11,15),Block.box(11,7,11,15,11,15),Block.box(11,1,11,15,5,15),Block.box(11,1,1,15,5,5),Block.box(11,7,1,15,11,5),Block.box(1,7,1,5,11,5),Block.box(1,1,1,5,5,5),Block.box(6,6,0,10,10,2),Block.box(0,6,6,2,10,10),Block.box(14,6,6,16,10,10),Block.box(4,2,4,12,12,12),Block.box(4,12,4,12,14,12),Block.box(2,14,2,14,16,14),Block.box(2,0,2,14,2,14),Block.box(2,2,2,7,5,7),Block.box(9,2,9,14,5,14),Block.box(2,2,9,7,5,14));

	public MixerCentrifugeBlock(Properties properties, SoundType topSound) {
		super(properties, topSound);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? BASE_AABB : TOP_AABB;
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return Shapes.block();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		if (pState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
			return RegistryManager.MIXER_CENTRIFUGE_BOTTOM_ENTITY.get().create(pPos, pState);
		return RegistryManager.MIXER_CENTRIFUGE_TOP_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		if (pState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
			return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, RegistryManager.MIXER_CENTRIFUGE_BOTTOM_ENTITY.get(), MixerCentrifugeBottomBlockEntity::clientTick) : createTickerHelper(pBlockEntityType, RegistryManager.MIXER_CENTRIFUGE_BOTTOM_ENTITY.get(), MixerCentrifugeBottomBlockEntity::serverTick);
		return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, RegistryManager.MIXER_CENTRIFUGE_TOP_ENTITY.get(), MixerCentrifugeTopBlockEntity::clientTick) : null;
	}
}
