package com.rekindled.embers.item;

import org.jetbrains.annotations.Nullable;

import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.block.GlimmerBlock;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class GlimmerCrystalItem extends Item {

	public GlimmerCrystalItem(Properties pProperties) {
		super(pProperties);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		if (!world.isClientSide() && world.getGameTime() % 10 == 0 && world.getBrightness(LightLayer.SKY, entity.blockPosition()) - world.getSkyDarken() > 12) {
			stack.setDamageValue(stack.getDamageValue() -1);
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged || !ItemStack.isSameItem(oldStack, newStack);
	}

	@Override
	public InteractionResult useOn(UseOnContext pContext) {
		InteractionResult interactionresult = this.place(new BlockPlaceContext(pContext));
		if (!interactionresult.consumesAction() && this.isEdible()) {
			InteractionResult interactionresult1 = this.use(pContext.getLevel(), pContext.getPlayer(), pContext.getHand()).getResult();
			return interactionresult1 == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : interactionresult1;
		} else {
			return interactionresult;
		}
	}

	public InteractionResult place(BlockPlaceContext context) {
		if (!context.canPlace()) {
			return InteractionResult.FAIL;
		} else {
			BlockState blockstate = this.getPlacementState(context);
			if (blockstate == null) {
				return InteractionResult.FAIL;
			} else if (!this.placeBlock(context, blockstate)) {
				return InteractionResult.FAIL;
			} else {
				BlockPos blockpos = context.getClickedPos();
				Level level = context.getLevel();
				Player player = context.getPlayer();
				ItemStack itemstack = context.getItemInHand();
				BlockState blockstate1 = level.getBlockState(blockpos);
				if (blockstate1.is(blockstate.getBlock())) {
					blockstate1.getBlock().setPlacedBy(level, blockpos, blockstate1, player, itemstack);
					if (player instanceof ServerPlayer) {
						CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, blockpos, itemstack);
					}
				}

				SoundType soundtype = blockstate1.getSoundType(level, blockpos, context.getPlayer());
				level.playSound(player, blockpos, this.getPlaceSound(blockstate1, level, blockpos, player), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				level.gameEvent(GameEvent.BLOCK_PLACE, blockpos, GameEvent.Context.of(player, blockstate1));
				if (player == null || !player.getAbilities().instabuild) {
					itemstack.hurtAndBreak(1, player, e -> {});
				}
				level.addParticle(GlimmerBlock.GLIMMER, blockpos.getX()+0.5f, blockpos.getY()+0.5f, blockpos.getZ()+0.5f, 0, 0, 0);
				level.addParticle(GlimmerBlock.EMBER, blockpos.getX()+0.5f, blockpos.getY()+0.5f, blockpos.getZ()+0.5f, 0, 0.001, 0);

				return InteractionResult.sidedSuccess(level.isClientSide);
			}
		}
	}

	protected SoundEvent getPlaceSound(BlockState state, Level world, BlockPos pos, Player entity) {
		return state.getSoundType(world, pos, entity).getPlaceSound();
	}

	protected boolean placeBlock(BlockPlaceContext pContext, BlockState pState) {
		return pContext.getLevel().setBlock(pContext.getClickedPos(), pState, 11);
	}

	@Nullable
	protected BlockState getPlacementState(BlockPlaceContext pContext) {
		BlockState blockstate = RegistryManager.GLIMMER.get().getStateForPlacement(pContext);
		return blockstate;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return true;
	}

	@Override
	public int getBarColor(ItemStack pStack) {
		return 0xFFFFFF;
	}
}
