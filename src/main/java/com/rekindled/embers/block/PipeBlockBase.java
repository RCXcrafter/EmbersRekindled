package com.rekindled.embers.block;

import javax.annotation.Nullable;

import com.rekindled.embers.datagen.EmbersBlockTags;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

public abstract class PipeBlockBase extends BaseEntityBlock implements SimpleWaterloggedBlock {

	public static final EnumProperty<PipeConnection> DOWN = EnumProperty.create("down", PipeConnection.class);
	public static final EnumProperty<PipeConnection> UP = EnumProperty.create("up", PipeConnection.class);
	public static final EnumProperty<PipeConnection> NORTH = EnumProperty.create("north", PipeConnection.class);
	public static final EnumProperty<PipeConnection> SOUTH = EnumProperty.create("south", PipeConnection.class);
	public static final EnumProperty<PipeConnection> WEST = EnumProperty.create("west", PipeConnection.class);
	public static final EnumProperty<PipeConnection> EAST = EnumProperty.create("east", PipeConnection.class);

	@SuppressWarnings("unchecked")
	public static final EnumProperty<PipeConnection>[] DIRECTIONS = new EnumProperty[] { DOWN, UP, NORTH, SOUTH, WEST, EAST };

	public static final VoxelShape CENTER_AABB = Block.box(6,6,6,10,10,10);
	public static final VoxelShape PIPE_DOWN_AABB = Block.box(6,0,6,10,6,10);
	public static final VoxelShape END_DOWN_AABB = Shapes.joinUnoptimized(Block.box(5,0,5,11,4,11), PIPE_DOWN_AABB, BooleanOp.OR);
	public static final VoxelShape PIPE_UP_AABB = Block.box(6,10,6,10,16,10);
	public static final VoxelShape END_UP_AABB = Shapes.joinUnoptimized(Block.box(5,12,5,11,16,11), PIPE_UP_AABB, BooleanOp.OR);
	public static final VoxelShape PIPE_NORTH_AABB = Block.box(6,6,0,10,10,6);
	public static final VoxelShape END_NORTH_AABB = Shapes.joinUnoptimized(Block.box(5,5,0,11,11,4), PIPE_NORTH_AABB, BooleanOp.OR);
	public static final VoxelShape PIPE_SOUTH_AABB = Block.box(6,6,10,10,10,16);
	public static final VoxelShape END_SOUTH_AABB = Shapes.joinUnoptimized(Block.box(5,5,12,11,11,16), PIPE_SOUTH_AABB, BooleanOp.OR);
	public static final VoxelShape PIPE_WEST_AABB = Block.box(0,6,6,6,10,10);
	public static final VoxelShape END_WEST_AABB = Shapes.joinUnoptimized(Block.box(0,5,5,4,11,11), PIPE_WEST_AABB, BooleanOp.OR);
	public static final VoxelShape PIPE_EAST_AABB = Block.box(10,6,6,16,10,10);
	public static final VoxelShape END_EAST_AABB = Shapes.joinUnoptimized(Block.box(12,5,5,16,11,11), PIPE_EAST_AABB, BooleanOp.OR);
	public static final VoxelShape[] SHAPES = new VoxelShape[729];

	static {
		for (PipeConnection down : PipeConnection.values()) {
			for (PipeConnection up : PipeConnection.values()) {
				for (PipeConnection north : PipeConnection.values()) {
					for (PipeConnection south : PipeConnection.values()) {
						for (PipeConnection west : PipeConnection.values()) {
							for (PipeConnection east : PipeConnection.values()) {
								VoxelShape shape = CENTER_AABB;
								if (down == PipeConnection.PIPE)
									shape = Shapes.joinUnoptimized(shape, PIPE_DOWN_AABB, BooleanOp.OR);
								else if (down == PipeConnection.END)
									shape = Shapes.joinUnoptimized(shape, END_DOWN_AABB, BooleanOp.OR);
								if (up == PipeConnection.PIPE)
									shape = Shapes.joinUnoptimized(shape, PIPE_UP_AABB, BooleanOp.OR);
								else if (up == PipeConnection.END)
									shape = Shapes.joinUnoptimized(shape, END_UP_AABB, BooleanOp.OR);
								if (north == PipeConnection.PIPE)
									shape = Shapes.joinUnoptimized(shape, PIPE_NORTH_AABB, BooleanOp.OR);
								else if (north == PipeConnection.END)
									shape = Shapes.joinUnoptimized(shape, END_NORTH_AABB, BooleanOp.OR);
								if (south == PipeConnection.PIPE)
									shape = Shapes.joinUnoptimized(shape, PIPE_SOUTH_AABB, BooleanOp.OR);
								else if (south == PipeConnection.END)
									shape = Shapes.joinUnoptimized(shape, END_SOUTH_AABB, BooleanOp.OR);
								if (west == PipeConnection.PIPE)
									shape = Shapes.joinUnoptimized(shape, PIPE_WEST_AABB, BooleanOp.OR);
								else if (west == PipeConnection.END)
									shape = Shapes.joinUnoptimized(shape, END_WEST_AABB, BooleanOp.OR);
								if (east == PipeConnection.PIPE)
									shape = Shapes.joinUnoptimized(shape, PIPE_EAST_AABB, BooleanOp.OR);
								else if (east == PipeConnection.END)
									shape = Shapes.joinUnoptimized(shape, END_EAST_AABB, BooleanOp.OR);
								SHAPES[getShapeIndex(down, up, north, south, west, east)] = shape.optimize();
							}
						}
					}
				}
			}
		}
	}

	public PipeBlockBase(Properties pProperties) {
		super(pProperties);
		this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false)
				.setValue(DOWN, PipeConnection.NONE)
				.setValue(UP, PipeConnection.NONE)
				.setValue(NORTH, PipeConnection.NONE)
				.setValue(SOUTH, PipeConnection.NONE)
				.setValue(WEST, PipeConnection.NONE)
				.setValue(EAST, PipeConnection.NONE));
	}

	public static int getShapeIndex(PipeConnection down, PipeConnection up, PipeConnection north, PipeConnection south, PipeConnection west, PipeConnection east) {
		return (((((down.index * 3 + up.index) * 3 + north.index) * 3 + south.index) * 3 + west.index) * 3) + east.index;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES[getShapeIndex(state.getValue(DOWN), state.getValue(UP), state.getValue(NORTH), state.getValue(SOUTH), state.getValue(WEST), state.getValue(EAST))];
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState blockstate = this.defaultBlockState();
		for (Direction direction : Direction.values()) {
			BlockState facingState = context.getLevel().getBlockState(context.getClickedPos().relative(direction));
			if (facingState.is(EmbersBlockTags.ITEM_PIPE_CONNECTION)) {
				blockstate = blockstate.setValue(DIRECTIONS[direction.get3DDataValue()], PipeConnection.PIPE);
			} else {
				BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos().relative(direction));
				if (connected(direction, facingState) || (blockEntity != null && blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite()).isPresent())) {
					blockstate = blockstate.setValue(DIRECTIONS[direction.get3DDataValue()], PipeConnection.END);
				} else {
					blockstate = blockstate.setValue(DIRECTIONS[direction.get3DDataValue()], PipeConnection.NONE);
				}
			}
		}
		return blockstate.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER));
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
		if (pState.getValue(BlockStateProperties.WATERLOGGED)) {
			pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}
		if (pFacingState.is(EmbersBlockTags.ITEM_PIPE_CONNECTION)) {
			pState = pState.setValue(DIRECTIONS[pFacing.get3DDataValue()], PipeConnection.PIPE);
		} else {
			BlockEntity blockEntity = pLevel.getBlockEntity(pFacingPos);
			if (connected(pFacing, pFacingState) || (blockEntity != null && blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, pFacing.getOpposite()).isPresent())) {
				pState = pState.setValue(DIRECTIONS[pFacing.get3DDataValue()], PipeConnection.END);
			} else {
				pState = pState.setValue(DIRECTIONS[pFacing.get3DDataValue()], PipeConnection.NONE);
			}
		}
		return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
	}

	public static boolean facingConnected(Direction facing, BlockState state, DirectionProperty property) {
		return !state.hasProperty(property) || state.getValue(property) == facing;
	}

	public abstract boolean connected(Direction direction, BlockState state);

	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		if (!pState.is(pNewState.getBlock())) {
			BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
			IItemHandler handler = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).orElse(null);
			if (handler != null) {
				Misc.spawnInventoryInWorld(pLevel, pPos.getX() + 0.5, pPos.getY() + 0.5, pPos.getZ() + 0.5, handler);
				pLevel.updateNeighbourForOutputSignal(pPos, this);
			}
			super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(DIRECTIONS).add(BlockStateProperties.WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}

	public static enum PipeConnection implements StringRepresentable {
		NONE("none", 0),
		PIPE("pipe", 1),
		END("end", 2);

		private final String name;
		public final int index;

		private PipeConnection(String pName, int index) {
			this.name = pName;
			this.index = index;
		}

		public String toString() {
			return this.name;
		}

		public String getSerializedName() {
			return this.name;
		}
	}
}
