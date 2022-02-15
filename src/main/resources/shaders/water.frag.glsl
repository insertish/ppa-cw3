#version 330 core

in vec2 fragUV;

out vec4 color;

uniform sampler2D texSampler;

void main() {
    color = texture(texSampler, fragUV);
    color.a = 0.4;
}