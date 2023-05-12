package com.rekindled.embers.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.rekindled.embers.ConfigManager;

import net.minecraft.client.gui.Font;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class Misc {

	public static final double LOG_E = Math.log10(Math.exp(1));
	public static Random random = new Random();
	public static Direction[] horizontals = {
			Direction.NORTH,
			Direction.SOUTH,
			Direction.WEST,
			Direction.EAST
	};
	public static final List<BiPredicate<Player, InteractionHand>> IS_HOLDING_HAMMER = new ArrayList<BiPredicate<Player, InteractionHand>>();
	public static final List<Function<Player, BlockPos>> GET_HAMMER_TARGET = new ArrayList<Function<Player, BlockPos>>();
	public static final List<Predicate<Player>> IS_WEARING_LENS = new ArrayList<Predicate<Player>>();

	public static void spawnInventoryInWorld(Level world, double x, double y, double z, IItemHandler inventory) {
		if (inventory != null && !world.isClientSide) {
			for (int i = 0; i < inventory.getSlots(); i++) {
				if (!inventory.getStackInSlot(i).isEmpty()) {
					world.addFreshEntity(new ItemEntity(world, x, y, z, inventory.getStackInSlot(i)));
				}
			}
		}
	}

	public static boolean isHoldingHammer(Player player, InteractionHand hand) {
		for (BiPredicate<Player, InteractionHand> predicate : IS_HOLDING_HAMMER) {
			if (predicate.test(player, hand)) {
				return true;
			}
		}
		return false;
	}

	public static BlockPos getHammerTarget(Player player) {
		for (Function<Player, BlockPos> func : GET_HAMMER_TARGET) {
			BlockPos pos = func.apply(player);
			if (pos != null) {
				return pos;
			}
		}
		return null;
	}

	public static boolean isWearingLens(Player player) {
		for (Predicate<Player> predicate : IS_WEARING_LENS) {
			if (predicate.test(player)) {
				return true;
			}
		}
		return false;
	}

	public static Direction readNullableFacing(int index) {
		return index > 0 ? Direction.from3DDataValue(index) : null;
	}

	public static int writeNullableFacing(Direction facing) {
		return facing != null ? facing.get3DDataValue() : -1;
	}

	public static FluidStack deserializeFluidStack(JsonObject json) {
		String fluidName = GsonHelper.getAsString(json, "fluid");
		Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));
		if (fluid == null || fluid == Fluids.EMPTY) {
			throw new JsonSyntaxException("Unknown fluid " + fluidName);
		}
		int amount = GsonHelper.getAsInt(json, "amount");
		return new FluidStack(fluid, amount);
	}

	public static JsonObject serializeFluidStack(FluidStack stack) {
		JsonObject json = new JsonObject();
		json.addProperty("fluid", Objects.requireNonNull(ForgeRegistries.FLUIDS.getResourceKey(stack.getFluid()).get().location()).toString());
		json.addProperty("amount", stack.getAmount());
		return json;
	}

	public static boolean isGaseousFluid(FluidStack resource) {
		return resource != null && resource.getFluid().getFluidType().getDensity() <= 0;
	}

	public static double getDiminishedPower(double power, double softcap, double slope) {
		if (power > softcap)
			return softcap * slope + Math.log10(power - softcap + LOG_E / slope) - Math.log10(LOG_E / slope);
		else
			return power * slope;
	}

	public static void drawComponents(Font fontRenderer, PoseStack stack, int x, int y, Component... components) {
		for (Component component : components) {
			fontRenderer.drawShadow(stack, component, x, y, 0xFFFFFF);
			fontRenderer.draw(stack, component, x, y, 0xFFFFFF);
			y += fontRenderer.lineHeight + 2;
		}
	}

	public static int intColor(int r, int g, int b) {
		return (r * 65536 + g * 256 + b);
	}

	public static int intColor(int a, int r, int g, int b) {
		return (a << 24) + (r << 16) + (g << 8) + (b);
	}

	public static Vector3f colorFromInt(int color) {
		return new Vector3f(((0xFF0000 & color) >> 16) / 255.0f, ((0x00FF00 & color) >> 8) / 255.0f, (0x0000FF & color) / 255.0f);
	}

	public static <C extends Container, T extends Recipe<C>> T getRecipe(T cache, RecipeType<T> type, C container, Level level) {
		if (cache != null && cache.matches(container, level))
			return cache;
		List<T> recipes = level.getRecipeManager().getRecipesFor(type, container, level);
		if (recipes.isEmpty())
			return null;
		return recipes.get(0);
	}

	public static HashMap<ResourceLocation, ItemStack> tagItems = new HashMap<ResourceLocation, ItemStack>();

	public static ItemStack getTaggedItem(TagKey<Item> tag) {
		if (tagItems.containsKey(tag.location()))
			return tagItems.get(tag.location());

		ItemStack output = ItemStack.EMPTY;
		int index = Integer.MAX_VALUE;
		List<? extends String> preferences = ConfigManager.TAG_PREFERENCES.get();
		for (Holder<Item> holder : Registry.ITEM.getTagOrEmpty(tag)) {
			for (int i = 0; i < preferences.size(); i ++) {
				if (i < index && preferences.get(i).equals(Registry.ITEM.getKey(holder.get()).getNamespace())) {
					output = new ItemStack(holder);
					index = i;
				}
			}
			if (output.isEmpty())
				output = new ItemStack(holder);
		}
		tagItems.put(tag.location(), output);
		return output;
	}

	public static List<EntityHitResult> getEntityHitResults(Level level, Entity projectile, Entity shooter, Vec3 startVec, Vec3 endVec, AABB boundingBox, Predicate<Entity> pFilter, float pInflationAmount) {
		List<EntityHitResult> entities = new ArrayList<>();
		double motionX = endVec.x - startVec.x;
		double motionY = endVec.y - startVec.y;
		double motionZ = endVec.z - startVec.z;

		for (Entity entity : level.getEntities(projectile, boundingBox.expandTowards(motionX, motionY, motionZ).inflate(1.0D), pFilter)) {
			if (entity != shooter) {
				AABB aabb = entity.getBoundingBox().inflate((double)pInflationAmount);
				Optional<Vec3> optional = aabb.clip(startVec, endVec);
				if (optional.isPresent()) {
					entities.add(new EntityHitResult(entity, optional.get()));
				}
			}
		}
		entities.sort((o1, o2) -> Double.compare(startVec.distanceToSqr(o1.getLocation()), startVec.distanceToSqr(o2.getLocation())));

		return entities;
	}
}
