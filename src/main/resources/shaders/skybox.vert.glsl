#version 330 core
layout (location = 0) in vec3 vertexPos;

out vec3 texCoords;

uniform mat4 modelViewProjection;

void main() {
    texCoords = vertexPos;
    gl_Position = modelViewProjection * vec4(vertexPos, 1.0);
}
