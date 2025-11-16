package com.rekindled.embers.item;

import java.util.List;

import com.rekindled.embers.Embers;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class UpgradeItem extends BlockItem {

	public UpgradeItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		ResourceLocation upgradeLoc = BuiltInRegistries.ITEM.getKey(this);
		tooltip.add(Component.translatable(Embers.MODID + ".tooltip.upgrade").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable(Embers.MODID + ".tooltip.upgrade.desc." + upgradeLoc.toLanguageKey()).withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.translatable(Embers.MODID + ".tooltip.upgrade.compatible").withStyle(ChatFormatting.GRAY));
		for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(BlockTags.create(new ResourceLocation(Embers.MODID, "upgradeable_with/" + upgradeLoc.getNamespace() + "/" + upgradeLoc.getPath())))) {
			tooltip.add(Component.translatable(Embers.MODID + ".tooltip.upgrade.compatible.list", Component.translatable(holder.get().getDescriptionId())).withStyle(ChatFormatting.GRAY));
		}
	}
}
