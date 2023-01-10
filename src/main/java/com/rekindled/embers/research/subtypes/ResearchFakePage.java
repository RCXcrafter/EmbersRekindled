package com.rekindled.embers.research.subtypes;

import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.gui.GuiCodex;
import com.rekindled.embers.research.ResearchBase;
import com.rekindled.embers.util.Vec2i;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ResearchFakePage extends ResearchBase {
    ResearchBase targetPage;

    public ResearchFakePage(ResearchBase page, double x, double y) {
        super(page.name, ItemStack.EMPTY, x, y);
        targetPage = page;
    }

    public ResearchFakePage(ResearchBase page, Vec2i pos) {
        this(page,pos.x,pos.y);
    }

    @Override
    public String getName() {
        return targetPage.getName();
    }

    @Override
    public String getTitle() {
        return targetPage.getTitle();
    }

    @Override
    public ItemStack getIcon() {
        return targetPage.getIcon();
    }

    @Override
    public ResourceLocation getIconBackground() {
        return targetPage.getIconBackground();
    }

    @Override
    public double getIconBackgroundU() {
        return targetPage.getIconBackgroundU();
    }

    @Override
    public double getIconBackgroundV() {
        return targetPage.getIconBackgroundV();
    }

    @Override
    public boolean onOpen(GuiCodex gui) {
        gui.researchPage = targetPage;
        gui.playSound(EmbersSounds.CODEX_PAGE_OPEN.get());
        return false;
    }
}
