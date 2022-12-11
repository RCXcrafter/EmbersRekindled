package com.rekindled.embers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.rekindled.embers.block.CopperCellBlock;
import com.rekindled.embers.block.CreativeEmberBlock;
import com.rekindled.embers.block.EmberDialBlock;
import com.rekindled.embers.block.EmberEmitterBlock;
import com.rekindled.embers.block.EmberReceiverBlock;
import com.rekindled.embers.block.ItemDialBlock;
import com.rekindled.embers.block.ItemExtractorBlock;
import com.rekindled.embers.block.ItemPipeBlock;
import com.rekindled.embers.block.WaterloggableLeverBlock;
import com.rekindled.embers.blockentity.CopperCellBlockEntity;
import com.rekindled.embers.blockentity.CreativeEmberBlockEntity;
import com.rekindled.embers.blockentity.EmberEmitterBlockEntity;
import com.rekindled.embers.blockentity.EmberReceiverBlockEntity;
import com.rekindled.embers.blockentity.ItemExtractorBlockEntity;
import com.rekindled.embers.blockentity.ItemPipeBlockEntity;
import com.rekindled.embers.entity.EmberPacketEntity;
import com.rekindled.embers.fluidtypes.EmbersFluidType.FluidInfo;
import com.rekindled.embers.item.CopperCellBlockItem;
import com.rekindled.embers.item.TinkerHammerItem;
import com.rekindled.embers.particle.GlowParticleOptions;
import com.rekindled.embers.particle.SmokeParticleOptions;
import com.rekindled.embers.particle.SparkParticleOptions;
import com.rekindled.embers.particle.StarParticleOptions;
import com.rekindled.embers.particle.VaporParticleOptions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RegistryManager {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Embers.MODID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Embers.MODID);
	public static final DeferredRegister<FluidType> FLUIDTYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Embers.MODID);
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Embers.MODID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Embers.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Embers.MODID);
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Embers.MODID);
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Embers.MODID);

	public static List<FluidStuff> fluidList = new ArrayList<FluidStuff>();

	public static FluidStuff addFluid(String localizedName, FluidInfo info, Material material, BiFunction<FluidType.Properties, FluidInfo, FluidType> type, BiFunction<Supplier<? extends FlowingFluid>, BlockBehaviour.Properties, LiquidBlock> block, Function<ForgeFlowingFluid.Properties, ForgeFlowingFluid.Source> source, Function<ForgeFlowingFluid.Properties, ForgeFlowingFluid.Flowing> flowing, @Nullable Consumer<ForgeFlowingFluid.Properties> fluidProperties, FluidType.Properties prop) {
		FluidStuff fluid = new FluidStuff(info.name, localizedName, info.color, type.apply(prop, info), block, fluidProperties, source, flowing, material);
		fluidList.add(fluid);
		return fluid;
	}

	public static FluidStuff addFluid(String localizedName, FluidInfo info, Material material, BiFunction<FluidType.Properties, FluidInfo, FluidType> type, BiFunction<Supplier<? extends FlowingFluid>, BlockBehaviour.Properties, LiquidBlock> block, @Nullable Consumer<ForgeFlowingFluid.Properties> fluidProperties, FluidType.Properties prop) {
		return addFluid(localizedName, info, material, type, block, ForgeFlowingFluid.Source::new, ForgeFlowingFluid.Flowing::new, fluidProperties, prop);
	}

	public static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String name, EntityType.Builder<T> builder) {
		return ENTITY_TYPES.register(name, () -> builder.build(Embers.MODID + ":" + name));
	}

	public static <T extends ParticleOptions> RegistryObject<ParticleType<T>> registerParticle(String name, boolean overrideLimiter, ParticleOptions.Deserializer<T> deserializer, Codec<T> codec) {
		return PARTICLE_TYPES.register(name, () -> new ParticleType<T>(overrideLimiter, deserializer) {
			public Codec<T> codec() {
				return codec;
			}
		});
	}

	public static RegistryObject<SoundEvent> registerSound(String name) {
		return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(Embers.MODID, name)));
	}

	//blocks
	public static final RegistryObject<Block> COPPER_CELL = BLOCKS.register("copper_cell", () -> new CopperCellBlock(Properties.of(Material.METAL, MaterialColor.TERRACOTTA_ORANGE).sound(SoundType.METAL).strength(1.4f).noOcclusion()));
	public static final RegistryObject<Block> CREATIVE_EMBER = BLOCKS.register("creative_ember_source", () -> new CreativeEmberBlock(Properties.of(Material.METAL, MaterialColor.TERRACOTTA_YELLOW).sound(SoundType.METAL).strength(1.6f)));
	public static final RegistryObject<Block> EMBER_DIAL = BLOCKS.register("ember_dial", () -> new EmberDialBlock(Properties.of(Material.METAL, MaterialColor.NONE).sound(SoundType.METAL).strength(1.6f).noOcclusion()));
	public static final RegistryObject<Block> ITEM_DIAL = BLOCKS.register("item_dial", () -> new ItemDialBlock(Properties.of(Material.METAL, MaterialColor.NONE).sound(SoundType.METAL).strength(1.6f).noOcclusion()));
	public static final RegistryObject<Block> EMBER_EMITTER = BLOCKS.register("ember_emitter", () -> new EmberEmitterBlock(Properties.of(Material.METAL, MaterialColor.NONE).sound(SoundType.METAL).strength(0.6f).noOcclusion()));
	public static final RegistryObject<Block> EMBER_RECEIVER = BLOCKS.register("ember_receiver", () -> new EmberReceiverBlock(Properties.of(Material.METAL, MaterialColor.NONE).sound(SoundType.METAL).strength(0.6f).noOcclusion()));
	public static final RegistryObject<Block> CAMINITE_LEVER = BLOCKS.register("caminite_lever", () -> new WaterloggableLeverBlock(Properties.of(Material.METAL, MaterialColor.NONE).noCollission().sound(SoundType.STONE).strength(0.75f)));
	public static final RegistryObject<Block> ITEM_PIPE = BLOCKS.register("item_pipe", () -> new ItemPipeBlock(Properties.of(Material.METAL, MaterialColor.NONE).sound(SoundType.STONE).strength(0.75f)));
	public static final RegistryObject<Block> ITEM_EXTRACTOR = BLOCKS.register("item_extractor", () -> new ItemExtractorBlock(Properties.of(Material.METAL, MaterialColor.NONE).sound(SoundType.STONE).strength(0.75f)));

	//itemblocks
	public static final RegistryObject<Item> COPPER_CELL_ITEM = ITEMS.register("copper_cell", () -> new CopperCellBlockItem(COPPER_CELL.get(), new Item.Properties().stacksTo(1).tab(Embers.TAB_EMBERS)));
	public static final RegistryObject<Item> CREATIVE_EMBER_ITEM = ITEMS.register("creative_ember_source", () -> new BlockItem(CREATIVE_EMBER.get(), new Item.Properties().tab(Embers.TAB_EMBERS)));
	public static final RegistryObject<Item> EMBER_DIAL_ITEM = ITEMS.register("ember_dial", () -> new BlockItem(EMBER_DIAL.get(), new Item.Properties().tab(Embers.TAB_EMBERS)));
	public static final RegistryObject<Item> ITEM_DIAL_ITEM = ITEMS.register("item_dial", () -> new BlockItem(ITEM_DIAL.get(), new Item.Properties().tab(Embers.TAB_EMBERS)));
	public static final RegistryObject<Item> EMBER_EMITTER_ITEM = ITEMS.register("ember_emitter", () -> new BlockItem(EMBER_EMITTER.get(), new Item.Properties().tab(Embers.TAB_EMBERS)));
	public static final RegistryObject<Item> EMBER_RECEIVER_ITEM = ITEMS.register("ember_receiver", () -> new BlockItem(EMBER_RECEIVER.get(), new Item.Properties().tab(Embers.TAB_EMBERS)));
	public static final RegistryObject<Item> CAMINITE_LEVER_ITEM = ITEMS.register("caminite_lever", () -> new BlockItem(CAMINITE_LEVER.get(), new Item.Properties().tab(Embers.TAB_EMBERS)));
	public static final RegistryObject<Item> ITEM_PIPE_ITEM = ITEMS.register("item_pipe", () -> new BlockItem(ITEM_PIPE.get(), new Item.Properties().tab(Embers.TAB_EMBERS)));
	public static final RegistryObject<Item> ITEM_EXTRACTOR_ITEM = ITEMS.register("item_extractor", () -> new BlockItem(ITEM_EXTRACTOR.get(), new Item.Properties().tab(Embers.TAB_EMBERS)));

	//items
	public static final RegistryObject<Item> TINKER_HAMMER = ITEMS.register("tinker_hammer", () -> new TinkerHammerItem(new Item.Properties().stacksTo(1).tab(Embers.TAB_EMBERS)));

	//block entities
	public static final RegistryObject<BlockEntityType<CopperCellBlockEntity>> COPPER_CELL_ENTITY = BLOCK_ENTITY_TYPES.register("copper_cell", () -> BlockEntityType.Builder.of(CopperCellBlockEntity::new, COPPER_CELL.get()).build(null));
	public static final RegistryObject<BlockEntityType<CreativeEmberBlockEntity>> CREATIVE_EMBER_ENTITY = BLOCK_ENTITY_TYPES.register("creative_ember_source", () -> BlockEntityType.Builder.of(CreativeEmberBlockEntity::new, CREATIVE_EMBER.get()).build(null));
	public static final RegistryObject<BlockEntityType<EmberEmitterBlockEntity>> EMBER_EMITTER_ENTITY = BLOCK_ENTITY_TYPES.register("ember_emitter", () -> BlockEntityType.Builder.of(EmberEmitterBlockEntity::new, EMBER_EMITTER.get()).build(null));
	public static final RegistryObject<BlockEntityType<EmberReceiverBlockEntity>> EMBER_RECEIVER_ENTITY = BLOCK_ENTITY_TYPES.register("ember_receiver", () -> BlockEntityType.Builder.of(EmberReceiverBlockEntity::new, EMBER_RECEIVER.get()).build(null));
	public static final RegistryObject<BlockEntityType<ItemPipeBlockEntity>> ITEM_PIPE_ENTITY = BLOCK_ENTITY_TYPES.register("item_pipe", () -> BlockEntityType.Builder.of(ItemPipeBlockEntity::new, ITEM_PIPE.get()).build(null));
	public static final RegistryObject<BlockEntityType<ItemExtractorBlockEntity>> ITEM_EXTRACTOR_ENTITY = BLOCK_ENTITY_TYPES.register("item_extractor", () -> BlockEntityType.Builder.of(ItemExtractorBlockEntity::new, ITEM_EXTRACTOR.get()).build(null));

	//entities
	public static final RegistryObject<EntityType<EmberPacketEntity>> EMBER_PACKET = registerEntity("ember_packet", EntityType.Builder.<EmberPacketEntity>of(EmberPacketEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune().clientTrackingRange(3).updateInterval(1));

	//particle types
	public static final RegistryObject<ParticleType<GlowParticleOptions>> GLOW_PARTICLE = registerParticle("glow", false, GlowParticleOptions.DESERIALIZER, GlowParticleOptions.CODEC);
	public static final RegistryObject<ParticleType<StarParticleOptions>> STAR_PARTICLE = registerParticle("star", false, StarParticleOptions.DESERIALIZER, StarParticleOptions.CODEC);
	public static final RegistryObject<ParticleType<SparkParticleOptions>> SPARK_PARTICLE = registerParticle("spark", false, SparkParticleOptions.DESERIALIZER, SparkParticleOptions.CODEC);
	public static final RegistryObject<ParticleType<SmokeParticleOptions>> SMOKE_PARTICLE = registerParticle("smoke", false, SmokeParticleOptions.DESERIALIZER, SmokeParticleOptions.CODEC);
	public static final RegistryObject<ParticleType<VaporParticleOptions>> VAPOR_PARTICLE = registerParticle("vapor", false, VaporParticleOptions.DESERIALIZER, VaporParticleOptions.CODEC);

	//sounds
	public static final RegistryObject<SoundEvent> ALCHEMY_FAIL = registerSound("block.alchemy.fail");
	public static final RegistryObject<SoundEvent> ALCHEMY_SUCCESS = registerSound("block.alchemy.success");
	public static final RegistryObject<SoundEvent> ALCHEMY_LOOP = registerSound("block.alchemy.loop");
	public static final RegistryObject<SoundEvent> ALCHEMY_START = registerSound("block.alchemy.start");

	public static final RegistryObject<SoundEvent> PEDESTAL_LOOP = registerSound("block.pedestal.loop");

	public static final RegistryObject<SoundEvent> BEAM_CANNON_FIRE = registerSound("block.beam_cannon.fire");
	public static final RegistryObject<SoundEvent> BEAM_CANNON_HIT = registerSound("block.beam_cannon.hit");

	public static final RegistryObject<SoundEvent> CRYSTAL_CELL_LOOP = registerSound("block.crystal_cell.loop");
	public static final RegistryObject<SoundEvent> CRYSTAL_CELL_GROW = registerSound("block.crystal_cell.grow");

	public static final RegistryObject<SoundEvent> GENERATOR_LOOP = registerSound("block.generator.loop");
	public static final RegistryObject<SoundEvent> ACTIVATOR = registerSound("block.activator.plume");
	public static final RegistryObject<SoundEvent> PRESSURE_REFINERY = registerSound("block.boiler.plume");
	public static final RegistryObject<SoundEvent> IGNEM_REACTOR = registerSound("block.ignem_reactor.plume");

	public static final RegistryObject<SoundEvent> BORE_START = registerSound("block.bore.start");
	public static final RegistryObject<SoundEvent> BORE_STOP = registerSound("block.bore.stop");
	public static final RegistryObject<SoundEvent> BORE_LOOP = registerSound("block.bore.loop");
	public static final RegistryObject<SoundEvent> BORE_LOOP_MINE = registerSound("block.bore.loop_mine");

	public static final RegistryObject<SoundEvent> CATALYTIC_PLUG_START = registerSound("block.catalytic_plug.start");
	public static final RegistryObject<SoundEvent> CATALYTIC_PLUG_STOP = registerSound("block.catalytic_plug.stop");
	public static final RegistryObject<SoundEvent> CATALYTIC_PLUG_LOOP = registerSound("block.catalytic_plug.loop");
	public static final RegistryObject<SoundEvent> CATALYTIC_PLUG_LOOP_READY = registerSound("block.catalytic_plug.loop_ready");

	public static final RegistryObject<SoundEvent> STAMPER_DOWN = registerSound("block.stamper.down");
	public static final RegistryObject<SoundEvent> STAMPER_UP = registerSound("block.stamper.up");

	public static final RegistryObject<SoundEvent> HEATCOIL_HIGH = registerSound("block.heat_coil.high_loop");
	public static final RegistryObject<SoundEvent> HEATCOIL_MID = registerSound("block.heat_coil.mid_loop");
	public static final RegistryObject<SoundEvent> HEATCOIL_LOW = registerSound("block.heat_coil.low_loop");
	public static final RegistryObject<SoundEvent> HEATCOIL_COOK = registerSound("block.heat_coil.cooking_loop");

	public static final RegistryObject<SoundEvent> PLINTH_LOOP = registerSound("block.melter.loopblock.plinth.loop");
	public static final RegistryObject<SoundEvent> MELTER_LOOP = registerSound("block.melter.loop");
	public static final RegistryObject<SoundEvent> MIXER_LOOP = registerSound("block.mixer.loop");
	public static final RegistryObject<SoundEvent> COPPER_CHARGER_LOOP = registerSound("block.copper_charger.loop");
	public static final RegistryObject<SoundEvent> INJECTOR_LOOP = registerSound("block.injector.loop");

	public static final RegistryObject<SoundEvent> METAL_SEED_LOOP = registerSound("block.metal_seed.loop");
	public static final RegistryObject<SoundEvent> METAL_SEED_PING = registerSound("block.metal_seed.ping");

	public static final RegistryObject<SoundEvent> INFERNO_FORGE_FAIL = registerSound("block.inferno_forge.fail");
	public static final RegistryObject<SoundEvent> INFERNO_FORGE_SUCCESS = registerSound("block.inferno_forge.success");
	public static final RegistryObject<SoundEvent> INFERNO_FORGE_LOOP = registerSound("block.inferno_forge.loop");
	public static final RegistryObject<SoundEvent> INFERNO_FORGE_START = registerSound("block.inferno_forge.start");
	public static final RegistryObject<SoundEvent> INFERNO_FORGE_OPEN = registerSound("block.inferno_forge.open");
	public static final RegistryObject<SoundEvent> INFERNO_FORGE_CLOSE = registerSound("block.inferno_forge.close");

	public static final RegistryObject<SoundEvent> FIELD_CHART_LOOP = registerSound("block.field_chart.loop");

	public static final RegistryObject<SoundEvent> EMBER_EMIT = registerSound("block.ember_transfer.emit.small");
	public static final RegistryObject<SoundEvent> EMBER_EMIT_BIG = registerSound("block.ember_transfer.emit.big");
	public static final RegistryObject<SoundEvent> EMBER_RECEIVE = registerSound("block.ember_transfer.receive.small");
	public static final RegistryObject<SoundEvent> EMBER_RECEIVE_BIG = registerSound("block.ember_transfer.receive.big");
	public static final RegistryObject<SoundEvent> EMBER_RELAY = registerSound("block.ember_transfer.relay");

	public static final RegistryObject<SoundEvent> STEAM_ENGINE_START_STEAM = registerSound("block.steam_engine.start_steam");
	public static final RegistryObject<SoundEvent> STEAM_ENGINE_START_BURN = registerSound("block.steam_engine.start_burn");
	public static final RegistryObject<SoundEvent> STEAM_ENGINE_LOOP_STEAM = registerSound("block.steam_engine.steam_loop");
	public static final RegistryObject<SoundEvent> STEAM_ENGINE_LOOP_BURN = registerSound("block.steam_engine.burn_loop");
	public static final RegistryObject<SoundEvent> STEAM_ENGINE_STOP = registerSound("block.steam_engine.stop");

	public static final RegistryObject<SoundEvent> MINI_BOILER_RUPTURE = registerSound("block.mini_boiler.rupture");
	public static final RegistryObject<SoundEvent> MINI_BOILER_LOOP_SLOW = registerSound("block.mini_boiler.loop_slow");
	public static final RegistryObject<SoundEvent> MINI_BOILER_LOOP_MID = registerSound("block.mini_boiler.loop_mid");
	public static final RegistryObject<SoundEvent> MINI_BOILER_LOOP_FAST = registerSound("block.mini_boiler.loop_fast");
	public static final RegistryObject<SoundEvent> MINI_BOILER_PRESSURE_LOW = registerSound("block.mini_boiler.pressure_loop_low");
	public static final RegistryObject<SoundEvent> MINI_BOILER_PRESSURE_MID = registerSound("block.mini_boiler.pressure_loop_mid");
	public static final RegistryObject<SoundEvent> MINI_BOILER_PRESSURE_HIGH = registerSound("block.mini_boiler.pressure_loop_high");

	public static final RegistryObject<SoundEvent> PUMP_SLOW = registerSound("block.pump.slow");
	public static final RegistryObject<SoundEvent> PUMP_MID = registerSound("block.pump.mid");
	public static final RegistryObject<SoundEvent> PUMP_FAST = registerSound("block.pump.fast");

	public static final RegistryObject<SoundEvent> PIPE_CONNECT = registerSound("block.pipe.connect");
	public static final RegistryObject<SoundEvent> PIPE_DISCONNECT = registerSound("block.pipe.disconnect");

	public static final RegistryObject<SoundEvent> FIREBALL_BIG = registerSound("fireball.big.fire");
	public static final RegistryObject<SoundEvent> FIREBALL_BIG_HIT = registerSound("fireball.big.hit");
	public static final RegistryObject<SoundEvent> FIREBALL = registerSound("fireball.small.fire");
	public static final RegistryObject<SoundEvent> FIREBALL_HIT = registerSound("fireball.small.hit");

	public static final RegistryObject<SoundEvent> BLAZING_RAY_FIRE = registerSound("item.blazing_ray.fire");
	public static final RegistryObject<SoundEvent> BLAZING_RAY_EMPTY = registerSound("item.blazing_ray.empty");

	public static final RegistryObject<SoundEvent> CINDER_STAFF_CHARGE = registerSound("item.cinder_staff.charge");
	public static final RegistryObject<SoundEvent> CINDER_STAFF_FAIL = registerSound("item.cinder_staff.fail");
	public static final RegistryObject<SoundEvent> CINDER_STAFF_LOOP = registerSound("item.cinder_staff.loop");

	public static final RegistryObject<SoundEvent> EXPLOSION_CHARM_ABSORB = registerSound("item.explosion_charm.absorb");
	public static final RegistryObject<SoundEvent> EXPLOSION_CHARM_RECHARGE = registerSound("item.explosion_charm.recharge");

	public static final RegistryObject<SoundEvent> ASHEN_AMULET_BURN = registerSound("item.ash_amulet.burn");

	public static final RegistryObject<SoundEvent> HEATED_ITEM_LEVELUP = registerSound("item.heated.level_up");
	public static final RegistryObject<SoundEvent> RESONATING_BELL = registerSound("item.resonating_bell.ring");
	public static final RegistryObject<SoundEvent> CINDER_JET = registerSound("item.cinder_jet.boost");
	public static final RegistryObject<SoundEvent> INFLICTOR_GEM = registerSound("item.inflictor_gem.absorb");

	public static final RegistryObject<SoundEvent> METALLURGIC_DUST = registerSound("item.metallurgic_dust.convert");
	public static final RegistryObject<SoundEvent> METALLURGIC_DUST_FAIL = registerSound("item.metallurgic_dust.fail");

	public static final RegistryObject<SoundEvent> CODEX_OPEN = registerSound("item.codex.open");
	public static final RegistryObject<SoundEvent> CODEX_CLOSE = registerSound("item.codex.close");
	public static final RegistryObject<SoundEvent> CODEX_CATEGORY_OPEN = registerSound("item.codex.category.open");
	public static final RegistryObject<SoundEvent> CODEX_CATEGORY_CLOSE = registerSound("item.codex.category.close");
	public static final RegistryObject<SoundEvent> CODEX_CATEGORY_SELECT = registerSound("item.codex.category.select");
	public static final RegistryObject<SoundEvent> CODEX_CATEGORY_UNSELECT = registerSound("item.codex.category.unselect");
	public static final RegistryObject<SoundEvent> CODEX_CATEGORY_SWITCH = registerSound("item.codex.category.switch");
	public static final RegistryObject<SoundEvent> CODEX_PAGE_OPEN = registerSound("item.codex.page.open");
	public static final RegistryObject<SoundEvent> CODEX_PAGE_CLOSE = registerSound("item.codex.page.close");
	public static final RegistryObject<SoundEvent> CODEX_PAGE_SWITCH = registerSound("item.codex.page.switch");
	public static final RegistryObject<SoundEvent> CODEX_CHECK = registerSound("item.codex.check");
	public static final RegistryObject<SoundEvent> CODEX_UNCHECK = registerSound("item.codex.uncheck");
	public static final RegistryObject<SoundEvent> CODEX_LOCK = registerSound("item.codex.lock");
	public static final RegistryObject<SoundEvent> CODEX_UNLOCK = registerSound("item.codex.unlock");

	public static final RegistryObject<SoundEvent> BAUBLE_EQUIP = registerSound("item.bauble.equip");
	public static final RegistryObject<SoundEvent> BAUBLE_UNEQUIP = registerSound("item.bauble.unequip");

	public static final RegistryObject<SoundEvent> TYRFING_HIT = registerSound("item.tyrfing.hit");
	public static final RegistryObject<SoundEvent> SHIFTING_SCALES_BREAK = registerSound("item.scale.break");
	public static final RegistryObject<SoundEvent> WINDING_GEARS_SPRING = registerSound("item.windup.spring");

	public static final RegistryObject<SoundEvent> ANCIENT_GOLEM_STEP = registerSound("entity.ancient_golem.step");
	public static final RegistryObject<SoundEvent> ANCIENT_GOLEM_HURT = registerSound("entity.ancient_golem.hurt");
	public static final RegistryObject<SoundEvent> ANCIENT_GOLEM_PUNCH = registerSound("entity.ancient_golem.punch");
	public static final RegistryObject<SoundEvent> ANCIENT_GOLEM_DEATH = registerSound("entity.ancient_golem.death");


	public static void registerDispenserBehaviour(final FMLCommonSetupEvent event) {
		DispenseItemBehavior dispenseBucket = new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

			@Override
			public ItemStack execute(BlockSource source, ItemStack stack) {
				DispensibleContainerItem container = (DispensibleContainerItem)stack.getItem();
				BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
				Level level = source.getLevel();
				if (container.emptyContents(null, level, blockpos, null)) {
					container.checkExtraContent(null, level, stack, blockpos);
					return new ItemStack(Items.BUCKET);
				} else {
					return this.defaultDispenseItemBehavior.dispense(source, stack);
				}
			}
		};
		event.enqueueWork(() -> {
			for (FluidStuff fluid : fluidList) {
				DispenserBlock.registerBehavior(fluid.FLUID_BUCKET.get(), dispenseBucket);
			}
		});
	}

	public static class FluidStuff {

		public final ForgeFlowingFluid.Properties PROPERTIES;

		public final RegistryObject<ForgeFlowingFluid.Source> FLUID;
		public final RegistryObject<ForgeFlowingFluid.Flowing> FLUID_FLOW;
		public final RegistryObject<FluidType> TYPE;

		public final RegistryObject<LiquidBlock> FLUID_BLOCK;

		public final RegistryObject<BucketItem> FLUID_BUCKET;

		public final String name;
		public final String localizedName;
		public final int color;

		public FluidStuff(String name, String localizedName, int color, FluidType type, BiFunction<Supplier<? extends FlowingFluid>, BlockBehaviour.Properties, LiquidBlock> block, @Nullable Consumer<ForgeFlowingFluid.Properties> fluidProperties, Function<ForgeFlowingFluid.Properties, ForgeFlowingFluid.Source> source, Function<ForgeFlowingFluid.Properties, ForgeFlowingFluid.Flowing> flowing, Material material) {
			this.name = name;
			this.localizedName = localizedName;
			this.color = color;

			FLUID = FLUIDS.register(name, () -> source.apply(getFluidProperties()));
			FLUID_FLOW = FLUIDS.register("flowing_" + name, () -> flowing.apply(getFluidProperties()));
			TYPE = FLUIDTYPES.register(name, () -> type);

			PROPERTIES = new ForgeFlowingFluid.Properties(TYPE, FLUID, FLUID_FLOW);
			if (fluidProperties != null)
				fluidProperties.accept(PROPERTIES);

			FLUID_BLOCK = BLOCKS.register(name + "_block", () -> block.apply(FLUID, Block.Properties.of(material).lightLevel((state) -> { return type.getLightLevel(); }).randomTicks().strength(100.0F).noLootTable()));
			FLUID_BUCKET = ITEMS.register(name + "_bucket", () -> new BucketItem(FLUID, new BucketItem.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(CreativeModeTab.TAB_MISC)));

			PROPERTIES.bucket(FLUID_BUCKET).block(FLUID_BLOCK);
		}

		public ForgeFlowingFluid.Properties getFluidProperties() {
			return PROPERTIES;       
		}
	}
}
