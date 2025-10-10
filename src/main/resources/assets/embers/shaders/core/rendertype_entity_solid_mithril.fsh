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
in mat4 modelViewMat;
in mat4 projMat;
in mat3 iViewRotMat;
in vec3 playerPos;

out vec4 fragColor;

void main() {
	vec4 normalMap = texture(Sampler0, texCoord0);

	if (normalMap.a <= 0.0) {
		discard;
	}

	normalMap = normalize(normalMap * 2.0 - 1.0);

	/*vec3 pos = (position + normal * 0.03125) * inverse(iViewRotMat) + mod(playerPos, 0.0625);

	pos -= mod(pos, 0.0625);

	pos = pos * iViewRotMat;*/

	vec3 e = normalize(position);//normalize(pos);
	vec3 n = normalize(normal + normalMap.rgb);
	vec3 r = reflect(e, n);
	float m = 2 * sqrt(
		pow(r.x, 2) +
		pow(r.y, 2) +
		pow(r.z + 1, 2)
	);
	vec2 texPos = vec2(r.x, -r.y) / m + 0.5;

	vec4 color = texture(Sampler3, texPos);
	color *= vertexColor * ColorModulator;
	color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
	color *= lightMapColor;
	color.a *= normalMap.a;
	fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
	
	
	//vec3 pos = position * inverse(iViewRotMat) + playerPos;
	
	//fragColor = vec4(mod(pos.x, 1.0), mod(pos.y, 1.0), mod(pos.z, 1.0), 1.0);
}