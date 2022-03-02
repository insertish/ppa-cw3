#[glsl_version]
#include "lighting.header.vert"

layout (location = 0) in vec3 vertexPos;
layout (location = 1) in vec2 vertexUV;
layout (location = 3) in float vertexDepth;

uniform float time;

uniform float waterWaveHeight;
uniform float waterWaveFrequency;
uniform float waterWaveSpeed;
uniform float waterDisplacementModifier;
uniform float waterRandomDisplacement;

uniform mat4 model;
uniform mat4 modelViewProjection;

out vec2 fragUV;
out float fragDepth;

void main() {
    vec3 pos = vec3(vertexPos);
    pos.y += abs(
        sin(
            (
                pos.x
                    - cos(pos.z - waterRandomDisplacement) * waterDisplacementModifier
                    - time * waterWaveSpeed
            )
            * waterWaveFrequency
        )
    ) * waterWaveHeight;

    gl_Position = modelViewProjection * vec4(pos, 1.0);

    fragUV = vec2(vertexUV.x + 0.01 * sin(time), vertexUV.y + 0.01 * sin(time));
    fragDepth = vertexDepth;

    #include "lighting.body.vert"
}
