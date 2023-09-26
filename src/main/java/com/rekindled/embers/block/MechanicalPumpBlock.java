package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.MechanicalPumpBottomBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MechanicalPumpBlock extends DoubleTallMachineBlock {

	public static final BooleanProperty PISTON = BooleanProperty.create("piston");

	protected static final VoxelShape BOTTOM_Z_AABB = Shapes.or(Block.box(0,0,0,16,9,16), Block.box(1,9,0,15,11,16), Block.box(2,11,0,14,16,16));
	protected static final VoxelShape BOTTOM_X_AABB = Shapes.or(Block.box(0,0,0,16,9,16), Block.box(0,9,1,16,11,15), Block.box(0,11,2,16,16,14));
	protected static final VoxelShape TOP_AABB = Block.box(1,0,1,15,12,15);

	public MechanicalPumpBlock(Properties properties, SoundType topSound) {
		super(properties, topSound);
		this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_AXIS, Axis.Z).setValue(PISTON, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? state.getValue(BlockStateProperties.HORIZONTAL_AXIS) == Axis.Z ? BOTTOM_Z_AABB : BOTTOM_X_AABB : TOP_AABB;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return super.getStateForPlacement(pContext).setValue(BlockStateProperties.HORIZONTAL_AXIS, pContext.getHorizontalDirection().getAxis());
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
			BlockState topState = state.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(level.getFluidState(pos.above()).getType() == Fluids.WATER));
			level.setBlock(pos.above(), topState.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), UPDATE_ALL);
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		if (pState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
			return RegistryManager.MECHANICAL_PUMP_BOTTOM_ENTITY.get().create(pPos, pState);
		return RegistryManager.MECHANICAL_PUMP_TOP_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		if (pState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
			return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, RegistryManager.MECHANICAL_PUMP_BOTTOM_ENTITY.get(), MechanicalPumpBottomBlockEntity::clientTick) : createTickerHelper(pBlockEntityType, RegistryManager.MECHANICAL_PUMP_BOTTOM_ENTITY.get(), MechanicalPumpBottomBlockEntity::serverTick);
		return null;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(BlockStateProperties.HORIZONTAL_AXIS).add(PISTON);
	}
}
