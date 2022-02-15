#version 330 core
layout (location = 0) in vec3 vertexPos;
layout (location = 1) in vec3 vertexColour;

uniform mat4 modelViewProjection;

out vec3 fragColour;
out float yCoord;

void main() {
    gl_Position = modelViewProjection * vec4(vertexPos, 1.0);
    fragColour = vertexColour;
    yCoord = vertexPos.y;
}
