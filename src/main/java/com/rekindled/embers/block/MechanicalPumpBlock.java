package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.MechanicalPumpBottomBlockEntity;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MechanicalPumpBlock extends DoubleTallMachineBlock {

	protected static final VoxelShape BOTTOM_Z_AABB = Shapes.or(Block.box(1,1,1,5,5,15),Block.box(11,1,1,15,5,15),Block.box(3,0,3,13,8,13),Block.box(0,5,0,6,9,16),Block.box(2,14,2,14,16,14),Block.box(4,7,1,12,15,15),Block.box(6,6,0,10,10,16),Block.box(10,5,0,16,9,16));
	protected static final VoxelShape BOTTOM_X_AABB = Misc.rotateVoxelShape(Direction.NORTH, Direction.EAST, BOTTOM_Z_AABB);
	protected static final VoxelShape TOP_AABB = Shapes.or(Block.box(11,0,11,15,12,15),Block.box(2,9,2,14,11,14),Block.box(1,0,11,5,12,15),Block.box(1,0,1,5,12,5),Block.box(11,0,1,15,12,5),Block.box(4,1,4,12,9,12),Block.box(3,0,3,13,4,13),Block.box(6,6,0,10,10,16),Block.box(0,6,6,16,10,10),Block.box(3,11,3,13,13,13));
	protected static final VoxelShape BOTTOM_Z_INTERACTION = Shapes.or(Block.box(0,0,0,16,9,16),Block.box(2,9,0,14,16,16));
	protected static final VoxelShape BOTTOM_X_INTERACTION = Shapes.or(Block.box(0,0,0,16,9,16),Block.box(0,9,2,16,16,14));
	protected static final VoxelShape TOP_INTERACTION = Block.box(0,0,0,16,12,16);

	public MechanicalPumpBlock(Properties properties, SoundType topSound) {
		super(properties, topSound);
		this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_AXIS, Axis.Z));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? state.getValue(BlockStateProperties.HORIZONTAL_AXIS) == Axis.Z ? BOTTOM_Z_AABB : BOTTOM_X_AABB : TOP_AABB;
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? state.getValue(BlockStateProperties.HORIZONTAL_AXIS) == Axis.Z ? BOTTOM_Z_INTERACTION : BOTTOM_X_INTERACTION : TOP_INTERACTION;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState state = super.getStateForPlacement(pContext);
		if (state == null)
			return null;
		return state.setValue(BlockStateProperties.HORIZONTAL_AXIS, pContext.getHorizontalDirection().getAxis());
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
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
		pBuilder.add(BlockStateProperties.HORIZONTAL_AXIS);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		if (rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90) {
			if (state.getValue(BlockStateProperties.HORIZONTAL_AXIS) == Axis.Z)
				return state.setValue(BlockStateProperties.HORIZONTAL_AXIS, Axis.X);
			return state.setValue(BlockStateProperties.HORIZONTAL_AXIS, Axis.Z);
		}
		return state;
	}
}
