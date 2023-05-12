package com.rekindled.embers.item;

import com.rekindled.embers.datagen.EmbersBlockTags;
import com.rekindled.embers.util.EmbersTiers;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

public class ClockworkHammerItem extends ClockworkToolItem {

	public ClockworkHammerItem(Properties properties) {
		super(4, -3.0f, EmbersTiers.CLOCKWORK_HAMMER, EmbersBlockTags.MINABLE_WITH_HAMMER, properties);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return hasEmber(stack) && (toolAction == ToolActions.PICKAXE_DIG || toolAction == ToolActions.SHOVEL_DIG || toolAction == ToolActions.AXE_DIG);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchant) {
		return super.canApplyAtEnchantingTable(stack, enchant) && enchant != Enchantments.SILK_TOUCH && enchant != Enchantments.BLOCK_FORTUNE && (enchant.category == EnchantmentCategory.WEAPON || enchant.category == EnchantmentCategory.DIGGER);
	}
}
