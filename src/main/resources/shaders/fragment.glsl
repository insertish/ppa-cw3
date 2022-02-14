#version 330 core

in vec2 fragUV;
in float yCoord;

out vec4 color;

uniform sampler2D texSampler;
// uniform vec3 deez;

void main() {
    // color = vec4(deez, 1.0);
    // color = texture(texSampler, fragUV);// * vec4(deez, 1.0);
    float v = (yCoord / 20.0) + 0.5;
    color = vec4(v, v, v, 1.0);
}
