package com.rekindled.embers.util;

import java.awt.Color;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.rekindled.embers.EmbersClientEvents;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderUtil {

	public static void renderWavyEmberLine(BufferBuilder b, double x1, double y1, double x2, double y2, double thickness){
		renderWavyEmberLine(b,x1,y1,x2,y2,thickness,1.0,new Color(255,64,16));
	}

	public static void renderWavyEmberLine(BufferBuilder b, double x1, double y1, double x2, double y2, double thickness, double density, Color color){
		double dx = x2-x1;
		double dy = y2-y1;
		double angleRads = Math.atan2(y2-y1, x2-x1);
		double dist = Math.sqrt(dx*dx+dy*dy);
		double orthoX = Math.cos(angleRads+(Math.PI/2.0));
		double orthoY = Math.sin(angleRads+(Math.PI/2.0));
		//double rayX = Math.cos(angleRads);
		//double rayY = Math.sin(angleRads);
		for (int i = 0; i <= 10; i ++){
			float coeff = (float)i / 10f;
			double thickCoeff = Math.min(1.0, 1.4f*Mth.sqrt(2.0f*(0.5f-Math.abs((coeff-0.5f)))));
			//double thickCoeff = 1.0+0.25*Math.sin(coeff*Math.PI*2.0f);
			double tx = x1*(1.0f-coeff) + x2*coeff;
			double ty = NoiseGenUtil.interpolate((float)y1, (float)y2, coeff);
			float tick = Minecraft.getInstance().getPartialTick() + EmbersClientEvents.ticks;
			int offX = (int)(6f*tick);
			int offZ = (int)(6f*tick);
			float sine = (float)Math.sin(coeff*Math.PI*2.0f + 0.25f*(tick)) + 0.25f*(float)Math.sin(coeff*Math.PI*3.47f + 0.25f*(tick));
			float sineOff = (4.0f + (float)thickness)/3.0f;
			float densityCoeff = (float)(0.5+0.5*Math.sin(coeff*Math.PI*2.0*dist*0.01 + tick * 0.2));
			float minusDensity = (float)density * densityCoeff * EmberGenUtil.getEmberDensity(1, offX+(int)(tx-thickness*orthoX*thickCoeff), offZ+(int)(ty-thickness*orthoY*thickCoeff));
			float plusDensity = (float)density * densityCoeff * EmberGenUtil.getEmberDensity(1, offX+(int)(tx-thickness*orthoX*thickCoeff), offZ+(int)(ty-thickness*orthoY*thickCoeff));
			b.vertex(tx-thickness*(0.5f+minusDensity)*orthoX*thickCoeff-thickCoeff*orthoX*sine*sineOff, ty-thickness*(0.5f+minusDensity)*orthoY*thickCoeff-thickCoeff*orthoY*sine*sineOff, 0).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, (float)Math.pow(0.5f*(float)Math.max(0,thickCoeff-0.4f)*minusDensity,1)).endVertex();
			b.vertex(tx+thickness*(0.5f+plusDensity)*orthoX*thickCoeff-thickCoeff*orthoX*sine*sineOff, ty+thickness*(0.5f+plusDensity)*orthoY*thickCoeff-thickCoeff*orthoY*sine*sineOff, 0).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, (float)Math.pow(0.5f*(float)Math.max(0,thickCoeff-0.4f)*plusDensity,1)).endVertex();
		}
	}

	public static void renderHighlightCircle(BufferBuilder b, double x1, double y1, double thickness){
		renderHighlightCircle(b,x1,y1,thickness,0,new Color(255,64,16));
	}

	public static void renderHighlightCircle(BufferBuilder b, double x1, double y1, double thickness, double z, Color color){
		for (int i = 0; i < 40; i ++) {
			float coeff = (float)i / 40f;
			int i2 = i+1;
			if (i2 == 40){
				i2 = 0;
			}
			float coeff2 = (float)(i2) / 40f;
			double angle = Math.PI * 2.0 * coeff;
			double angle2 = Math.PI * 2.0 * coeff2;
			float tick = Minecraft.getInstance().getPartialTick() + EmbersClientEvents.ticks;
			double calcAngle2 = angle2;
			float density1 = EmberGenUtil.getEmberDensity(4, (int)x1+(int)(480.0*angle), (int)y1+4*(int)tick + (int)(4.0f*thickness));
			float density2 = EmberGenUtil.getEmberDensity(4, (int)x1+(int)(480.0*calcAngle2), (int)y1+4*(int)tick + (int)(4.0f*thickness));
			double tx = x1 + Math.sin(angle+0.03125f*tick)*(thickness - (thickness * 0.5f * density1));
			double ty = y1 + Math.cos(angle+0.03125f*tick)*(thickness - (thickness * 0.5f * density1));
			double tx2 = x1 + Math.sin(angle2+0.03125f*tick)*(thickness - (thickness * 0.5f * density2));
			double ty2 = y1 + Math.cos(angle2+0.03125f*tick)*(thickness - (thickness * 0.5f * density2));
			b.vertex(x1, y1, z).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1.0f).endVertex();
			b.vertex(tx, ty, z).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 0.0f).endVertex();
			b.vertex(tx2, ty2, z).color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 0.0f).endVertex();
		}
	}
}
