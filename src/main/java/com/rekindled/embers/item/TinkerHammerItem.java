package com.rekindled.embers.item;

import java.util.List;

import javax.annotation.Nullable;

import com.rekindled.embers.Embers;
import com.rekindled.embers.EmbersClientEvents;
import com.rekindled.embers.api.EmbersAPI;
import com.rekindled.embers.api.power.IEmberPacketProducer;
import com.rekindled.embers.api.power.IEmberPacketReceiver;
import com.rekindled.embers.api.power.ITargetable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TinkerHammerItem extends Item {

	public TinkerHammerItem(Properties pProperties) {
		super(pProperties);
		EmbersAPI.registerLinkingHammer(this);
		EmbersAPI.registerHammerTargetGetter(this);
	}

	@Override
	public final ItemStack getCraftingRemainingItem(ItemStack stack) {
		if (stack.isEmpty())
			return new ItemStack(this, stack.getCount(), stack.getTag());
		return stack.copy();
	}

	@Override
	public boolean hasCraftingRemainingItem() {
		return true;
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		CompoundTag nbt = stack.getOrCreateTag();
		BlockPos pos = context.getClickedPos();
		Level world = context.getLevel();
		BlockEntity tile = world.getBlockEntity(pos);
		if (world != null && nbt.contains("targetWorld") && world.dimension().location().toString().equals(nbt.getString("targetWorld"))) {
			BlockPos targetPos = new BlockPos(nbt.getInt("targetX"), nbt.getInt("targetY"), nbt.getInt("targetZ"));
			BlockEntity targetTile = world.getBlockEntity(targetPos);
			if (targetTile instanceof ITargetable) {
				if (tile instanceof IEmberPacketReceiver) {
					Direction face = Direction.byName(nbt.getString("targetFace"));
					((ITargetable) targetTile).setTargetPosition(pos, face);
					//calculate the trajectory of the ember packet
					if (targetTile instanceof IEmberPacketProducer) {
						Vec3 hitPos = Vec3.atCenterOf(pos.subtract(targetPos));
						Vec3 motion = ((IEmberPacketProducer) targetTile).getEmittingDirection(face);
						Vec3 oldPos = new Vec3(0.5, 0.5, 0.5);
						Vec3 newPos = oldPos.add(motion);

						for (int i = 0; i <= 80; ++i) {
							Vec3 targetVector = hitPos.subtract(newPos);
							double length = targetVector.length();
							targetVector = targetVector.scale(0.3 / length);
							double weight = 0;
							if (length <= 3) {
								weight = 0.9 * ((3.0 - length) / 3.0);
								if (length <= 0.2) {
									break;
								}
							}
							motion = new Vec3(
									(0.9 - weight) * motion.x + (0.1 + weight) * targetVector.x,
									(0.9 - weight) * motion.y + (0.1 + weight) * targetVector.y,
									(0.9 - weight) * motion.z + (0.1 + weight) * targetVector.z);
							newPos = oldPos.add(motion);
							oldPos = newPos;
						}
						((IEmberPacketReceiver) tile).setIncomingDirection(motion);
					}
					world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.5f, 1.5f + world.random.nextFloat() * 0.1f, false);
					nbt.remove("targetWorld");
					return InteractionResult.SUCCESS;
				}
			}
		}
		if (world != null && tile instanceof IEmberPacketProducer && tile instanceof ITargetable) {
			Direction face = context.getClickedFace();
			Vec3 emitDirection = ((IEmberPacketProducer) tile).getEmittingDirection(face);
			if (emitDirection == null)
				return InteractionResult.PASS;
			nbt.putString("targetWorld", world.dimension().location().toString());
			nbt.putString("targetFace", face.getName());
			nbt.putInt("targetX", pos.getX());
			nbt.putInt("targetY", pos.getY());
			nbt.putInt("targetZ", pos.getZ());
			world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.5f, 1.95f + world.random.nextFloat() * 0.2f, false);
			if (world.isClientSide)
				EmbersClientEvents.lastTarget = null;
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
		if (level != null && stack.hasTag()) {
			CompoundTag nbt = stack.getTag();
			if (nbt.contains("targetWorld")) {
				String dimension = nbt.getString("targetWorld");
				if(level.dimension().location().toString().equals(dimension)) {
					BlockPos pos = new BlockPos(nbt.getInt("targetX"), nbt.getInt("targetY"), nbt.getInt("targetZ"));
					BlockState blockState = level.getBlockState(pos);
					tooltip.add(Component.translatable(Embers.MODID + ".tooltip.aiming_block", blockState.getBlock().getName()).withStyle(ChatFormatting.GRAY));
					tooltip.add(Component.translatable(" X=" + pos.getX()).withStyle(ChatFormatting.GRAY));
					tooltip.add(Component.translatable(" Y=" + pos.getY()).withStyle(ChatFormatting.GRAY));
					tooltip.add(Component.translatable(" Z=" + pos.getZ()).withStyle(ChatFormatting.GRAY));
				}
			}
		}
	}
}
