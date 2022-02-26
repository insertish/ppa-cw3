#version 330 core
layout (location = 0) in vec3 vertexPos;
layout (location = 1) in vec2 vertexUV;

uniform mat4 model;
uniform mat4 modelViewProjection;

uniform int atlas;
uniform int atlasX;
uniform int atlasY;

out vec2 fragUV;

void main() {
    gl_Position = modelViewProjection * vec4(vertexPos, 1.0);
    fragUV = vec2(vertexUV.x + atlasX, vertexUV.y + atlasY) / atlas;
}
