package com.rekindled.embers.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.blockentity.CrystalSeedBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class CrystalSeedStructureProcessor extends StructureProcessor {

	public static final Codec<CrystalSeedStructureProcessor> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(
				Codec.INT.fieldOf("min_xp").forGetter((processor) -> {
					return processor.minXp;
				}), Codec.INT.fieldOf("max_xp").forGetter((processor) -> {
					return processor.maxXp;
				}), Codec.INT.fieldOf("size").forGetter((processor) -> {
					return processor.size;
				})).apply(instance, CrystalSeedStructureProcessor::new);
	});

	public final int minXp;
	public final int maxXp;
	public final int size;

	public CrystalSeedStructureProcessor(int minXp, int maxXp, int size) {
		this.minXp = minXp;
		this.maxXp = maxXp;
		this.size = size;
	}

	@Override
	public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos offset, BlockPos pos, StructureTemplate.StructureBlockInfo blockInfo, StructureTemplate.StructureBlockInfo relativeBlockInfo, StructurePlaceSettings settings) {
		CompoundTag nbt = relativeBlockInfo.nbt();
		if (nbt != null && nbt.contains("id")) {
			ResourceLocation id = new ResourceLocation(nbt.getString("id"));
			if (id.getNamespace().equals(Embers.MODID) && id.getPath().contains("crystal_seed")) {
				nbt = nbt.copy();
				int xp = getXp(settings.getRandom(relativeBlockInfo.pos()));

				nbt.putInt("xp", xp);
				nbt.putInt("size", size);
				nbt.putString("spawns", CrystalSeedBlockEntity.getSpawnString(CrystalSeedBlockEntity.getSpawns(xp)));

				return new StructureTemplate.StructureBlockInfo(relativeBlockInfo.pos(), relativeBlockInfo.state(), nbt);
			}
		}
		return relativeBlockInfo;
	}

	public int getXp(RandomSource rand) {
		int xp = rand.nextInt(minXp, maxXp);
		xp = xp - xp % 1000;
		xp += size;
		return xp;
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return RegistryManager.CRYSTAL_SEED_PROCESSOR.get();
	}
}
