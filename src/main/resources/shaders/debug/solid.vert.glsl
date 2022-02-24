#version 330 core
#include "lighting.header.vert"

layout (location = 0) in vec3 vertexPos;

uniform mat4 modelViewProjection;

void main() {
    gl_Position = modelViewProjection * vec4(vertexPos, 1.0);
    
    #include "lighting.body.vert"
}
