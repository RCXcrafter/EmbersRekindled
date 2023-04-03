package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EmberRelayBlock extends EmberReceiverBlock {

	protected static final VoxelShape UP_AABB = Shapes.or(box(5,0,5,11,2,11), box(7,2,7,9,3,9), box(6,3,6,10,5,10), box(5,5,5,11,11,11));
	protected static final VoxelShape DOWN_AABB = Shapes.or(box(5,14,5,11,16,11), box(7,13,7,9,14,9), box(6,11,6,10,13,10), box(5,5,5,11,11,11));
	protected static final VoxelShape NORTH_AABB = Shapes.or(box(5,5,14,11,11,16), box(7,7,13,9,9,14), box(6,6,11,10,10,13), box(5,5,5,11,11,11));
	protected static final VoxelShape SOUTH_AABB = Shapes.or(box(5,5,0,11,11,2), box(7,7,2,9,9,3), box(6,6,3,10,10,5), box(5,5,5,11,11,11));
	protected static final VoxelShape WEST_AABB = Shapes.or(box(14,5,5,16,11,11), box(13,7,7,14,9,9), box(11,6,6,13,10,10), box(5,5,5,11,11,11));
	protected static final VoxelShape EAST_AABB = Shapes.or(box(0,5,5,2,11,11), box(2,7,7,3,9,9), box(3,6,6,5,10,10), box(5,5,5,11,11,11));

	public EmberRelayBlock(Properties properties) {
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
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.EMBER_RELAY_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return null;
	}
}
