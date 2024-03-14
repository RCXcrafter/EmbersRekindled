#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler2;
uniform sampler2D Sampler3;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat3 IViewRotMat;
uniform int FogShape;

uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;

out float vertexDistance;
out vec4 vertexColor;
out vec4 lightMapColor;
out vec4 overlayColor;
out vec2 texCoord0;
out vec3 normal;
out vec3 position;
out mat3 TBN;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    vertexDistance = fog_distance(ModelViewMat, IViewRotMat * Position, FogShape);
    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, Color);
    lightMapColor = texelFetch(Sampler3, UV2 / 16, 0);
    overlayColor = texelFetch(Sampler2, UV1, 0);
    texCoord0 = UV0;

	vec3 N = normalize(-Position);
	vec3 T = normalize(cross(vec3(0, 1, 0), N));
	vec3 B = normalize(cross(T, N));
	TBN = transpose(mat3(T, B, N));

	normal = Normal;
	position = Position;
	lightMapColor = vec4(1, 1, 1, 1);
	overlayColor = vec4(1, 1, 1, 1);
}