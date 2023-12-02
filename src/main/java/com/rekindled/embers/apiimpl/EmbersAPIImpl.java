package com.rekindled.embers.apiimpl;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;

import com.rekindled.embers.api.EmbersAPI;
import com.rekindled.embers.api.IEmbersAPI;
import com.rekindled.embers.api.upgrades.UpgradeUtil;
import com.rekindled.embers.util.EmberGenUtil;
import com.rekindled.embers.util.EmberInventoryUtil;
import com.rekindled.embers.util.Misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class EmbersAPIImpl implements IEmbersAPI {

	public static void init() {
		EmbersAPI.IMPL = new EmbersAPIImpl();
		//ItemModUtil.IMPL = new ItemModUtilImpl();
		UpgradeUtil.IMPL = new UpgradeUtilImpl();
	}

	/*@Override
    public void registerModifier(Item item, ModifierBase modifier) {
        ItemModUtil.registerModifier(item, modifier);
    }*/

	/*@Override
    public void registerEmberToolEffeciency(Ingredient ingredient, double efficiency) {
        registerEmberToolEffeciency(new IFuel() { //TODO: move to actual class in apiimpl
            @Override
            public boolean matches(ItemStack stack) {
                return ingredient.apply(stack);
            }

            @Override
            public double getFuelValue(ItemStack stack) {
                return efficiency;
            }
        });
    }

    @Override
    public void registerEmberToolEffeciency(IFuel fuel) {
        emberEfficiency.add(fuel);
    }

    @Override
    public void unregisterEmberToolEffeciency(IFuel fuel) {
        emberEfficiency.remove(fuel);
    }

    @Override
    public IFuel getEmberToolEfficiency(ItemStack stack) {
        for(IFuel fuel : emberEfficiency)
            if(fuel.matches(stack))
                return fuel;
        return null;
    }

    @Override
    public  getEmberEfficiency(ItemStack stack) {
        IFuel fuel = getEmberToolEfficiency(stack);
        return fuel != null ? fuel.getFuelValue(stack) : 1;
    }*/

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

	/*@Override
    public double getScales(EntityLivingBase entity) {
        IAttributeInstance instance = entity.getEntityAttribute(ModifierShiftingScales.SCALES);
        return instance.getBaseValue();
    }

    @Override
    public void setScales(EntityLivingBase entity, double scales) {
        IAttributeInstance instance = entity.getEntityAttribute(ModifierShiftingScales.SCALES);
        instance.setBaseValue(scales);
    }*/
}
