package com.rekindled.embers.datagen;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;
import com.rekindled.embers.util.sound.MachineSound;

import net.minecraft.client.Minecraft;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import net.minecraftforge.registries.RegistryObject;

public class EmbersSounds extends SoundDefinitionsProvider {

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
	public static final RegistryObject<SoundEvent> BORE_LOOP_SUPERSPEED = registerSound("block.bore.loop.superspeed");
	public static final RegistryObject<SoundEvent> BORE_LOOP_MINE_SUPERSPEED = registerSound("block.bore.loop_mine.superspeed");

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

	//public static final RegistryObject<SoundEvent> SILENCE = registerSound("silence");

	//sound types
	//this isn't needed whoops
	//public static final SoundType METAL_EDGE = new ForgeSoundType(1.0F, 1.5F, SILENCE, () -> SoundEvents.METAL_STEP, SILENCE, () -> SoundEvents.METAL_HIT, () -> SoundEvents.METAL_FALL);


	public EmbersSounds(DataGenerator generator, ExistingFileHelper helper) {
		super(generator, Embers.MODID, helper);
	}

	public static RegistryObject<SoundEvent> registerSound(String name) {
		return RegistryManager.SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(Embers.MODID, name)));
	}

	public static void playMachineSound(BlockEntity tile, int id, SoundEvent soundIn, SoundSource categoryIn, boolean repeat, float volume, float pitch, float xIn, float yIn, float zIn) {
		Minecraft.getInstance().getSoundManager().play(new MachineSound(tile, id, soundIn, categoryIn, repeat, volume, pitch, xIn, yIn, zIn));
	}

	@Override
	public void registerSounds() {
		withSubtitle(ALCHEMY_FAIL, definition().with(
				sound(resource("alchemy_tablet/fail"))));
		withSubtitle(ALCHEMY_SUCCESS, definition().with(
				sound(resource("alchemy_tablet/success"))));
		add(ALCHEMY_LOOP, definition().with(
				sound(resource("alchemy_tablet/loop"))));
		withSubtitle(ALCHEMY_START, definition().with(
				sound(resource("alchemy_tablet/start"))));

		add(PEDESTAL_LOOP, definition().with(
				sound(resource("pedestal_loop"))));

		withSubtitle(BEAM_CANNON_FIRE, definition().with(
				sound(resource("beam_cannon/fire"))));
		withSubtitle(BEAM_CANNON_HIT, definition().with(
				sound(resource("beam_cannon/hit1")),
				sound(resource("beam_cannon/hit2"))));

		add(CRYSTAL_CELL_LOOP, definition().with(
				sound(resource("crystal_cell/loop"))));
		withSubtitle(CRYSTAL_CELL_GROW, definition().with(
				sound(resource("crystal_cell/zap1")),
				sound(resource("crystal_cell/zap2")),
				sound(resource("crystal_cell/zap3")),
				sound(resource("crystal_cell/zap4"))));

		add(GENERATOR_LOOP, definition().with(
				sound(resource("generator_hold_embers"))));
		withSubtitle(ACTIVATOR, definition().with(
				sound(resource("activator/plume1")),
				sound(resource("activator/plume2"))));
		withSubtitle(PRESSURE_REFINERY, definition().with(
				sound(resource("boiler/plume1")),
				sound(resource("boiler/plume2"))));
		withSubtitle(IGNEM_REACTOR, definition().with(
				sound(resource("ignem_reactor/plume1")),
				sound(resource("ignem_reactor/plume2"))));

		withSubtitle(BORE_START, definition().with(
				sound(resource("ember_bore/start"))));
		withSubtitle(BORE_STOP, definition().with(
				sound(resource("ember_bore/stop"))));
		add(BORE_LOOP, definition().with(
				sound(resource("ember_bore/run_loop"))));
		add(BORE_LOOP_MINE, definition().with(
				sound(resource("ember_bore/mine_loop"))));
		add(BORE_LOOP_SUPERSPEED, definition().with(
				sound(resource("ember_bore/superspeed_run_loop"))));
		add(BORE_LOOP_MINE_SUPERSPEED, definition().with(
				sound(resource("ember_bore/superspeed_mine_loop"))));

		withSubtitle(CATALYTIC_PLUG_START, definition().with(
				sound(resource("catalytic_plug/start"))));
		withSubtitle(CATALYTIC_PLUG_STOP, definition().with(
				sound(resource("catalytic_plug/stop"))));
		add(CATALYTIC_PLUG_LOOP, definition().with(
				sound(resource("catalytic_plug/run_loop"))));
		add(CATALYTIC_PLUG_LOOP_READY, definition().with(
				sound(resource("catalytic_plug/ready_loop"))));

		withSubtitle(STAMPER_DOWN, definition().with(
				sound(resource("stamper/down1")),
				sound(resource("stamper/down2")),
				sound(resource("stamper/down3"))));
		withSubtitle(STAMPER_UP, definition().with(
				sound(resource("stamper/up1")),
				sound(resource("stamper/up2")),
				sound(resource("stamper/up3"))));

		add(HEATCOIL_HIGH, definition().with(
				sound(resource("heat_coil/high_loop"))));
		add(HEATCOIL_MID, definition().with(
				sound(resource("heat_coil/mid_loop"))));
		add(HEATCOIL_LOW, definition().with(
				sound(resource("heat_coil/low_loop"))));
		add(HEATCOIL_COOK, definition().with(
				sound(resource("heat_coil/cooking_loop"))));

		add(PLINTH_LOOP, definition().with(
				sound(resource("cinder_plinth_loop"))));
		add(MELTER_LOOP, definition().with(
				sound(resource("melter_loop"))));
		add(MIXER_LOOP, definition().with(
				sound(resource("mixer_loop"))));
		add(COPPER_CHARGER_LOOP, definition().with(
				sound(resource("copper_charger_loop"))));
		add(INJECTOR_LOOP, definition().with(
				sound(resource("injector_loop"))));

		add(METAL_SEED_LOOP, definition().with(
				sound(resource("metal_seed/loop"))));
		withSubtitle(METAL_SEED_PING, definition().with(
				sound(resource("metal_seed/ping1")),
				sound(resource("metal_seed/ping2")),
				sound(resource("metal_seed/ping3"))));

		withSubtitle(INFERNO_FORGE_FAIL, definition().with(
				sound(resource("inferno_forge/fail"))));
		withSubtitle(INFERNO_FORGE_SUCCESS, definition().with(
				sound(resource("inferno_forge/success"))));
		add(INFERNO_FORGE_LOOP, definition().with(
				sound(resource("inferno_forge/loop"))));
		withSubtitle(INFERNO_FORGE_START, definition().with(
				sound(resource("inferno_forge/start"))));
		withSubtitle(INFERNO_FORGE_OPEN, definition().with(
				sound(resource("inferno_forge/open"))));
		withSubtitle(INFERNO_FORGE_CLOSE, definition().with(
				sound(resource("inferno_forge/close"))));

		add(FIELD_CHART_LOOP, definition().with(
				sound(resource("field_chart_loop"))));

		withSubtitle(EMBER_EMIT, definition().with(
				sound(resource("ember_transmission/emit1")),
				sound(resource("ember_transmission/emit2")),
				sound(resource("ember_transmission/emit3")),
				sound(resource("ember_transmission/emit4"))));
		withSubtitle(EMBER_EMIT_BIG, definition().with(
				sound(resource("ember_transmission/emit_big1")),
				sound(resource("ember_transmission/emit_big2")),
				sound(resource("ember_transmission/emit_big3")),
				sound(resource("ember_transmission/emit_big4"))));
		withSubtitle(EMBER_RECEIVE, definition().with(
				sound(resource("ember_transmission/recep1")),
				sound(resource("ember_transmission/recep2")),
				sound(resource("ember_transmission/recep3")),
				sound(resource("ember_transmission/recep4"))));
		withSubtitle(EMBER_RECEIVE_BIG, definition().with(
				sound(resource("ember_transmission/recep_big1")),
				sound(resource("ember_transmission/recep_big2")),
				sound(resource("ember_transmission/recep_big3")),
				sound(resource("ember_transmission/recep_big4"))));
		withSubtitle(EMBER_RELAY, definition().with(
				sound(resource("ember_transmission/relay1")),
				sound(resource("ember_transmission/relay2")),
				sound(resource("ember_transmission/relay3")),
				sound(resource("ember_transmission/relay4"))));

		withSubtitle(STEAM_ENGINE_START_STEAM, definition().with(
				sound(resource("steam_engine/start_steam"))));
		withSubtitle(STEAM_ENGINE_START_BURN, definition().with(
				sound(resource("steam_engine/start_burn"))));
		add(STEAM_ENGINE_LOOP_STEAM, definition().with(
				sound(resource("steam_engine/steam_loop"))));
		add(STEAM_ENGINE_LOOP_BURN, definition().with(
				sound(resource("steam_engine/burn_loop"))));
		withSubtitle(STEAM_ENGINE_STOP, definition().with(
				sound(resource("steam_engine/stop"))));

		withSubtitle(MINI_BOILER_RUPTURE, definition().with(
				sound(resource("mini_boiler/rupture"))));
		add(MINI_BOILER_LOOP_SLOW, definition().with(
				sound(resource("mini_boiler/slow_loop"))));
		add(MINI_BOILER_LOOP_MID, definition().with(
				sound(resource("mini_boiler/mid_loop"))));
		add(MINI_BOILER_LOOP_FAST, definition().with(
				sound(resource("mini_boiler/fast_loop"))));
		add(MINI_BOILER_PRESSURE_LOW, definition().with(
				sound(resource("mini_boiler/pressure_loop1"))));
		add(MINI_BOILER_PRESSURE_MID, definition().with(
				sound(resource("mini_boiler/pressure_loop2"))));
		add(MINI_BOILER_PRESSURE_HIGH, definition().with(
				sound(resource("mini_boiler/pressure_loop3"))));

		withSubtitle(PUMP_SLOW, definition().with(
				sound(resource("pump/slow"))));
		withSubtitle(PUMP_MID, definition().with(
				sound(resource("pump/mid1")),
				sound(resource("pump/mid2")),
				sound(resource("pump/mid3"))));
		withSubtitle(PUMP_FAST, definition().with(
				sound(resource("pump/fast1")),
				sound(resource("pump/fast2")),
				sound(resource("pump/fast3"))));

		withSubtitle(PIPE_CONNECT, definition().with(
				sound(resource("pipe_connect1")),
				sound(resource("pipe_connect2"))));
		withSubtitle(PIPE_DISCONNECT, definition().with(
				sound(resource("pipe_disconnect1")),
				sound(resource("pipe_disconnect2"))));

		withSubtitle(FIREBALL_BIG, definition().with(
				sound(resource("fireball_big_launch1")),
				sound(resource("fireball_big_launch2"))));
		withSubtitle(FIREBALL_BIG_HIT, definition().with(
				sound(resource("fireball_big_hit1")),
				sound(resource("fireball_big_hit2"))));
		withSubtitle(FIREBALL, definition().with(
				sound(resource("fireball_small_launch1")),
				sound(resource("fireball_small_launch2"))));
		withSubtitle(FIREBALL_HIT, definition().with(
				sound(resource("fireball_small_hit1")),
				sound(resource("fireball_small_hit2"))));

		withSubtitle(BLAZING_RAY_FIRE, definition().with(
				sound(resource("ignition_cannon/fire1")),
				sound(resource("ignition_cannon/fire2"))));
		withSubtitle(BLAZING_RAY_EMPTY, definition().with(
				sound(resource("ignition_cannon/empty"))));

		withSubtitle(CINDER_STAFF_CHARGE, definition().with(
				sound(resource("cinder_staff/charge"))));
		withSubtitle(CINDER_STAFF_FAIL, definition().with(
				sound(resource("cinder_staff/fail"))));
		add(CINDER_STAFF_LOOP, definition().with(
				sound(resource("cinder_staff/loop"))));

		withSubtitle(EXPLOSION_CHARM_ABSORB, definition().with(
				sound(resource("explosion_charm/absorb1")),
				sound(resource("explosion_charm/absorb2"))));
		add(EXPLOSION_CHARM_RECHARGE, definition().with(
				sound(resource("explosion_charm/recharge"))));

		withSubtitle(ASHEN_AMULET_BURN, definition().with(
				sound(resource("ash_amulet/burn1")),
				sound(resource("ash_amulet/burn2"))));

		withSubtitle(HEATED_ITEM_LEVELUP, definition().with(
				sound(resource("heated_tool_level"))));
		withSubtitle(RESONATING_BELL, definition().with(
				sound(resource("resonating_bell1")),
				sound(resource("resonating_bell2"))));
		withSubtitle(CINDER_JET, definition().with(
				sound(resource("cinder_jet1")),
				sound(resource("cinder_jet2"))));
		withSubtitle(INFLICTOR_GEM, definition().with(
				sound(resource("inflictor_gem_absorb"))));

		withSubtitle(METALLURGIC_DUST, definition().with(
				sound(resource("metallurgic_dust/convert1")),
				sound(resource("metallurgic_dust/convert2"))));
		withSubtitle(METALLURGIC_DUST_FAIL, definition().with(
				sound(resource("metallurgic_dust/fail1")),
				sound(resource("metallurgic_dust/fail2"))));

		add(CODEX_OPEN, definition().with(
				sound(resource("ancient_codex/open"))));
		add(CODEX_CLOSE, definition().with(
				sound(resource("ancient_codex/close"))));
		add(CODEX_CATEGORY_OPEN, definition().with(
				sound(resource("ancient_codex/category_open"))));
		add(CODEX_CATEGORY_CLOSE, definition().with(
				sound(resource("ancient_codex/category_close"))));
		add(CODEX_CATEGORY_SELECT, definition().with(
				sound(resource("ancient_codex/category_scrape_up1")),
				sound(resource("ancient_codex/category_scrape_up2")),
				sound(resource("ancient_codex/category_scrape_up3"))));
		add(CODEX_CATEGORY_UNSELECT, definition().with(
				sound(resource("ancient_codex/category_scrape_down1")),
				sound(resource("ancient_codex/category_scrape_down2")),
				sound(resource("ancient_codex/category_scrape_down3"))));
		add(CODEX_CATEGORY_SWITCH, definition().with(
				sound(resource("ancient_codex/flip1")),
				sound(resource("ancient_codex/flip2"))));
		add(CODEX_PAGE_OPEN, definition().with(
				sound(resource("ancient_codex/page_open"))));
		add(CODEX_PAGE_CLOSE, definition().with(
				sound(resource("ancient_codex/page_close"))));
		add(CODEX_PAGE_SWITCH, definition().with(
				sound(resource("ancient_codex/flip1")),
				sound(resource("ancient_codex/flip2"))));
		add(CODEX_CHECK, definition().with(
				sound(resource("ancient_codex/check"))));
		add(CODEX_UNCHECK, definition().with(
				sound(resource("ancient_codex/uncheck"))));
		add(CODEX_LOCK, definition().with(
				sound(resource("ancient_codex/lock"))));
		add(CODEX_UNLOCK, definition().with(
				sound(resource("ancient_codex/unlock"))));

		add(BAUBLE_EQUIP, definition().with(
				sound(resource("bauble_equip"))));
		add(BAUBLE_UNEQUIP, definition().with(
				sound(resource("bauble_unequip"))));

		add(TYRFING_HIT, definition().with(
				sound(resource("tyrfing_hit1")),
				sound(resource("tyrfing_hit2"))));
		add(SHIFTING_SCALES_BREAK, definition().with(
				sound(resource("scale_heart_break1")),
				sound(resource("scale_heart_break2")),
				sound(resource("scale_heart_break3")),
				sound(resource("scale_heart_break4"))));
		add(WINDING_GEARS_SPRING, definition().with(
				sound(resource("spring_launch1")),
				sound(resource("spring_launch2"))));

		withSubtitle(ANCIENT_GOLEM_STEP, definition().with(
				sound(resource("agolem/step1")),
				sound(resource("agolem/step2")),
				sound(resource("agolem/step3")),
				sound(resource("agolem/step4"))));
		withSubtitle(ANCIENT_GOLEM_HURT, definition().with(
				sound(resource("agolem/hit1")),
				sound(resource("agolem/hit2")),
				sound(resource("agolem/hit3")),
				sound(resource("agolem/hit4"))));
		withSubtitle(ANCIENT_GOLEM_PUNCH, definition().with(
				sound(resource("agolem/punch1")),
				sound(resource("agolem/punch2")),
				sound(resource("agolem/punch3")),
				sound(resource("agolem/punch4"))));
		withSubtitle(ANCIENT_GOLEM_DEATH, definition().with(
				sound(resource("agolem/die"))));
	}

	public void withSubtitle(RegistryObject<SoundEvent> soundEvent, SoundDefinition definition) {
		add(soundEvent, definition.subtitle("subtitles." + Embers.MODID + "." + soundEvent.getId().getPath()));
	}

	public ResourceLocation resource(String path) {
		return new ResourceLocation(Embers.MODID, path);
	}
}
