package com.rekindled.embers.block;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class MechEdgeBlockBase extends Block implements SimpleWaterloggedBlock {

	public static final EnumProperty<MechEdge> EDGE = EnumProperty.create("edge", MechEdge.class);

	public static final VoxelShape TOP_AABB = Block.box(0,12,0,16,16,16);
	public static final VoxelShape BOTTOM_AABB = Block.box(0,0,0,16,4,16);

	public static final VoxelShape NORTH_AABB = Shapes.or(Block.box(0,4,2,16,12,16), TOP_AABB, BOTTOM_AABB);
	public static final VoxelShape NORTHEAST_AABB = Shapes.or(Block.box(0,4,2,14,12,16), TOP_AABB, BOTTOM_AABB);
	public static final VoxelShape EAST_AABB = Shapes.or(Block.box(0,4,0,14,12,16), TOP_AABB, BOTTOM_AABB);
	public static final VoxelShape SOUTHEAST_AABB = Shapes.or(Block.box(0,4,0,14,12,14), TOP_AABB, BOTTOM_AABB);
	public static final VoxelShape SOUTH_AABB = Shapes.or(Block.box(0,4,0,16,12,14), TOP_AABB, BOTTOM_AABB);
	public static final VoxelShape SOUTHWEST_AABB = Shapes.or(Block.box(2,4,0,16,12,14), TOP_AABB, BOTTOM_AABB);
	public static final VoxelShape WEST_AABB = Shapes.or(Block.box(2,4,0,16,12,16), TOP_AABB, BOTTOM_AABB);
	public static final VoxelShape NORTHWEST_AABB = Shapes.or(Block.box(2,4,2,16,12,16), TOP_AABB, BOTTOM_AABB);
	public static final VoxelShape[] SHAPES = new VoxelShape[] { NORTH_AABB, NORTHEAST_AABB, EAST_AABB, SOUTHEAST_AABB, SOUTH_AABB, SOUTHWEST_AABB, WEST_AABB, NORTHWEST_AABB };

	public MechEdgeBlockBase(Properties pProperties) {
		super(pProperties);
		this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false).setValue(EDGE, MechEdge.NORTH));
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockPos centerPos = pos.offset(state.getValue(EDGE).centerPos);
			if (level.getBlockState(centerPos).getBlock() == getCenterBlock()) {
				//the center block takes care of removing the remaining edges
				level.destroyBlock(centerPos, true);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES[state.getValue(EDGE).index];
	}

	public abstract Block getCenterBlock();

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return super.getStateForPlacement(pContext).setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER));
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
		pBuilder.add(EDGE).add(BlockStateProperties.WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}

	public static enum MechEdge implements StringRepresentable {
		NORTH("north", 0, new Vec3i(0, 0, 1), false, 0),
		NORTHEAST("northeast", 1, new Vec3i(-1, 0, 1), true, 0),
		EAST("east", 2, new Vec3i(-1, 0, 0), false, 90),
		SOUTHEAST("southeast", 3, new Vec3i(-1, 0, -1), true, 90),
		SOUTH("south", 4, new Vec3i(0, 0, -1), false, 180),
		SOUTHWEST("southwest", 5, new Vec3i(1, 0, -1), true, 180),
		WEST("west", 6, new Vec3i(1, 0, 0), false, 270),
		NORTHWEST("northwest", 7, new Vec3i(1, 0, 1), true, 270);

		private final String name;
		public final int index;
		public final Vec3i centerPos;
		public final boolean corner;
		public final int rotation;

		private MechEdge(String name, int index, Vec3i center, boolean corner, int rotation) {
			this.name = name;
			this.index = index;
			this.centerPos = center;
			this.corner = corner;
			this.rotation = rotation;
		}

		public String toString() {
			return this.name;
		}

		public String getSerializedName() {
			return this.name;
		}
	}
}
