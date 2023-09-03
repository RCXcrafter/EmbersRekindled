package com.rekindled.embers.item;

import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.EmbersClientEvents;
import com.rekindled.embers.api.item.ITyrfingWeapon;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.TyrfingParticleOptions;
import com.rekindled.embers.util.Misc;

import net.minecraft.ChatFormatting;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class TyrfingItem extends SwordItem implements ITyrfingWeapon {

	public TyrfingItem(Tier tier, int damage, float speed, Properties prop) {
		super(tier, damage, speed, prop);
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		tooltip.add(Component.translatable(Embers.MODID + ".tooltip.tyrfing").withStyle(ChatFormatting.GRAY));
	}

	@Override
	public void attack(LivingHurtEvent event, double armor) {
		if (armor > 0) {
			event.getEntity().playSound(EmbersSounds.TYRFING_HIT.get(),1.0f,1.0f);
			if (event.getEntity().level() instanceof ServerLevel serverLevel) {
				serverLevel.sendParticles(TyrfingParticleOptions.TYRFING, event.getEntity().position().x, event.getEntity().position().y + event.getEntity().getBbHeight() / 2.0f, event.getEntity().position().z, 80, 0.1, 0.1, 0.1, 0.0);
			}
			event.setAmount((event.getAmount() / 4.0f) * (4.0f + (float) armor * 1.0f));
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static class ColorHandler implements ItemColor {
		@Override
		public int getColor(ItemStack stack, int tintIndex) {
			if (tintIndex == 1) {
				float timerSine = ((float)Math.sin(8.0*Math.toRadians(EmbersClientEvents.ticks % 360))+1.0f)/2.0f;
				int r = (int)(64.0f*timerSine);
				int g = (int)(16.0f);
				int b = (int)(32.0f+32.0f*timerSine);
				return Misc.intColor(r, g, b);
			}
			return 0xFFFFFF;
		}		
	}
}
