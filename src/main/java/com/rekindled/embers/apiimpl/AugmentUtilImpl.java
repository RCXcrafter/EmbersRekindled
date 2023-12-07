package com.rekindled.embers.apiimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.augment.IAugment;
import com.rekindled.embers.api.augment.IAugmentUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class AugmentUtilImpl implements IAugmentUtil {

	@Override
	public Collection<IAugment> getAllAugments() {
		return RegistryManager.augmentRegistry.values();
	}

	@Override
	public IAugment getAugment(ResourceLocation name) {
		return RegistryManager.augmentRegistry.get(name);
	}

	@Override
	public List<IAugment> getAugments(ItemStack stack) {
		if (hasHeat(stack)) {
			ListTag tagAugments = stack.getOrCreateTag().getCompound(IAugmentUtil.HEAT_TAG).getList("augments", Tag.TAG_COMPOUND);
			if (tagAugments.size() > 0) {
				List<IAugment> results = new ArrayList<>();
				for (int i = 0; i < tagAugments.size(); i++) {
					CompoundTag tagAugment = tagAugments.getCompound(i);
					IAugment augment = getAugment(new ResourceLocation(tagAugment.getString("name")));
					if (augment != null)
						results.add(augment);
				}
				return results;
			}
		}
		return Lists.newArrayList();
	}

	@Override
	public int getTotalAugmentLevel(ItemStack stack) {
		int total = 0;
		if (hasHeat(stack)) {
			ListTag list = stack.getOrCreateTag().getCompound(HEAT_TAG).getList("augments", Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i ++) {
				CompoundTag compound = list.getCompound(i);
				IAugment augment = getAugment(new ResourceLocation(compound.getString("name")));
				if (augment.countTowardsTotalLevel()) {
					total += compound.getInt("level");
				}
			}
		}
		return total;
	}

	@Override
	public boolean hasAugment(ItemStack stack, IAugment augment) {
		if (hasHeat(stack)) {
			ListTag list = stack.getOrCreateTag().getCompound(HEAT_TAG).getList("augments", Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i ++) {
				CompoundTag compound = list.getCompound(i);
				if (compound.contains("name")) {
					if (compound.getString("name").compareTo(augment.getName().toString()) == 0) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void addAugment(ItemStack stack, ItemStack augmentStack, IAugment augment) {
		checkForTag(stack);
		ListTag list = stack.getOrCreateTag().getCompound(HEAT_TAG).getList("augments", Tag.TAG_COMPOUND);
		int level = getAugmentLevel(stack, augment);
		if (level == 0) {
			CompoundTag augmentCompound = new CompoundTag();
			augmentCompound.putString("name", augment.getName().toString());
			ListTag items = new ListTag();
			augmentCompound.put("items", items);
			items.add(augmentStack.save(new CompoundTag()));
			augmentCompound.putInt("level", 1);
			list.add(augmentCompound);
		} else {
			for (int i = 0; i < list.size(); i ++) {
				CompoundTag augmentCompound = list.getCompound(i);
				if (augmentCompound.contains("name")) {
					if (augmentCompound.getString("name").compareTo(augment.getName().toString()) == 0) {
						ListTag items = augmentCompound.getList("items", Tag.TAG_COMPOUND);
						items.add(augmentStack.save(new CompoundTag()));
						augmentCompound.putInt("level", level + 1);
					}
				}
			}
		}
		augment.onApply(stack);
	}

	@Override
	public List<ItemStack> removeAllAugments(ItemStack stack) {
		if (hasHeat(stack)) {
			CompoundTag tagCompound = stack.getOrCreateTag();
			ListTag tagAugments = tagCompound.getCompound(IAugmentUtil.HEAT_TAG).getList("augments", Tag.TAG_COMPOUND);
			if (tagAugments.size() > 0) { //TODO: cleanup
				List<ItemStack> results = new ArrayList<>();
				ListTag remainingAugments = new ListTag();
				List<IAugment> removedAugments = new ArrayList<>();
				for (int i = 0; i < tagAugments.size(); i ++) {
					CompoundTag tagAugment = tagAugments.getCompound(i);
					IAugment augment = getAugment(new ResourceLocation(tagAugment.getString("name")));
					if (augment != null) {
						if (augment.canRemove()) {
							for (int j = 0; j < tagAugment.getInt("level"); j++) {
								removedAugments.add(augment);
							}
							if (tagAugment.contains("items")) {
								ListTag items = tagAugment.getList("items", Tag.TAG_COMPOUND);
								for (int j = 0; j < items.size(); j ++) {
									results.add(ItemStack.of(items.getCompound(j)));
								}
							}
						} else {
							remainingAugments.add(tagAugment);
						}
					}
				}
				tagCompound.getCompound(IAugmentUtil.HEAT_TAG).put("augments", remainingAugments);
				for (IAugment augment : removedAugments) {
					augment.onRemove(stack);
				}
				return results;
			}
		}
		return Lists.newArrayList();
	}

	@Override
	public void addAugmentLevel(ItemStack stack, IAugment augment, int levels) {
		setAugmentLevel(stack, augment, getAugmentLevel(stack, augment) + levels); //This is redundant but intuitive
	}

	@Override
	public void setAugmentLevel(ItemStack stack, IAugment augment, int level) {
		checkForTag(stack);
		ListTag list = stack.getOrCreateTag().getCompound(IAugmentUtil.HEAT_TAG).getList("augments", Tag.TAG_COMPOUND);
		for (int i = 0; i < list.size(); i ++) {
			CompoundTag compound = list.getCompound(i);
			if (compound.contains("name")) {
				if (compound.getString("name").compareTo(augment.getName().toString()) == 0) {
					compound.putInt("level", level);
				}
			}
		}
	}

	@Override
	public int getAugmentLevel(ItemStack stack, IAugment augment) {
		if (hasHeat(stack)) {
			ListTag list = stack.getOrCreateTag().getCompound(IAugmentUtil.HEAT_TAG).getList("augments", Tag.TAG_COMPOUND);
			for (int i = 0; i < list.size(); i ++) {
				CompoundTag compound = list.getCompound(i);
				if (compound.contains("name")) {
					if (compound.getString("name").compareTo(augment.getName().toString()) == 0) {
						return compound.getInt("level");
					}
				}
			}
		}
		return 0;
	}

	@Override
	public boolean hasHeat(ItemStack stack) {
		if (!stack.isEmpty()) {
			if (stack.hasTag()) {
				if (stack.getTag().contains(HEAT_TAG)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void addHeat(ItemStack stack, float heat) {
		checkForTag(stack);
		stack.getTag().getCompound(HEAT_TAG).putFloat("heat", Math.min(getMaxHeat(stack), getHeat(stack) + heat));
	}

	@Override
	public void setHeat(ItemStack stack, float heat) {
		checkForTag(stack);
		stack.getTag().getCompound(HEAT_TAG).putFloat("heat", heat);
	}

	@Override
	public float getHeat(ItemStack stack) {
		if (hasHeat(stack)) {
			return stack.getTag().getCompound(HEAT_TAG).getFloat("heat");
		}
		return 0.0f;
	}

	@Override
	public float getMaxHeat(ItemStack stack) {
		if (hasHeat(stack)) {
			return 500f + 250f*stack.getTag().getCompound(HEAT_TAG).getFloat("heat_level");
		}
		return 0.0f;
	}

	@Override
	public int getLevel(ItemStack stack) {
		if (hasHeat(stack)) {
			return stack.getTag().getCompound(HEAT_TAG).getInt("heat_level");
		}
		return 0;
	}

	@Override
	public void setLevel(ItemStack stack, int level) {
		checkForTag(stack);
		stack.getTag().getCompound(HEAT_TAG).putInt("heat_level",level);
	}

	@Override
	public int getArmorAugmentLevel(LivingEntity entity, IAugment augment) {
		int maxLevel = 0;
		if (hasHeat(entity.getItemBySlot(EquipmentSlot.HEAD))) {
			int l = getAugmentLevel(entity.getItemBySlot(EquipmentSlot.HEAD), augment);
			if (l > maxLevel) {
				maxLevel = l;
			}
		}
		if (hasHeat(entity.getItemBySlot(EquipmentSlot.CHEST))) {
			int l = getAugmentLevel(entity.getItemBySlot(EquipmentSlot.CHEST), augment);
			if (l > maxLevel) {
				maxLevel = l;
			}
		}
		if (hasHeat(entity.getItemBySlot(EquipmentSlot.LEGS))) {
			int l = getAugmentLevel(entity.getItemBySlot(EquipmentSlot.LEGS), augment);
			if (l > maxLevel) {
				maxLevel = l;
			}
		}
		if (hasHeat(entity.getItemBySlot(EquipmentSlot.FEET))) {
			int l = getAugmentLevel(entity.getItemBySlot(EquipmentSlot.FEET), augment);
			if (l > maxLevel) {
				maxLevel = l;
			}
		}
		return maxLevel;
	}

	@Override
	public IAugment registerAugment(IAugment augment) {
		RegistryManager.augmentRegistry.put(augment.getName(), augment);
		return augment;
	}

	public static void checkForTag(ItemStack stack) {
		if (!stack.getOrCreateTag().contains(HEAT_TAG)) {
			stack.getTag().put(HEAT_TAG, new CompoundTag());
			stack.getTag().getCompound(HEAT_TAG).putInt("heat_level", 0);
			stack.getTag().getCompound(HEAT_TAG).putFloat("heat", 0);
			stack.getTag().getCompound(HEAT_TAG).put("augments", new ListTag());
		}
	}
}
