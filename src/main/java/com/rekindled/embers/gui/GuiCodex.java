package com.rekindled.embers.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.joml.Matrix4f;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import com.rekindled.embers.Embers;
import com.rekindled.embers.EmbersClientEvents;
import com.rekindled.embers.datagen.EmbersSounds;
import com.rekindled.embers.render.SneakyBufferSourceWrapper;
import com.rekindled.embers.research.ResearchBase;
import com.rekindled.embers.research.ResearchCategory;
import com.rekindled.embers.research.ResearchManager;
import com.rekindled.embers.research.subtypes.ResearchSwitchCategory;
import com.rekindled.embers.util.Misc;
import com.rekindled.embers.util.RenderUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiCodex extends Screen {
	public double mouseX = 0;
	public double mouseY = 0; 
	public double smoothMouseX = 0;
	public double smoothMouseY = 0; 
	public int selectedIndex = -1;
	public int selectedPageIndex = -1;
	public ResearchCategory researchCategory;
	public ResearchBase researchPage;

	public float ticks = 1.0f;

	public boolean showLeftArrow = false, showRightArrow = false;

	public int tooltipX = 0, tooltipY = 0;
	ItemStack tooltipStack = null;
	public boolean renderTooltip = false;
	public int framesExisted = 0;
	public float[] raise = null;
	public float[] raiseTargets = null;
	public String[] sentences = null;
	LinkedList<ResearchCategory> lastCategories = new LinkedList<>();
	public boolean nextPageSelected;
	public boolean previousPageSelected;

	public String searchString = "";
	public int searchDelay;
	public ArrayList<ResearchBase> searchResult = new ArrayList<>();

	public static ResourceLocation INDEX = new ResourceLocation(Embers.MODID, "textures/gui/codex_index.png");
	public static ResourceLocation PARTS = new ResourceLocation(Embers.MODID, "textures/gui/codex_parts.png");

	public static GuiCodex instance = new GuiCodex();

	public GuiCodex() {
		super(Component.translatable("gui." + Embers.MODID + ".codex.title"));
	}

	public void markTooltipForRender(ItemStack stack, int x, int y){
		renderTooltip = true;
		tooltipX = x;
		tooltipY = y;
		tooltipStack = stack;
	}

	public void doRenderTooltip(GuiGraphics graphics) {
		if (renderTooltip) {
			graphics.renderTooltip(font, tooltipStack, tooltipX, tooltipY);
			renderTooltip = false;
		}
	}

	public void pushLastCategory(ResearchCategory category) {
		ListIterator<ResearchCategory> iterator = lastCategories.listIterator();
		boolean clear = false;
		while(iterator.hasNext()) {
			ResearchCategory lastCategory = iterator.next();
			if(lastCategory == category)
				clear = true;
			if(clear)
				iterator.remove();
		}
		lastCategories.add(category);
	}

	public ResearchCategory popLastCategory() {
		if(lastCategories.isEmpty())
			return null;
		return lastCategories.removeLast();
	}

	public ResearchCategory peekLastCategory() {
		if(lastCategories.isEmpty())
			return null;
		return lastCategories.getLast();
	}

	public void renderItemStackAt(GuiGraphics graphics, ItemStack stack, int x, int y, int mouseX, int mouseY){
		if (!stack.isEmpty()) {
			graphics.renderFakeItem(stack, x, y);
			graphics.renderItemDecorations(font, stack, x, y, stack.getCount() != 1 ? Integer.toString(stack.getCount()) : "");
			if (mouseX >= x && mouseY >= y && mouseX < x+16 && mouseY < y+16){
				this.markTooltipForRender(stack, mouseX, mouseY);
			}
		}
	}

	public void renderItemStackMinusTooltipAt(GuiGraphics graphics, ItemStack stack, int x, int y){
		if (!stack.isEmpty()) {
			graphics.renderFakeItem(stack, x, y);
			//rendering an item disables this
			RenderSystem.enableBlend();
			//this.itemRenderer.renderGuiItemDecorations(font, stack, x, y, stack.getCount() != 1 ? Integer.toString(stack.getCount()) : "");
		}
	}

	@SuppressWarnings("resource")
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == InputConstants.KEY_ESCAPE) {
			if (researchCategory != null) {
				if (researchPage != null) {
					researchPage = null;
					playSound(EmbersSounds.CODEX_PAGE_CLOSE.get());
					return false;
				}
				researchCategory = popLastCategory();
				playSound(researchCategory == null ? EmbersSounds.CODEX_CATEGORY_CLOSE.get() : EmbersSounds.CODEX_CATEGORY_SWITCH.get());
				return false;
			}
		}
		if (researchPage != null && researchPage.hasMultiplePages()) {
			if (Minecraft.getInstance().options.keyLeft.matches(keyCode, scanCode)) {
				switchPreviousPage();
				return false;
			}
			if (Minecraft.getInstance().options.keyRight.matches(keyCode, scanCode)) {
				switchNextPage();
				return false;
			}
		} else if (researchPage == null) {
			if(keyCode == InputConstants.KEY_BACKSPACE) {
				if (!searchString.isEmpty())
					setSearchString(searchString.substring(0, searchString.length() - 1));
			}
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	public boolean shouldCloseOnEsc() {
		return researchCategory == null;
	}

	@Override
	public boolean charTyped(char typedChar, int modifiers) {
		if(researchPage == null) {
			if (!Character.isISOControl(typedChar))
				setSearchString(searchString + typedChar);
		}
		return super.charTyped(typedChar, modifiers);
	}

	private void setSearchString(String string) {
		searchString = string;
		searchDelay = 20;
	}

	private void switchNextPage() {
		researchPage = researchPage.getNextPage();
		playSound(EmbersSounds.CODEX_PAGE_SWITCH.get());
	}

	private void switchPreviousPage() {
		researchPage = researchPage.getPreviousPage();
		playSound(EmbersSounds.CODEX_PAGE_SWITCH.get());
	}

	public void playSound(SoundEvent sound) {
		playSound(sound, 0.75f);
	}

	public void playSound(SoundEvent sound, float pitch) {
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, 1.0F));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (selectedIndex != -1 && this.researchCategory == null){
			ResearchCategory selectedCategory = ResearchManager.researches.get(selectedIndex);
			if (selectedCategory.isChecked()) {
				this.researchCategory = selectedCategory;
				playSound(EmbersSounds.CODEX_CATEGORY_OPEN.get());
			}
		}
		if (selectedPageIndex != -1 && this.researchPage == null){
			ResearchBase selectedResearchPage = researchCategory.researches.get(selectedPageIndex);
			if (mouseButton == 0) {
				if (selectedResearchPage.onOpen(this)) {
					this.researchPage = selectedResearchPage;
					playSound(EmbersSounds.CODEX_PAGE_OPEN.get());
				}
			} else if (mouseButton == 1) {
				boolean newChecked = !selectedResearchPage.isChecked();
				selectedResearchPage.check(newChecked);
				if (newChecked)
					playSound(EmbersSounds.CODEX_CHECK.get());
				else
					playSound(EmbersSounds.CODEX_UNCHECK.get());
				if (selectedResearchPage instanceof ResearchSwitchCategory selectedCategory) {
					for (ResearchBase research : selectedCategory.targetCategory.researches) {
						ResearchManager.sendCheckmark(research, newChecked);
					}
				} else {
					ResearchManager.sendCheckmark(selectedResearchPage, newChecked);
				}
			}
		}
		if (researchPage != null && researchPage.hasMultiplePages()) {
			if (nextPageSelected) {
				switchNextPage();
			} else if (previousPageSelected) {
				switchPreviousPage();
			}
		}
		return false;
	}

	public static void drawText(Font font, GuiGraphics graphics, FormattedCharSequence s, int x, int y, int color) {
		int shadowColor = Misc.intColor(64, 0, 0, 0);

		graphics.drawString(font, s, x-1, y, shadowColor, false);
		graphics.drawString(font, s, x+1, y, shadowColor, false);
		graphics.drawString(font, s, x, y-1, shadowColor, false);
		graphics.drawString(font, s, x, y+1, shadowColor, false);
		graphics.drawString(font, s, x, y, color, false);
	}

	/*public static void drawTextLessShadow(Font font, PoseStack poseStack, FormattedCharSequence s, int x, int y, int color) {
		RenderUtil.drawTextRGBA(font, s, x-1, y, 0, 0, 0, 64);
		RenderUtil.drawTextRGBA(font, s, x+1, y, 0, 0, 0, 64);
		RenderUtil.drawTextRGBA(font, s, x, y-1, 0, 0, 0, 64);
		RenderUtil.drawTextRGBA(font, s, x, y+1, 0, 0, 0, 64);
		font.drawString(s, x, y, color);
	}*/

	public static void drawTextGlowing(Font font, GuiGraphics graphics, FormattedCharSequence s, int x, int y) {
		float sine = 0.5f*((float)Math.sin(Math.toRadians(4.0f*((float)EmbersClientEvents.ticks + Minecraft.getInstance().getPartialTick())))+1.0f);
		//String stringColorStripped = s.replaceAll(RenderUtil.COLOR_CODE_MATCHER.pattern(),"");
		int shadowColor = Misc.intColor(64, 0, 0, 0);
		graphics.drawString(font, s, x-1, y, shadowColor, false);
		graphics.drawString(font, s, x+1, y, shadowColor, false);
		graphics.drawString(font, s, x, y-1, shadowColor, false);
		graphics.drawString(font, s, x, y+1, shadowColor, false);
		int shadowColor2 = Misc.intColor(40, 0, 0, 0);
		graphics.drawString(font, s, x-2, y, shadowColor2, false);
		graphics.drawString(font, s, x+2, y, shadowColor2, false);
		graphics.drawString(font, s, x, y-2, shadowColor2, false);
		graphics.drawString(font, s, x, y+2, shadowColor2, false);
		graphics.drawString(font, s, x-1, y+1, shadowColor2, false);
		graphics.drawString(font, s, x+1, y-1, shadowColor2, false);
		graphics.drawString(font, s, x-1, y-1, shadowColor2, false);
		graphics.drawString(font, s, x+1, y+1, shadowColor2, false);
		graphics.drawString(font, s, x, y, Misc.intColor(255, 64+(int)(64*sine), 16), false);
	}

	public void drawModalRectGlowing(GuiGraphics graphics, ResourceLocation texture, int x, int y, int textureX, int textureY, int width, int height) {
		float sine = 0.5f*((float)Math.sin(Math.toRadians(4.0f*((float)EmbersClientEvents.ticks + Minecraft.getInstance().getPartialTick())))+1.0f);
		RenderSystem.setShaderColor(0,0,0,64f/255);
		graphics.blit(texture, x-1, y,textureX,textureY,width,height);
		graphics.blit(texture, x+1, y,textureX,textureY,width,height);
		graphics.blit(texture, x, y-1,textureX,textureY,width,height);
		graphics.blit(texture, x, y+1,textureX,textureY,width,height);
		RenderSystem.setShaderColor(0,0,0,40f/255);
		graphics.blit(texture, x-2, y,textureX,textureY,width,height);
		graphics.blit(texture, x+2, y,textureX,textureY,width,height);
		graphics.blit(texture, x, y-2,textureX,textureY,width,height);
		graphics.blit(texture, x, y+2,textureX,textureY,width,height);
		graphics.blit(texture, x-1, y+1,textureX,textureY,width,height);
		graphics.blit(texture, x+1, y-1,textureX,textureY,width,height);
		graphics.blit(texture, x-1, y-1,textureX,textureY,width,height);
		graphics.blit(texture, x+1, y+1,textureX,textureY,width,height);
		RenderSystem.setShaderColor(255f/255,(64f+64*sine)/255,16f/255,1.0f);
		graphics.blit(texture, x, y,textureX,textureY,width,height);
	}

	public static void drawTextGlowingAura(Font font, GuiGraphics graphics, FormattedCharSequence s, int x, int y) {
		float sine = 0.5f*((float)Math.sin(Math.toRadians(4.0f*((float)EmbersClientEvents.ticks + Minecraft.getInstance().getPartialTick())))+1.0f);
		Matrix4f matrix = graphics.pose().last().pose();
		MultiBufferSource buffer = new SneakyBufferSourceWrapper(graphics.bufferSource());

		int shadowColor = Misc.intColor(40, 255, 64+(int)(64*sine), 16);
		font.drawInBatch(s, x-1, y, shadowColor, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
		font.drawInBatch(s, x-1, y, shadowColor, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
		font.drawInBatch(s, x+1, y, shadowColor, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
		font.drawInBatch(s, x, y-1, shadowColor, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
		font.drawInBatch(s, x, y+1, shadowColor, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
		int shadowColor2 = Misc.intColor(40, 127, 32+(int)(32*sine), 8);
		font.drawInBatch(s, x-2, y, shadowColor2, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
		font.drawInBatch(s, x+2, y, shadowColor2, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
		font.drawInBatch(s, x, y-2, shadowColor2, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
		font.drawInBatch(s, x, y+2, shadowColor2, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
		font.drawInBatch(s, x-1, y+1, shadowColor2, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
		font.drawInBatch(s, x+1, y-1, shadowColor2, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
		font.drawInBatch(s, x-1, y-1, shadowColor2, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
		font.drawInBatch(s, x+1, y+1, shadowColor2, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
		font.drawInBatch(s, x, y, Misc.intColor(255, 64+(int)(64*sine), 16), false, matrix, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
	}

	/*public static void drawTextGlowingAuraTransparent(Font font, PoseStack poseStack, FormattedCharSequence s, int x, int y, int r, int g, int b, int a) {
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		font.drawString(s, x, y, Misc.intColor(r,g,b) + (a << 24));
		RenderUtil.drawTextRGBA(font, s, x-1, y, r,g,b, (40*a)/255);
		RenderUtil.drawTextRGBA(font, s, x+1, y, r,g,b, (40*a)/255);
		RenderUtil.drawTextRGBA(font, s, x, y-1, r,g,b, (40*a)/255);
		RenderUtil.drawTextRGBA(font, s, x, y+1, r,g,b, (40*a)/255);
		RenderUtil.drawTextRGBA(font, s, x-2, y, r,g,b, (20*a)/255);
		RenderUtil.drawTextRGBA(font, s, x+2, y, r,g,b, (20*a)/255);
		RenderUtil.drawTextRGBA(font, s, x, y-2, r,g,b, (20*a)/255);
		RenderUtil.drawTextRGBA(font, s, x, y+2, r,g,b, (20*a)/255);
		RenderUtil.drawTextRGBA(font, s, x-1, y+1, r,g,b, (20*a)/255);
		RenderUtil.drawTextRGBA(font, s, x+1, y-1, r,g,b, (20*a)/255);
		RenderUtil.drawTextRGBA(font, s, x-1, y-1, r,g,b, (20*a)/255);
		RenderUtil.drawTextRGBA(font, s, x+1, y+1, r,g,b, (20*a)/255);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
	}*/

	/*public static void drawTextGlowingAuraTransparentIntColor(Font font, PoseStack poseStack, FormattedCharSequence s, int x, int y, int color, int a) {
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		font.drawString(s, x, y, color + (a << 24));
		font.drawString(s, x-1, y, color + (((40*a)/255) << 24));
		font.drawString(s, x+1, y, color + (((40*a)/255) << 24));
		font.drawString(s, x, y-1, color + (((40*a)/255) << 24));
		font.drawString(s, x, y+1, color + (((40*a)/255) << 24));
		font.drawString(s, x-2, y, color + (((20*a)/255) << 24));
		font.drawString(s, x+2, y, color + (((20*a)/255) << 24));
		font.drawString(s, x, y-2, color + (((20*a)/255) << 24));
		font.drawString(s, x, y+2, color + (((20*a)/255) << 24));
		font.drawString(s, x-1, y+1, color + (((20*a)/255) << 24));
		font.drawString(s, x+1, y-1, color + (((20*a)/255) << 24));
		font.drawString(s, x-1, y-1, color + (((20*a)/255) << 24));
		font.drawString(s, x+1, y+1, color + (((20*a)/255) << 24));
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
	}*/

	/*public static void drawTextGlowingAuraTransparent(Font font, PoseStack poseStack, FormattedCharSequence s, int x, int y, int a) {
		float sine = 0.5f*((float)Math.sin(Math.toRadians(4.0f*((float)EventManager.ticks + Minecraft.getMinecraft().getRenderPartialTicks())))+1.0f);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		int r = 255;
		int g = 64+(int)(64*sine);
		int b = 16;
		font.drawString(s, x, y, Misc.intColor(r,g,b) + (a << 24));
		RenderUtil.drawTextRGBA(font, s, x-1, y, r,g,b, (40*a)/255);
		RenderUtil.drawTextRGBA(font, s, x+1, y, r,g,b, (40*a)/255);
		RenderUtil.drawTextRGBA(font, s, x, y-1, r,g,b, (40*a)/255);
		RenderUtil.drawTextRGBA(font, s, x, y+1, r,g,b, (40*a)/255);
		RenderUtil.drawTextRGBA(font, s, x-2, y, r,g,b, (20*a)/255);
		RenderUtil.drawTextRGBA(font, s, x+2, y, r,g,b, (20*a)/255);
		RenderUtil.drawTextRGBA(font, s, x, y-2, r,g,b, (20*a)/255);
		RenderUtil.drawTextRGBA(font, s, x, y+2, r,g,b, (20*a)/255);
		RenderUtil.drawTextRGBA(font, s, x-1, y+1, r,g,b, (20*a)/255);
		RenderUtil.drawTextRGBA(font, s, x+1, y-1, r,g,b, (20*a)/255);
		RenderUtil.drawTextRGBA(font, s, x-1, y-1, r,g,b, (20*a)/255);
		RenderUtil.drawTextRGBA(font, s, x+1, y+1, r,g,b, (20*a)/255);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
	}*/

	public static void drawCenteredText(Font font, GuiGraphics graphics, FormattedCharSequence s, int x, int y, int color) {
		drawText(font, graphics, s,x-font.width(s)/2,y, color);
	}

	public static void drawCenteredTextGlowing(Font font, GuiGraphics graphics, FormattedCharSequence s, int x, int y) {
		drawTextGlowing(font, graphics, s,x-font.width(s)/2,y);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		boolean showTooltips = true;
		boolean showSearchString = searchDelay >= 0 && !searchString.isEmpty();
		boolean doUpdateSynced = ticks > partialTicks;
		ticks = partialTicks;
		int numResearches = ResearchManager.researches.size();
		if (this.raise == null){
			this.raise = new float[numResearches];
			for (int i = 0; i < raise.length; i ++){
				raise[i] = 0f;
			}
		}
		if (this.raiseTargets == null){
			this.raiseTargets = new float[numResearches];
			for (int i = 0; i < raiseTargets.length; i ++){
				raiseTargets[i] = 0f;
			}
		}

		this.renderBackground(graphics);
		RenderSystem.enableBlend();

		int basePosX = (int)((float)width/2.0f)-96;
		int basePosY = (int)((float)height/2.0f)-128;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		int lastSelectedIndex = this.selectedIndex;
		this.selectedIndex = -1;
		this.selectedPageIndex = -1;
		if (this.researchCategory == null){
			//RenderSystem.setShaderTexture(0, new ResourceLocation(Embers.MODID, "textures/gui/codex_index.png"));
			graphics.blit(INDEX, basePosX, basePosY, 0, 0, 192, 256);

			/*for (int i = 0; i < sentences.length; i ++) {
				drawCenteredTextGlowing(Minecraft.getInstance().fontFilterFishy, poseStack, Component.literal(sentences[i]).getVisualOrderText(), basePosX+96, basePosY+22+i*12);
			}*/

			//RenderSystem.setShaderTexture(0, new ResourceLocation(Embers.MODID, "textures/gui/codex_parts.png"));
			graphics.blit(PARTS, basePosX-16, basePosY-16, 0, 0, 48, 48);
			graphics.blit(PARTS, basePosX+160, basePosY-16, 48, 0, 48, 48);
			graphics.blit(PARTS, basePosX+160, basePosY+224, 96, 0, 48, 48);
			graphics.blit(PARTS, basePosX-16, basePosY+224, 144, 0, 48, 48);
			graphics.blit(PARTS, basePosX+72, basePosY-16, 0, 48, 48, 48);
			graphics.blit(PARTS, basePosX+72, basePosY+224, 0, 48, 48, 48);
			graphics.blit(PARTS, basePosX-16, basePosY+64, 0, 48, 48, 48);
			graphics.blit(PARTS, basePosX+160, basePosY+64, 0, 48, 48, 48);

			for (float i = 0; i < numResearches; i ++){
				float mouseDir = (float)Math.toDegrees(Math.atan2(mouseY-(basePosY+88), mouseX-(basePosX+96)))+90f;
				float distSq = (mouseX - (basePosX+96))*(mouseX - (basePosX+96)) + (mouseY - (basePosY+96))*(mouseY - (basePosY+96));
				float angle = i * (360.0f/(float) numResearches);
				boolean selected = false;
				float diff = Math.min(Math.min(Math.abs(mouseDir - angle),Math.abs((mouseDir-360f) - angle)), (Math.abs(mouseDir+360f) - angle));
				ResearchCategory category = ResearchManager.researches.get((int) i);
				//RenderSystem.setShaderTexture(0, category.getIndexTexture());
				boolean alreadyGlowing = category.researches.stream().anyMatch(entry -> searchResult.contains(entry));
				if (diff < 180.0f/(float) numResearches && distSq < 16000){
					if (lastSelectedIndex != (int)i) {
						if(category.isChecked() && !alreadyGlowing)
							playSound(EmbersSounds.CODEX_CATEGORY_SELECT.get());
						else
							playSound(EmbersSounds.CODEX_CATEGORY_UNSELECT.get());
					}
					selected = true;
					selectedIndex = (int)i;
					if (raise[(int)i] < 1.0f && doUpdateSynced){
						raise[(int)i] = raiseTargets[(int)i];
						raiseTargets[(int)i] = raiseTargets[(int)i] * 0.5f + 0.5f;
					}
				}
				else {
					if (lastSelectedIndex == (int)i)
						playSound(EmbersSounds.CODEX_CATEGORY_UNSELECT.get());
					if (doUpdateSynced){
						raise[(int)i] = raiseTargets[(int)i];
						raiseTargets[(int)i] = raiseTargets[(int)i] * 0.5f;
					}
				}
				float instRaise = raise[(int)i] * (1.0f-partialTicks) + raiseTargets[(int)i] * (partialTicks);
				graphics.pose().pushPose();
				graphics.pose().translate(basePosX+96, basePosY+88, 0);
				graphics.pose().mulPose(Axis.ZP.rotationDegrees(angle));
				boolean glowing = alreadyGlowing || selected && category.isChecked();
				graphics.blit(category.getIndexTexture(), -16, (int) (-88-12f*instRaise), 192, 112, 32, 64);
				graphics.blit(category.getIndexTexture(), -6, (int) (-80-12f*instRaise), (int) category.getIconU()+(glowing ? 16 : 0), (int) category.getIconV(), 12, 12);
				graphics.pose().popPose();
			}

			//RenderSystem.setShaderTexture(0, new ResourceLocation(Embers.MODID, "textures/gui/codex_index.png"));
			graphics.blit(INDEX, basePosX+64, basePosY+56, 192, 176, 64, 64);

			if(!showSearchString && selectedIndex >= 0) {
				ResearchCategory category = ResearchManager.researches.get(selectedIndex);
				drawCenteredTextGlowing(this.font, graphics, Component.literal(category.getName()).getVisualOrderText(), basePosX + 96, basePosY + 207);

			} else if(!searchString.isEmpty()) {
				drawCenteredTextGlowing(this.font, graphics, Component.literal(getSearchStringPrint()).getVisualOrderText(), basePosX+96, basePosY+207);
			} else {
				drawCenteredTextGlowing(this.font, graphics, Component.translatable(Embers.MODID + ".research.null").getVisualOrderText(), basePosX+96, basePosY+207);
			}

			if(selectedIndex >= 0) {
				ResearchCategory category = ResearchManager.researches.get(selectedIndex);
				List<Component> tooltip = category.getTooltip(showTooltips);
				if (!tooltip.isEmpty())
					renderEmberTooltip(graphics, tooltip, mouseX, mouseY);
			} else if(mouseX > basePosX-16 && mouseY > basePosY+224 && mouseX < basePosX-16+48 && mouseY < basePosY+224+48) {
				List<Component> tooltip = new ArrayList<Component>();
				for (String line : I18n.get(Embers.MODID + ".research.controls").split(";")) {
					tooltip.add(Component.literal(line));
				}
				renderEmberTooltip(graphics, tooltip, mouseX, mouseY);
			}
		} else {
			if (this.researchPage == null) {
				float showSpeed = 0.3f;
				boolean playUnlockSound = false;
				boolean playLockSound = false;

				basePosX = (int)((float)width/2.0f)-192;
				basePosY = (int)((float)height/2.0f)-136;
				int basePosY2 = Math.min(height-33, basePosY+272);

				//RenderSystem.setShaderTexture(0, researchCategory.getBackgroundTexture());
				graphics.blit(researchCategory.getBackgroundTexture(), basePosX, basePosY, 0, 0, 0, 384, 272, 512, 512);
				for (int i = 0; i < researchCategory.researches.size(); i ++){
					ResearchBase r = researchCategory.researches.get(i);
					if (r.isHidden())
						continue;
					r.shownAmount = r.shownTarget;
					if(r.areAncestorsChecked()) {
						if(r.shownTarget <= 0)
							playUnlockSound = true;
						r.shownTarget = Math.min(1.0f, r.shownTarget + partialTicks * 0.1f * showSpeed);//r.shownTarget*(1.0f-partialTicks) + (r.shownTarget * 0.8f + 0.2f) *partialTicks;
					} else {
						if(r.shownTarget >= 1)
							playLockSound = true;
						r.shownTarget = Math.max(0.0f, r.shownTarget - partialTicks * 0.1f * showSpeed);
					}
					boolean isShown = r.shownAmount >= 1.0;
					if (isShown && mouseX >= basePosX+r.x-24 && mouseY >= basePosY+r.y-24 && mouseX <= basePosX+r.x+24 && mouseY <= basePosY+r.y+24){
						this.selectedPageIndex = i;
						if (r.selectedAmount < 1.0f){
							r.selectedAmount = r.selectionTarget;
							r.selectionTarget = r.selectionTarget*(1.0f-partialTicks) + (r.selectionTarget * 0.8f + 0.2f) *partialTicks;
						}
					}
					else if (r.selectedAmount > 0.0f){
						r.selectedAmount = r.selectionTarget;
						r.selectionTarget = r.selectionTarget*(1.0f-partialTicks) + (r.selectionTarget * 0.9f) *partialTicks;
					}
					//Highlight search results
					if (isShown && (searchResult.contains(r) || ResearchManager.isPathToLock(r) && searchResult.isEmpty())) {
						Tesselator tess = Tesselator.getInstance();
						BufferBuilder b = tess.getBuilder();
						float x = r.x;
						float y = r.y;
						RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
						RenderSystem.setShader(GameRenderer::getPositionColorShader);
						int index = searchResult.indexOf(r);
						float amt = Mth.clamp(-searchDelay,0,10) / 10.0f;
						amt *= Mth.clampedLerp(0.5,1.0,(float)(searchResult.size()-index) / searchResult.size());
						for (float j = 0; j < 3; j ++){
							float coeff = (j+1.0f) / 3.0f;
							b.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
							RenderUtil.renderHighlightCircle(b,basePosX+x,basePosY+y,(25.0f+20.0f*coeff*coeff)* amt);
							tess.end();
						}
						RenderSystem.defaultBlendFunc();
					}
					//Highlight selection
					if (isShown && r.selectedAmount > 0.1f) {
						Tesselator tess = Tesselator.getInstance();
						BufferBuilder b = tess.getBuilder();
						float x = r.x;
						float y = r.y;
						RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
						RenderSystem.setShader(GameRenderer::getPositionColorShader);
						float amt = r.selectedAmount;
						for (float j = 0; j < 8; j ++){
							float coeff = (j+1.0f) /8.0f;
							b.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
							RenderUtil.renderHighlightCircle(b,basePosX+x,basePosY+y,(25.0f+20.0f*coeff*coeff)* amt);
							tess.end();
						}
						RenderSystem.defaultBlendFunc();
					}
					if (r.ancestors.size() > 0) {
						for (int l = 0; l < r.ancestors.size(); l ++){
							Tesselator tess = Tesselator.getInstance();
							BufferBuilder b = tess.getBuilder();
							ResearchBase ancestor = r.ancestors.get(l);
							float x1 = r.x;
							float y1 = r.y;
							float x2 = ancestor.x;
							float y2 = ancestor.y;
							//float dx = Math.abs(x1-x2);
							//float dy = Math.abs(y1-y2);
							RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
							RenderSystem.setShader(GameRenderer::getPositionColorShader);
							for (float j = 0; j < 8; j ++){
								float coeff = (float)Math.pow((j+1.0f)/8.0f,1.5f);
								float appearCoeff = Math.min(r.shownAmount,ancestor.shownAmount);

								b.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
								RenderUtil.renderWavyEmberLine(b, basePosX+x1, basePosY+y1, basePosX+x2, basePosY+y2, 4.0f*coeff, appearCoeff, new Color(255,64,16));
								tess.end();
							}
							RenderSystem.defaultBlendFunc();
						}
					}
				}
				for (int i = 0; i < researchCategory.researches.size(); i ++) {
					ResearchBase r = researchCategory.researches.get(i);
					if (r.isHidden())
						continue;
					if (r.shownAmount > 0.5) {
						RenderSystem.setShaderTexture(0, r.getIconBackground());
						int u = (int) r.getIconBackgroundU();
						int v = (int) r.getIconBackgroundV();
						graphics.blit(r.getIconBackground(), basePosX + r.x - 24, basePosY + r.y - 24, 0, u, v, 48, 48, 512, 512);
						if (!r.isChecked()) { //TODO: cleanup
							RenderSystem.setShaderTexture(0, ResearchManager.PAGE_ICONS);
							int uOverlay = 5 * 48;
							int vOverlay = 0 * 48;
							graphics.blit(r.getIconBackground(), basePosX + r.x - 24, basePosY + r.y - 24, 0, uOverlay, vOverlay, 48, 48, 512, 512);
						}
						this.renderItemStackMinusTooltipAt(graphics, r.getIcon(), basePosX + r.x - 8, basePosY + r.y - 8);
					}
				}
				//RenderSystem.setShaderTexture(0, researchCategory.getBackgroundTexture());
				graphics.blit(researchCategory.getBackgroundTexture(), basePosX, basePosY2, 0, 0, 272, 384, 33, 512, 512);
				if (!showSearchString && this.selectedPageIndex >= 0) {
					ResearchBase research = researchCategory.researches.get(this.selectedPageIndex);
					drawCenteredTextGlowing(this.font, graphics, Component.literal(research.getName()).getVisualOrderText(), basePosX + 192, basePosY2 + 13);
				} else if (!searchString.isEmpty()) {
					drawCenteredTextGlowing(this.font, graphics, Component.literal(getSearchStringPrint()).getVisualOrderText(), basePosX + 192, basePosY2 + 13);
				}
				for (int i = 0; i < researchCategory.researches.size(); i ++) {
					ResearchBase r = researchCategory.researches.get(i);
					if (r.isHidden())
						continue;
					//Appearance effect
					if (r.shownAmount > 0.0 && r.shownAmount < 1.0f){
						Tesselator tess = Tesselator.getInstance();
						BufferBuilder b = tess.getBuilder();
						float x = r.x;
						float y = r.y;
						RenderSystem.enableBlend();
						RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
						RenderSystem.setShader(GameRenderer::getPositionColorShader);
						float amt = (float)Math.sin(r.shownAmount*Math.PI);
						for (float j = 0; j < 8; j ++){
							float coeff = (j+1.0f) /8.0f;
							b.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
							RenderUtil.renderHighlightCircle(b,basePosX+x,basePosY+y,(25.0f+20.0f*coeff*coeff)* amt);
							tess.end();
						}
						RenderSystem.defaultBlendFunc();
					}
				}

				if(selectedPageIndex >= 0) {
					ResearchBase page = researchCategory.researches.get(selectedPageIndex);
					List<Component> tooltip = page.getTooltip(showTooltips);
					if(!tooltip.isEmpty())
						renderEmberTooltip(graphics, tooltip, mouseX, mouseY);
				}

				if(playLockSound)
					playSound(EmbersSounds.CODEX_LOCK.get(), showSpeed);
				if(playUnlockSound)
					playSound(EmbersSounds.CODEX_UNLOCK.get(), showSpeed);

			} else {
				//RenderSystem.setShaderTexture(0, researchPage.getBackground());
				graphics.blit(researchPage.getBackground(), basePosX, basePosY, 0, 0, 192, 256);

				drawCenteredTextGlowing(this.font, graphics, Component.literal(researchPage.getTitle()).getVisualOrderText(), basePosX+96, basePosY+19);
				researchPage.renderPageContent(graphics, this, basePosX, basePosY, this.font);

				if (researchPage.hasMultiplePages()) {
					//RenderSystem.setShaderTexture(0, researchPage.getBackground());
					nextPageSelected = false;
					previousPageSelected = false;
					int arrowY = basePosY + 256 - 13;
					RenderSystem.enableBlend();
					if(researchPage.getNextPage() != researchPage) {
						int rightArrowX = basePosX + 192 - 9 - 8;
						drawModalRectGlowing(graphics, researchPage.getBackground(), rightArrowX, arrowY, 192, 24, 18, 13);
						nextPageSelected = mouseX >= rightArrowX-3 && mouseY >= arrowY-3 && mouseX <= rightArrowX+3 + 18 && mouseY <= arrowY+3 + 13;
					}
					if(researchPage.getPreviousPage() != researchPage) {
						int leftArrowX = basePosX - 9 + 8;
						drawModalRectGlowing(graphics, researchPage.getBackground(), leftArrowX, arrowY, 192, 24 + 13, 18, 13);
						previousPageSelected = mouseX >= leftArrowX-3 && mouseY >= arrowY-3 && mouseX <= leftArrowX+3 + 18 && mouseY <= arrowY+3 + 13;
					}
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				}
			}
		}
		doRenderTooltip(graphics);

		RenderSystem.disableBlend();
	}

	private String getSearchStringPrint() {
		String searchStringFormat;
		if(searchDelay > 0)
			searchStringFormat = "";
		else if(searchResult.isEmpty())
			searchStringFormat = ChatFormatting.DARK_GRAY.toString();
		else
			searchStringFormat = ChatFormatting.GREEN.toString();
		return searchStringFormat + searchString;
	}

	public void renderEmberTooltip(GuiGraphics graphics, List<Component> text, int x, int y) {
		List<ClientTooltipComponent> components = net.minecraftforge.client.ForgeHooksClient.gatherTooltipComponents(ItemStack.EMPTY, text, x, width, height, this.font);
		drawHoveringTextGlowing(graphics, components, x, y, width, height, -1, this.font);
	}

	/*public void renderAura(float x, float y) {
		//Tesselator tess = Tesselator.getInstance();
		//BufferBuilder b = tess.getBuilder();
		//b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		//for (float i = -80; i < 80; i += 10f){
		//	RenderUtil.drawQuadGuiExt(b, x-10, y-10, x+10, y-10, x+10, y+10, x-10, y+10, 0, 0, 1, 1, 1, 1, 0.25f, 0.25f, 0.25f, 255f);
		//}
		//tess.draw();
	}*/

	public float getVert(float i, float f1, float f2) {
		float coeff = Math.abs(i) + EmbersClientEvents.ticks + Minecraft.getInstance().getPartialTick();
		float vert = Math.abs(10.0f*((1.0f-(Math.abs(i/80.0f))) * (float)(Math.sin(coeff*f1) + 0.4f*Math.sin(coeff*f2))));
		return vert;
	}

	@Override
	public void onClose() {
		super.onClose();
		for (ResearchCategory category : ResearchManager.researches){
			for (ResearchBase base : category.researches){
				base.selectedAmount = 0.0f;
			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		searchDelay--;
		if(searchDelay == 0) {
			searchResult.clear();
			Map<ResearchBase,Integer> results = ResearchManager.findByTag(searchString);
			results.entrySet().stream().sorted((x,y) -> y.getValue().compareTo(x.getValue())).map(Map.Entry::getKey).forEach(result -> searchResult.add(result));
		}
	}

	//TODO: get rid of this
	//copied from Screen#renderTooltipInternal
	public static void drawHoveringTextGlowing(GuiGraphics graphics, List<ClientTooltipComponent> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, Font font) {
		if (!textLines.isEmpty()) {
			//net.minecraftforge.client.event.RenderTooltipEvent.Pre preEvent = net.minecraftforge.client.ForgeHooksClient.onRenderTooltipPre(this.tooltipStack, pPoseStack, mouseX, mouseY, width, height, pClientTooltipComponents, this.tooltipFont, this.font);
			//if (preEvent.isCanceled()) return;
			int i = 0;
			int j = textLines.size() == 1 ? -2 : 0;

			for(ClientTooltipComponent clienttooltipcomponent : textLines) {
				int k = clienttooltipcomponent.getWidth(font);
				if (k > i) {
					i = k;
				}

				j += clienttooltipcomponent.getHeight();
			}

			int j2 = mouseX + 12;
			int k2 = mouseY - 12;
			if (j2 + i > screenWidth) {
				j2 -= 28 + i;
			}

			if (k2 + j + 6 > screenHeight) {
				k2 = screenHeight - j - 6;
			}

			graphics.pose().pushPose();
			//int l = -267386864;
			//int i1 = 1347420415;
			//int j1 = 1344798847;
			//int k1 = 400;
			//float f = this.itemRenderer.blitOffset;
			//this.itemRenderer.blitOffset = 400.0F;
			Tesselator tesselator = Tesselator.getInstance();
			BufferBuilder bufferbuilder = tesselator.getBuilder();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
			Matrix4f matrix4f = graphics.pose().last().pose();
			//net.minecraftforge.client.event.RenderTooltipEvent.Color colorEvent = net.minecraftforge.client.ForgeHooksClient.onRenderTooltipColor(this.tooltipStack, pPoseStack, j2, k2, preEvent.getFont(), pClientTooltipComponents);
			int backgroundColor = new Color(0,0,0,128).getRGB();
			float sine = 0.5f*((float)Math.sin(Math.toRadians(4.0f*((float)EmbersClientEvents.ticks + Minecraft.getInstance().getPartialTick())))+1.0f);
			float cosine = 0.5f*((float)Math.cos(Math.toRadians(4.0f*((float)EmbersClientEvents.ticks + Minecraft.getInstance().getPartialTick())))+1.0f);
			int borderColorStart = new Color(255,64+(int)(64*sine),16,128).getRGB();
			int borderColorEnd =  new Color(255,64+(int)(64*cosine),16,128).getRGB();

			graphics.fillGradient(j2 - 3, k2 - 4, j2 + i + 3, k2 - 3, 400, backgroundColor, backgroundColor);
			graphics.fillGradient(j2 - 3, k2 + j + 3, j2 + i + 3, k2 + j + 4, 400, backgroundColor, backgroundColor);
			graphics.fillGradient(j2 - 3, k2 - 3, j2 + i + 3, k2 + j + 3, 400, backgroundColor, backgroundColor);
			graphics.fillGradient(j2 - 4, k2 - 3, j2 - 3, k2 + j + 3, 400, backgroundColor, backgroundColor);
			graphics.fillGradient(j2 + i + 3, k2 - 3, j2 + i + 4, k2 + j + 3, 400, backgroundColor, backgroundColor);
			graphics.fillGradient(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + j + 3 - 1, 400, borderColorStart, borderColorEnd);
			graphics.fillGradient(j2 + i + 2, k2 - 3 + 1, j2 + i + 3, k2 + j + 3 - 1, 400, borderColorStart, borderColorEnd);
			graphics.fillGradient(j2 - 3, k2 - 3, j2 + i + 3, k2 - 3 + 1, 400, borderColorStart, borderColorStart);
			graphics.fillGradient(j2 - 3, k2 + j + 2, j2 + i + 3, k2 + j + 3, 400, borderColorEnd, borderColorEnd);
			RenderSystem.enableDepthTest();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			BufferUploader.drawWithShader(bufferbuilder.end());
			RenderSystem.disableBlend();
			MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
			graphics.pose().translate(0.0D, 0.0D, 400.0D);
			int l1 = k2;

			for(int i2 = 0; i2 < textLines.size(); ++i2) {
				ClientTooltipComponent clienttooltipcomponent1 = textLines.get(i2);
				clienttooltipcomponent1.renderText(font, j2, l1, matrix4f, multibuffersource$buffersource);
				l1 += clienttooltipcomponent1.getHeight() + (i2 == 0 ? 2 : 0);
			}

			multibuffersource$buffersource.endBatch();
			graphics.pose().popPose();
			l1 = k2;

			/*for (int l2 = 0; l2 < textLines.size(); ++l2) {
				ClientTooltipComponent clienttooltipcomponent2 = textLines.get(l2);
				clienttooltipcomponent2.renderImage(font, j2, l1, pPoseStack, this.itemRenderer, 400);
				l1 += clienttooltipcomponent2.getHeight() + (l2 == 0 ? 2 : 0);
			}*/
			//this.itemRenderer.blitOffset = f;
		}
	}
}
