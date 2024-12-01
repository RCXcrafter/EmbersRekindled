package com.rekindled.embers.augment;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class ToolExplosion extends Explosion {

	public ItemStack tool = ItemStack.EMPTY;

	public ToolExplosion(ItemStack tool, Level pLevel, @Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, List<BlockPos> pPositions) {
		super(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pPositions);
		this.tool = tool;
	}

	public ToolExplosion(ItemStack tool, Level pLevel, @Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, Explosion.BlockInteraction pBlockInteraction, List<BlockPos> pPositions) {
		super(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction, pPositions);
		this.tool = tool;
	}

	public ToolExplosion(ItemStack tool, Level pLevel, @Nullable Entity pSource, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, Explosion.BlockInteraction pBlockInteraction) {
		super(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction);
		this.tool = tool;
	}

	public ToolExplosion(ItemStack tool, Level pLevel, Entity pSource, DamageSource pDamageSource, ExplosionDamageCalculator pDamageCalculator, double pToBlowX, double pToBlowY, double pToBlowZ, float pRadius, boolean pFire, BlockInteraction pBlockInteraction) {
		super(pLevel, pSource, pDamageSource, pDamageCalculator, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction);
		this.tool = tool;
	}

	/**
	 * Does the second part of the explosion (sound, particles, drop spawn)
	 */
	public void finalizeExplosion(boolean pSpawnParticles) {
		if (this.level.isClientSide) {
			this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
		}

		boolean flag = this.interactsWithBlocks();
		if (pSpawnParticles) {
			if (!(this.radius < 2.0F) && flag) {
				this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
			} else {
				this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
			}
		}

		if (flag) {
			ObjectArrayList<Pair<ItemStack, BlockPos>> objectarraylist = new ObjectArrayList<>();
			boolean flag1 = this.getIndirectSourceEntity() instanceof Player;
			Util.shuffle(this.toBlow, this.level.random);

			for(BlockPos blockpos : this.toBlow) {
				BlockState blockstate = this.level.getBlockState(blockpos);
				if (!blockstate.isAir()) {
					BlockPos blockpos1 = blockpos.immutable();
					this.level.getProfiler().push("explosion_blocks");
					if (blockstate.canDropFromExplosion(this.level, blockpos, this)) {
						Level $$9 = this.level;
						if ($$9 instanceof ServerLevel) {
							ServerLevel serverlevel = (ServerLevel)$$9;
							BlockEntity blockentity = blockstate.hasBlockEntity() ? this.level.getBlockEntity(blockpos) : null;
							LootParams.Builder lootparams$builder = (new LootParams.Builder(serverlevel)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockpos)).withParameter(LootContextParams.TOOL, tool).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockentity).withOptionalParameter(LootContextParams.THIS_ENTITY, this.source);
							if (this.blockInteraction == Explosion.BlockInteraction.DESTROY_WITH_DECAY) {
								lootparams$builder.withParameter(LootContextParams.EXPLOSION_RADIUS, this.radius);
							}

							blockstate.spawnAfterBreak(serverlevel, blockpos, ItemStack.EMPTY, flag1);
							blockstate.getDrops(lootparams$builder).forEach((p_46074_) -> {
								addBlockDrops(objectarraylist, p_46074_, blockpos1);
							});
						}
					}

					blockstate.onBlockExploded(this.level, blockpos, this);
					this.level.getProfiler().pop();
				}
			}

			for(Pair<ItemStack, BlockPos> pair : objectarraylist) {
				Block.popResource(this.level, pair.getSecond(), pair.getFirst());
			}
		}

		if (this.fire) {
			for(BlockPos blockpos2 : this.toBlow) {
				if (this.random.nextInt(3) == 0 && this.level.getBlockState(blockpos2).isAir() && this.level.getBlockState(blockpos2.below()).isSolidRender(this.level, blockpos2.below())) {
					this.level.setBlockAndUpdate(blockpos2, BaseFireBlock.getState(this.level, blockpos2));
				}
			}
		}
	}
}
