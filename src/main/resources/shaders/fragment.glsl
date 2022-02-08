#version 330 core

in vec2 fragUV;

out vec4 color;

uniform sampler2D texSampler;

void main() {
    color = texture(texSampler, fragUV);
    // color = vec4(1.0, 0.0, 0.0, 1.0);
}
