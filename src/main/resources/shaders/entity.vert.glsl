#version 330 core
#include "lighting.header.vert"

layout (location = 0) in vec3 vertexPos;
layout (location = 1) in vec2 vertexUV;
layout (location = 3) in mat4 model;
//layout (location = 4) in mat4 model;
// layout (location = 4) in vec4 a;

uniform mat4 viewProjection;

out vec2 fragUV;

void main() {
    gl_Position = viewProjection * model * vec4(vertexPos, 1.0);
    fragUV = vertexUV;

    #include "lighting.body.vert"
}
