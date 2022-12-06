package com.rekindled.embers.datagen;

import com.rekindled.embers.Embers;
import com.rekindled.embers.RegistryManager;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import net.minecraftforge.registries.RegistryObject;

public class EmbersSounds extends SoundDefinitionsProvider {

	public EmbersSounds(DataGenerator generator, ExistingFileHelper helper) {
		super(generator, Embers.MODID, helper);
	}

	@Override
	public void registerSounds() {
		withSubtitle(RegistryManager.ALCHEMY_FAIL, definition().with(
				sound(resource("alchemy_tablet/fail"))));
		withSubtitle(RegistryManager.ALCHEMY_SUCCESS, definition().with(
				sound(resource("alchemy_tablet/success"))));
		add(RegistryManager.ALCHEMY_LOOP, definition().with(
				sound(resource("alchemy_tablet/loop"))));
		withSubtitle(RegistryManager.ALCHEMY_START, definition().with(
				sound(resource("alchemy_tablet/start"))));
		
		add(RegistryManager.PEDESTAL_LOOP, definition().with(
				sound(resource("pedestal_loop"))));

		withSubtitle(RegistryManager.BEAM_CANNON_FIRE, definition().with(
				sound(resource("beam_cannon/fire"))));
		withSubtitle(RegistryManager.BEAM_CANNON_HIT, definition().with(
				sound(resource("beam_cannon/hit1")),
				sound(resource("beam_cannon/hit2"))));
		
		add(RegistryManager.CRYSTAL_CELL_LOOP, definition().with(
				sound(resource("crystal_cell/loop"))));
		withSubtitle(RegistryManager.CRYSTAL_CELL_GROW, definition().with(
				sound(resource("crystal_cell/zap1")),
				sound(resource("crystal_cell/zap2")),
				sound(resource("crystal_cell/zap3")),
				sound(resource("crystal_cell/zap4"))));

		add(RegistryManager.GENERATOR_LOOP, definition().with(
				sound(resource("generator_hold_embers"))));
		withSubtitle(RegistryManager.ACTIVATOR, definition().with(
				sound(resource("activator/plume1")),
				sound(resource("activator/plume2"))));
		withSubtitle(RegistryManager.PRESSURE_REFINERY, definition().with(
				sound(resource("boiler/plume1")),
				sound(resource("boiler/plume2"))));
		withSubtitle(RegistryManager.IGNEM_REACTOR, definition().with(
				sound(resource("ignem_reactor/plume1")),
				sound(resource("ignem_reactor/plume2"))));

		withSubtitle(RegistryManager.BORE_START, definition().with(
				sound(resource("ember_bore/start"))));
		withSubtitle(RegistryManager.BORE_STOP, definition().with(
				sound(resource("ember_bore/stop"))));
		add(RegistryManager.BORE_LOOP, definition().with(
				sound(resource("ember_bore/run_loop"))));
		add(RegistryManager.BORE_LOOP_MINE, definition().with(
				sound(resource("ember_bore/mine_loop"))));

		withSubtitle(RegistryManager.CATALYTIC_PLUG_START, definition().with(
				sound(resource("catalytic_plug/start"))));
		withSubtitle(RegistryManager.CATALYTIC_PLUG_STOP, definition().with(
				sound(resource("catalytic_plug/stop"))));
		add(RegistryManager.CATALYTIC_PLUG_LOOP, definition().with(
				sound(resource("catalytic_plug/run_loop"))));
		add(RegistryManager.CATALYTIC_PLUG_LOOP_READY, definition().with(
				sound(resource("catalytic_plug/ready_loop"))));

		withSubtitle(RegistryManager.STAMPER_DOWN, definition().with(
				sound(resource("stamper/down1")),
				sound(resource("stamper/down2")),
				sound(resource("stamper/down3"))));
		withSubtitle(RegistryManager.STAMPER_UP, definition().with(
				sound(resource("stamper/up1")),
				sound(resource("stamper/up2")),
				sound(resource("stamper/up3"))));

		add(RegistryManager.HEATCOIL_HIGH, definition().with(
				sound(resource("heat_coil/high_loop"))));
		add(RegistryManager.HEATCOIL_MID, definition().with(
				sound(resource("heat_coil/mid_loop"))));
		add(RegistryManager.HEATCOIL_LOW, definition().with(
				sound(resource("heat_coil/low_loop"))));
		add(RegistryManager.HEATCOIL_COOK, definition().with(
				sound(resource("heat_coil/cooking_loop"))));

		add(RegistryManager.PLINTH_LOOP, definition().with(
				sound(resource("cinder_plinth_loop"))));
		add(RegistryManager.MELTER_LOOP, definition().with(
				sound(resource("melter_loop"))));
		add(RegistryManager.MIXER_LOOP, definition().with(
				sound(resource("mixer_loop"))));
		add(RegistryManager.COPPER_CHARGER_LOOP, definition().with(
				sound(resource("copper_charger_loop"))));
		add(RegistryManager.INJECTOR_LOOP, definition().with(
				sound(resource("injector_loop"))));

		add(RegistryManager.METAL_SEED_LOOP, definition().with(
				sound(resource("metal_seed/loop"))));
		withSubtitle(RegistryManager.METAL_SEED_PING, definition().with(
				sound(resource("metal_seed/ping1")),
				sound(resource("metal_seed/ping2")),
				sound(resource("metal_seed/ping3"))));

		withSubtitle(RegistryManager.INFERNO_FORGE_FAIL, definition().with(
				sound(resource("inferno_forge/fail"))));
		withSubtitle(RegistryManager.INFERNO_FORGE_SUCCESS, definition().with(
				sound(resource("inferno_forge/success"))));
		add(RegistryManager.INFERNO_FORGE_LOOP, definition().with(
				sound(resource("inferno_forge/loop"))));
		withSubtitle(RegistryManager.INFERNO_FORGE_START, definition().with(
				sound(resource("inferno_forge/start"))));
		withSubtitle(RegistryManager.INFERNO_FORGE_OPEN, definition().with(
				sound(resource("inferno_forge/open"))));
		withSubtitle(RegistryManager.INFERNO_FORGE_CLOSE, definition().with(
				sound(resource("inferno_forge/close"))));

		add(RegistryManager.FIELD_CHART_LOOP, definition().with(
				sound(resource("field_chart_loop"))));

		withSubtitle(RegistryManager.EMBER_EMIT, definition().with(
				sound(resource("ember_transmission/emit1")),
				sound(resource("ember_transmission/emit2")),
				sound(resource("ember_transmission/emit3")),
				sound(resource("ember_transmission/emit4"))));
		withSubtitle(RegistryManager.EMBER_EMIT_BIG, definition().with(
				sound(resource("ember_transmission/emit_big1")),
				sound(resource("ember_transmission/emit_big2")),
				sound(resource("ember_transmission/emit_big3")),
				sound(resource("ember_transmission/emit_big4"))));
		withSubtitle(RegistryManager.EMBER_RECEIVE, definition().with(
				sound(resource("ember_transmission/recep1")),
				sound(resource("ember_transmission/recep2")),
				sound(resource("ember_transmission/recep3")),
				sound(resource("ember_transmission/recep4"))));
		withSubtitle(RegistryManager.EMBER_RECEIVE_BIG, definition().with(
				sound(resource("ember_transmission/recep_big1")),
				sound(resource("ember_transmission/recep_big2")),
				sound(resource("ember_transmission/recep_big3")),
				sound(resource("ember_transmission/recep_big4"))));
		withSubtitle(RegistryManager.EMBER_RELAY, definition().with(
				sound(resource("ember_transmission/relay1")),
				sound(resource("ember_transmission/relay2")),
				sound(resource("ember_transmission/relay3")),
				sound(resource("ember_transmission/relay4"))));

		withSubtitle(RegistryManager.STEAM_ENGINE_START_STEAM, definition().with(
				sound(resource("steam_engine/start_steam"))));
		withSubtitle(RegistryManager.STEAM_ENGINE_START_BURN, definition().with(
				sound(resource("steam_engine/start_burn"))));
		add(RegistryManager.STEAM_ENGINE_LOOP_STEAM, definition().with(
				sound(resource("steam_engine/steam_loop"))));
		add(RegistryManager.STEAM_ENGINE_LOOP_BURN, definition().with(
				sound(resource("steam_engine/burn_loop"))));
		withSubtitle(RegistryManager.STEAM_ENGINE_STOP, definition().with(
				sound(resource("steam_engine/stop"))));

		withSubtitle(RegistryManager.MINI_BOILER_RUPTURE, definition().with(
				sound(resource("mini_boiler/rupture"))));
		add(RegistryManager.MINI_BOILER_LOOP_SLOW, definition().with(
				sound(resource("mini_boiler/slow_loop"))));
		add(RegistryManager.MINI_BOILER_LOOP_MID, definition().with(
				sound(resource("mini_boiler/mid_loop"))));
		add(RegistryManager.MINI_BOILER_LOOP_FAST, definition().with(
				sound(resource("mini_boiler/fast_loop"))));
		add(RegistryManager.MINI_BOILER_PRESSURE_LOW, definition().with(
				sound(resource("mini_boiler/pressure_loop1"))));
		add(RegistryManager.MINI_BOILER_PRESSURE_MID, definition().with(
				sound(resource("mini_boiler/pressure_loop2"))));
		add(RegistryManager.MINI_BOILER_PRESSURE_HIGH, definition().with(
				sound(resource("mini_boiler/pressure_loop3"))));

		withSubtitle(RegistryManager.PUMP_SLOW, definition().with(
				sound(resource("pump/slow"))));
		withSubtitle(RegistryManager.PUMP_MID, definition().with(
				sound(resource("pump/mid1")),
				sound(resource("pump/mid2")),
				sound(resource("pump/mid3"))));
		withSubtitle(RegistryManager.PUMP_FAST, definition().with(
				sound(resource("pump/fast1")),
				sound(resource("pump/fast2")),
				sound(resource("pump/fast3"))));

		withSubtitle(RegistryManager.PIPE_CONNECT, definition().with(
				sound(resource("pipe_connect1")),
				sound(resource("pipe_connect2"))));
		withSubtitle(RegistryManager.PIPE_DISCONNECT, definition().with(
				sound(resource("pipe_disconnect1")),
				sound(resource("pipe_disconnect2"))));

		withSubtitle(RegistryManager.FIREBALL_BIG, definition().with(
				sound(resource("fireball_big_launch1")),
				sound(resource("fireball_big_launch2"))));
		withSubtitle(RegistryManager.FIREBALL_BIG_HIT, definition().with(
				sound(resource("fireball_big_hit1")),
				sound(resource("fireball_big_hit2"))));
		withSubtitle(RegistryManager.FIREBALL, definition().with(
				sound(resource("fireball_small_launch1")),
				sound(resource("fireball_small_launch2"))));
		withSubtitle(RegistryManager.FIREBALL_HIT, definition().with(
				sound(resource("fireball_small_hit1")),
				sound(resource("fireball_small_hit2"))));

		withSubtitle(RegistryManager.BLAZING_RAY_FIRE, definition().with(
				sound(resource("ignition_cannon/fire1")),
				sound(resource("ignition_cannon/fire2"))));
		withSubtitle(RegistryManager.BLAZING_RAY_EMPTY, definition().with(
				sound(resource("ignition_cannon/empty"))));

		withSubtitle(RegistryManager.CINDER_STAFF_CHARGE, definition().with(
				sound(resource("cinder_staff/charge"))));
		withSubtitle(RegistryManager.CINDER_STAFF_FAIL, definition().with(
				sound(resource("cinder_staff/fail"))));
		add(RegistryManager.CINDER_STAFF_LOOP, definition().with(
				sound(resource("cinder_staff/loop"))));

		withSubtitle(RegistryManager.EXPLOSION_CHARM_ABSORB, definition().with(
				sound(resource("explosion_charm/absorb1")),
				sound(resource("explosion_charm/absorb2"))));
		add(RegistryManager.EXPLOSION_CHARM_RECHARGE, definition().with(
				sound(resource("explosion_charm/recharge"))));

		withSubtitle(RegistryManager.ASHEN_AMULET_BURN, definition().with(
				sound(resource("ash_amulet/burn1")),
				sound(resource("ash_amulet/burn2"))));

		withSubtitle(RegistryManager.HEATED_ITEM_LEVELUP, definition().with(
				sound(resource("heated_tool_level"))));
		withSubtitle(RegistryManager.RESONATING_BELL, definition().with(
				sound(resource("resonating_bell1")),
				sound(resource("resonating_bell2"))));
		withSubtitle(RegistryManager.CINDER_JET, definition().with(
				sound(resource("cinder_jet1")),
				sound(resource("cinder_jet2"))));
		withSubtitle(RegistryManager.INFLICTOR_GEM, definition().with(
				sound(resource("inflictor_gem_absorb"))));

		withSubtitle(RegistryManager.METALLURGIC_DUST, definition().with(
				sound(resource("metallurgic_dust/convert1")),
				sound(resource("metallurgic_dust/convert2"))));
		withSubtitle(RegistryManager.METALLURGIC_DUST_FAIL, definition().with(
				sound(resource("metallurgic_dust/fail1")),
				sound(resource("metallurgic_dust/fail2"))));

		add(RegistryManager.CODEX_OPEN, definition().with(
				sound(resource("ancient_codex/open"))));
		add(RegistryManager.CODEX_CLOSE, definition().with(
				sound(resource("ancient_codex/close"))));
		add(RegistryManager.CODEX_CATEGORY_OPEN, definition().with(
				sound(resource("ancient_codex/category_open"))));
		add(RegistryManager.CODEX_CATEGORY_CLOSE, definition().with(
				sound(resource("ancient_codex/category_close"))));
		add(RegistryManager.CODEX_CATEGORY_SELECT, definition().with(
				sound(resource("ancient_codex/category_scrape_up1")),
				sound(resource("ancient_codex/category_scrape_up2")),
				sound(resource("ancient_codex/category_scrape_up3"))));
		add(RegistryManager.CODEX_CATEGORY_UNSELECT, definition().with(
				sound(resource("ancient_codex/category_scrape_down1")),
				sound(resource("ancient_codex/category_scrape_down2")),
				sound(resource("ancient_codex/category_scrape_down3"))));
		add(RegistryManager.CODEX_CATEGORY_SWITCH, definition().with(
				sound(resource("ancient_codex/flip1")),
				sound(resource("ancient_codex/flip2"))));
		add(RegistryManager.CODEX_PAGE_OPEN, definition().with(
				sound(resource("ancient_codex/page_open"))));
		add(RegistryManager.CODEX_PAGE_CLOSE, definition().with(
				sound(resource("ancient_codex/page_close"))));
		add(RegistryManager.CODEX_PAGE_SWITCH, definition().with(
				sound(resource("ancient_codex/flip1")),
				sound(resource("ancient_codex/flip2"))));
		add(RegistryManager.CODEX_CHECK, definition().with(
				sound(resource("ancient_codex/check"))));
		add(RegistryManager.CODEX_UNCHECK, definition().with(
				sound(resource("ancient_codex/uncheck"))));
		add(RegistryManager.CODEX_LOCK, definition().with(
				sound(resource("ancient_codex/lock"))));
		add(RegistryManager.CODEX_UNLOCK, definition().with(
				sound(resource("ancient_codex/unlock"))));

		add(RegistryManager.BAUBLE_EQUIP, definition().with(
				sound(resource("bauble_equip"))));
		add(RegistryManager.BAUBLE_UNEQUIP, definition().with(
				sound(resource("bauble_unequip"))));

		add(RegistryManager.TYRFING_HIT, definition().with(
				sound(resource("tyrfing_hit1")),
				sound(resource("tyrfing_hit2"))));
		add(RegistryManager.SHIFTING_SCALES_BREAK, definition().with(
				sound(resource("scale_heart_break1")),
				sound(resource("scale_heart_break2")),
				sound(resource("scale_heart_break3")),
				sound(resource("scale_heart_break4"))));
		add(RegistryManager.WINDING_GEARS_SPRING, definition().with(
				sound(resource("spring_launch1")),
				sound(resource("spring_launch2"))));

		withSubtitle(RegistryManager.ANCIENT_GOLEM_STEP, definition().with(
				sound(resource("agolem/step1")),
				sound(resource("agolem/step2")),
				sound(resource("agolem/step3")),
				sound(resource("agolem/step4"))));
		withSubtitle(RegistryManager.ANCIENT_GOLEM_HURT, definition().with(
				sound(resource("agolem/hit1")),
				sound(resource("agolem/hit2")),
				sound(resource("agolem/hit3")),
				sound(resource("agolem/hit4"))));
		withSubtitle(RegistryManager.ANCIENT_GOLEM_PUNCH, definition().with(
				sound(resource("agolem/punch1")),
				sound(resource("agolem/punch2")),
				sound(resource("agolem/punch3")),
				sound(resource("agolem/punch4"))));
		withSubtitle(RegistryManager.ANCIENT_GOLEM_DEATH, definition().with(
				sound(resource("agolem/die"))));
	}

	public void withSubtitle(RegistryObject<SoundEvent> soundEvent, SoundDefinition definition) {
		add(soundEvent, definition.subtitle("subtitles." + Embers.MODID + "." + soundEvent.getId().getPath()));
	}

	public ResourceLocation resource(String path) {
		return new ResourceLocation(Embers.MODID, path);
	}
}
