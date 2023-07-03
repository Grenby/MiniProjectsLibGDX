#version 120


uniform float u_amplitude;
uniform float u_frequency;
uniform int u_octaves;
uniform int[512] u_transitions;
uniform float u_cellSize;
uniform vec2[256] u_directions;


void main() {
    gl_FragColor = vec4(u_amplitude,0.5,1,1);
}
