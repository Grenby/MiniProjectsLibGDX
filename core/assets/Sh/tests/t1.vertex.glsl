
attribute vec3 a_position;
attribute vec3 a_normal;

uniform mat4 u_projViewTrans;
uniform mat4 u_viewTrans;
uniform mat3 u_normalMatrix;
uniform mat4 u_worldTrans;

varying vec3 v_normal;
varying vec3 v_pos;


void main() {
    gl_Position =   u_projViewTrans * u_worldTrans * vec4(a_position,1);
    v_normal = u_normalMatrix * a_normal;
    v_pos = (u_viewTrans*u_worldTrans*vec4(a_position,1)).xyz;
}
