package com.rekindled.embers.upgrade;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.rekindled.embers.api.capabilities.EmbersCapabilities;
import com.rekindled.embers.api.upgrades.IUpgradeProvider;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class DefaultUpgradeProvider implements IUpgradeProvider {

	protected final String id;
	protected final BlockEntity tile;
	private final LazyOptional<IUpgradeProvider> holder;

	public DefaultUpgradeProvider(String id, BlockEntity tile) {
		this.id = id;
		this.tile = tile;
		this.holder = LazyOptional.of(() -> this);
	}

	@Override
	public String getUpgradeId() {
		return id;
	}

	public void invalidate() {
		holder.invalidate();
	}

	public <T> LazyOptional<T> getCapability(@NotNull final Capability<T> cap, final @Nullable Direction side) {
		if (EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY != null && cap == EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY)
			return EmbersCapabilities.UPGRADE_PROVIDER_CAPABILITY.orEmpty(cap, holder);
		return LazyOptional.empty();
	}
}
