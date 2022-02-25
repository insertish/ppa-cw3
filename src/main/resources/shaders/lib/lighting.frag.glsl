in vec3 fragPosition;
in vec3 fragNormal;

struct Light {
    vec4 direction;
  
    vec3 ambient;
    vec3 diffuse;
};

uniform Light light;

vec4 lighting(vec4 objectColour) {
    // Ambient
    vec3 ambient = light.ambient;

    // Diffuse
    vec3 norm = normalize(fragNormal);

    vec3 lightDir;
    if (light.direction.w == 0.0) {
        lightDir = normalize(light.direction.xyz - fragPosition);
    } else {
        lightDir = normalize(-light.direction.xyz);
    }

    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * light.diffuse;

    return vec4(ambient + diffuse, 1.0) * objectColour;
}
