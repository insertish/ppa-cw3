#[glsl_version]
in vec2 fragUV;

out vec4 color;

uniform sampler2D texSampler;

void main() {
    color = vec4(1.0, 1.0, 1.0, texture(texSampler, fragUV).x);
}
