#version 330 core

in vec2 fragUV;

out vec4 color;

uniform sampler2D texSampler;
// uniform vec3 deez;

void main() {
    // color = vec4(deez, 1.0);
    // color = texture(texSampler, fragUV);// * vec4(deez, 1.0);
    color = vec4(0.0, 0.0, 0.0, 1.0);
}
