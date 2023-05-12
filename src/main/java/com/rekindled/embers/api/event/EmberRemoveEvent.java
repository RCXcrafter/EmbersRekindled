package com.rekindled.embers.api.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;

public class EmberRemoveEvent extends Event {

	private Player player;
	private double amount = 0;
	private double originalAmount = 0;
	private List<Double> reductions = new ArrayList<Double>();

	public EmberRemoveEvent(Player player, double amount) {
		this.player = player;
		this.originalAmount = amount;
		this.amount = amount;
	}

	public Player getPlayer() {
		return player;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getOriginal() {
		return originalAmount;
	}

	public void addReduction(double reduction) {
		reductions.add(reduction);
	}

	public double getFinal() {
		double coeff = 0;
		for (double d : reductions) {
			coeff += d;
		}
		return amount * Math.max(0.0,1.0-coeff);
	}
}
