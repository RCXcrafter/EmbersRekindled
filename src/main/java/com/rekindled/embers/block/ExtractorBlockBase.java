package com.rekindled.embers.block;

import com.rekindled.embers.datagen.EmbersBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class ExtractorBlockBase extends PipeBlockBase {

	public static final VoxelShape CENTER_AABB = Block.box(5,5,5,11,11,11);
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

	public ExtractorBlockBase(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES[getShapeIndex(state.getValue(DOWN), state.getValue(UP), state.getValue(NORTH), state.getValue(SOUTH), state.getValue(WEST), state.getValue(EAST))];
	}

	@Override
	public boolean connected(Direction direction, BlockState state) {
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
		BooleanProperty sideProp = EmberEmitterBlock.DIRECTIONS[direction.getOpposite().get3DDataValue()];
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
		if (state.hasProperty(BlockStateProperties.AXIS)) {
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
	public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return Shapes.block();
	}
}
