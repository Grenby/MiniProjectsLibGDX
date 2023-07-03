#version 120

attribute vec3 a_position;

uniform mat4 u_projection;

varying vec2 v_pos;

void main() {
    v_pos = (u_projection*vec4(a_position,1)).xy;
    gl_Position = vec4(a_position,1);
}
