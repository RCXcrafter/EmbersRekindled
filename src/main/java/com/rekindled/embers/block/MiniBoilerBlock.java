package com.rekindled.embers.block;

import javax.annotation.Nullable;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.block.IPipeConnection;
import com.rekindled.embers.blockentity.MiniBoilerBlockEntity;
import com.rekindled.embers.blockentity.PipeBlockEntityBase;
import com.rekindled.embers.blockentity.PipeBlockEntityBase.PipeConnection;
import com.rekindled.embers.datagen.EmbersBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
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

public class MiniBoilerBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, IPipeConnection {

	protected static final VoxelShape BOILER_AABB = Shapes.or(Block.box(3,0,3,13,16,13), Block.box(2,2,2,14,14,14));

	public MiniBoilerBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false).setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return BOILER_AABB;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.MINI_BOILER_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, RegistryManager.MINI_BOILER_ENTITY.get(), MiniBoilerBlockEntity::clientTick) : createTickerHelper(pBlockEntityType, RegistryManager.MINI_BOILER_ENTITY.get(), MiniBoilerBlockEntity::serverTick);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		Direction direction;
		if (pContext.getClickedFace().getAxis() != Axis.Y) {
			direction = pContext.getClickedFace().getOpposite();
		} else {
			direction = pContext.getHorizontalDirection();
		}
		return super.getStateForPlacement(pContext).setValue(BlockStateProperties.HORIZONTAL_FACING, direction).setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER));
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
		if (pState.getValue(BlockStateProperties.WATERLOGGED)) {
			pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}

		if (pFacing.getAxis() != Axis.Y && pLevel.getBlockEntity(pCurrentPos) instanceof PipeBlockEntityBase pipe) {
			BlockEntity facingBE = pLevel.getBlockEntity(pFacingPos);
			if (pFacingState.is(EmbersBlockTags.FLUID_PIPE_CONNECTION)) {
				if (facingBE instanceof PipeBlockEntityBase && ((PipeBlockEntityBase) facingBE).getConnection(pFacing.getOpposite()) == PipeConnection.DISABLED) {
					pipe.setConnection(pFacing, PipeConnection.NONE);
				} else {
					pipe.setConnection(pFacing, PipeConnection.PIPE);
				}
			} else {
				pipe.setConnection(pFacing, PipeConnection.NONE);
			}
		}
		return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.WATERLOGGED, BlockStateProperties.HORIZONTAL_FACING);
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}

	@Override
	public PipeConnection getPipeConnection(BlockState state, Direction direction) {
		return direction.getAxis() == Axis.Y ? PipeConnection.END : PipeConnection.PIPE;
	}
}
