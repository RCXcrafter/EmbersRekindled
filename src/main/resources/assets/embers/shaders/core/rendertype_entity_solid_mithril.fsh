#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler3;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;
in vec3 normal;
in vec3 position;
in mat3 TBN;

out vec4 fragColor;

void main() {
	vec3 normalMap = texture(Sampler0, texCoord0).rgb;
	normalMap = normalize(normalMap * 2.0 - 1.0);

	vec2 texPos = normalize(vec2(TBN * (normalMap + normal)));

	float angle = acos(dot(normalize(-position), normalize(normalMap + normal))) / 2.5;
	texPos *= angle;
	texPos += vec2(0.5, 0.5);

	vec4 color = texture(Sampler3, texPos);
	color *= vertexColor * ColorModulator;
	color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    color *= lightMapColor;
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}