package com.rekindled.embers.upgrade;

import java.util.List;
import java.util.Random;

import com.rekindled.embers.Embers;
import com.rekindled.embers.api.event.HeatCoilVisualEvent;
import com.rekindled.embers.api.event.UpgradeEvent;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.blockentity.HearthCoilBlockEntity;
import com.rekindled.embers.particle.SmokeParticleOptions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;


public class CharInstillerUpgrade extends DefaultUpgradeProvider {

	protected static Random random = new Random();

	public CharInstillerUpgrade(BlockEntity tile) {
		super(new ResourceLocation(Embers.MODID, "char_instiller"), tile);
	}

	@Override
	public int getPriority() {
		return -100; //before everything else
	}

	@Override
	public int getLimit(BlockEntity tile) {
		return tile instanceof HearthCoilBlockEntity ? 1 : 0;
	}

	@Override
	public double getSpeed(BlockEntity tile, double speed, int distance, int count) {
		return 2;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getOtherParameter(BlockEntity tile, String type, T value, int distance, int count) {
		if (type.equals("recipe_type") && value instanceof RecipeType<?>) {
			return (T) RecipeType.SMOKING;
		}
		return value;
	}

	@Override
	public void throwEvent(BlockEntity tile, List<UpgradeContext> upgrades, UpgradeEvent event, int distance, int count) {
		if (event instanceof HeatCoilVisualEvent visualEvent) {
			for (int i = 0; i < visualEvent.getParticles() / 3; i ++) {
				tile.getLevel().addParticle(SmokeParticleOptions.BIG_SMOKE, tile.getBlockPos().getX()-0.2f+random.nextFloat()*1.4f, tile.getBlockPos().getY()+1.275f, tile.getBlockPos().getZ()-0.2f+random.nextFloat()*1.4f,
						(Math.random() * 2.0D - 1.0D) * 0.2D, random.nextFloat() * 1000, (Math.random() * 2.0D - 1.0D) * 0.2D);
			}
			visualEvent.setParticles(visualEvent.getParticles() / 2);
		}
	}
}
