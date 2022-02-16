#version 330 core

in vec3 fragPosition;
in vec3 fragColour;
in vec3 fragNormal;
in vec2 fragUV;

out vec4 color;

uniform sampler2D texSampler;

uniform vec3 lightPos;

vec4 lighting(vec4 objectColour) {
    float ambientStrength = 0.3;
    vec3 ambientColour = vec3(1.0, 1.0, 1.0);

    float diffuseStrength = 0.7;
    vec3 diffuseColour = vec3(1.0, 1.0, 1.0);
    // vec3 lightPos = vec3(32.0, 64.0, 32.0);

    // Ambient
    vec3 ambient = ambientStrength * ambientColour;

    // Diffuse
    vec3 norm = normalize(fragNormal);
    vec3 lightDir = normalize(lightPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * diffuseColour;

    return vec4(ambient + diffuse, 1.0) * objectColour;
}

void main() {
    color = lighting(texture(texSampler, fragUV) * vec4(fragColour, 1.0));
}
