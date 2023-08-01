package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.AlchemyPedestalBlockEntity;
import com.rekindled.embers.blockentity.AlchemyPedestalTopBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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

public class AlchemyPedestalBlock extends DoubleTallMachineBlock {

	protected static final VoxelShape BASE_AABB = Shapes.or(Block.box(1,0,1,6,4,6), Block.box(10,0,10,15,4,15), Block.box(10,0,1,15,4,6), Block.box(1,0,10,6,4,15), Block.box(3,0,3,13,16,13));
	protected static final VoxelShape TOP_AABB = Shapes.joinUnoptimized(Shapes.or(Block.box(1,0,1,6,4,6), Block.box(10,0,10,15,4,15), Block.box(10,0,1,15,4,6), Block.box(1,0,10,6,4,15),
			Block.box(3,0,3,13,4,13), Block.box(4,4,4,12,6,12), Block.box(2,6,2,14,8,14)),
			Block.box(6,4,6,10,16,10), BooleanOp.ONLY_FIRST);

	public AlchemyPedestalBlock(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof AlchemyPedestalBlockEntity pedestalEntity) {
			ItemStack heldItem = player.getItemInHand(hand);
			if (!heldItem.isEmpty()) {
				ItemStack leftover = pedestalEntity.inventory.insertItem(0, heldItem, false);
				if (!leftover.equals(heldItem)) {
					player.setItemInHand(hand, leftover);
					return InteractionResult.SUCCESS;
				}
			} else {
				if (!pedestalEntity.inventory.getStackInSlot(0).isEmpty() && !level.isClientSide) {
					level.addFreshEntity(new ItemEntity(level, player.position().x, player.position().y, player.position().z, pedestalEntity.inventory.getStackInSlot(0)));
					pedestalEntity.inventory.setStackInSlot(0, ItemStack.EMPTY);
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
			return RegistryManager.ALCHEMY_PEDESTAL_ENTITY.get().create(pPos, pState);
		return RegistryManager.ALCHEMY_PEDESTAL_TOP_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pLevel.isClientSide && pState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER ? createTickerHelper(pBlockEntityType, RegistryManager.ALCHEMY_PEDESTAL_TOP_ENTITY.get(), AlchemyPedestalTopBlockEntity::clientTick) : null;
	}
}
