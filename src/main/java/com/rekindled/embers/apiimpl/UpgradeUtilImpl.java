package com.rekindled.embers.apiimpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.event.UpgradeEvent;
import com.rekindled.embers.api.upgrades.IUpgradeProvider;
import com.rekindled.embers.api.upgrades.IUpgradeProxy;
import com.rekindled.embers.api.upgrades.IUpgradeUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;

public class UpgradeUtilImpl implements IUpgradeUtil {

	public List<IUpgradeProvider> getUpgrades(Level world, BlockPos pos, Direction[] facings) {
		LinkedList<IUpgradeProvider> upgrades = new LinkedList<>();
		getUpgrades(world,pos,facings,upgrades);
		return upgrades;
	}

	public void getUpgrades(Level world, BlockPos pos, Direction[] facings, List<IUpgradeProvider> upgrades) {
		for (Direction facing: facings) {
			collectUpgrades(world, pos.relative(facing), facing.getOpposite(), upgrades);
		}
		resetCheckedProxies();
	}

	ThreadLocal<Set<IUpgradeProxy>> checkedProxies = ThreadLocal.withInitial(HashSet::new);

	private boolean isProxyChecked(IUpgradeProxy proxy) {
		return checkedProxies.get().contains(proxy);
	}

	private void addCheckedProxy(IUpgradeProxy proxy) {
		checkedProxies.get().add(proxy);
	}

	private void resetCheckedProxies() {
		checkedProxies.remove();
	}

	public void collectUpgrades(Level world, BlockPos pos, Direction side, List<IUpgradeProvider> upgrades) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te != null) {
			IUpgradeProvider cap = te.getCapability(EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY, side).orElse(null);
			if (cap != null)
				upgrades.add(cap);
		}
		if (te instanceof IUpgradeProxy) {
			IUpgradeProxy proxy = (IUpgradeProxy) te;
			if (!isProxyChecked(proxy) && proxy.isProvider(side)) { //Prevent infinite recursion
				addCheckedProxy(proxy);
				proxy.collectUpgrades(upgrades);
			}
		}
	}

	public void verifyUpgrades(BlockEntity tile,List<IUpgradeProvider> list) {
		//Count, remove, sort
		//This call is expensive. Ideally should be cached. The total time complexity is O(n + n^2 + n log n) = O(n^2) for an ArrayList.
		//Total time complexity for a LinkedList should be O(n + n + n log n) = O(n log n). Slightly better.
		HashMap<String,Integer> upgradeCounts = new HashMap<>();
		list.forEach(x -> {
			String id = x.getUpgradeId();
			upgradeCounts.put(x.getUpgradeId(), upgradeCounts.getOrDefault(id,0) + 1);
		});
		list.removeIf(x -> upgradeCounts.get(x.getUpgradeId()) > x.getLimit(tile));
		list.sort((x,y) -> Integer.compare(x.getPriority(),y.getPriority()));
	}

	@Override
	public int getWorkTime(BlockEntity tile, int time, List<IUpgradeProvider> list) {
		double speedmod = getTotalSpeedModifier(tile,list);
		if(speedmod == 0) //Stop.
			return Integer.MAX_VALUE;
		return (int)(time * (1.0 / speedmod));
	}

	public double getTotalSpeedModifier(BlockEntity tile,List<IUpgradeProvider> list) {
		double total = 1.0f;

		for (IUpgradeProvider upgrade : list) {
			total = upgrade.getSpeed(tile, total);
		}

		return total;
	}

	@Override
	public boolean doTick(BlockEntity tile, List<IUpgradeProvider> list) {
		for (IUpgradeProvider upgrade: list) {
			if(upgrade.doTick(tile,list))
				return true;
		}

		return false;
	}

	//DO NOT CALL FROM AN UPGRADE'S doWork METHOD!!
	public boolean doWork(BlockEntity tile, List<IUpgradeProvider> list) {
		for (IUpgradeProvider upgrade: list) {
			if(upgrade.doWork(tile,list))
				return true;
		}

		return false;
	}

	public double getTotalEmberConsumption(BlockEntity tile, double ember, List<IUpgradeProvider> list) {
		for (IUpgradeProvider upgrade : list) {
			ember = upgrade.transformEmberConsumption(tile, ember);
		}

		return ember;
	}

	public double getTotalEmberProduction(BlockEntity tile, double ember, List<IUpgradeProvider> list) {
		for (IUpgradeProvider upgrade : list) {
			ember = upgrade.transformEmberProduction(tile, ember);
		}

		return ember;
	}

	public void transformOutput(BlockEntity tile, List<ItemStack> outputs, List<IUpgradeProvider> list) {
		for (IUpgradeProvider upgrade : list) {
			upgrade.transformOutput(tile,outputs);
		}
	}

	public FluidStack transformOutput(BlockEntity tile, FluidStack output, List<IUpgradeProvider> list) {
		for (IUpgradeProvider upgrade : list) {
			output = upgrade.transformOutput(tile,output);
		}

		return output;
	}

	public boolean getOtherParameter(BlockEntity tile, String type, boolean initial, List<IUpgradeProvider> list) {
		for (IUpgradeProvider upgrade : list) {
			initial = upgrade.getOtherParameter(tile,type,initial);
		}

		return initial;
	}

	public double getOtherParameter(BlockEntity tile, String type, double initial, List<IUpgradeProvider> list) {
		for (IUpgradeProvider upgrade : list) {
			initial = upgrade.getOtherParameter(tile,type,initial);
		}

		return initial;
	}

	public int getOtherParameter(BlockEntity tile, String type, int initial, List<IUpgradeProvider> list) {
		for (IUpgradeProvider upgrade : list) {
			initial = upgrade.getOtherParameter(tile,type,initial);
		}

		return initial;
	}

	public String getOtherParameter(BlockEntity tile, String type, String initial, List<IUpgradeProvider> list) {
		for (IUpgradeProvider upgrade : list) {
			initial = upgrade.getOtherParameter(tile,type,initial);
		}

		return initial;
	}

	public <T> T getOtherParameter(BlockEntity tile, String type, T initial, List<IUpgradeProvider> list) {
		for (IUpgradeProvider upgrade : list) {
			initial = upgrade.getOtherParameter(tile,type,initial);
		}

		return initial;
	}

	@Override
	public void throwEvent(BlockEntity tile, UpgradeEvent event, List<IUpgradeProvider> list) {
		for (IUpgradeProvider upgrade : list) {
			upgrade.throwEvent(tile,event);
		}
	}
}
