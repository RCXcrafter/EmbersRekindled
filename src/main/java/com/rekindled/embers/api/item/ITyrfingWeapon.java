package com.rekindled.embers.api.item;

import net.minecraftforge.event.entity.living.LivingHurtEvent;

public interface ITyrfingWeapon {
	public void attack(LivingHurtEvent event, double armor);
}
