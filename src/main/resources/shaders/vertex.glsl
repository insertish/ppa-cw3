#version 330 core
layout (location = 0) in vec3 vertexPos;
layout (location = 1) in vec2 vertexUV;

uniform mat4 viewProjection;

out vec2 fragUV;

void main() {
    gl_Position = viewProjection * vec4(vertexPos, 1.0);
    fragUV = vertexUV;
}
