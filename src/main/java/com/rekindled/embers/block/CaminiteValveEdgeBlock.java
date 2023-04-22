package com.rekindled.embers.block;

import javax.annotation.Nullable;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.CaminiteValveBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CaminiteValveEdgeBlock extends MechEdgeBlockBase implements EntityBlock {

	public static final VoxelShape X_AABB = Shapes.or(Block.box(0,0,4,16,16,12), Block.box(1,0,3,7,16,13), Block.box(9,0,3,15,16,13), Block.box(4,4,1,12,12,15));
	public static final VoxelShape Z_AABB = Shapes.or(Block.box(4,0,0,12,16,16), Block.box(3,0,1,13,16,7), Block.box(3,0,9,13,16,15), Block.box(1,4,4,15,12,12));
	public static final VoxelShape NORTHEAST_AABB = Shapes.or(Block.box(0,0,4,12,16,12), Block.box(4,0,4,12,16,16));
	public static final VoxelShape SOUTHEAST_AABB = Shapes.or(Block.box(0,0,4,12,16,12), Block.box(4,0,0,12,16,12));
	public static final VoxelShape SOUTHWEST_AABB = Shapes.or(Block.box(4,0,4,16,16,12), Block.box(4,0,0,12,16,12));
	public static final VoxelShape NORTHWEST_AABB = Shapes.or(Block.box(4,0,4,16,16,12), Block.box(4,0,4,12,16,16));
	public static final VoxelShape[] SHAPES = new VoxelShape[] { X_AABB, NORTHEAST_AABB, Z_AABB, SOUTHEAST_AABB, X_AABB, SOUTHWEST_AABB, Z_AABB, NORTHWEST_AABB };

	public CaminiteValveEdgeBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES[state.getValue(EDGE).index];
	}

	@Override
	public Block getCenterBlock() {
		return RegistryManager.CAMINITE_VALVE.get();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return pState.getValue(EDGE).corner ? null : RegistryManager.CAMINITE_VALVE_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pState.getValue(EDGE).corner ? null : createTickerHelper(pBlockEntityType, RegistryManager.CAMINITE_VALVE_ENTITY.get(), CaminiteValveBlockEntity::commonTick);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> pServerType, BlockEntityType<E> pClientType, BlockEntityTicker<? super E> pTicker) {
		return pClientType == pServerType ? (BlockEntityTicker<A>)pTicker : null;
	}
}
