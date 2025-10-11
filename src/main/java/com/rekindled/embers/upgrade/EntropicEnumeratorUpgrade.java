package com.rekindled.embers.upgrade;

import java.util.List;

import com.rekindled.embers.Embers;
import com.rekindled.embers.api.event.AlchemyResultEvent;
import com.rekindled.embers.api.event.AlchemyStartEvent;
import com.rekindled.embers.api.event.UpgradeEvent;
import com.rekindled.embers.api.misc.AlchemyResult;
import com.rekindled.embers.api.upgrades.UpgradeContext;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.blockentity.AlchemyTabletBlockEntity;
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
			boolean willFail = true;
			AlchemyResult result = alchemyEvent.getRecipe().getResult(alchemyEvent.context);
			int requirement = alchemyEvent.getRecipe().getInputs().size();
			if (result.blackPins == requirement) {
				willFail = false;
			} else {
				boolean first = false;
				for (UpgradeContext upgrade : upgrades) {
					if (upgrade.upgrade() instanceof EntropicEnumeratorUpgrade firstEnumerator) {
						if (firstEnumerator == this) {
							first = true;
						} else {
							willFail = ((EntropicEnumeratorBlockEntity) firstEnumerator.tile).willFail;
						}
						break;
					}
				}
				if (first) {
					if (Misc.random.nextFloat(count + 3) > 3) {
						willFail = true;
					} else {
						int bonusWhite = Math.min(result.whitePins, count + 1);
						int bonusNothing = count / 2;
						if (requirement <= result.blackPins + bonusWhite + bonusNothing) {
							willFail = false;
						}
					}
				}
				enumerator.willFail = willFail;
			}

			int solveTime = UpgradeUtil.getWorkTime(tile, AlchemyTabletBlockEntity.PROCESSING_TIME * 10, upgrades) - 10;
			if (solveTime < 38 * EntropicEnumeratorBlockEntity.solvingMoveTime) {
				return; //not enough time to solve so don't bother
			}
			enumerator.solve(false, solveTime, willFail);
		}
		if (event instanceof AlchemyResultEvent alchemyEvent && this.tile instanceof EntropicEnumeratorBlockEntity enumerator) {
			alchemyEvent.setFailure(enumerator.willFail);
			enumerator.restartScramble(Misc.random.nextInt(EntropicEnumeratorBlockEntity.queueTime));
		}
	}
}
