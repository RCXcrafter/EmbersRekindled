package com.rekindled.embers.api.tile;

import com.rekindled.embers.api.filter.IFilter;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;

public class OrderStack {
    private BlockPos pos;
    private IFilter filter;
    private int size;

    public OrderStack(BlockPos pos, IFilter filter, int size) {
        this.pos = pos;
        this.filter = filter;
        this.size = size;
    }

    public OrderStack(CompoundTag tag) {
        readFromNBT(tag);
    }

    public BlockPos getPos() {
        return pos;
    }

    public IOrderSource getSource(Level world) {
        BlockEntity tile = world.getBlockEntity(pos);
        if(tile instanceof IOrderSource)
            return (IOrderSource) tile;
        return null;
    }

    public IFilter getFilter() {
        return filter;
    }

    public int getSize() {
        return size;
    }

    public boolean acceptsItem(Level world, ItemStack stack) {
        IOrderSource source = getSource(world);
        if(source != null) {
            IItemHandler itemHandler = source.getItemHandler();
            if(itemHandler != null)
                return filter.acceptsItem(stack, itemHandler);
        }
        return false;
    }

    public void deplete(int n) {
        size -= n;
    }

    public void increment(int n) {
        size += n;
    }

    public CompoundTag writeToNBT(CompoundTag tag) {
        tag.putInt("x",pos.getX());
        tag.putInt("y",pos.getY());
        tag.putInt("z",pos.getZ());
        tag.put("filter", filter.writeToNBT(new CompoundTag()));
        tag.putInt("size", size);
        return tag;
    }

    public void readFromNBT(CompoundTag tag) {
        pos = new BlockPos(tag.getInt("x"),tag.getInt("y"),tag.getInt("z"));
        //TODO: I'll do this later or something
        //filter = FilterUtil.deserializeFilter(tag.getCompound("filter"));
        size = tag.getInt("size");
    }

    public void reset(IFilter filter, int size) {
        this.filter = filter;
        this.size = size;
    }
}
