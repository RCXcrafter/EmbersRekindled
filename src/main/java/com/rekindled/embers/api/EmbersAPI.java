package com.rekindled.embers.api;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class EmbersAPI {

	public static IEmbersAPI IMPL;

	/*public static ModifierBase CORE;
    public static ModifierBase SUPERHEATER;
    public static ModifierBase JET_AUGMENT;
    public static ModifierBase CASTER_ORB;
    public static ModifierBase RESONATING_BELL;
    public static ModifierBase BLASTING_CORE;
    public static ModifierBase FLAME_BARRIER;
    public static ModifierBase ELDRITCH_INSIGNIA;
    public static ModifierBase INTELLIGENT_APPARATUS;
    public static ModifierBase DIFFRACTION;
    public static ModifierBase FOCAL_LENS;
    public static ModifierBase TINKER_LENS;
    public static ModifierBase ANTI_TINKER_LENS;
    public static ModifierBase SHIFTING_SCALES;
    public static ModifierBase WINDING_GEARS;
    public static ModifierBase CORE_STONE;*/

	/*public static void registerModifier(Item item, ModifierBase modifier) {
        IMPL.registerModifier(item, modifier);
    }*/

	/*public static void registerEmberToolEffeciency(Ingredient ingredient, double efficiency) {
		IMPL.registerEmberToolEffeciency(ingredient, efficiency);
	}

	public static void registerEmberToolEffeciency(IFuel fuel) {
		IMPL.registerEmberToolEffeciency(fuel);
	}

	public static void unregisterEmberToolEffeciency(IFuel fuel) {
		IMPL.unregisterEmberToolEffeciency(fuel);
	}

	public static IFuel getEmberToolEfficiency(ItemStack stack) {
		return IMPL.getEmberToolEfficiency(stack);
	}

	public static double getEmberEfficiency(ItemStack stack) {
		return IMPL.getEmberEfficiency(stack);
	}*/

	public static float getEmberDensity(long seed, int x, int z) {
		return IMPL.getEmberDensity(seed, x, z);
	}

	public static float getEmberStability(long seed, int x, int z) {
		return IMPL.getEmberStability(seed, x, z);
	}

	public static void registerLinkingHammer(Item item) {
		IMPL.registerLinkingHammer(item);
	}

	public static void registerLinkingHammer(BiPredicate<Player, InteractionHand> predicate) {
		IMPL.registerLinkingHammer(predicate);
	}

	public static void registerHammerTargetGetter(Item item) {
		IMPL.registerHammerTargetGetter(item);
	}

	public static void registerHammerTargetGetter(Function<Player, Pair<BlockPos, Direction>> predicate) {
		IMPL.registerHammerTargetGetter(predicate);
	}

	boolean isHoldingHammer(Player player, InteractionHand hand) {
		return IMPL.isHoldingHammer(player, hand);
	}

	Pair<BlockPos, Direction> getHammerTarget(Player player) {
		return IMPL.getHammerTarget(player);
	}

	public static void registerLens(Item item) {
		IMPL.registerLens(item);
	}

	public static void registerWearableLens(Item item) {
		IMPL.registerWearableLens(item);
	}

	public static void registerLens(Predicate<Player> predicate) {
		IMPL.registerLens(predicate);
	}

	boolean isWearingLens(Player player) {
		return IMPL.isWearingLens(player);
	}

	public static double getEmberTotal(Player player) {
		return IMPL.getEmberTotal(player);
	}

	public static double getEmberCapacityTotal(Player player) {
		return IMPL.getEmberCapacityTotal(player);
	}

	public static void removeEmber(Player player, double amount) {
		IMPL.removeEmber(player, amount);
	}

	ItemStack getTaggedItem(TagKey<Item> tag) {
		return IMPL.getTaggedItem(tag);
	}

	/*public static double getScales(LivingEntity entity) {
		return IMPL.getScales(entity);
	}

	public static void setScales(LivingEntity entity, double scales) {
		IMPL.setScales(entity,scales);
	}*/
}
