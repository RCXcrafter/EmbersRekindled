package com.rekindled.embers.api.capabilities;

import com.rekindled.embers.api.power.IEmberCapability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class EmbersCapabilities {
	//public static final Capability<IUpgradeProvider> UPGRADE_PROVIDER_CAPABILITY = null;
	public static final Capability<IEmberCapability> EMBER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
}
