package com.rekindled.embers.block;

import java.util.List;

import javax.annotation.Nullable;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.blockentity.CopperCellBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CopperCellBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

	protected static final VoxelShape CELL_AABB = Shapes.or(Block.box(2,2,2,14,14,14),Block.box(0,0,0,4,6,4),Block.box(12,0,0,16,6,4),Block.box(12,0,12,16,6,16),Block.box(0,0,12,4,6,16),Block.box(0,10,12,4,16,16),Block.box(12,10,12,16,16,16),Block.box(12,10,0,16,16,4),Block.box(0,10,0,4,16,4),Block.box(0,12,4,4,16,6),Block.box(0,12,10,4,16,12),Block.box(0,0,10,4,4,12),Block.box(0,0,4,4,4,6),Block.box(12,0,4,16,4,6),Block.box(12,0,10,16,4,12),Block.box(12,12,10,16,16,12),Block.box(12,12,4,16,16,6),Block.box(10,12,12,12,16,16),Block.box(4,12,12,6,16,16),Block.box(4,0,12,6,4,16),Block.box(10,0,12,12,4,16),Block.box(10,0,0,12,4,4),Block.box(4,0,0,6,4,4),Block.box(4,12,0,6,16,4),Block.box(10,12,0,12,16,4),Block.box(1,6,1,3,10,3),Block.box(13,6,1,15,10,3),Block.box(13,6,13,15,10,15),Block.box(1,6,13,3,10,15),Block.box(1,1,6,3,3,10),Block.box(1,13,6,3,15,10),Block.box(13,13,6,15,15,10),Block.box(13,1,6,15,3,10),Block.box(6,1,13,10,3,15),Block.box(6,13,13,10,15,15),Block.box(6,13,1,10,15,3),Block.box(6,1,1,10,3,3),Block.box(5,0,5,11,16,11));

	public CopperCellBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return CELL_AABB;
	}

	@Override
	public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return Shapes.block();
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return Shapes.block();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.COPPER_CELL_ENTITY.get().create(pPos, pState);
	}

	@Override
	public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pBuilder) {
		List<ItemStack> items = super.getDrops(pState, pBuilder);
		BlockEntity blockentity = pBuilder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockentity instanceof CopperCellBlockEntity) {
			CompoundTag nbt = blockentity.saveWithoutMetadata();
			if (nbt != null) {
				for (ItemStack stack : items) {
					if (stack.getItem() == RegistryManager.COPPER_CELL_ITEM.get()) {
						IEmberCapability cap = stack.getCapability(EmbersCapabilities.EMBER_CAPABILITY).orElse(null);
						if (cap != null) {
							cap.setEmber(nbt.getDouble(IEmberCapability.EMBER));
							cap.setEmberCapacity(nbt.getDouble(IEmberCapability.EMBER_CAPACITY));
						}
					}
				}
			}
		}
		return items;
	}

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
		pBuilder.add(BlockStateProperties.WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}
}
