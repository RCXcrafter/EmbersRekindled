package com.rekindled.embers.augment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.rekindled.embers.api.EmbersAPI;
import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.particle.XRayGlowParticleOptions;
import com.rekindled.embers.util.EmberInventoryUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ResonatingBellAugment extends AugmentBase {

	public static HashMap<UUID, Float> cooldownTicksServer = new HashMap<>();

	public ResonatingBellAugment(ResourceLocation id) {
		super(id, 5.0);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static void setCooldown(UUID uuid, float ticks) {
		cooldownTicksServer.put(uuid, ticks);
	}

	public static boolean hasCooldown(UUID uuid) {
		return cooldownTicksServer.getOrDefault(uuid, 0.0f) > 0;
	}

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
			for (UUID uuid : cooldownTicksServer.keySet()) {
				Float ticks = cooldownTicksServer.get(uuid) - 1;
				cooldownTicksServer.put(uuid, ticks);
			}
		}
	}

	@SubscribeEvent
	public void onClick(PlayerInteractEvent.RightClickBlock event) {
		ItemStack heldStack = event.getItemStack();
		Level world = event.getLevel();
		Player player = event.getEntity();
		BlockPos pos = event.getPos();
		if (AugmentUtil.hasHeat(heldStack)) {
			int level = AugmentUtil.getAugmentLevel(heldStack, this);
			UUID uuid = player.getUUID();
			if (!world.isClientSide() && level > 0 && EmberInventoryUtil.getEmberTotal(player) >= cost && !hasCooldown(uuid)) {
				double resonance = EmbersAPI.getEmberResonance(heldStack);
				int blockLimit = (int) (150 * level * resonance);
				int radius = (int) (1 + 3 * level * resonance);

				setCooldown(uuid, 80);
				BlockState state = world.getBlockState(pos);
				int count = 0;
				List<BlockPos> positions = new ArrayList<>();
				BlockPos.MutableBlockPos mutablePos = pos.mutable();
				int baseX = pos.getX();
				int baseY = pos.getY();
				int baseZ = pos.getZ();
				for (int i = -radius; i <= radius; i++) {
					for (int j = -radius; j <= radius; j++) {
						for (int k = -radius; k <= radius; k++) {
							mutablePos.set(baseX + i, baseY + j, baseZ + k);
							if (world.getBlockState(mutablePos) == state) {
								positions.add(mutablePos.immutable());
								count++;
								if (count > blockLimit)
									break;
							}
						}
					}
				}
				if (count <= blockLimit) {
					if (world instanceof ServerLevel serverLevel) {
						for (BlockPos p : positions) {
							serverLevel.sendParticles(XRayGlowParticleOptions.EMBER_BIG_NOMOTION, p.getX() + 0.5f, p.getY() + 0.5f, p.getZ() + 0.5f, 3, 0.0625, 0.0625, 0.0625, 1.0);
						}
					}
					world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), EmbersSounds.RESONATING_BELL.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
				} else {
					world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), EmbersSounds.RESONATING_BELL.get(), SoundSource.PLAYERS, 1.0f, 0.1f);
				}
				EmberInventoryUtil.removeEmber(player, cost);
			}
		}
	}
}
