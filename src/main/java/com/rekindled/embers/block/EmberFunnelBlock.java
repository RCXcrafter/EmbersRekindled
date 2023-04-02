package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.EmberFunnelBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EmberFunnelBlock extends EmberReceiverBlock {

	protected static final VoxelShape UP_AABB = Shapes.or(box(4,0,4,12,4,12), box(2,4,2,14,12,14), box(0,12,0,16,16,16));
	protected static final VoxelShape DOWN_AABB = Shapes.or(box(4,4,4,12,16,12), box(2,4,2,14,12,14), box(0,0,0,16,4,16));
	protected static final VoxelShape NORTH_AABB = Shapes.or(box(4,4,4,12,12,16), box(2,2,4,14,14,12), box(0,0,0,16,16,4));
	protected static final VoxelShape SOUTH_AABB = Shapes.or(box(4,4,0,12,12,4), box(2,2,4,14,14,12), box(0,0,12,16,16,16));
	protected static final VoxelShape WEST_AABB = Shapes.or(box(4,4,4,16,12,12), box(4,2,2,12,14,14), box(0,0,0,4,16,16));
	protected static final VoxelShape EAST_AABB = Shapes.or(box(0,4,4,4,12,12), box(4,2,2,12,14,14), box(12,0,0,16,16,16));

	public EmberFunnelBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		switch (pState.getValue(FACING)) {
		case UP:
			return UP_AABB;
		case DOWN:
			return DOWN_AABB;
		case EAST:
			return EAST_AABB;
		case WEST:
			return WEST_AABB;
		case SOUTH:
			return SOUTH_AABB;
		case NORTH:
		default:
			return NORTH_AABB;
		}
	}

	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.EMBER_FUNNEL_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pLevel.isClientSide ? null : createTickerHelper(pBlockEntityType, RegistryManager.EMBER_FUNNEL_ENTITY.get(), EmberFunnelBlockEntity::serverTick);
	}
}
