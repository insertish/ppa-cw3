#[glsl_version]
#include "lighting.frag"

in vec2 fragUV;

out vec4 color;

uniform sampler2D texSampler;

void main() {
    color = lighting(texture(texSampler, fragUV));
}
