package com.rekindled.embers.item;

import com.rekindled.embers.datagen.EmbersBlockTags;
import com.rekindled.embers.util.EmbersTiers;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

public class ClockworkPickaxeItem extends ClockworkToolItem {

	public ClockworkPickaxeItem(Properties properties) {
		super(2, -3.0f, EmbersTiers.CLOCKWORK_PICK, EmbersBlockTags.MINABLE_WITH_PICKAXE_SHOVEL, properties);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return hasEmber(stack) && (toolAction == ToolActions.PICKAXE_DIG || toolAction == ToolActions.SHOVEL_DIG);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchant) {
		return super.canApplyAtEnchantingTable(stack, enchant) && (enchant.category == EnchantmentCategory.WEAPON || enchant.category == EnchantmentCategory.DIGGER);
	}
}
