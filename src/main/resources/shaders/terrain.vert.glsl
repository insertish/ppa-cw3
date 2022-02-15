#version 330 core
layout (location = 0) in vec3 vertexPos;
layout (location = 1) in vec3 vertexColour;
layout (location = 2) in vec3 vertexNormal;

uniform mat4 model;
uniform mat4 modelViewProjection;

out vec3 fragPosition;
out vec3 fragColour;
out vec3 fragNormal;
out float yCoord;

void main() {
    gl_Position = modelViewProjection * vec4(vertexPos, 1.0);
    
    fragPosition = vec3(model * vec4(vertexPos, 1.0));
    fragColour = vertexColour;
    fragNormal = vertexNormal;
    
    yCoord = vertexPos.y;
}
