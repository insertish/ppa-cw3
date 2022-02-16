#version 330 core
layout (location = 0) in vec3 vertexPos;
layout (location = 1) in vec3 vertexColour;
layout (location = 2) in vec3 vertexNormal;
layout (location = 3) in vec2 vertexUV;

uniform mat4 model;
uniform mat4 modelViewProjection;

out vec3 fragPosition;
out vec3 fragColour;
out vec2 fragUV;
out vec3 fragNormal;

void main() {
    gl_Position = modelViewProjection * vec4(vertexPos, 1.0);
    
    fragPosition = vec3(model * vec4(vertexPos, 1.0));
    fragColour = vertexColour;
    fragNormal = vertexNormal;
    fragUV = vertexUV * 8 * 2;
}
