#ifdef GL_ES
precision mediump float;
#endif

struct s{
    vec3 v;
};

varying vec4 v_color;
varying s ss;

void main(){
    gl_FragColor = v_color ;
}