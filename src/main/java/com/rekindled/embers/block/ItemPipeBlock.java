package com.rekindled.embers.block;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.ItemPipeBlockEntity;
import com.rekindled.embers.blockentity.ItemPipeBlockEntityBase;
import com.rekindled.embers.datagen.EmbersBlockTags;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ItemPipeBlock extends PipeBlockBase {

	public ItemPipeBlock(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public boolean connected(Direction direction, BlockState state) {
		return false;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return RegistryManager.ITEM_PIPE_ENTITY.get().create(pPos, pState);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return pLevel.isClientSide ? null : createTickerHelper(pBlockEntityType, RegistryManager.ITEM_PIPE_ENTITY.get(), ItemPipeBlockEntity::serverTick);
	}

	@Override
	public TagKey<Block> getConnectionTag() {
		return EmbersBlockTags.ITEM_PIPE_CONNECTION;
	}

	@Override
	public TagKey<Block> getToggleConnectionTag() {
		return EmbersBlockTags.ITEM_PIPE_CONNECTION_TOGGLEABLE;
	}

	@Override
	public boolean connectToTile(BlockEntity blockEntity, Direction direction) {
		return blockEntity != null && blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite()).isPresent();
	}

	@Override
	public boolean unclog(BlockEntity blockEntity, Level level, BlockPos pos) {
		if (blockEntity instanceof ItemPipeBlockEntityBase pipeEntity && pipeEntity.clogged) {
			IItemHandler handler = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).orElse(null);
			if (handler instanceof IItemHandlerModifiable) {
				Misc.spawnInventoryInWorld(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, handler);
				level.updateNeighbourForOutputSignal(pos, this);
				((IItemHandlerModifiable) handler).setStackInSlot(0, ItemStack.EMPTY);
				return true;
			}
		}
		return false;
	}
}
