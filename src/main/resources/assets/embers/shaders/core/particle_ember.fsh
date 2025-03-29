#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D DepthBuffer;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform vec2 ScreenSize;
uniform mat4 ProjMatInv;
uniform float Offset;
uniform float Fade;
uniform float AlphaCutoff;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;
in vec4 viewSpacePos;

out vec4 fragColor;

void main() {
	vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;

	if (color.a <= AlphaCutoff) {
		discard;
	}

	vec2 screenPos = gl_FragCoord.xy / ScreenSize;
	vec4 solidDepth = ProjMatInv * vec4(screenPos * 2.0 - 1.0, texture(DepthBuffer, screenPos).r * 2.0 - 1.0, 1.0);
	solidDepth.z /= solidDepth.w;
	color.a *= min((viewSpacePos.z - solidDepth.z + Offset / 2.0) / Fade, 1.0);

	if (color.a <= 0.0) {
		discard;
	}

	fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}