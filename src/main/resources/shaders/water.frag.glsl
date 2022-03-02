#[glsl_version]
#include "lighting.frag"

in vec2 fragUV;
in float fragDepth;

out vec4 color;

uniform float waterHeight;
uniform float waterFadeUnits;
uniform float waterTransparency;
uniform sampler2D texSampler;

void main() {
    color = lighting(texture(texSampler, fragUV));
    color.a = min(waterTransparency,
        (waterHeight - fragDepth) / waterFadeUnits);
}
