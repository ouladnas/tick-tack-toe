#version 330 core

layout (location = 0) in vec2 a_position;
layout (location = 1) in vec2 a_texCoords;
layout (location = 2) in float a_texIndex;

flat out int v_texIndex;
out vec2 v_texCoords;

uniform mat4 u_proj;

void main() {
    gl_Position = u_proj * vec4(a_position, 0.0, 1.0);
    v_texCoords = a_texCoords.xy;
    v_texIndex = int(a_texIndex);
}