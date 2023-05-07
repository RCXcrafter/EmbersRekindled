package com.rekindled.embers.item;

import java.text.DecimalFormat;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.rekindled.embers.Embers;
import com.rekindled.embers.EmbersClientEvents;
import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.power.DefaultEmberItemCapability;
import com.rekindled.embers.util.DecimalFormats;
import com.rekindled.embers.util.Misc;

import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public abstract class EmberStorageItem extends Item {

	public EmberStorageItem(Properties properties) {
		super(properties);
	}

	public ItemStack withFill(double ember) {
		ItemStack stack = new ItemStack(this);
		IEmberCapability cap = stack.getCapability(EmbersCapabilities.EMBER_CAPABILITY).orElse(null);
		cap.setEmber(ember);
		return stack;
	}

	public abstract double getCapacity();

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
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || !oldStack.sameItem(newStack);
	}

	@Override
	public int getBarColor(ItemStack pStack) {
		return 0xFF6600;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		return new DefaultEmberItemCapability(stack, getCapacity());
	}

	@Override
	public void fillItemCategory(CreativeModeTab pGroup, NonNullList<ItemStack> pItems) {
		if (this.allowedIn(pGroup)) {
			pItems.add(new ItemStack(this));
			pItems.add(withFill(getCapacity()));
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		IEmberCapability cap = stack.getCapability(EmbersCapabilities.EMBER_CAPABILITY).orElse(null);
		if (cap != null) {
			DecimalFormat emberFormat = DecimalFormats.getDecimalFormat(Embers.MODID + ".decimal_format.ember");
			tooltip.add(Component.translatable(Embers.MODID + ".tooltip.item.ember", emberFormat.format(cap.getEmber()),  emberFormat.format(cap.getEmberCapacity())).withStyle(ChatFormatting.GRAY));
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static class ColorHandler implements ItemColor {
		@Override
		public int getColor(ItemStack stack, int tintIndex) {
			if (tintIndex == 0) {
				IEmberCapability capability = stack.getCapability(EmbersCapabilities.EMBER_CAPABILITY, null).orElse(null);
				if (capability!= null) {
					float coeff = (float)(capability.getEmber() / capability.getEmberCapacity());
					float timerSine = ((float)Math.sin(8.0*Math.toRadians(EmbersClientEvents.ticks % 360))+1.0f)/2.0f;
					int r = 255;
					int g = (int)(255.0f*(1.0f-coeff) + (64.0f*timerSine+64.0f)*coeff);
					int b = (int)(255.0f*(1.0f-coeff) + 16.0f*coeff);
					return Misc.intColor(r, g, b);
				}
			}
			return 0xFFFFFF;
		}
	}
}
