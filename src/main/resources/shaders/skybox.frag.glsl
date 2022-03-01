#version 330 core
in vec3 texCoords;

out vec4 color;

uniform samplerCube skybox;
uniform vec3 skyColour;

void main() {
    color = vec4(skyColour, 1.0) * texture(skybox, texCoords);
}
