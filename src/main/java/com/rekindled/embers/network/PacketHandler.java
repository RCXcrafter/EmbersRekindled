package com.rekindled.embers.network;

import com.rekindled.embers.Embers;
import com.rekindled.embers.network.message.MessageEmberRayFX;
import com.rekindled.embers.network.message.MessageResearchData;
import com.rekindled.embers.network.message.MessageResearchTick;
import com.rekindled.embers.network.message.MessageTEUpdateRequest;
import com.rekindled.embers.network.message.MessageWorldSeed;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(Embers.MODID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
			);

	static int id = 0;

	public static void init() {
		INSTANCE.registerMessage(id++, MessageTEUpdateRequest.class, MessageTEUpdateRequest::encode, MessageTEUpdateRequest::decode, MessageTEUpdateRequest::handle);
		INSTANCE.registerMessage(id++, MessageResearchTick.class, MessageResearchTick::encode, MessageResearchTick::decode, MessageResearchTick::handle);
		INSTANCE.registerMessage(id++, MessageResearchData.class, MessageResearchData::encode, MessageResearchData::decode, MessageResearchData::handle);
		INSTANCE.registerMessage(id++, MessageWorldSeed.class, MessageWorldSeed::encode, MessageWorldSeed::decode, MessageWorldSeed::handle);
		INSTANCE.registerMessage(id++, MessageEmberRayFX.class, MessageEmberRayFX::encode, MessageEmberRayFX::decode, MessageEmberRayFX::handle);
	}
}
