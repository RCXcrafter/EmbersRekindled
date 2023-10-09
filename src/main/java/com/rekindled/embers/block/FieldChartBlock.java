package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.block.MechEdgeBlockBase.MechEdge;
import com.rekindled.embers.blockentity.FieldChartBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FieldChartBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

	public static final BooleanProperty INVERTED = BooleanProperty.create("inverted");

	public static final VoxelShape CHART_AABB = Block.box(0, 0, 0, 16, 6, 16);

	public FieldChartBlock(Properties pProperties) {
		super(pProperties);
		this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false).setValue(INVERTED, false));
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return CHART_AABB;
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (state.hasProperty(INVERTED))
			level.setBlock(pos, state.setValue(INVERTED, !state.getValue(INVERTED)), UPDATE_ALL);
		return InteractionResult.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			for (MechEdge edge : MechEdge.values()) {
				BlockPos cornerPos = pos.subtract(edge.centerPos);
				if (level.getBlockState(cornerPos).getBlock() instanceof MechEdgeBlockBase) {
					level.destroyBlock(cornerPos, false);
				}
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		boolean clear = true;
		for (MechEdge edge : MechEdge.values()) {
			if (!pContext.getLevel().getBlockState(pContext.getClickedPos().subtract(edge.centerPos)).canBeReplaced(pContext))
				clear = false;
		}
		return clear ? super.getStateForPlacement(pContext).setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER)) : null;
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		for (MechEdge edge : MechEdge.values()) {
			BlockState edgeState = RegistryManager.FIELD_CHART_EDGE.get().defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(level.getFluidState(pos.subtract(edge.centerPos)).getType() == Fluids.WATER));
			level.setBlock(pos.subtract(edge.centerPos), edgeState.setValue(MechEdgeBlockBase.EDGE, edge), UPDATE_ALL);
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.FIELD_CHART_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, RegistryManager.FIELD_CHART_ENTITY.get(), FieldChartBlockEntity::clientTick) : null;
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
		pBuilder.add(BlockStateProperties.WATERLOGGED).add(INVERTED);
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}
}
