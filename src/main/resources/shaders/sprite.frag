#version 330 core

#define MAX_TEXTURES 8

in vec2 v_texCoords;
flat in int v_texIndex;

uniform sampler2D u_textures[MAX_TEXTURES];

layout(location = 0) out vec4 f_color;

void main() {
    f_color = texture(u_textures[v_texIndex], v_texCoords);
}