// World View Projection matrix that will transform the input vertices
// to screen space.
attribute vec4 position;
attribute vec4 color;

uniform mat4 world;
uniform mat4 view;
uniform mat4 projection;
varying vec4 v_color;

/**
 * The vertex shader simply transforms the input vertices to screen space.
 */
void main() {
    // Multiply the vertex positions by the worldViewProjection matrix to
    // transform them to screen space.
    v_color.x = color.x/2;
    v_color.y = color.y/2;
    v_color.z = color.z/2;
    v_color.w =1;
    gl_Position = projection * view * world * position;
}