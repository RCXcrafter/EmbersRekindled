package com.rekindled.embers.item;

import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.gui.GuiCodex;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AncientCodexItem extends Item {

	public AncientCodexItem(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if (level.isClientSide) {
			Minecraft.getInstance().setScreen(GuiCodex.instance);
		}
		level.playSound(player, player, EmbersSounds.CODEX_OPEN.get(), SoundSource.MASTER, 0.75f, 1.0f);
		return InteractionResultHolder.success(player.getItemInHand(hand));
	}
}
