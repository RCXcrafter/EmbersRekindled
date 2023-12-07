package com.rekindled.embers.item;

import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.Multimap;
import com.rekindled.embers.Embers;
import com.rekindled.embers.api.EmbersAPI;
import com.rekindled.embers.model.AshenArmorModel;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class AshenArmorItem extends ArmorItem {

	public AshenArmorItem(ArmorMaterial material, Type type, Properties properties) {
		super(material, type, properties);
		if (type == Type.HELMET) {
			EmbersAPI.registerWearableLens(this);
		}
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return Embers.MODID + ":textures/models/armor/robe.png";
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		Multimap<Attribute, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);
		if (isBroken(stack)) {
			modifiers.removeAll(Attributes.ARMOR);
			modifiers.removeAll(Attributes.ARMOR_TOUGHNESS);
		}
		return modifiers;
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		super.setDamage(stack, Math.min(damage, getMaxDamage(stack) - 1));
	}

	public boolean isBroken(ItemStack armor) {
		return armor.getDamageValue() >= armor.getMaxDamage() - 1;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		super.appendHoverText(stack, level, tooltip, isAdvanced);
		if (isBroken(stack))
			tooltip.add(Component.translatable(Embers.MODID + ".tooltip.broken").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(AshenArmorModel.ARMOR_MODEL_GETTER);
	}
}
