package com.rekindled.embers.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import org.joml.Vector3f;

import com.mojang.blaze3d.platform.NativeImage;
import com.rekindled.embers.Embers;
import com.rekindled.embers.api.EmbersAPI;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class EmbersColors implements ResourceManagerReloadListener {

	public static ResourceLocation CUSTOM_ID = new ResourceLocation(Embers.MODID, "custom");

	public static ResourceLocation EMBER_ID = new ResourceLocation(Embers.MODID, "ember");
	public static int EMBER_ABGR = 0xFF1040FF;
	public static Vector3f EMBER = new Vector3f(255.0F / 255.0F, 64.0F / 255.0F, 16.0F / 255.0F);
	public static ResourceLocation EMBER_INVERTED_ID = new ResourceLocation(Embers.MODID, "ember_inverted");
	public static Vector3f EMBER_INVERTED = new Vector3f(EMBER.z, EMBER.y, EMBER.x);
	public static ResourceLocation OVERHEAT_ID = new ResourceLocation(Embers.MODID, "hearth_coil_overheat");
	public static int OVERHEAT_ABGR = 0xFF40C0FF;
	public static Vector3f OVERHEAT = new Vector3f(255.0F / 255.0F, 192.0F / 255.0F, 64.0F / 255.0F);
	public static ResourceLocation SMOKE_ID = new ResourceLocation(Embers.MODID, "smoke");
	public static int SMOKE_ABGR = 0xFF404040;
	public static Vector3f SMOKE = new Vector3f(64.0F / 255.0F, 64.0F / 255.0F, 64.0F / 255.0F);
	public static ResourceLocation VAPOR_ID = new ResourceLocation(Embers.MODID, "vapor");
	public static int VAPOR_ABGR = 0xFF404040;
	public static Vector3f VAPOR = new Vector3f(64.0F / 255.0F, 64.0F / 255.0F, 64.0F / 255.0F);
	public static ResourceLocation GLIMMER_ID = new ResourceLocation(Embers.MODID, "glimmer");
	public static int GLIMMER_ABGR = 0xFF10FFFF;
	public static Vector3f GLIMMER = new Vector3f(255.0F / 255.0F, 255.0F / 255.0F, 16.0F / 255.0F);
	public static ResourceLocation GLIMMER_PROJECTILE_ID = new ResourceLocation(Embers.MODID, "glimmer_projectile");
	public static int GLIMMER_PROJECTILE_ABGR = 0xFF1080FF;
	public static Vector3f GLIMMER_PROJECTILE = new Vector3f(255.0F / 255.0F, 128.0F / 255.0F, 16.0F / 255.0F);
	public static ResourceLocation PIPE_CLOGGED_ID = new ResourceLocation(Embers.MODID, "pipe_clogged");
	public static int PIPE_CLOGGED_ABGR = 0xFF1010FF;
	public static Vector3f PIPE_CLOGGED = new Vector3f(255.0F / 255.0F, 16.0F / 255.0F, 16.0F / 255.0F);
	public static ResourceLocation PIPE_FLOWING_ID = new ResourceLocation(Embers.MODID, "pipe_flowing");
	public static int PIPE_FLOWING_ABGR = 0xFF10FF10;
	public static Vector3f PIPE_FLOWING = new Vector3f(16.0F / 255.0F, 255.0F / 255.0F, 16.0F / 255.0F);

	public static HashMap<ResourceLocation, Vector3f> colors = new HashMap<ResourceLocation, Vector3f>();

	static {
		EmbersAPI.registerColor(EMBER_ID, EMBER);
		EmbersAPI.registerColor(EMBER_INVERTED_ID, EMBER_INVERTED);
		EmbersAPI.registerColor(OVERHEAT_ID, OVERHEAT);
		EmbersAPI.registerColor(SMOKE_ID, SMOKE);
		EmbersAPI.registerColor(VAPOR_ID, VAPOR);
		EmbersAPI.registerColor(GLIMMER_ID, GLIMMER);
		EmbersAPI.registerColor(GLIMMER_PROJECTILE_ID, GLIMMER_PROJECTILE);
		EmbersAPI.registerColor(PIPE_CLOGGED_ID, PIPE_CLOGGED);
		EmbersAPI.registerColor(PIPE_FLOWING_ID, PIPE_FLOWING);
	}

	@Override
	public void onResourceManagerReload(ResourceManager manager) {
		try {
			EMBER_ABGR = getColorFromTexture(manager, EMBER_ID.getPath());
			OVERHEAT_ABGR = getColorFromTexture(manager, OVERHEAT_ID.getPath());
			SMOKE_ABGR = getColorFromTexture(manager, SMOKE_ID.getPath());
			VAPOR_ABGR = getColorFromTexture(manager, VAPOR_ID.getPath());
			GLIMMER_ABGR = getColorFromTexture(manager, GLIMMER_ID.getPath());
			GLIMMER_PROJECTILE_ABGR = getColorFromTexture(manager, GLIMMER_PROJECTILE_ID.getPath());
			PIPE_CLOGGED_ABGR = getColorFromTexture(manager, PIPE_CLOGGED_ID.getPath());
			PIPE_FLOWING_ABGR = getColorFromTexture(manager, PIPE_FLOWING_ID.getPath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		EMBER.set(Misc.colorFromABGRInt(EMBER_ABGR));
		EMBER_INVERTED.set(EMBER.z, EMBER.y, EMBER.x);
		OVERHEAT.set(Misc.colorFromABGRInt(OVERHEAT_ABGR));
		SMOKE.set(Misc.colorFromABGRInt(SMOKE_ABGR));
		VAPOR.set(Misc.colorFromABGRInt(VAPOR_ABGR));
		GLIMMER.set(Misc.colorFromABGRInt(GLIMMER_ABGR));
		GLIMMER_PROJECTILE.set(Misc.colorFromABGRInt(GLIMMER_PROJECTILE_ABGR));
		PIPE_CLOGGED.set(Misc.colorFromABGRInt(PIPE_CLOGGED_ABGR));
		PIPE_FLOWING.set(Misc.colorFromABGRInt(PIPE_FLOWING_ABGR));
	}

	public static int getColorFromTexture(ResourceManager manager, String name) throws FileNotFoundException, IOException {
		NativeImage image = NativeImage.read(manager.getResource(new ResourceLocation(Embers.MODID + ":textures/colors/" + name + ".png")).orElseThrow(FileNotFoundException::new).open());
		int color = image.getPixelRGBA(0, 0); //yeah so this is actually ABGR :)
		image.close();

		//maybe I'll bother with this later
		/*Optional<Resource> meta = manager.getResource(new ResourceLocation(Embers.MODID + ":textures/colors/" + name + ".png.mcmeta"));
		if (meta.isPresent()) {
			JsonObject metaJson = GsonHelper.parse(meta.orElseThrow(FileNotFoundException::new).openAsReader());
		}*/
		return color;
	}
}
