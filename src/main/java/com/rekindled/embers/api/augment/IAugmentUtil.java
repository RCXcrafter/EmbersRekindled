package com.rekindled.embers.api.augment;

import java.util.Collection;
import java.util.List;

import com.rekindled.embers.Embers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IAugmentUtil {

	String HEAT_TAG = Embers.MODID + ":heat_tag";

	Collection<IAugment> getAllAugments();

	IAugment getAugment(ResourceLocation name);

	List<IAugment> getAugments(ItemStack stack);

	int getTotalAugmentLevel(ItemStack stack);

	boolean hasAugment(ItemStack stack, IAugment augment);

	void addAugment(ItemStack stack, ItemStack augmentStack, IAugment augment);

	List<ItemStack> removeAllAugments(ItemStack stack);

	void addAugmentLevel(ItemStack stack, IAugment augment, int levels);

	void setAugmentLevel(ItemStack stack, IAugment augment, int level);

	int getAugmentLevel(ItemStack stack, IAugment augment);

	boolean hasHeat(ItemStack stack);

	void addHeat(ItemStack stack, float heat);

	void setHeat(ItemStack stack, float heat);

	float getHeat(ItemStack stack);

	float getMaxHeat(ItemStack stack);

	int getLevel(ItemStack stack);

	void setLevel(ItemStack stack, int level);

	int getArmorAugmentLevel(LivingEntity entity, IAugment augment);

	public IAugment registerAugment(IAugment augment);
}
