package com.rekindled.embers.research.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class ResearchCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
	private IResearchCapability capability;

	public LazyOptional<IResearchCapability> holder = LazyOptional.of(() -> capability);

	public ResearchCapabilityProvider() {
		capability = new DefaultResearchCapability();
		holder = LazyOptional.of(() -> capability);
	}

	public ResearchCapabilityProvider(IResearchCapability capability) {
		this.capability = capability;
		holder = LazyOptional.of(() -> this.capability);
	}

	public static final Capability<IResearchCapability> researchCapability = CapabilityManager.get(new CapabilityToken<>(){});


	@Nullable
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		if (researchCapability != null && capability == researchCapability)
			return researchCapability.orEmpty(capability, holder);
		return LazyOptional.empty();
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag compound = new CompoundTag();
		capability.writeToNBT(compound);
		return compound;
	}

	@Override
	public void deserializeNBT(CompoundTag compound) {
		capability.readFromNBT(compound);
	}
}
