#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
	color.rgb *= color.a;
	color.a = (color.r + color.g + color.b) / 3.0;

	color *= vertexColor;

	color.rgb *= 2.5;
	color.a /= 2.5;

	if (color.r < vertexColor.r || color.g < vertexColor.g || color.b < vertexColor.b) {
		color.rgb = vertexColor.rgb;
	}

	color *= ColorModulator;

    if (color.a < 0.04) {
        discard;
    }

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}