package com.rekindled.embers.research.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.rekindled.embers.api.capabilities.EmbersCapabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
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

	@Nullable
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		if (EmbersCapabilities.RESEARCH_CAPABILITY != null && capability == EmbersCapabilities.RESEARCH_CAPABILITY)
			return EmbersCapabilities.RESEARCH_CAPABILITY.orEmpty(capability, holder);
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
