package com.rekindled.embers.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.rekindled.embers.ConfigManager;
import com.rekindled.embers.Embers;
import com.rekindled.embers.api.event.InfoGogglesEvent;
import com.rekindled.embers.api.misc.HammerTarget;
import com.rekindled.embers.api.tile.IProxyable;
import com.rekindled.embers.datagen.EmbersBlockTags;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
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
	public static final List<Function<Player, HammerTarget>> GET_HAMMER_TARGET = new ArrayList<Function<Player, HammerTarget>>();
	public static final List<Predicate<Player>> IS_WEARING_LENS = new ArrayList<Predicate<Player>>();
	public static final List<Function<ItemStack, Double>> GET_EMBER_RESONANCE = new ArrayList<Function<ItemStack, Double>>();

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

	public static HammerTarget getHammerTarget(Player player) {
		for (Function<Player, HammerTarget> func : GET_HAMMER_TARGET) {
			HammerTarget target = func.apply(player);
			if (target != null) {
				return target;
			}
		}
		return null;
	}

	public static boolean isWearingLens(Player player) {
		boolean wearingLens = false;
		for (Predicate<Player> predicate : IS_WEARING_LENS) {
			if (predicate.test(player)) {
				wearingLens = true;
			}
		}
		InfoGogglesEvent event = new InfoGogglesEvent(player, wearingLens);
		MinecraftForge.EVENT_BUS.post(event);
		return event.shouldDisplay();
	}

	public static double getEmberResonance(ItemStack stack) {
		for (Function<ItemStack, Double> func : GET_EMBER_RESONANCE) {
			double resonance = func.apply(stack);
			if (resonance >= 1.0) {
				return resonance;
			}
		}
		return 1.0;
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

	public static void drawComponents(Font fontRenderer, GuiGraphics guiGraphics, int x, int y, Component... components) {
		for (Component component : components) {
			guiGraphics.drawString(fontRenderer, component, x, y, 0xFFFFFF);
			y += fontRenderer.lineHeight + 2;
		}
	}

	public static Vector3f multColor(Vector3f color, float mult) {
		return new Vector3f(Math.min(color.x + mult * color.x, 1.0f), Math.min(color.y + mult * color.y, 1.0f), Math.min(color.z + mult * color.z, 1.0f));
	}

	public static int intColor(Vector3f color) {
		return intColor(color.x, color.y, color.z);
	}

	public static int intColor(float a, Vector3f color) {
		return intColor(a, color.x, color.y, color.z);
	}

	public static int intColor(float r, float g, float b) {
		return intColor((int) (r * 255), (int) (g * 255), (int) (b * 255));
	}

	public static int intColor(float a, float r, float g, float b) {
		return intColor((int) (a * 255), (int) (r * 255), (int) (g * 255), (int) (b * 255));
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

	public static Vector3f colorFromABGRInt(int color) {
		return new Vector3f((0x000000FF & color) / 255.0f, ((0x0000FF00 & color) >> 8) / 255.0f, ((0x00FF0000 & color) >> 16) / 255.0f);
	}

	public static Vector3f lerpColor(Vector3f color1, Vector3f color2, float scale) {
		return new Vector3f(
				lerp(color1.x,color2.x,scale),
				lerp(color1.y,color2.y,scale),
				lerp(color1.z,color2.z,scale)
				);
	}

	public static float lerp(float a, float b, float scale) {
		return a*(1-scale)+b*(scale);
	}

	@SuppressWarnings("unchecked")
	public static <C extends Container, T extends Recipe<C>> T getRecipe(T cache, RecipeType<?> type, C container, Level level) {
		if (cache != null && cache.matches(container, level) )
			return cache;
		List<T> recipes = level.getRecipeManager().getRecipesFor((RecipeType<T>) type, container, level);
		if (recipes.isEmpty())
			return null;
		return recipes.get(0);
	}

	public static ConcurrentHashMap<ResourceLocation, Item> tagItems = new ConcurrentHashMap<ResourceLocation, Item>();
	public static ConcurrentHashMap<ResourceLocation, Fluid> tagFluids = new ConcurrentHashMap<ResourceLocation, Fluid>();

	public static Item getTaggedItem(TagKey<Item> tag) {
		if (tagItems.containsKey(tag.location()))
			return tagItems.get(tag.location());

		Item output = Items.AIR;
		int index = Integer.MAX_VALUE;
		List<? extends String> preferences = ConfigManager.TAG_PREFERENCES.get();
		for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
			for (int i = 0; i < preferences.size(); i ++) {
				if (i < index && preferences.get(i).equals(BuiltInRegistries.ITEM.getKey(holder.get()).getNamespace())) {
					output = holder.get();
					index = i;
				}
			}
			if (output == Items.AIR)
				output = holder.get();
		}
		if (output != Items.AIR)
			tagItems.put(tag.location(), output);
		return output;
	}

	public static Fluid getTaggedFluid(TagKey<Fluid> tag) {
		if (tagFluids.containsKey(tag.location()))
			return tagFluids.get(tag.location());

		Fluid output = Fluids.EMPTY;
		int index = Integer.MAX_VALUE;
		List<? extends String> preferences = ConfigManager.TAG_PREFERENCES.get();
		for (Holder<Fluid> holder : BuiltInRegistries.FLUID.getTagOrEmpty(tag)) {
			for (int i = 0; i < preferences.size(); i ++) {
				if (i < index && preferences.get(i).equals(BuiltInRegistries.FLUID.getKey(holder.get()).getNamespace())) {
					output = holder.get();
					index = i;
				}
			}
			if (output == Fluids.EMPTY)
				output = holder.get();
		}
		if (output != Fluids.EMPTY)
			tagFluids.put(tag.location(), output);
		return output;
	}

	public static ItemStack getPreferredItem(ItemStack[] items) {
		ItemStack output = ItemStack.EMPTY;
		int index = Integer.MAX_VALUE;
		List<? extends String> itemPreferences = ConfigManager.ITEM_PREFERENCES.get();
		List<? extends String> preferences = ConfigManager.TAG_PREFERENCES.get();
		for (ItemStack item : items) {
			ResourceLocation key = BuiltInRegistries.ITEM.getKey(item.getItem());
			for (int i = 0; i < itemPreferences.size(); i ++) {
				if (key.toString().equals(itemPreferences.get(i)))
					return item;
			}
			for (int i = 0; i < preferences.size(); i ++) {
				if (i < index && preferences.get(i).equals(key.getNamespace())) {
					output = item;
					index = i;
				}
			}
			if (output.isEmpty())
				output = item;
		}
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

	public static EquipmentSlot handToSlot(InteractionHand hand) {
		switch(hand) {
		case MAIN_HAND:
			return EquipmentSlot.MAINHAND;
		case OFF_HAND:
			return EquipmentSlot.OFFHAND;
		default:
			return null;
		}
	}

	public static IItemHandler makeRestrictedItemHandler(IItemHandler handler, boolean input, boolean output) {
		return new IItemHandler() {
			@Override
			public int getSlots() {
				return handler.getSlots();
			}

			@Override
			public ItemStack getStackInSlot(int slot) {
				return handler.getStackInSlot(slot);
			}

			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
				if(!input)
					return stack;
				return handler.insertItem(slot,stack,simulate);
			}

			@Override
			public ItemStack extractItem(int slot, int amount, boolean simulate) {
				if(!output)
					return ItemStack.EMPTY;
				return handler.extractItem(slot,amount,simulate);
			}

			@Override
			public int getSlotLimit(int slot) {
				return handler.getSlotLimit(slot);
			}

			@Override
			public boolean isItemValid(int slot, @NotNull ItemStack stack) {
				return input && handler.isItemValid(slot, stack);
			}
		};
	}

	public static IFluidHandler makeRestrictedFluidHandler(IFluidHandler handler, boolean input, boolean output) {
		return new IFluidHandler() {

			@Override
			public int fill(FluidStack resource, FluidAction action) {
				if(!input)
					return 0;
				return handler.fill(resource, action);
			}

			@Override
			public FluidStack drain(FluidStack resource, FluidAction action) {
				if(!output)
					return null;
				return handler.drain(resource, action);
			}

			@Override
			public FluidStack drain(int maxDrain, FluidAction action) {
				if(!output)
					return null;
				return handler.drain(maxDrain, action);
			}

			@Override
			public int getTanks() {
				return handler.getTanks();
			}

			@Override
			public @NotNull FluidStack getFluidInTank(int tank) {
				return handler.getFluidInTank(tank);
			}

			@Override
			public int getTankCapacity(int tank) {
				return handler.getTankCapacity(tank);
			}

			@Override
			public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
				return input && handler.isFluidValid(tank, stack);
			}
		};
	}

	public static Direction.Axis getOtherAxis(Direction.Axis axis1, Direction.Axis axis2) {
		switch (axis1) {
		default:
			return axis2;
		case X:
			switch (axis2) {
			case Y: return Direction.Axis.Z;
			case Z: return Direction.Axis.Y;
			default: return axis2;
			}
		case Y:
			switch (axis2) {
			case X: return Direction.Axis.Z;
			case Z: return Direction.Axis.X;
			default: return axis2;
			}
		case Z:
			switch (axis2) {
			case X: return Direction.Axis.Y;
			case Y: return Direction.Axis.X;
			default: return axis2;
			}
		}
	}

	//Setup light animation tables. 'a' is total darkness, 'z' is maxbright.
	public static String lightstyle[] = {
			// 0 normal
			"m",
			// 1 FLICKER (first variety)
			"mmnmmommommnonmmonqnmmo",
			// 2 SLOW STRONG PULSE
			"abcdefghijklmnopqrstuvwxyzyxwvutsrqponmlkjihgfedcba",
			// 3 CANDLE (first variety)
			"mmmmmaaaaammmmmaaaaaabcdefgabcdefg",
			// 4 FAST STROBE
			"mamamamamama",
			// 5 GENTLE PULSE 1
			"jklmnopqrstuvwxyzyxwvutsrqponmlkj",
			// 6 FLICKER (second variety)
			"nmonqnmomnmomomno",
			// 7 CANDLE (second variety)
			"mmmaaaabcdefgmmmmaaaammmaamm",
			// 8 CANDLE (third variety)
			"mmmaaammmaaammmabcdefaaaammmmabcdefmmmaaaa",
			// 9 SLOW STROBE (fourth variety)
			"aaaaaaaazzzzzzzz",
			// 10 FLUORESCENT FLICKER
			"mmamammmmammamamaaamammma",
			// 11 SLOW PULSE NOT FADE TO BLACK
			"abcdefghijklmnopqrrqponmlkjihgfedcba",
			// 12 testing
			"a"
	};

	public static float getLightBrightness(int style, int ticks) {
		return (lightstyle[style].charAt((ticks / 2) % lightstyle[style].length()) - 'a') / 25.0f;
	}

	public static Ingredient getRepairIngredient(Item item) {
		if (item instanceof TieredItem tool) {
			try {
				Ingredient ingredient = tool.getTier().getRepairIngredient();
				if (ingredient != null)
					return ingredient;
				Embers.LOGGER.error("Item: \"" + ForgeRegistries.ITEMS.getKey(item) + "\" has no proper repair ingredient, this is a bug and should be reported to " + ForgeRegistries.ITEMS.getKey(item).getNamespace());
			} catch (Exception e) {
				Embers.LOGGER.error("Item: \"" + ForgeRegistries.ITEMS.getKey(item) + "\" has no proper tool tier, this is a bug and should be reported to " + ForgeRegistries.ITEMS.getKey(item).getNamespace(), e);
			}
		}
		if (item instanceof ArmorItem armor) {
			try {
				Ingredient ingredient = armor.getMaterial().getRepairIngredient();
				if (ingredient != null)
					return ingredient;
				Embers.LOGGER.error("Item: \"" + ForgeRegistries.ITEMS.getKey(item) + "\" of material: \"" + armor.getMaterial().getName() + "\" has no proper repair ingredient, this is a bug and should be reported to " + ForgeRegistries.ITEMS.getKey(item).getNamespace());
			} catch (Exception e) {
				Embers.LOGGER.error("Item: \"" + ForgeRegistries.ITEMS.getKey(item) + "\" has no proper armor material, this is a bug and should be reported to " + ForgeRegistries.ITEMS.getKey(item).getNamespace(), e);
			}
		}
		return Ingredient.EMPTY;
	}

	public static InteractionResult useItemOnInventory(IItemHandlerModifiable inventory, Level level, Player player, InteractionHand hand) {
		ItemStack heldItem = player.getItemInHand(hand);
		if (!heldItem.isEmpty()) {
			ItemStack leftover = inventory.insertItem(0, heldItem, false);
			if (!ItemStack.matches(heldItem, leftover)) {
				player.setItemInHand(hand, leftover);
				return InteractionResult.SUCCESS;
			}
		}
		if (!inventory.getStackInSlot(0).isEmpty()) {
			if (!level.isClientSide) {
				giveItemToPlayer(inventory.getStackInSlot(0), player, player.getInventory().selected);
				inventory.setStackInSlot(0, ItemStack.EMPTY);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	public static void giveItemToPlayer(ItemStack stack, Player player) {
		giveItemToPlayer(stack, player, -1);
	}

	public static void giveItemToPlayer(ItemStack stack, Player player, int slot) {
		boolean success = false;

		if (slot != -1 && player.getInventory().items.get(slot).isEmpty()) {
			success = player.getInventory().add(slot, stack) && stack.isEmpty();
		}
		if (!success)
			success = player.getInventory().add(stack) && stack.isEmpty();

		if (success) {
			player.level().playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
			player.containerMenu.broadcastChanges();
		} else {
			ItemEntity itementity = player.drop(stack, false);
			if (itementity != null) {
				itementity.setNoPickUpDelay();
				itementity.setTarget(player.getUUID());
			}
		}
	}

	@SuppressWarnings("resource")
	public static void sendToTrackingPlayers(Level level, BlockPos pos, Packet<?> packet) {
		if (level instanceof ServerLevel && packet != null) {
			for (ServerPlayer serverplayer : ((ServerChunkCache) level.getChunkSource()).chunkMap.getPlayers(new ChunkPos(pos), false)) {
				serverplayer.connection.send(packet);
			}
		}
	}

	public static boolean isSideProxyable(BlockState state, BlockEntity tile, Direction face) {
		return state.is(EmbersBlockTags.MECH_CORE_PROXYABLE) || (state.is(EmbersBlockTags.MECH_CORE_PROXYABLE_BOTTOM) && face == Direction.DOWN) || (state.is(EmbersBlockTags.MECH_CORE_PROXYABLE_TOP) && face == Direction.UP) || (tile instanceof IProxyable proxyable && proxyable.isSideProxyable(face));
	}

	public static void calculateTrajectoryChunks(HashSet<ChunkPos> trajectoryChunks, BlockPos origin, BlockPos target, Vec3 motion) {
		trajectoryChunks.clear();
		if (target == null)
			return;

		Vec3 hitPos = Vec3.atCenterOf(target);
		Vec3 oldPos = Vec3.atCenterOf(origin);
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
			trajectoryChunks.add(new ChunkPos(BlockPos.containing(newPos)));
		}
		trajectoryChunks.add(new ChunkPos(target));
	}

	public static VoxelShape rotateVoxelShape(Direction to, VoxelShape shape) {
		return rotateVoxelShape(Direction.UP, to, shape);
	}

	private static VoxelShape rotated = Shapes.empty();
	public static VoxelShape rotateVoxelShape(Direction from, Direction to, VoxelShape shape) {
		if (from == to)
			return shape;
		rotated = Shapes.empty();

		Vec3i vecF = from.getNormal();
		Vec3i vecT = to.getNormal();

		shape = shape.move(-0.5, -0.5, -0.5);

		int[][] map = new int[3][3];
		int[] skip = new int[] { -1, -1, -1 };
		boolean opposites = from.getOpposite() == to;

		for (int i = 0; i < 3; ++i) {
			int f = vecF.get(Axis.VALUES[i]);
			for (int j = 0; j < 3; ++j) {
				int k = j;
				if (!opposites)
					k = 2 - k;
				if (k == skip[0] || k == skip[1] || k == skip[2])
					continue;
				int t = vecT.get(Axis.VALUES[k]);
				if (t == f) {
					map[k][i] = 1;
					skip[i] = k;
					break;
				} else if (t == -f) {
					map[k][i] = -1;
					skip[i] = k;
					break;
				}
			}
		}

		shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> rotated = Shapes.joinUnoptimized(rotated,
				Shapes.create(
						Math.min(minX * map[0][0], maxX * map[0][0]) + Math.min(minY * map[0][1], maxY * map[0][1]) + Math.min(minZ * map[0][2], maxZ * map[0][2]),
						Math.min(minX * map[1][0], maxX * map[1][0]) + Math.min(minY * map[1][1], maxY * map[1][1]) + Math.min(minZ * map[1][2], maxZ * map[1][2]),
						Math.min(minX * map[2][0], maxX * map[2][0]) + Math.min(minY * map[2][1], maxY * map[2][1]) + Math.min(minZ * map[2][2], maxZ * map[2][2]),

						Math.max(minX * map[0][0], maxX * map[0][0]) + Math.max(minY * map[0][1], maxY * map[0][1]) + Math.max(minZ * map[0][2], maxZ * map[0][2]),
						Math.max(minX * map[1][0], maxX * map[1][0]) + Math.max(minY * map[1][1], maxY * map[1][1]) + Math.max(minZ * map[1][2], maxZ * map[1][2]),
						Math.max(minX * map[2][0], maxX * map[2][0]) + Math.max(minY * map[2][1], maxY * map[2][1]) + Math.max(minZ * map[2][2], maxZ * map[2][2])),
				BooleanOp.OR));

		return rotated.move(0.5, 0.5, 0.5).optimize();
	}
}
