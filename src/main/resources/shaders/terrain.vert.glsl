#[glsl_version]
#include "lighting.header.vert"

layout (location = 0) in vec3 vertexPos;
layout (location = 1) in vec3 vertexColour;
layout (location = 3) in vec2 vertexUV;

uniform mat4 model;
uniform mat4 modelViewProjection;

out vec3 fragColour;
out vec2 fragUV;

void main() {
    gl_Position = modelViewProjection * vec4(vertexPos, 1.0);
    
    fragColour = vertexColour;
    fragUV = vertexUV * 8 * 2;

    #include "lighting.body.vert"
}
