#version 330 core

out vec4 color;

#include "lighting.frag"

uniform sampler2D texSampler;

void main() {
    color = lighting(vec4(1.0, 0.0, 0.0, 1.0));
}
