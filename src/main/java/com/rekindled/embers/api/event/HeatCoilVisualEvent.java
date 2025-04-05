package com.rekindled.embers.api.event;

import org.joml.Vector3f;

import net.minecraft.world.level.block.entity.BlockEntity;

public class HeatCoilVisualEvent extends UpgradeEvent {
	Vector3f color;
    int particles;
    float verticalSpeed;

    public HeatCoilVisualEvent(BlockEntity tile, Vector3f color, int particles, float verticalSpeed) {
        super(tile);
        this.color = color;
        this.particles = particles;
        this.verticalSpeed = verticalSpeed;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public int getParticles() {
        return particles;
    }

    public void setParticles(int particles) {
        this.particles = particles;
    }

    public float getVerticalSpeed() {
        return verticalSpeed;
    }

    public void setVerticalSpeed(float verticalSpeed) {
        this.verticalSpeed = verticalSpeed;
    }
}
