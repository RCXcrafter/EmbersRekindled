package com.rekindled.embers.item;

import java.util.List;

import javax.annotation.Nullable;

import com.rekindled.embers.Embers;
import com.rekindled.embers.api.power.IEmberPacketProducer;
import com.rekindled.embers.api.power.IEmberPacketReceiver;
import com.rekindled.embers.api.tile.ITargetable;

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

public class TinkerHammerItem extends Item {

	public TinkerHammerItem(Properties pProperties) {
		super(pProperties);
		pProperties.craftRemainder(this);
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
			if (targetTile instanceof IEmberPacketProducer) {
				if (tile instanceof IEmberPacketReceiver) {
					((IEmberPacketProducer) targetTile).setTargetPosition(pos, Direction.byName(nbt.getString("targetFace")));
					world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 1.0f, 1.5f + world.random.nextFloat() * 0.1f, false);
					nbt.remove("targetWorld");
					return InteractionResult.SUCCESS;
				}
			} else if (targetTile instanceof ITargetable) {
				((ITargetable) targetTile).setTarget(pos);
				world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 1.0f, 1.5f + world.random.nextFloat() * 0.1f, false);
				nbt.remove("targetWorld");
				return InteractionResult.SUCCESS;
			}
		}
		if (world != null && (tile instanceof IEmberPacketProducer || tile instanceof ITargetable)) {
			nbt.putString("targetWorld", world.dimension().location().toString());
			nbt.putString("targetFace", context.getClickedFace().getName());
			nbt.putInt("targetX", pos.getX());
			nbt.putInt("targetY", pos.getY());
			nbt.putInt("targetZ", pos.getZ());
			world.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 1.0f, 1.95f + world.random.nextFloat() * 0.2f, false);
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
