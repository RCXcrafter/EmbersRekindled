package com.rekindled.embers.item;

import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.gui.GuiCodex;
import com.rekindled.embers.research.ResearchBase;
import com.rekindled.embers.research.ResearchManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AncientCodexItem extends Item {

	public AncientCodexItem(Properties pProperties) {
		super(pProperties);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		if (Screen.hasControlDown()) {
			ResearchBase research = ResearchManager.researchByItem.get(context.getLevel().getBlockState(context.getClickedPos()).getBlock().asItem());
			if (research != null) {
				GuiCodex.instance.researchPage = research;
				ResearchManager.sendCheckmark(research, true);
				Minecraft.getInstance().setScreen(GuiCodex.instance);
				context.getLevel().playSound(context.getPlayer(), context.getPlayer(), EmbersSounds.CODEX_OPEN.get(), SoundSource.MASTER, 0.75f, 1.0f);
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		Minecraft.getInstance().setScreen(GuiCodex.instance);
		level.playSound(player, player, EmbersSounds.CODEX_OPEN.get(), SoundSource.MASTER, 0.75f, 1.0f);
		return InteractionResultHolder.success(player.getItemInHand(hand));
	}
}
