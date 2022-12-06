package com.rekindled.embers.item;

import java.text.DecimalFormat;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.rekindled.embers.Embers;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.power.DefaultEmberItemCapability;
import com.rekindled.embers.util.DecimalFormats;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class CopperCellBlockItem extends BlockItem {

	public CopperCellBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return stack.getCapability(EmbersCapabilities.EMBER_CAPABILITY).isPresent();
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		IEmberCapability cap = stack.getCapability(EmbersCapabilities.EMBER_CAPABILITY).orElse(null);
		if (cap != null) {
			return Math.round(13.0F - (float) (cap.getEmberCapacity()-cap.getEmber()) * 13.0F / (float) cap.getEmberCapacity());
		}
		return 0;
	}

	@Override
	public int getBarColor(ItemStack pStack) {
		return 0xFF6600;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new DefaultEmberItemCapability(stack, 24000);
	}

	@Override
	public void fillItemCategory(CreativeModeTab pGroup, NonNullList<ItemStack> pItems) {
		if (this.allowedIn(pGroup)) {
			pItems.add(new ItemStack(this));

			ItemStack chargedCell = new ItemStack(this);
			IEmberCapability cap = chargedCell.getCapability(EmbersCapabilities.EMBER_CAPABILITY).orElse(null);
			cap.setEmber(cap.getEmberCapacity());
			pItems.add(chargedCell);
		}
	}

	@Override
	protected boolean updateCustomBlockEntityTag(BlockPos pPos, Level pLevel, @Nullable Player pPlayer, ItemStack pStack, BlockState pState) {
		IEmberCapability cap = pStack.getCapability(EmbersCapabilities.EMBER_CAPABILITY).orElse(null);
		if (cap != null) {
			cap.writeToNBT(pStack.getOrCreateTagElement("BlockEntityTag"));
		}
		return BlockItem.updateCustomBlockEntityTag(pLevel, pPlayer, pPos, pStack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		IEmberCapability cap = stack.getCapability(EmbersCapabilities.EMBER_CAPABILITY).orElse(null);
		if (cap != null) {
			DecimalFormat emberFormat = DecimalFormats.getDecimalFormat(Embers.MODID + ".decimal_format.ember");
			tooltip.add(Component.translatable(Embers.MODID + ".tooltip.item.ember", emberFormat.format(cap.getEmber()),  emberFormat.format(cap.getEmberCapacity())).withStyle(ChatFormatting.GRAY));
		}
	}
}
