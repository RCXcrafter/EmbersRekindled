package com.rekindled.embers.blockentity;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager.MetalCrystalSeed;
import com.rekindled.embers.api.tile.IEmberInjectable;
import com.rekindled.embers.api.tile.IExtraCapabilityInformation;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.util.Misc;
import com.rekindled.embers.util.sound.ISoundController;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CrystalSeedBlockEntity extends BlockEntity implements IEmberInjectable, ISoundController, IExtraCapabilityInformation {

	public String type;
	public ResourceLocation texture;
	public TagKey<Item> tag;
	public boolean[] willSpawn;
	public int size = 0;
	public int xp = 0;
	public static int bonusParts = 0;
	public int ticksExisted = 0;
	protected static Random random = new Random();

	public static final int SOUND_AMBIENT = 1;
	public static final int[] SOUND_IDS = new int[]{SOUND_AMBIENT};

	HashSet<Integer> soundsPlaying = new HashSet<>();

	public CrystalSeedBlockEntity(BlockPos pos, BlockState blockState, String type) {
		this(MetalCrystalSeed.seeds.get(type).BLOCKENTITY.get(), pos, blockState, type);
	}

	public CrystalSeedBlockEntity(BlockEntityType<?> entityType, BlockPos pos, BlockState blockState, String type) {
		super(entityType, pos, blockState);
		willSpawn = getSpawns(xp);
		this.type = type;
		this.texture = new ResourceLocation(Embers.MODID + ":textures/block/material_" + type + ".png");
		this.tag = ItemTags.create(new ResourceLocation("forge", "nuggets/" + type));
	}

	public static boolean[] getSpawns(int xp) {
		int segments = Math.max(6 + bonusParts, 1);
		segments += getLevelBonus(getLevel(xp));
		boolean[] willSpawn = new boolean[segments];
		for (int i = 0; i < willSpawn.length; i ++){
			willSpawn[i] = random.nextInt(3) == 0;
		}
		return willSpawn;
	}

	public static int getLevelBonus(int level) {
		if (level > 50) {
			return getLevelBonus(50) + (level-50)/25;
		} else if (level > 20) {
			return getLevelBonus(20) + (level-20)/10;
		} else if (level > 10) {
			return getLevelBonus(10) + (level-10)/5;
		} else if (level > 5) {
			return getLevelBonus(5) + (level-5)/3;
		} else {
			return (level+1)/2;
		}
	}

	public static String getSpawnString(boolean[] willSpawn) {
		String result = "";
		for (int i = 0; i < willSpawn.length; i ++) {
			result += willSpawn[i] ? "1" : "0";
		}
		return result;
	}

	public void loadSpawnsFromString(String s) {
		willSpawn = new boolean[s.length()];
		for (int i = 0; i < s.length(); i ++){
			willSpawn[i] = s.substring(i, i+1).compareTo("1") == 0;
		}
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		loadSpawnsFromString(nbt.getString("spawns"));
		size = nbt.getInt("size");
		xp = nbt.getInt("xp");
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.putString("spawns", getSpawnString(willSpawn));
		nbt.putInt("size", size);
		nbt.putInt("xp", xp);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbt = super.getUpdateTag();
		saveAdditional(nbt);
		return nbt;
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, CrystalSeedBlockEntity blockEntity) {
		blockEntity.ticksExisted++;
		blockEntity.handleSound();
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, CrystalSeedBlockEntity blockEntity) {
		blockEntity.ticksExisted++;
		if (blockEntity.size > 1000) {
			blockEntity.size = 0;
			ItemStack[] stacks = blockEntity.getNuggetDrops(blockEntity.willSpawn.length);
			double oneAng = 360.0 / blockEntity.willSpawn.length;
			for (int i = 0; i < blockEntity.willSpawn.length; i ++) {
				if (blockEntity.willSpawn[i]) {
					ItemStack nuggetStack = stacks[i];
					float offX = 0.4f*(float)Math.sin(Math.toRadians(i * oneAng));
					float offZ = 0.4f*(float)Math.cos(Math.toRadians(i * oneAng));
					level.addFreshEntity(new ItemEntity(level, pos.getX()+0.5+offX, pos.getY()+0.5f, pos.getZ()+0.5+offZ, nuggetStack));
					level.playSound(null, pos.getX()+0.5+offX, pos.getY()+0.5f, pos.getZ()+0.5+offZ, EmbersSounds.METAL_SEED_PING.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
				}
			}
			blockEntity.setChanged();
			blockEntity.willSpawn = getSpawns(blockEntity.xp);
		}
	}

	protected ItemStack[] getNuggetDrops(int n) {
		return IntStream.range(0,n).mapToObj(i -> Misc.getTaggedItem(tag).copy()).toArray(ItemStack[]::new);
	}

	public void addExperience(int xp) {
		this.xp += xp;
	}

	public int getRequiredExperienceForLevel(int level) {
		return ((level*(level+1))/2)*1000;
	}

	public static int getLevel(int xp) {
		return (int)Math.floor((Math.sqrt(5)*Math.sqrt(xp+125)-25)/50);
	}

	@Override
	public void inject(BlockEntity injector, double ember) {
		size++;
		addExperience(1);
		setChanged();
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level instanceof ServerLevel serverLevel) {
			for (ServerPlayer serverplayer : serverLevel.getServer().getPlayerList().getPlayers()) {
				serverplayer.connection.send(this.getUpdatePacket());
			}
		}
	}

	@Override
	public void playSound(int id) {
		switch (id) {
		case SOUND_AMBIENT:
			EmbersSounds.playMachineSound(this, SOUND_AMBIENT, EmbersSounds.METAL_SEED_LOOP.get(), SoundSource.BLOCKS, true, 1.0f, 1.0f, worldPosition.getX() + 0.5f, worldPosition.getY() + 0.5f, worldPosition.getZ() + 0.5f);
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
		return id == SOUND_AMBIENT;
	}

	@Override
	public void addOtherDescription(List<String> strings, Direction facing) {
		int level = getLevel(xp);
		int requiredCurrentXP = getRequiredExperienceForLevel(level);
		int requiredNextXP = getRequiredExperienceForLevel(level+1);

		strings.add(I18n.get(Embers.MODID + ".tooltip.crystal.level", level));
		strings.add(I18n.get(Embers.MODID + ".tooltip.crystal.xp", xp-requiredCurrentXP, requiredNextXP-requiredCurrentXP));
	}
}
