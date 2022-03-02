#[glsl_version]
#include "lighting.frag"

out vec4 color;

void main() {
    color = lighting(vec4(fragNormal, 1));
}
