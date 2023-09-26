package com.rekindled.embers.util;

import org.jetbrains.annotations.NotNull;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

public class MultiblockSoundType extends SoundType {

	public final SoundType type;

	public MultiblockSoundType(SoundType type) {
		super(type.getVolume(), type.getPitch(), (SoundEvent) null, (SoundEvent) null, (SoundEvent) null, (SoundEvent) null, (SoundEvent) null);
		this.type = type;
	}

	@Override
	public SoundEvent getBreakSound() {
		return SoundEvents.EMPTY;
	}

	@Override
	public SoundEvent getStepSound() {
		return type.getStepSound();
	}

	@Override
	public SoundEvent getPlaceSound() {
		return SoundEvents.EMPTY;
	}

	@Override
	public SoundEvent getHitSound() {
		return type.getHitSound();
	}

	@Override
	public SoundEvent getFallSound() {
		return type.getFallSound();
	}
}
