#version 330 core
#include "lighting.frag"

in vec2 fragUV;

out vec4 color;

uniform sampler2D texSampler;

void main() {
    color = vec4(1.0, 0.0, 0.0, 1.0);
    // color = lighting(texture(texSampler, fragUV));
}
