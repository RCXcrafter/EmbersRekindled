package com.rekindled.embers.util.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MachineSound extends AbstractTickableSoundInstance {

	protected BlockEntity boundTile;
	protected boolean donePlaying;
	protected int id;

	public MachineSound(BlockEntity tile, int id, SoundEvent soundIn, SoundSource categoryIn, boolean repeatIn, float volumeIn, float pitchIn, float xIn, float yIn, float zIn) {
		super(soundIn, categoryIn, tile.getLevel().getRandom());
		this.boundTile = tile;
		this.id = id;
		this.volume = volumeIn;
		this.pitch = pitchIn;
		this.x = xIn;
		this.y = yIn;
		this.z = zIn;
		this.looping = repeatIn;
		this.attenuation = Attenuation.LINEAR;
	}

	@Override
	public boolean isStopped() {
		return donePlaying;
	}

	@Override
	public void tick() {
		if(boundTile == null || boundTile.isRemoved())
			donePlaying = true;
		else if(boundTile instanceof ISoundController) {
			ISoundController controller = (ISoundController) boundTile;
			if(!controller.shouldPlaySound(id))
				donePlaying = true;
			volume = controller.getCurrentVolume(id,volume);
			pitch = controller.getCurrentPitch(id,pitch);
			if(donePlaying && controller.isSoundPlaying(id))
				controller.stopSound(id);
		}
	}
}