package com.rekindled.embers.api;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.joml.Vector3f;

import com.rekindled.embers.api.misc.HammerTarget;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public interface IEmbersAPI {

	float getEmberDensity(long seed, int x, int z);

	float getEmberStability(long seed, int x, int z);

	void registerLinkingHammer(Item item);

	void registerLinkingHammer(BiPredicate<Player, InteractionHand> predicate);

	void registerHammerTargetGetter(Item item);

	void registerHammerTargetGetter(Function<Player, HammerTarget> predicate);

	boolean isHoldingHammer(Player player, InteractionHand hand);

	HammerTarget getHammerTarget(Player player);

	void registerLens(Ingredient ingredient);

	void registerWearableLens(Ingredient ingredient);

	void registerLens(Predicate<Player> predicate);

	boolean isWearingLens(Player player);

	void registerEmberResonance(Ingredient ingredient, double resonance);

	double getEmberResonance(ItemStack stack);

	double getEmberTotal(Player player);

	double getEmberCapacityTotal(Player player);

	void removeEmber(Player player, double amount);

	Item getTaggedItem(TagKey<Item> tag);

	double getScales(LivingEntity entity);

	void setScales(LivingEntity entity, double scales);

	void registerColor(ResourceLocation id, Vector3f color);

	Vector3f getColor(ResourceLocation id, Vector3f color);

	Vector3f getColor(ResourceLocation id);
}
