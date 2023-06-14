package com.rekindled.embers.util.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemUseSound extends AbstractTickableSoundInstance {
	LivingEntity linkedEntity;
	Item itemType;

	public ItemUseSound(LivingEntity linkedEntity, Item itemType, SoundEvent soundIn, SoundSource categoryIn, boolean repeatIn, float volumeIn, float pitchIn) {
		super(soundIn, categoryIn, linkedEntity.level.getRandom());
		this.linkedEntity = linkedEntity;
		this.itemType = itemType;
		this.volume = volumeIn;
		this.pitch = pitchIn;
		this.looping = repeatIn;
	}

	@Override
	public void tick() {
		if(linkedEntity == null) {
			stop();
			return;
		}
		ItemStack heldItem = linkedEntity.getItemInHand(linkedEntity.getUsedItemHand());
		if(linkedEntity.isRemoved() || !linkedEntity.isUsingItem() || heldItem.getItem() != itemType) {
			stop();
			return;
		}
		x = linkedEntity.getX();
		y = linkedEntity.getY();
		z = linkedEntity.getZ();
	}
}
