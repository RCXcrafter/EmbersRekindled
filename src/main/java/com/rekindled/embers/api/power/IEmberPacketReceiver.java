package com.rekindled.embers.api.power;

import com.rekindled.embers.entity.EmberPacketEntity;

public interface IEmberPacketReceiver {
	//boolean isFull();
	boolean hasRoomFor(double ember);
	boolean onReceive(EmberPacketEntity packet);
}
