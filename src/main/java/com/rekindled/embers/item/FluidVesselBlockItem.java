package com.rekindled.embers.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.rekindled.embers.ConfigManager;
import com.rekindled.embers.block.FluidDialBlock;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public class FluidVesselBlockItem extends BlockItem {

	public FluidVesselBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		if (stack.hasTag() && !FluidStack.loadFluidStackFromNBT(stack.getTag().getCompound(FluidHandlerItemStack.FLUID_NBT_KEY)).isEmpty()) {
			return 1;
		}
		return super.getMaxStackSize(stack);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new FluidHandlerItemStack(stack, ConfigManager.FLUID_VESSEL_CAPACITY.get());
	}

	@Override
	protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
		CompoundTag nbt = pStack.getOrCreateTag();
		if (nbt.contains(FluidHandlerItemStack.FLUID_NBT_KEY)) {
			nbt.put("BlockEntityTag", nbt.get(FluidHandlerItemStack.FLUID_NBT_KEY));
		}
		return BlockItem.updateCustomBlockEntityTag(pLevel, pPlayer, pPos, pStack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		IFluidHandler cap = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
		if (cap != null) {
			tooltip.add(Component.literal(FluidDialBlock.formatFluidStack(cap.getFluidInTank(0), cap.getTankCapacity(0))).withStyle(ChatFormatting.GRAY));
		}
	}
}
