package com.rekindled.embers.api.tile;

import com.rekindled.embers.api.filter.IFilter;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface IOrderDestination {
    void order(BlockEntity source, IFilter filter, int orderSize);

    void resetOrder(BlockEntity source);
}
