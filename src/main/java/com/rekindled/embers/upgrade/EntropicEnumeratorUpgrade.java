package com.rekindled.embers.upgrade;

import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.api.event.AlchemyResultEvent;
import com.rekindled.embers.api.event.AlchemyStartEvent;
import com.rekindled.embers.api.event.UpgradeEvent;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.blockentity.EntropicEnumeratorBlockEntity;
import com.rekindled.embers.util.Misc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class EntropicEnumeratorUpgrade extends DefaultUpgradeProvider {

	public EntropicEnumeratorUpgrade(BlockEntity tile) {
		super(new ResourceLocation(Embers.MODID, "entropic_enumerator"), tile);
	}

	@Override
	public int getPriority() {
		return -90; //before most other things
	}

	@Override
	public void throwEvent(BlockEntity tile, List<UpgradeContext> upgrades, UpgradeEvent event, int distance, int count) {
		if (event instanceof AlchemyStartEvent alchemyEvent && alchemyEvent.getRecipe() != null && this.tile instanceof EntropicEnumeratorBlockEntity enumerator) {
			if (alchemyEvent.getRecipe().getResult(alchemyEvent.context).blackPins != alchemyEvent.getRecipe().getInputs().size())
				enumerator.solve();
		}
		if (event instanceof AlchemyResultEvent alchemyEvent && this.tile instanceof EntropicEnumeratorBlockEntity enumerator) {
			int blackPins = alchemyEvent.getResult().blackPins;
			int requirement = alchemyEvent.getRecipe().getInputs().size();
			if (blackPins != requirement) {
				if (upgrades.get(0).upgrade() == this) {
					if (Misc.random.nextFloat(count + 3) > 3) {
						alchemyEvent.setFailure(true);
					} else {
						int bonusWhite = Math.min(alchemyEvent.getResult().whitePins, count + 1);
						int bonusNothing = count / 2;
						if (alchemyEvent.isFailure() && requirement <= blackPins + bonusWhite + bonusNothing) {
							alchemyEvent.setFailure(false);
						}
					}
				}
				enumerator.restartScramble();
			}
		}
	}
}
