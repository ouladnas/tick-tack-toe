#version 330 core

#define MAX_TEXTURES 8

in vec2 v_texCoords;
flat in int v_texIndex;

uniform sampler2D u_textures[MAX_TEXTURES];

layout(location = 0) out vec4 f_color;

vec4 TEXTURE();

void main() {
    f_color = TEXTURE();
//    f_color = vec4(1.0f, 0.0f, 1.0f, 1.0f);
}