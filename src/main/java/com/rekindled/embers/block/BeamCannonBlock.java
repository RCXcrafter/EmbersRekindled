package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.BeamCannonBlockEntity;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeamCannonBlock extends EmberEmitterBlock {

	protected static final VoxelShape UP_AABB = Shapes.or(Block.box(3,0,3,13,2,13),Block.box(7,-1,1,9,7,3),Block.box(7,-1,13,9,7,15),Block.box(13,-1,7,15,7,9),Block.box(1,-1,7,3,7,9),Block.box(2,2,2,4,4,14),Block.box(12,2,2,14,4,14),Block.box(4,2,2,12,4,4),Block.box(4,2,12,12,4,14),Block.box(5.5,1.5,5.5,10.5,6.5,10.5),Block.box(5,12,5,11,14,11),Block.box(5,9,5,11,11,11),Block.box(5,6,5,11,8,11),Block.box(6,6,6,10,16,10),Block.box(5.1,-2,5.1,10.9,0,10.9));
	protected static final VoxelShape DOWN_AABB = Misc.rotateVoxelShape(Direction.DOWN, UP_AABB);
	protected static final VoxelShape NORTH_AABB = Misc.rotateVoxelShape(Direction.NORTH, UP_AABB);
	protected static final VoxelShape SOUTH_AABB = Misc.rotateVoxelShape(Direction.SOUTH, UP_AABB);
	protected static final VoxelShape WEST_AABB = Misc.rotateVoxelShape(Direction.WEST, UP_AABB);
	protected static final VoxelShape EAST_AABB = Misc.rotateVoxelShape(Direction.EAST, UP_AABB);

	protected static final VoxelShape X_INTERACTION = Shapes.box(0,0.3125,0.3125,1,0.6875,0.6875);
	protected static final VoxelShape Y_INTERACTION = Shapes.box(0.3125,0,0.3125,0.6875,1,0.6875);
	protected static final VoxelShape Z_INTERACTION = Shapes.box(0.3125,0.3125,0,0.6875,0.6875,1);
	protected static final VoxelShape SUPPORT_UP = Shapes.or(Shapes.box(0,0,0,1,1,0.1), Shapes.box(0,0,0.9,1,1,1), Shapes.box(0,0,0,0.1,1,1), Shapes.box(0.9,0,0,1,1,1), Shapes.box(0,0,0,1,0.1,1));
	protected static final VoxelShape SUPPORT_DOWN = Shapes.or(Shapes.box(0,0,0,1,1,0.1), Shapes.box(0,0,0.9,1,1,1), Shapes.box(0,0,0,0.1,1,1), Shapes.box(0.9,0,0,1,1,1), Shapes.box(0,0.9,0,1,1,1));
	protected static final VoxelShape SUPPORT_NORTH = Shapes.or(Shapes.box(0,0,0,1,0.1,1), Shapes.box(0,0.9,0,1,1,1), Shapes.box(0,0,0,0.1,1,1), Shapes.box(0.9,0,0,1,1,1), Shapes.box(0,0,0.9,1,1,1));
	protected static final VoxelShape SUPPORT_SOUTH = Shapes.or(Shapes.box(0,0,0,1,0.1,1), Shapes.box(0,0.9,0,1,1,1), Shapes.box(0,0,0,0.1,1,1), Shapes.box(0.9,0,0,1,1,1), Shapes.box(0,0,0,1,1,0.1));
	protected static final VoxelShape SUPPORT_WEST = Shapes.or(Shapes.box(0,0,0,1,1,0.1), Shapes.box(0,0,0.9,1,1,1), Shapes.box(0,0,0,1,0.1,1), Shapes.box(0,0.9,0,1,1,1), Shapes.box(0.9,0,0,1,1,1));
	protected static final VoxelShape SUPPORT_EAST = Shapes.or(Shapes.box(0,0,0,1,1,0.1), Shapes.box(0,0,0.9,1,1,1), Shapes.box(0,0,0,1,0.1,1), Shapes.box(0,0.9,0,1,1,1), Shapes.box(0,0,0,0.1,1,1));

	public BeamCannonBlock(Properties properties) {
		super(properties);
	}

	public VoxelShape[][] shapeCache = new VoxelShape[6][16];

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		switch (state.getValue(FACING)) {
		case UP:
			return addPipeConnections(state, UP_AABB, shapeCache);
		case DOWN:
			return addPipeConnections(state, DOWN_AABB, shapeCache);
		case EAST:
			return addPipeConnections(state, EAST_AABB, shapeCache);
		case WEST:
			return addPipeConnections(state, WEST_AABB, shapeCache);
		case SOUTH:
			return addPipeConnections(state, SOUTH_AABB, shapeCache);
		case NORTH:
		default:
			return addPipeConnections(state, NORTH_AABB, shapeCache);
		}
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		switch (state.getValue(FACING).getAxis()) {
		case X:
			return X_INTERACTION;
		case Y:
			return Y_INTERACTION;
		case Z:
		default:
			return Z_INTERACTION;
		}
	}

	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.BEAM_CANNON_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, RegistryManager.BEAM_CANNON_ENTITY.get(), BeamCannonBlockEntity::clientTick) : createTickerHelper(pBlockEntityType, RegistryManager.BEAM_CANNON_ENTITY.get(), BeamCannonBlockEntity::serverTick);
	}

	@Override
	public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		switch (pState.getValue(FACING)) {
		case UP:
			return SUPPORT_UP;
		case DOWN:
			return SUPPORT_DOWN;
		case EAST:
			return SUPPORT_EAST;
		case WEST:
			return SUPPORT_WEST;
		case SOUTH:
			return SUPPORT_SOUTH;
		case NORTH:
		default:
			return SUPPORT_NORTH;
		}
	}
}
