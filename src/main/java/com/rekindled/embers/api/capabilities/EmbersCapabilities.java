package com.rekindled.embers.api.capabilities;

import com.rekindled.embers.api.power.IEmberCapability;
import com.rekindled.embers.api.upgrades.IUpgradeProvider;
import com.rekindled.embers.augment.ShiftingScalesAugment.IScalesCapability;
import com.rekindled.embers.research.capability.IResearchCapability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class EmbersCapabilities {
	public static final Capability<IUpgradeProvider> UPGRADE_PROVIDER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
	public static final Capability<IEmberCapability> EMBER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
	public static final Capability<IResearchCapability> RESEARCH_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
	public static final Capability<IScalesCapability> SCALES_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
}
