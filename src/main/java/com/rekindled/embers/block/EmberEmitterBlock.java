package com.rekindled.embers.block;

import javax.annotation.Nullable;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.EmberEmitterBlockEntity;
import com.rekindled.embers.datagen.EmbersBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EmberEmitterBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

	protected static final VoxelShape UP_AABB = Shapes.box(0.25,0,0.25,0.75,0.9375,0.75);
	protected static final VoxelShape DOWN_AABB = Shapes.box(0.25,0.0625,0.25,0.75,1.0,0.75);
	protected static final VoxelShape NORTH_AABB = Shapes.box(0.25,0.25,0.0625,0.75,0.75,1.0);
	protected static final VoxelShape SOUTH_AABB = Shapes.box(0.25,0.25,0,0.75,0.75,0.9375);
	protected static final VoxelShape WEST_AABB = Shapes.box(0.0625,0.25,0.25,1.0,0.75,0.75);
	protected static final VoxelShape EAST_AABB = Shapes.box(0.0,0.25,0.25,0.9375,0.75,0.75);
	protected static final VoxelShape SUPPORT_X = Shapes.or(Shapes.box(0,0,0,1,1,0.1), Shapes.box(0,0,0.9,1,1,1), Shapes.box(0,0,0,1,0.1,1), Shapes.box(0,0.9,0,1,1,1));
	protected static final VoxelShape SUPPORT_Y = Shapes.or(Shapes.box(0,0,0,1,1,0.1), Shapes.box(0,0,0.9,1,1,1), Shapes.box(0,0,0,0.1,1,1), Shapes.box(0.9,0,0,1,1,1));
	protected static final VoxelShape SUPPORT_Z = Shapes.or(Shapes.box(0,0,0,1,0.1,1), Shapes.box(0,0.9,0,1,1,1), Shapes.box(0,0,0,0.1,1,1), Shapes.box(0.9,0,0,1,1,1));

	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final Direction[] X_DIRECTIONS = { Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH };
	public static final Direction[] Y_DIRECTIONS = { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
	public static final Direction[] Z_DIRECTIONS = { Direction.DOWN, Direction.UP, Direction.WEST, Direction.EAST };
	public static final BooleanProperty[] DIRECTIONS = { BooleanProperty.create("front"), BooleanProperty.create("right"), BooleanProperty.create("back"), BooleanProperty.create("left") };
	public static final BooleanProperty[] ALL_DIRECTIONS = { BlockStateProperties.DOWN, BlockStateProperties.UP, BlockStateProperties.NORTH, BlockStateProperties.SOUTH, BlockStateProperties.WEST, BlockStateProperties.EAST };

	public EmberEmitterBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(BlockStateProperties.WATERLOGGED, false)
				.setValue(DIRECTIONS[0], false)
				.setValue(DIRECTIONS[1], false)
				.setValue(DIRECTIONS[2], false)
				.setValue(DIRECTIONS[3], false));
	}

	public static int getIndexForDirection(Direction.Axis axis, Direction direction) {
		int index = direction.get3DDataValue();
		switch (axis) {
		case Z:
			return index > 1 ? index - 2 : index;
		case Y:
			return index - 2;
		case X:
		default:
			return index;
		}
	}

	public static Direction getDirectionForIndex(Direction.Axis axis, int index) {
		switch (axis) {
		case X:
			return X_DIRECTIONS[index];
		case Z:
			return Z_DIRECTIONS[index];
		case Y:
		default:
			return Y_DIRECTIONS[index];
		}
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
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		return canAttach(pLevel, pPos, pState.getValue(FACING).getOpposite());
	}

	public static boolean canAttach(LevelReader pReader, BlockPos pPos, Direction pDirection) {
		return !pReader.getBlockState(pPos.relative(pDirection)).isAir();
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		for (Direction direction : pContext.getNearestLookingDirections()) {
			BlockState blockstate = this.defaultBlockState().setValue(FACING, direction.getOpposite());
			if (blockstate.canSurvive(pContext.getLevel(), pContext.getClickedPos())) {
				for (int i = 0; i < DIRECTIONS.length; i++) {
					Direction facing = getDirectionForIndex(direction.getAxis(), i);
					blockstate = blockstate.setValue(DIRECTIONS[i], connected(facing, pContext.getLevel().getBlockState(pContext.getClickedPos().relative(facing))));
				}
				return blockstate.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER));
			}
		}
		return null;
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
		if (pState.getValue(BlockStateProperties.WATERLOGGED)) {
			pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}
		if (pState.getValue(FACING).getAxis() != pFacing.getAxis()) {
			pState = pState.setValue(DIRECTIONS[getIndexForDirection(pState.getValue(FACING).getAxis(), pFacing)], connected(pFacing, pFacingState));
		}
		return pState.getValue(FACING).getOpposite() == pFacing && !pState.canSurvive(pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
	}

	private static boolean facingConnected(Direction facing, BlockState state, DirectionProperty property) {
		return !state.hasProperty(property) || state.getValue(property) == facing;
	}

	private static boolean connected(Direction direction, BlockState state) {
		if (!state.is(EmbersBlockTags.EMITTER_CONNECTION)) {
			return false;
		}
		//always connect to floor or ceiling blocks but only on the top and bottom
		if (state.is(EmbersBlockTags.EMITTER_CONNECTION_FLOOR)) {
			if (direction == Direction.DOWN && state.is(EmbersBlockTags.EMITTER_CONNECTION_CEILING)) {
				return true;
			}
			return direction == Direction.UP;
		}
		//if the block has a side property, use that
		BooleanProperty sideProp = ALL_DIRECTIONS[direction.getOpposite().get3DDataValue()];
		if (state.hasProperty(sideProp) && state.getValue(sideProp)) {
			return true;
		}
		//only support ceiling bells
		if (state.hasProperty(BlockStateProperties.BELL_ATTACHMENT) && state.getValue(BlockStateProperties.BELL_ATTACHMENT) == BellAttachType.CEILING && direction == Direction.DOWN) {
			return true;
		}
		//lantern hanging property
		if (state.hasProperty(BlockStateProperties.HANGING)) {
			if (direction == Direction.DOWN && state.getValue(BlockStateProperties.HANGING))
				return true;
			if (direction == Direction.UP && !state.getValue(BlockStateProperties.HANGING))
				return true;
			return false;
		}
		//connect to blocks on the same axis
		if (state.hasProperty(BlockStateProperties.AXIS) && !state.hasProperty(BlockStateProperties.FACING)) {
			return state.getValue(BlockStateProperties.AXIS) == direction.getAxis();
		}
		//if there is a face property and it is not wall, only check floor and ceiling
		if (state.hasProperty(BlockStateProperties.ATTACH_FACE) && state.getValue(BlockStateProperties.ATTACH_FACE) != AttachFace.WALL) {
			if (direction == Direction.DOWN && state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.CEILING)
				return true;
			if (direction == Direction.UP && state.getValue(BlockStateProperties.ATTACH_FACE) == AttachFace.FLOOR)
				return true;
			return false;
		}
		//try relevant facing properties, if any are present must be facing this
		return facingConnected(direction, state, BlockStateProperties.HORIZONTAL_FACING)
				&& facingConnected(direction, state, BlockStateProperties.FACING)
				&& facingConnected(direction, state, BlockStateProperties.FACING_HOPPER);
	}

	@Override
	public BlockState rotate(BlockState pState, Rotation pRot) {
		return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(FACING).add(DIRECTIONS).add(BlockStateProperties.WATERLOGGED);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.EMBER_EMITTER_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pLevel.isClientSide ? null : createTickerHelper(pBlockEntityType, RegistryManager.EMBER_EMITTER_ENTITY.get(), EmberEmitterBlockEntity::serverTick);
	}

	@Override
	public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		switch (pState.getValue(FACING).getAxis()) {
		case X:
			return SUPPORT_X;
		case Y:
			return SUPPORT_Y;
		case Z:
		default:
			return SUPPORT_Z;
		}
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}
}
