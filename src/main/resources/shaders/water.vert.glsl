#version 330 core
layout (location = 0) in vec3 vertexPos;
layout (location = 1) in vec2 vertexUV;

uniform float time;
uniform mat4 modelViewProjection;

out vec2 fragUV;

void main() {
    vec3 pos = vec3(vertexPos);
    pos.y += abs(sin((pos.x + time / 10.0) * 32))
        * abs(cos((pos.y + time / 10.0) * 32))
        * sin(time);

    gl_Position = modelViewProjection * vec4(pos, 1.0);

    fragUV = vec2(vertexUV.x + 0.01 * sin(time), vertexUV.y + 0.01 * sin(time));
}
