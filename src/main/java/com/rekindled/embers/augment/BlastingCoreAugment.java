package com.rekindled.embers.augment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import com.rekindled.embers.api.EmbersAPI;
import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.util.EmberInventoryUtil;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlastingCoreAugment extends AugmentBase {

	public BlastingCoreAugment(ResourceLocation id) {
		super(id, 2.0);
		MinecraftForge.EVENT_BUS.register(this);
	}

	private double getChanceBonus(double resonance) {
		if (resonance > 1)
			return 1 + (resonance - 1) * 0.5;
		else
			return resonance;
	}

	@SubscribeEvent
	public void onBreak(BreakEvent event) {
		LevelAccessor world = event.getLevel();
		BlockPos pos = event.getPos();
		if (event.getPlayer() != null){
			if (!event.getPlayer().getMainHandItem().isEmpty()) {
				ItemStack s = event.getPlayer().getMainHandItem();
				int blastingLevel = AugmentUtil.getAugmentLevel(s, this);
				if (blastingLevel > 0 && EmberInventoryUtil.getEmberTotal(event.getPlayer()) >= cost) { 
					event.getPlayer().level().explode(event.getPlayer(), pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 0.5f, ExplosionInteraction.BLOCK);
					double resonance = EmbersAPI.getEmberResonance(s);
					double chance = (double) blastingLevel / (blastingLevel + 1) * getChanceBonus(resonance);

					for (BlockPos toExplode : getBlastCube(world, pos, event.getPlayer(), chance)) {
						BlockState state = world.getBlockState(toExplode);
						if (state.getDestroySpeed(world, pos) >= 0 && event.getPlayer().hasCorrectToolForDrops(world.getBlockState(toExplode))) {
							world.destroyBlock(toExplode, true);
						}
					}
					EmberInventoryUtil.removeEmber(event.getPlayer(), cost);
				}
			}
		}
	}

	public Iterable<BlockPos> getBlastAdjacent(LevelAccessor world, BlockPos pos, Player player, double chance) {
		ArrayList<BlockPos> posList = new ArrayList<>();
		for (Direction face : Direction.values()) {
			if (Misc.random.nextDouble() < chance) {
				posList.add(pos.relative(face));
			}
		}
		return posList;
	}

	public Iterable<BlockPos> getBlastCube(LevelAccessor world, BlockPos pos, Player player, double chance) {
		ArrayList<BlockPos> posList = new ArrayList<>();
		for (Direction facePrimary : Direction.values()) {
			if (Misc.random.nextDouble() < chance) {
				BlockPos posPrimary = pos.relative(facePrimary);
				posList.add(posPrimary);

				for (Direction faceSecondary : Direction.values()) {
					if(faceSecondary.getAxis() == facePrimary.getAxis())
						continue;
					if (Misc.random.nextDouble() < chance - 0.5) {
						BlockPos posSecondary = posPrimary.relative(faceSecondary);
						posList.add(posSecondary);

						for (Direction faceTertiary : Direction.values()) {
							if (faceTertiary.getAxis() == facePrimary.getAxis() || faceTertiary.getAxis() == faceSecondary.getAxis())
								continue;
							if (Misc.random.nextDouble() < chance - 1.0) {
								BlockPos posTertiary = posSecondary.relative(faceTertiary);
								posList.add(posTertiary);
							}
						}
					}
				}
			}
		}
		return posList;
	}

	private HashSet<Entity> blastedEntities = new HashSet<>();

	@SubscribeEvent
	public void onHit(LivingHurtEvent event) {
		if (!blastedEntities.contains(event.getEntity()) && event.getSource().getEntity() != event.getEntity() && event.getSource().getDirectEntity() != event.getEntity())
			try {
				if (event.getSource().getEntity() instanceof Player damager) {
					blastedEntities.add(damager);
					ItemStack s = damager.getMainHandItem();
					if (!s.isEmpty()) {
						int blastingLevel = AugmentUtil.getAugmentLevel(s, this);
						if (blastingLevel > 0 && EmberInventoryUtil.getEmberTotal(damager) >= cost) {
							double resonance = EmbersAPI.getEmberResonance(s);
							float strength = (float) ((resonance + 1) * (Math.atan(0.6 * (blastingLevel)) / (Math.PI)));

							EmberInventoryUtil.removeEmber(damager, cost);
							List<LivingEntity> entities = damager.level().getEntitiesOfClass(LivingEntity.class, new AABB(event.getEntity().getX() - 4.0 * strength, event.getEntity().getY() - 4.0 * strength, event.getEntity().getZ() - 4.0 * strength,
									event.getEntity().getX() + 4.0 * strength, event.getEntity().getY() + 4.0 * strength, event.getEntity().getZ() + 4.0 * strength));
							Explosion explosion = damager.level().explode(damager, event.getEntity().getX(), event.getEntity().getY() + event.getEntity().getBbHeight() / 2.0, event.getEntity().getZ(), 0.5f, ExplosionInteraction.BLOCK);
							for (LivingEntity e : entities) {
								if (!Objects.equals(e.getUUID(), damager.getUUID())) {
									e.hurt(damager.level().damageSources().explosion(explosion), event.getAmount() * strength);
									e.hurtTime = 0;
								}
							}
						}
					}
				}
				if (event.getEntity() instanceof Player damager) {
					int blastingLevel = AugmentUtil.getArmorAugmentLevel(damager, this);

					if (blastingLevel > 0 && EmberInventoryUtil.getEmberTotal(damager) >= cost) {
						float strength = (float) (2.0 * (Math.atan(0.6 * (blastingLevel)) / (Math.PI)));
						EmberInventoryUtil.removeEmber(damager, cost);
						List<LivingEntity> entities = damager.level().getEntitiesOfClass(LivingEntity.class, new AABB(damager.getX() - 4.0 * strength, damager.getY() - 4.0 * strength, damager.getZ() - 4.0 * strength,
								damager.getX() + 4.0 * strength, damager.getY() + 4.0 * strength, damager.getZ() + 4.0 * strength));
						Explosion explosion = damager.level().explode(damager, damager.getX(), damager.getY() + damager.getBbHeight() / 2.0, damager.getZ(), 0.5f, ExplosionInteraction.BLOCK);
						for (LivingEntity e : entities) {
							if (!Objects.equals(e.getUUID(), event.getEntity().getUUID())) {
								blastedEntities.add(e);
								e.hurt(damager.level().damageSources().explosion(explosion), event.getAmount() * strength * 0.25f);
								e.knockback(2.0f * strength, -e.getX() + damager.getX(), -e.getZ() + damager.getZ());
								e.hurtTime = 0;
							}
						}
					}
				}
			} finally {
				blastedEntities.clear();
			}
	}
}
