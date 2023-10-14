package com.rekindled.embers.item;

import com.rekindled.embers.api.item.IInflictorGem;
import com.rekindled.embers.api.item.IInflictorGemHolder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AshenCloakItem extends AshenArmorItem implements IInflictorGemHolder {

	public AshenCloakItem(ArmorMaterial material, Type type, Properties properties) {
		super(material, type, properties);
	}

	@Override
	public int getGemSlots(ItemStack holder) {
		return 7;
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
}
