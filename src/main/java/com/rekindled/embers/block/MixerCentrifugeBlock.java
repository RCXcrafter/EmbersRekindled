package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.MixerCentrifugeBottomBlockEntity;
import com.rekindled.embers.blockentity.MixerCentrifugeTopBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MixerCentrifugeBlock extends DoubleTallMachineBlock {

	protected static final VoxelShape BASE_AABB = Shapes.or(Block.box(0,0,0,16,4,16), Block.box(2,0,2,14,16,14));
	protected static final VoxelShape TOP_AABB = Shapes.or(Block.box(2,0,2,14,12,14), Block.box(4,12,4,12,14,12), Block.box(2,14,2,14,16,14));

	public MixerCentrifugeBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(BlockStateProperties.BOTTOM) ? BASE_AABB : TOP_AABB;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		if (pState.getValue(BlockStateProperties.BOTTOM))
			return RegistryManager.MIXER_CENTRIFUGE_BOTTOM_ENTITY.get().create(pPos, pState);
		return RegistryManager.MIXER_CENTRIFUGE_TOP_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		if (pState.getValue(BlockStateProperties.BOTTOM))
			return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, RegistryManager.MIXER_CENTRIFUGE_BOTTOM_ENTITY.get(), MixerCentrifugeBottomBlockEntity::clientTick) : createTickerHelper(pBlockEntityType, RegistryManager.MIXER_CENTRIFUGE_BOTTOM_ENTITY.get(), MixerCentrifugeBottomBlockEntity::serverTick);
		return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, RegistryManager.MIXER_CENTRIFUGE_TOP_ENTITY.get(), MixerCentrifugeTopBlockEntity::clientTick) : null;
	}
}
