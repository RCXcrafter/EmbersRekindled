package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.CombustionChamberBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CombustionChamberBlock extends ChamberBlockBase {

	protected static final VoxelShape BASE_AABB = Shapes.or(Block.box(0,0,0,16,4,16),Block.box(1,4,1,5,16,5),Block.box(11,4,1,15,16,5),Block.box(1,4,11,5,16,15),Block.box(11,4,11,15,16,15),Block.box(2,4,2,14,12,14),Block.box(6,6,0,10,10,16),Block.box(0,6,6,16,10,10),Block.box(4,12,4,12,14,12),Block.box(5,14,5,11,16,11));

	public CombustionChamberBlock(Properties properties, SoundType topSound) {
		super(properties, topSound);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(CONNECTION) == ChamberConnection.BOTTOM ? BASE_AABB : TOP_AABB;
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getValue(CONNECTION) == ChamberConnection.BOTTOM ? Shapes.block() : Shapes.empty();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		if (pState.getValue(CONNECTION) == ChamberConnection.BOTTOM)
			return RegistryManager.COMBUSTION_CHAMBER_ENTITY.get().create(pPos, pState);
		return null;
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		if (pState.getValue(CONNECTION) == ChamberConnection.BOTTOM)
			return pLevel.isClientSide ? null : createTickerHelper(pBlockEntityType, RegistryManager.COMBUSTION_CHAMBER_ENTITY.get(), CombustionChamberBlockEntity::serverTick);
		return null;
	}
}
