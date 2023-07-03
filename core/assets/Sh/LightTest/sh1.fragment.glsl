#ifdef GL_ES
precision mediump float;
#endif

struct plane{
    vec3 point;
    vec3 nor;
    vec4 color;
};

struct sphere{
    vec3 point;
    float r;
    vec4 color;
};



varying vec4 v_color;

void main(){
    gl_FragColor = v_color ;
}
