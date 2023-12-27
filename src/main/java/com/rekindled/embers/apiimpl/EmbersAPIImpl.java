package com.rekindled.embers.apiimpl;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;

import com.rekindled.embers.api.EmbersAPI;
import com.rekindled.embers.api.IEmbersAPI;
import com.rekindled.embers.api.augment.AugmentUtil;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.augment.ShiftingScalesAugment.IScalesCapability;
import com.rekindled.embers.augment.ShiftingScalesAugment.ScalesCapabilityProvider;
import com.rekindled.embers.network.PacketHandler;
import com.rekindled.embers.network.message.MessageScalesData;
import com.rekindled.embers.util.EmberGenUtil;
import com.rekindled.embers.util.EmberInventoryUtil;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.network.PacketDistributor;

public class EmbersAPIImpl implements IEmbersAPI {

	public static void init() {
		EmbersAPI.IMPL = new EmbersAPIImpl();
		AugmentUtil.IMPL = new AugmentUtilImpl();
		UpgradeUtil.IMPL = new UpgradeUtilImpl();
	}

	@Override
	public float getEmberDensity(long seed, int x, int z) {
		return EmberGenUtil.getEmberDensity(seed, x, z);
	}

	@Override
	public float getEmberStability(long seed, int x, int z) {
		return EmberGenUtil.getEmberStability(seed, x, z);
	}

	@Override
	public void registerLinkingHammer(Item item) {
		Misc.IS_HOLDING_HAMMER.add((player, hand) -> player.getItemInHand(hand).getItem() == item);
	}

	@Override
	public void registerLinkingHammer(BiPredicate<Player, InteractionHand> predicate) {
		Misc.IS_HOLDING_HAMMER.add(predicate);
	}

	@Override
	public void registerHammerTargetGetter(Item item) {
		Misc.GET_HAMMER_TARGET.add(player -> {
			ItemStack stack = player.getMainHandItem();
			if (stack.getItem() != item) {
				stack = player.getOffhandItem();
			}
			if (stack.getItem() == item && stack.hasTag()) {	
				CompoundTag nbt = stack.getTag();
				if (stack.hasTag() && nbt.contains("targetWorld") && player.level().dimension().location().toString().equals(nbt.getString("targetWorld"))) {
					return Pair.of(new BlockPos(nbt.getInt("targetX"), nbt.getInt("targetY"), nbt.getInt("targetZ")), Direction.byName(nbt.getString("targetFace")));
				}
			}
			return null;
		});
	}

	@Override
	public void registerHammerTargetGetter(Function<Player, Pair<BlockPos, Direction>> predicate) {
		Misc.GET_HAMMER_TARGET.add(predicate);
	}

	@Override
	public boolean isHoldingHammer(Player player, InteractionHand hand) {
		return Misc.isHoldingHammer(player, hand);
	}

	@Override
	public Pair<BlockPos, Direction> getHammerTarget(Player player) {
		return Misc.getHammerTarget(player);
	}

	@Override
	public void registerLens(Item item) {
		Misc.IS_WEARING_LENS.add((player) -> player.getMainHandItem().getItem() == item || player.getOffhandItem().getItem() == item);
	}

	@Override
	public void registerWearableLens(Item item) {
		Misc.IS_WEARING_LENS.add((player) -> player.getInventory().armor.get(EquipmentSlot.HEAD.getIndex()).getItem() == item);
	}

	@Override
	public void registerLens(Predicate<Player> predicate) {
		Misc.IS_WEARING_LENS.add(predicate);
	}

	@Override
	public boolean isWearingLens(Player player) {
		return Misc.isWearingLens(player);
	}

	@Override
	public void registerEmberResonance(Ingredient ingredient, double resonance) {
		Misc.GET_EMBER_RESONANCE.add((stack) -> ingredient.test(stack) ? resonance : -1.0);
	}

	@Override
	public double getEmberResonance(ItemStack stack) {
		return Misc.getEmberResonance(stack);
	}

	@Override
	public double getEmberTotal(Player player) {
		return EmberInventoryUtil.getEmberTotal(player);
	}

	@Override
	public double getEmberCapacityTotal(Player player) {
		return EmberInventoryUtil.getEmberCapacityTotal(player);
	}

	@Override
	public void removeEmber(Player player, double amount) {
		EmberInventoryUtil.removeEmber(player, amount);
	}

	@Override
	public Item getTaggedItem(TagKey<Item> tag) {
		return Misc.getTaggedItem(tag);
	}

	@Override
	public double getScales(LivingEntity entity) {
		IScalesCapability cap = entity.getCapability(ScalesCapabilityProvider.scalesCapability).orElse(null);
		if (cap != null) {
			return cap.getScales();
		}
		return 0;
	}

	@Override
	public void setScales(LivingEntity entity, double scales) {
		IScalesCapability cap = entity.getCapability(ScalesCapabilityProvider.scalesCapability).orElse(null);
		if (cap != null) {
			if (entity instanceof ServerPlayer player && cap.getScales() != scales) {
				PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new MessageScalesData(scales));
			}
			cap.setScales(scales);
		}
	}
}
