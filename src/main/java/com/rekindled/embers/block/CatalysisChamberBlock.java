package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.CatalysisChamberBlockEntity;

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

public class CatalysisChamberBlock extends ChamberBlockBase {

	protected static final VoxelShape BASE_AABB = Shapes.or(Block.box(0,0,0,16,4,16),Block.box(1,4,6,15,16,10),Block.box(6,4,1,10,16,15),Block.box(6,6,0,10,10,16),Block.box(0,6,6,16,10,10),Block.box(2,4,2,14,11,14),Block.box(3,11,3,13,14,13));

	public CatalysisChamberBlock(Properties properties, SoundType topSound) {
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
			return RegistryManager.CATALYSIS_CHAMBER_ENTITY.get().create(pPos, pState);
		return null;
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		if (pState.getValue(CONNECTION) == ChamberConnection.BOTTOM)
			return pLevel.isClientSide ? null : createTickerHelper(pBlockEntityType, RegistryManager.CATALYSIS_CHAMBER_ENTITY.get(), CatalysisChamberBlockEntity::serverTick);
		return null;
	}
}
