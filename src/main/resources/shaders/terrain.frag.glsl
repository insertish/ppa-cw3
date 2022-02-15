#version 330 core

in vec3 fragColour;
in float yCoord;

out vec4 color;

uniform sampler2D texSampler;

void main() {
    float v = (yCoord / 20.0) + 0.5;
    color = vec4(fragColour, 1.0);
}
