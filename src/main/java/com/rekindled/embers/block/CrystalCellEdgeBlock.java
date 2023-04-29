package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CrystalCellEdgeBlock extends MechEdgeBlockBase {

	public static final VoxelShape NORTH_AABB = Shapes.or(Block.box(2,0,4,14,13,16),
			Block.box(0,0,2,2,15,16), Block.box(14,0,2,16,15,16),
			Block.box(2,0,0,14,4,2), Block.box(2,0,1,14,8,16),
			Block.box(3,13,6,13,15,16));
	public static final VoxelShape NORTHEAST_AABB = Shapes.or(BOTTOM_AABB, Block.box(0,0,1,15,8,16), Block.box(0,0,4,12,13,16), Block.box(0,0,6,10,15,16));
	public static final VoxelShape EAST_AABB = Shapes.or(Block.box(0,0,2,12,13,14),
			Block.box(0,0,0,14,15,2), Block.box(0,0,14,14,15,16),
			Block.box(14,0,2,16,4,14), Block.box(0,0,2,15,8,14),
			Block.box(0,13,3,10,15,13));
	public static final VoxelShape SOUTHEAST_AABB = Shapes.or(BOTTOM_AABB, Block.box(0,0,0,15,8,15), Block.box(0,0,0,12,13,12), Block.box(0,0,0,10,15,10));
	public static final VoxelShape SOUTH_AABB = Shapes.or(Block.box(2,0,0,14,13,12),
			Block.box(0,0,0,2,15,14), Block.box(14,0,0,16,15,14),
			Block.box(2,0,14,14,4,16), Block.box(2,0,0,14,8,15),
			Block.box(3,13,0,13,15,10));
	public static final VoxelShape SOUTHWEST_AABB = Shapes.or(BOTTOM_AABB, Block.box(1,0,0,16,8,15), Block.box(4,0,0,16,13,12), Block.box(6,0,0,16,15,10));
	public static final VoxelShape WEST_AABB = Shapes.or(Block.box(4,0,2,16,13,14),
			Block.box(2,0,0,16,15,2), Block.box(2,0,14,16,15,16),
			Block.box(0,0,2,2,4,14), Block.box(1,0,2,16,8,14),
			Block.box(6,13,3,16,15,13));
	public static final VoxelShape NORTHWEST_AABB = Shapes.or(BOTTOM_AABB, Block.box(1,0,1,16,8,16), Block.box(4,0,4,16,13,16), Block.box(6,0,6,16,15,16));
	public static final VoxelShape[] SHAPES = new VoxelShape[] { NORTH_AABB, NORTHEAST_AABB, EAST_AABB, SOUTHEAST_AABB, SOUTH_AABB, SOUTHWEST_AABB, WEST_AABB, NORTHWEST_AABB };

	public CrystalCellEdgeBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES[state.getValue(EDGE).index];
	}

	@Override
	public Block getCenterBlock() {
		return RegistryManager.CRYSTAL_CELL.get();
	}
}
