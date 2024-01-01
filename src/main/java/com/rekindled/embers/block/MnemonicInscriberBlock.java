package com.rekindled.embers.block;

import javax.annotation.Nullable;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.MnemonicInscriberBlockEntity;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

public class MnemonicInscriberBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

	public static final VoxelShape UP_AABB = Shapes.or(Block.box(12,0,7,14,5,9), Block.box(7,0,2,9,5,4), Block.box(7,0,12,9,5,14), Block.box(2,0,7,4,5,9), Block.box(3.5,3,3.5,12.5,9,12.5));
	public static final VoxelShape DOWN_AABB = Shapes.or(Block.box(7,11,2,9,16,4), Block.box(12,11,7,14,16,9), Block.box(2,11,7,4,16,9), Block.box(7,11,12,9,16,14), Block.box(3.5,7,3.5,12.5,13,12.5));
	public static final VoxelShape NORTH_AABB = Shapes.or(Block.box(12,7,11,14,9,16), Block.box(7,2,11,9,4,16), Block.box(7,12,11,9,14,16), Block.box(2,7,11,4,9,16), Block.box(3.5,3.5,7,12.5,12.5,13));
	public static final VoxelShape SOUTH_AABB = Shapes.or(Block.box(2,7,0,4,9,5), Block.box(7,2,0,9,4,5), Block.box(7,12,0,9,14,5), Block.box(12,7,0,14,9,5), Block.box(3.5,3.5,3,12.5,12.5,9));
	public static final VoxelShape WEST_AABB = Shapes.or(Block.box(11,7,2,16,9,4), Block.box(11,2,7,16,4,9), Block.box(11,12,7,16,14,9), Block.box(11,7,12,16,9,14), Block.box(7,3.5,3.5,13,12.5,12.5));
	public static final VoxelShape EAST_AABB = Shapes.or(Block.box(0,7,12,5,9,14), Block.box(0,2,7,5,4,9), Block.box(0,12,7,5,14,9), Block.box(0,7,2,5,9,4), Block.box(3,3.5,3.5,9,12.5,12.5));

	public MnemonicInscriberBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.FACING, Direction.UP).setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof MnemonicInscriberBlockEntity inscriberEntity) {
			ItemStack heldItem = player.getItemInHand(hand);
			if (!heldItem.isEmpty()) {
				ItemStack leftover = inscriberEntity.inventory.insertItem(0, heldItem, false);
				if (!leftover.equals(heldItem)) {
					player.setItemInHand(hand, leftover);
					return InteractionResult.SUCCESS;
				}
			} else {
				if (!inscriberEntity.inventory.getStackInSlot(0).isEmpty() && !level.isClientSide) {
					level.addFreshEntity(new ItemEntity(level, player.position().x, player.position().y, player.position().z, inscriberEntity.inventory.getStackInSlot(0)));
					inscriberEntity.inventory.setStackInSlot(0, ItemStack.EMPTY);
					return InteractionResult.SUCCESS;
				}
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if (blockEntity != null) {
				IItemHandler handler = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).orElse(null);
				if (handler != null) {
					Misc.spawnInventoryInWorld(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, handler);
					level.updateNeighbourForOutputSignal(pos, this);
				}
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		switch (pState.getValue(BlockStateProperties.FACING)) {
		case UP:
			return UP_AABB;
		case DOWN:
			return DOWN_AABB;
		case EAST:
			return EAST_AABB;
		case WEST:
			return WEST_AABB;
		case SOUTH:
			return SOUTH_AABB;
		case NORTH:
		default:
			return NORTH_AABB;
		}
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		for (Direction direction : pContext.getNearestLookingDirections()) {
			BlockState blockstate = this.defaultBlockState().setValue(BlockStateProperties.FACING, direction.getOpposite());
			return blockstate.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER));
		}
		return null;
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
		if (pState.getValue(BlockStateProperties.WATERLOGGED)) {
			pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}
		return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
	}

	@Override
	public BlockState rotate(BlockState pState, Rotation pRot) {
		return pState.setValue(BlockStateProperties.FACING, pRot.rotate(pState.getValue(BlockStateProperties.FACING)));
	}

	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.rotate(pMirror.getRotation(pState.getValue(BlockStateProperties.FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(BlockStateProperties.FACING).add(BlockStateProperties.WATERLOGGED);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.MNEMONIC_INSCRIBER_ENTITY.get().create(pPos, pState);
	}

	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}
}
