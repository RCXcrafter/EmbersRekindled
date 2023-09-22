package com.rekindled.embers.block;

import javax.annotation.Nullable;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.block.IPipeConnection;
import com.rekindled.embers.blockentity.CatalyticPlugBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CatalyticPlugBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, IPipeConnection {

	protected static final VoxelShape UP_AABB = Shapes.or(Block.box(3, 0, 3, 13, 4, 13), Block.box(6, 4, 6, 10, 16, 10), Block.box(1, 1, 6, 15, 4, 10), Block.box(6, 1, 1, 10, 4, 15), Block.box(6, 4, 0, 10, 12, 16), Block.box(0, 4, 6, 16, 12, 10));
	protected static final VoxelShape DOWN_AABB = Shapes.or(Block.box(3, 12, 3, 13, 16, 13), Block.box(6, 0, 6, 10, 12, 10), Block.box(1, 12, 6, 15, 15, 10), Block.box(6, 12, 1, 10, 15, 15), Block.box(6, 4, 0, 10, 12, 16), Block.box(0, 4, 6, 16, 12, 10));
	protected static final VoxelShape NORTH_AABB = Shapes.or(Block.box(3, 3, 12, 13, 13, 16), Block.box(6, 6, 0, 10, 10, 12), Block.box(1, 6, 12, 15, 10, 15), Block.box(6, 1, 12, 10, 15, 15), Block.box(6, 0, 4, 10, 16, 12), Block.box(0, 6, 4, 16, 10, 12));
	protected static final VoxelShape SOUTH_AABB = Shapes.or(Block.box(3, 3, 0, 13, 13, 4), Block.box(6, 6, 4, 10, 10, 16), Block.box(1, 6, 1, 15, 10, 4), Block.box(6, 1, 1, 10, 15, 4), Block.box(6, 0, 4, 10, 16, 12), Block.box(0, 6, 4, 16, 10, 12));
	protected static final VoxelShape WEST_AABB = Shapes.or(Block.box(12, 3, 3, 16, 13, 13), Block.box(0, 6, 6, 12, 10, 10), Block.box(12, 6, 1, 15, 10, 15), Block.box(12, 1, 6, 15, 15, 10), Block.box(4, 0, 6, 12, 16, 10), Block.box(4, 6, 0, 12, 10, 16));
	protected static final VoxelShape EAST_AABB = Shapes.or(Block.box(0, 3, 3, 4, 13, 13), Block.box(4, 6, 6, 16, 10, 10), Block.box(1, 6, 1, 4, 10, 15), Block.box(1, 1, 6, 4, 15, 10), Block.box(4, 0, 6, 12, 16, 10), Block.box(4, 6, 0, 12, 10, 16));

	public CatalyticPlugBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.FACING, Direction.UP).setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		switch (pState.getValue(BlockStateProperties.FACING)) {
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
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		for (Direction direction : pContext.getNearestLookingDirections()) {
			BlockState blockstate = this.defaultBlockState().setValue(BlockStateProperties.FACING, direction.getOpposite());
			return blockstate.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER));
		}
		return null;
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
		if (pState.getValue(BlockStateProperties.WATERLOGGED)) {
			pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}
		return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
	}

	@Override
	public BlockState rotate(BlockState pState, Rotation pRot) {
		return pState.setValue(BlockStateProperties.FACING, pRot.rotate(pState.getValue(BlockStateProperties.FACING)));
	}

	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.rotate(pMirror.getRotation(pState.getValue(BlockStateProperties.FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.FACING).add(BlockStateProperties.WATERLOGGED);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.CATALYTIC_PLUG_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, RegistryManager.CATALYTIC_PLUG_ENTITY.get(), CatalyticPlugBlockEntity::clientTick) : createTickerHelper(pBlockEntityType, RegistryManager.CATALYTIC_PLUG_ENTITY.get(), CatalyticPlugBlockEntity::serverTick);
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}

	@Override
	public boolean connectPipe(BlockState state, Direction direction) {
		return state.getValue(BlockStateProperties.FACING) == direction;
	}
}
