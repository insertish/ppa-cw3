#version 430 core
#include "lighting.header.vert"

layout (location = 0) in vec3 vertexPos;
layout (location = 1) in vec2 vertexUV;
// layout (location = 3) in mat4 model;
//layout (location = 4) in mat4 model;
// layout (location = 4) in vec4 a;

uniform mat4 model;
uniform mat4 viewProjection;

layout(std430, binding = 0) buffer modelMatrices
{
    mat4 m[];
};

out vec2 fragUV;

void main() {
    vec3 v = vertexPos;
    v.y += gl_InstanceID * 2;
    gl_Position = viewProjection * m[gl_InstanceID] * vec4(v, 1.0);
    fragUV = vertexUV;

    #include "lighting.body.vert"
}
