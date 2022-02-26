#version 430 core
#include "lighting.header.vert"

layout (location = 0) in vec3 vertexPos;
layout (location = 1) in vec2 vertexUV;

uniform mat4 viewProjection;

layout(std430, binding = 0) buffer modelMatrices
{
    mat4 model[];
};

out vec2 fragUV;

void main() {
    gl_Position = viewProjection * model[gl_InstanceID] * vec4(vertexPos, 1.0);
    fragUV = vertexUV;

    #include "lighting.body.instanced.vert"
}
