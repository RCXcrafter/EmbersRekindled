package com.rekindled.embers.block;

import javax.annotation.Nullable;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.EmberActivatorBottomBlockEntity;
import com.rekindled.embers.blockentity.EmberActivatorTopBlockEntity;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

public class EmberActivatorBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

	protected static final VoxelShape BASE_AABB = Shapes.or(Block.box(0,0,0,16,4,16), Block.box(2,0,2,14,16,14));
	protected static final VoxelShape TOP_AABB = Shapes.or(Shapes.joinUnoptimized(Block.box(3,4,3,13,16,13), Block.box(5,5,5,11,16,11), BooleanOp.ONLY_FIRST), Block.box(2,2,2,14,4,14), Block.box(4,0,4,12,2,12));

	public EmberActivatorBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.BOTTOM, true).setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(BlockStateProperties.BOTTOM) ? BASE_AABB : TOP_AABB;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			if (state.getValue(BlockStateProperties.BOTTOM)) {
				IItemHandler handler = level.getBlockEntity(pos).getCapability(ForgeCapabilities.ITEM_HANDLER, null).orElse(null);
				if (handler != null) {
					Misc.spawnInventoryInWorld(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, handler);
					level.updateNeighbourForOutputSignal(pos, this);
				}
				BlockState above = level.getBlockState(pos.above());
				if (above.getBlock() == this && !above.getValue(BlockStateProperties.BOTTOM))
					level.destroyBlock(pos.above(), false);
			} else {
				BlockState below = level.getBlockState(pos.below());
				if (below.getBlock() == this && below.getValue(BlockStateProperties.BOTTOM))
					level.destroyBlock(pos.below(), false);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public SoundType getSoundType(BlockState state) {
		return state.getValue(BlockStateProperties.BOTTOM) ? super.getSoundType(state) : EmbersSounds.MULTIBLOCK_EXTRA;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		if (pState.getValue(BlockStateProperties.BOTTOM))
			return RegistryManager.EMBER_ACTIVATOR_BOTTOM_ENTITY.get().create(pPos, pState);
		return RegistryManager.EMBER_ACTIVATOR_TOP_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		if (pState.getValue(BlockStateProperties.BOTTOM))
			return pLevel.isClientSide ? null : createTickerHelper(pBlockEntityType, RegistryManager.EMBER_ACTIVATOR_BOTTOM_ENTITY.get(), EmberActivatorBottomBlockEntity::serverTick);
		return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, RegistryManager.EMBER_ACTIVATOR_TOP_ENTITY.get(), EmberActivatorTopBlockEntity::clientTick) : null;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		if (context.getLevel().getBlockState(context.getClickedPos().above()).canBeReplaced(context))
			return super.getStateForPlacement(context).setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER));
		return null;
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (state.getValue(BlockStateProperties.BOTTOM)) {
			BlockState topState = this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(level.getFluidState(pos.above()).getType() == Fluids.WATER));
			level.setBlock(pos.above(), topState.setValue(BlockStateProperties.BOTTOM, false), UPDATE_ALL);
		}
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
		if (pState.getValue(BlockStateProperties.WATERLOGGED)) {
			pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}
		return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.WATERLOGGED).add(BlockStateProperties.BOTTOM);
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}
}
