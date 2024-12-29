package com.rekindled.embers.item;

import java.util.List;
import java.util.function.Supplier;

import com.rekindled.embers.Embers;
import com.rekindled.embers.api.item.IInflictorGem;
import com.rekindled.embers.api.item.IInflictorGemHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class AshenArmorGemItem extends AshenArmorItem implements IInflictorGemHolder {

	public Supplier<Integer> gemSlots;

	public AshenArmorGemItem(ArmorMaterial material, Type type, Properties properties, Supplier<Integer> gemSlots) {
		super(material, type, properties);
		this.gemSlots = gemSlots;
	}

	@Override
	public int getGemSlots(ItemStack holder) {
		return gemSlots.get();
	}

	@Override
	public boolean canAttachGem(ItemStack holder, ItemStack gem) {
		return gem.getItem() instanceof IInflictorGem;
	}

	@Override
	public void attachGem(ItemStack holder, ItemStack gem, int slot) {
		holder.getOrCreateTag().put("gem" + slot, gem.serializeNBT());
	}

	@Override
	public ItemStack detachGem(ItemStack holder, int slot) {
		if (holder.getOrCreateTag().contains("gem" + slot)) {
			ItemStack gem = ItemStack.of(holder.getOrCreateTag().getCompound("gem" + slot));
			holder.getOrCreateTag().remove("gem" + slot);
			return gem;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void clearGems(ItemStack holder) {
		CompoundTag tagCompound = holder.getOrCreateTag();
		if(tagCompound == null)
			return;
		for (int i = 0; i < getGemSlots(holder); i++) {
			if (tagCompound.contains("gem" + i)) {
				tagCompound.remove("gem" + i);
			}
		}
	}

	@Override
	public ItemStack[] getAttachedGems(ItemStack holder) {
		ItemStack[] stacks = new ItemStack[getGemSlots(holder)];
		for (int i = 0; i < stacks.length; i++) {
			if(holder.getOrCreateTag().contains("gem" + i))
				stacks[i] = ItemStack.of(holder.getOrCreateTag().getCompound("gem" + i));
			else
				stacks[i] = ItemStack.EMPTY;
		}
		return stacks;
	}

	@Override
	public float getTotalDamageResistance(LivingEntity entity, DamageSource source, ItemStack holder) {
		float reduction = 0;

		if (!isBroken(holder)) {
			for (ItemStack stack : getAttachedGems(holder)) {
				Item item = stack.getItem();
				if (item instanceof IInflictorGem gem && gem.getAttunedSource(stack).equals(source.type().msgId())) {
					reduction += gem.getDamageResistance(stack, reduction);
				}
			}
		}
		return reduction;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tooltip, isAdvanced);
		ItemStack[] attached = getAttachedGems(stack);
		int filledSlots = 0;

		for (ItemStack stacks : attached) {
			if (!stacks.isEmpty()) {
				filledSlots++;
			}
		}
		if (getGemSlots(stack) > filledSlots)
			tooltip.add(Component.translatable(Embers.MODID + ".tooltip.inflictor.slots", getGemSlots(stack) - filledSlots).withStyle(ChatFormatting.GRAY));

		for (ItemStack stacks : attached) {
			if (!stacks.isEmpty()) {
				if (stacks.getOrCreateTag().contains("type")) {
					tooltip.add(Component.translatable(Embers.MODID + ".tooltip.inflictor", stacks.getOrCreateTag().getString("type")).withStyle(ChatFormatting.GRAY));
				} else {
					tooltip.add(Component.translatable(Embers.MODID + ".tooltip.inflictor.none").withStyle(ChatFormatting.GRAY));
				}
			}
		}
	}
}
