package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CaminiteRingEdgeBlock extends MechEdgeBlockBase {

	public static final VoxelShape X_AABB = Shapes.or(Block.box(0,0,4,16,16,12), Block.box(1,0,3,7,16,13), Block.box(9,0,3,15,16,13));
	public static final VoxelShape Z_AABB = Shapes.or(Block.box(4,0,0,12,16,16), Block.box(3,0,1,13,16,7), Block.box(3,0,9,13,16,15));
	public static final VoxelShape NORTHEAST_AABB = Shapes.or(Block.box(0,0,4,12,16,12), Block.box(4,0,4,12,16,16));
	public static final VoxelShape SOUTHEAST_AABB = Shapes.or(Block.box(0,0,4,12,16,12), Block.box(4,0,0,12,16,12));
	public static final VoxelShape SOUTHWEST_AABB = Shapes.or(Block.box(4,0,4,16,16,12), Block.box(4,0,0,12,16,12));
	public static final VoxelShape NORTHWEST_AABB = Shapes.or(Block.box(4,0,4,16,16,12), Block.box(4,0,4,12,16,16));
	public static final VoxelShape[] SHAPES = new VoxelShape[] { X_AABB, NORTHEAST_AABB, Z_AABB, SOUTHEAST_AABB, X_AABB, SOUTHWEST_AABB, Z_AABB, NORTHWEST_AABB };

	public CaminiteRingEdgeBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES[state.getValue(EDGE).index];
	}

	@Override
	public Block getCenterBlock() {
		return RegistryManager.CAMINITE_RING.get();
	}
}
