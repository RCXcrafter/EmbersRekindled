package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.MelterBottomBlockEntity;
import com.rekindled.embers.blockentity.MelterTopBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class MelterBlock extends DoubleTallMachineBlock {

	protected static final VoxelShape BASE_AABB = Shapes.or(Block.box(2,0,2,14,16,14), Block.box(0,8,0,4,16,4), Block.box(0,8,12,4,16,16), Block.box(12,8,0,16,16,4), Block.box(12,8,12,16,16,16), Block.box(1,0,1,4,8,4), Block.box(1,0,12,4,8,15), Block.box(12,0,1,15,8,4), Block.box(12,0,12,15,8,15));
	protected static final VoxelShape TOP_AABB = Shapes.join(Shapes.block(), Block.box(4,0,4,12,16,12), BooleanOp.ONLY_FIRST);

	public MelterBlock(Properties properties, SoundType topSound) {
		super(properties, topSound);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) != DoubleBlockHalf.LOWER && level.getBlockEntity(pos) instanceof MelterTopBlockEntity melterEntity) {
			ItemStack heldItem = player.getItemInHand(hand);
			if (!heldItem.isEmpty()) {
				IFluidHandler cap = melterEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, hit.getDirection()).orElse(null);
				if (cap != null) {
					boolean didFill = FluidUtil.interactWithFluidHandler(player, hand, cap);

					if (didFill) {
						return InteractionResult.SUCCESS;
					} else {
						ItemStack leftover = melterEntity.inventory.insertItem(0, heldItem, false);
						if (!leftover.equals(heldItem)) {
							player.setItemInHand(hand, leftover);
							return InteractionResult.SUCCESS;
						}
					}
				}
			} else {
				if (!melterEntity.inventory.getStackInSlot(0).isEmpty() && !level.isClientSide) {
					level.addFreshEntity(new ItemEntity(level, player.position().x, player.position().y, player.position().z, melterEntity.inventory.getStackInSlot(0)));
					melterEntity.inventory.setStackInSlot(0, ItemStack.EMPTY);
					return InteractionResult.SUCCESS;
				}
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? BASE_AABB : TOP_AABB;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		if (pState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
			return RegistryManager.MELTER_BOTTOM_ENTITY.get().create(pPos, pState);
		return RegistryManager.MELTER_TOP_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		if (pState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
			return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, RegistryManager.MELTER_BOTTOM_ENTITY.get(), MelterBottomBlockEntity::clientTick) : createTickerHelper(pBlockEntityType, RegistryManager.MELTER_BOTTOM_ENTITY.get(), MelterBottomBlockEntity::serverTick);
		return pLevel.isClientSide ? createTickerHelper(pBlockEntityType, RegistryManager.MELTER_TOP_ENTITY.get(), MelterTopBlockEntity::clientTick) : createTickerHelper(pBlockEntityType, RegistryManager.MELTER_TOP_ENTITY.get(), MelterTopBlockEntity::serverTick);
	}
}
