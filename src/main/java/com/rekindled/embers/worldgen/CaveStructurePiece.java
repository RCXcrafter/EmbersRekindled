package com.rekindled.embers.worldgen;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class CaveStructurePiece extends PoolElementStructurePiece {

	public CaveStructurePiece(StructurePieceSerializationContext pContext, CompoundTag pTag) {
		super(pContext, pTag);
	}

	public CaveStructurePiece(StructureTemplateManager pStructureTemplateManager, StructurePoolElement pElement, BlockPos pPosition, int pGroundLevelDelta, Rotation pRotation, BoundingBox pBox) {
		super(pStructureTemplateManager, pElement, pPosition, pGroundLevelDelta, pRotation, pBox);
	}

	@Override
	public void postProcess(WorldGenLevel worldGenLevel, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomSource, BoundingBox boundingBox, ChunkPos chunkPos, BlockPos pos) {
		BlockPos center = StructureTemplate.transform(new BlockPos(this.getBoundingBox().getXSpan() / 2, 0, this.getBoundingBox().getZSpan() / 2), Mirror.NONE, this.getRotation(), BlockPos.ZERO).offset(this.position);
		int caveHeight = getCaveFloor(center, worldGenLevel);
		if (caveHeight != Integer.MIN_VALUE) {
			this.position = new BlockPos(this.position.getX(), caveHeight, this.position.getZ());
			super.postProcess(worldGenLevel, structureManager, chunkGenerator, randomSource, boundingBox, chunkPos, new BlockPos(pos.getX(), caveHeight, pos.getZ()));
		}
	}

	public static int getCaveFloor(BlockPos endPos, WorldGenLevel level) {
		List<Integer> heights = new ArrayList<Integer>();
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(endPos.getX(), level.getMinBuildHeight() + 9, endPos.getZ());
		for (int i = level.getMinBuildHeight() + 9; i <= endPos.getY(); i++) {
			boolean solid = !level.getBlockState(pos).canBeReplaced();
			pos.setY(i + 1);
			boolean replaceable = level.getBlockState(pos).canBeReplaced();
			if (solid && replaceable) {
				heights.add(i);
			}
		}
		if (heights.isEmpty())
			return Integer.MIN_VALUE;
		if (heights.size() > 1)
			return heights.get(1);
		return heights.get(0);
	}
}
