#version 330 core
in vec2 fragUV;

out vec4 color;

uniform sampler2D texSampler;
uniform vec4 colour;

void main() {
    color = texture(texSampler, fragUV) * colour;
}
