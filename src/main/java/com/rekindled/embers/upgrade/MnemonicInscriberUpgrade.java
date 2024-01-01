package com.rekindled.embers.upgrade;

import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.api.event.AlchemyResultEvent;
import com.rekindled.embers.api.event.UpgradeEvent;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.blockentity.MnemonicInscriberBlockEntity;
import com.rekindled.embers.datagen.EmbersItemTags;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.GlowParticleOptions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class MnemonicInscriberUpgrade extends DefaultUpgradeProvider {

	public MnemonicInscriberUpgrade(BlockEntity tile) {
		super(new ResourceLocation(Embers.MODID, "mnemonic_inscriber"), tile);
	}

	@Override
	public int getPriority() {
		return 100; //after everything else
	}

	@Override
	public void throwEvent(BlockEntity tile, List<UpgradeContext> upgrades, UpgradeEvent event, int distance, int count) {
		if (event instanceof AlchemyResultEvent alchemyEvent && this.tile instanceof MnemonicInscriberBlockEntity inscriber) {
			if (!alchemyEvent.isFailure() && inscriber.inventory.getStackInSlot(0).is(EmbersItemTags.INSCRIBABLE_PAPER)) {
				inscriber.inventory.setStackInSlot(0, alchemyEvent.getResult().createResultStack(new ItemStack(RegistryManager.ALCHEMICAL_NOTE.get())));
				tile.getLevel().playSound(null, this.tile.getBlockPos(), EmbersSounds.EMBER_EMIT_BIG.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
				if (tile.getLevel() instanceof ServerLevel serverLevel) {
					serverLevel.sendParticles(new GlowParticleOptions(GlowParticleOptions.EMBER_COLOR, new Vec3(0.0, 0.000001, 0.0), 2.0F, 40), this.tile.getBlockPos().getX() + 0.5, this.tile.getBlockPos().getY() + 0.5, this.tile.getBlockPos().getZ() + 0.5, 40, 0.12f, 0.12f, 0.12f, 0.0);
				}
			}
		}
	}
}
