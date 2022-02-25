#version 330 core
#include "lighting.frag"

in vec3 fragColour;
in vec2 fragUV;

out vec4 color;

uniform sampler2D texSampler;

void main() {
    color = lighting(texture(texSampler, fragUV) * vec4(fragColour, 1.0));
}
