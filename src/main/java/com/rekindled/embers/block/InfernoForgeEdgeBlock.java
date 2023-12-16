package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class InfernoForgeEdgeBlock extends MechEdgeBlockBase {

	public static final VoxelShape NORTH_AABB = Shapes.or(Block.box(0,0,6,16,16,16), Block.box(2.5,3,2,13.5,10,6));
	public static final VoxelShape NORTHEAST_AABB = Shapes.or(Block.box(0,7,6,10,12,16), Block.box(0,0,4,12,7,16));
	public static final VoxelShape EAST_AABB = Shapes.or(Block.box(0,0,0,10,16,16), Block.box(10,3,2.5,14,10,13.5));
	public static final VoxelShape SOUTHEAST_AABB = Shapes.or(Block.box(0,7,0,10,12,10), Block.box(0,0,0,12,7,12));
	public static final VoxelShape SOUTH_AABB = Shapes.or(Block.box(0,0,0,16,16,10), Block.box(2.5,3,10,13.5,10,14));
	public static final VoxelShape SOUTHWEST_AABB = Shapes.or(Block.box(6,7,0,16,12,10), Block.box(4,0,0,16,7,12));
	public static final VoxelShape WEST_AABB = Shapes.or(Block.box(6,0,0,16,16,16), Block.box(2,3,2.5,6,10,13.5));
	public static final VoxelShape NORTHWEST_AABB = Shapes.or(Block.box(6,7,6,16,12,16), Block.box(4,0,4,16,7,16));
	public static final VoxelShape[] SHAPES_TOP = new VoxelShape[] { NORTH_AABB, NORTHEAST_AABB, EAST_AABB, SOUTHEAST_AABB, SOUTH_AABB, SOUTHWEST_AABB, WEST_AABB, NORTHWEST_AABB };

	public static final VoxelShape CORNER_CENTER_AABB = Shapes.or(Block.box(4,6,4,12,10,12), Block.box(2,4,2,14,6,14), Block.box(2,10,2,14,12,14));
	public static final VoxelShape NORTHEAST_BOTTOM_AABB = Shapes.or(Shapes.join(Block.box(0,0,1,15,16,16), Block.box(4,4,1,15,12,12), BooleanOp.ONLY_FIRST), CORNER_CENTER_AABB);
	public static final VoxelShape SOUTHEAST_BOTTOM_AABB = Shapes.or(Shapes.join(Block.box(0,0,0,15,16,15), Block.box(4,4,4,15,12,15), BooleanOp.ONLY_FIRST), CORNER_CENTER_AABB);
	public static final VoxelShape SOUTHWEST_BOTTOM_AABB = Shapes.or(Shapes.join(Block.box(1,0,0,16,16,15), Block.box(1,4,4,12,12,15), BooleanOp.ONLY_FIRST), CORNER_CENTER_AABB);
	public static final VoxelShape NORTHWEST_BOTTOM_AABB = Shapes.or(Shapes.join(Block.box(1,0,1,16,16,16), Block.box(1,4,1,12,12,12), BooleanOp.ONLY_FIRST), CORNER_CENTER_AABB);
	public static final VoxelShape[] SHAPES_BOTTOM = new VoxelShape[] { Shapes.block(), NORTHEAST_BOTTOM_AABB, Shapes.block(), SOUTHEAST_BOTTOM_AABB, Shapes.block(), SOUTHWEST_BOTTOM_AABB, Shapes.block(), NORTHWEST_BOTTOM_AABB };

	public InfernoForgeEdgeBlock(Properties pProperties) {
		super(pProperties);
		this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
			return SHAPES_BOTTOM[state.getValue(EDGE).index];
		return SHAPES_TOP[state.getValue(EDGE).index];
	}

	@Override
	public Block getCenterBlock() {
		return RegistryManager.INFERNO_FORGE.get();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(BlockStateProperties.DOUBLE_BLOCK_HALF);
	}
}
