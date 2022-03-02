#[glsl_version]
layout (location = 0) in vec3 vertexPos;
layout (location = 1) in vec2 vertexUV;

uniform mat4 model;
uniform mat4 modelViewProjection;

out vec2 fragUV;

void main() {
    gl_Position = modelViewProjection * vec4(vertexPos, 1.0);
    fragUV = vertexUV;
}
