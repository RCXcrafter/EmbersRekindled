package com.rekindled.embers.blockentity;

import java.util.HashSet;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.util.sound.ISoundController;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class FieldChartBlockEntity extends BlockEntity implements ISoundController {

	public static final int SOUND_LOOP = 1;
	public static final int[] SOUND_IDS = new int[]{SOUND_LOOP};

	HashSet<Integer> soundsPlaying = new HashSet<>();

	public FieldChartBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.FIELD_CHART_ENTITY.get(), pPos, pBlockState);
	}

	@Override
	public AABB getRenderBoundingBox() {
		return new AABB(worldPosition.offset(-1, 0, -1), worldPosition.offset(2, 1, 2));
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, FieldChartBlockEntity blockEntity) {
		blockEntity.handleSound();
	}

	@Override
	public void playSound(int id) {
		switch (id) {
		case SOUND_LOOP:
			EmbersSounds.playMachineSound(this, SOUND_LOOP, EmbersSounds.FIELD_CHART_LOOP.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, worldPosition.getX() + 0.5f, worldPosition.getY() + 0.5f, worldPosition.getZ() + 0.5f);
			break;
		}
		soundsPlaying.add(id);
	}

	@Override
	public void stopSound(int id) {
		soundsPlaying.remove(id);
	}

	@Override
	public boolean isSoundPlaying(int id) {
		return soundsPlaying.contains(id);
	}

	@Override
	public int[] getSoundIDs() {
		return SOUND_IDS;
	}

	@Override
	public boolean shouldPlaySound(int id) {
		return id == SOUND_LOOP;
	}
}
