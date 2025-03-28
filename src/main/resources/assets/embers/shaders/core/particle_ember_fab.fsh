#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D DepthBuffer;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform vec2 ScreenSize;
uniform float Offset;
uniform float Fade;
uniform float AlphaCutoff;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;
in vec4 viewSpacePos;
in mat4 projMatInv;

out vec4 fragColor;

void main() {
	vec4 color = texture(Sampler0, texCoord0);
	color.rgb *= color.a;
	color.a = (color.r + color.g + color.b) / 3.0;

	color *= vertexColor;

	color.rgb *= 2.0;
	color.a /= 2.0;

	if (color.r < vertexColor.r || color.g < vertexColor.g || color.b < vertexColor.b) {
		color.rgb = vertexColor.rgb;
	}

	vec2 screenPos = gl_FragCoord.xy / ScreenSize;
	vec4 solidDepth = projMatInv * vec4(screenPos * 2.0 - 1.0, texture(DepthBuffer, screenPos).r * 2.0 - 1.0, 1.0);
	solidDepth.xyz /= solidDepth.w;
	color.a *= clamp((viewSpacePos.z - solidDepth.z + Offset / 2.0) / Fade, 0.0, 1.0);

	color *= ColorModulator;

	if (color.a * 2.0 < AlphaCutoff) {
		discard;
	}

	fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}