#version 330 core
layout (location = 0) in vec3 vertexPos;
layout (location = 2) in vec3 vertexNormal;

out vec3 fragNormal;

uniform mat4 modelViewProjection;

void main() {
    gl_Position = modelViewProjection * vec4(vertexPos, 1.0);
    fragNormal = vertexNormal;
}
