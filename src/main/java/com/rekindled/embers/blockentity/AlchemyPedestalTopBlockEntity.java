package com.rekindled.embers.blockentity;

import java.util.HashSet;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.recipe.AlchemyRecipe.PedestalContents;
import com.rekindled.embers.util.sound.ISoundController;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

public class AlchemyPedestalTopBlockEntity extends AlchemyPedestalBlockEntity implements ISoundController {

	public int active = 0;

	public static final int SOUND_PROCESS = 1;
	public static final int[] SOUND_IDS = new int[]{SOUND_PROCESS};
	HashSet<Integer> soundsPlaying = new HashSet<>();

	public AlchemyPedestalTopBlockEntity(BlockPos pPos, BlockState pBlockState) {
		super(RegistryManager.ALCHEMY_PEDESTAL_TOP_ENTITY.get(), pPos, pBlockState);
		inventory = new ItemStackHandler(1) {
			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}

			@Override
			protected void onContentsChanged(int slot) {
				AlchemyPedestalTopBlockEntity.this.setChanged();
			}
		};
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, AlchemyPedestalTopBlockEntity blockEntity) {
		blockEntity.handleSound();
		blockEntity.active--;
	}

	public PedestalContents getContents() {
		ItemStack input = ItemStack.EMPTY;
		BlockEntity tile = level.getBlockEntity(worldPosition.below());
		if (tile instanceof AlchemyPedestalBlockEntity)
			input = ((AlchemyPedestalBlockEntity) tile).inventory.getStackInSlot(0);
		return new PedestalContents(input, inventory.getStackInSlot(0));
	}

	public boolean isValid() {
		if (inventory.getStackInSlot(0).isEmpty())
			return false;
		BlockEntity tile = level.getBlockEntity(worldPosition.below());
		if (tile instanceof AlchemyPedestalBlockEntity)
			return !((AlchemyPedestalBlockEntity) tile).inventory.getStackInSlot(0).isEmpty();
		return false;
	}

	public boolean isActive() {
		return active > 0;
	}

	public void setActive(int time) {
		active = time;
	}

	@Override
	public void playSound(int id) {
		switch (id) {
		case SOUND_PROCESS:
			EmbersSounds.playMachineSound(this, SOUND_PROCESS, EmbersSounds.PEDESTAL_LOOP.get(), SoundSource.BLOCKS, true, 0.1f, 1.0f, (float)worldPosition.getX()+0.5f,(float)worldPosition.getY()+1.0f,(float)worldPosition.getZ()+0.5f);
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
		return id == SOUND_PROCESS && isActive();
	}
}
