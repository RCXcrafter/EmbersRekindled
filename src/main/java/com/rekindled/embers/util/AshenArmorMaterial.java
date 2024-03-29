package com.rekindled.embers.util;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.crafting.Ingredient;

public class AshenArmorMaterial implements ArmorMaterial {

	public static final AshenArmorMaterial INSTANCE = new AshenArmorMaterial();

	@Override
	public int getDurabilityForType(Type type) {
		return ArmorMaterials.HEALTH_FUNCTION_FOR_TYPE.get(type) * 19;
	}

	@Override
	public int getDefenseForType(Type type) {
		switch (type) {
		case HELMET:
			return 3;
		case CHESTPLATE:
			return 7;
		case LEGGINGS:
			return 5;
		case BOOTS:
		default:
			return 3;
		}
	}

	@Override
	public int getEnchantmentValue() {
		return 18;
	}

	@Override
	public SoundEvent getEquipSound() {
		return SoundEvents.ARMOR_EQUIP_GENERIC;
	}

	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.of(RegistryManager.ASHEN_FABRIC.get());
	}

	@Override
	public String getName() {
		return Embers.MODID + ":ashen_cloak";
	}

	@Override
	public float getToughness() {
		return 1.0f;
	}

	@Override
	public float getKnockbackResistance() {
		return 0;
	}
}
