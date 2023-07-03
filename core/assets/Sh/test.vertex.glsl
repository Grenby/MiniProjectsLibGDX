#define MAX_DIST  1000
#define MIN_DIST  0.01
#define MAX_STEP  100

struct Light{
    vec3 pos;
    vec4 color;
};

struct Sphere{
    float r;
    vec3 pos;
    vec4 color;
};

attribute vec4 a_position;
attribute vec3 a_normal;//this is direction ray

uniform Sphere u_sphere [11];
uniform int u_num_sphere;
uniform vec3 u_camPos;
uniform Light u_light;

varying vec4 v_color;

float sphereSDF(vec3 z,float sh){
    float Scale=1.8;
    float Offset=2.9;
    float r;
    int n = 0;
    while (n < 20) {
        if(z.x+z.y<0) z.xy = -z.yx; // fold 1
        if(z.x+z.z<0) z.xz = -z.zx; // fold 2
        if(z.y+z.z<0) z.zy = -z.yz; // fold 3
        z = z*Scale - Offset*(Scale-1.0);
        n++;
        //Scale-=0.1;
    }
    return (length(z) ) * pow(Scale, -float(n));
   // return length(p)-r;
}

vec3 normalSphere(vec3 p,float rSphere){
    float epsilon = 0.001; // arbitrary — should be smaller than any surface detail in your distance function, but not so small as to get lost in float precision
    float centerDistance = sphereSDF(p,rSphere);
    float xDistance = sphereSDF(p + vec3(epsilon, 0, 0),rSphere);
    float yDistance = sphereSDF(p + vec3(0, epsilon, 0),rSphere);
    float zDistance = sphereSDF(p + vec3(0, 0, epsilon),rSphere);
    return normalize((vec3(xDistance, yDistance, zDistance) - centerDistance));
}

void main(){
    vec3 normal;
    vec3 direction=normalize(a_normal);
    vec4 color=vec4(0,0,0,0);

    float deth=0;
    float dist=0;

    for (int i=0;i<MAX_STEP;i++){
        dist=MAX_DIST+1;
        for(int j=0;j<u_num_sphere;j++){
            float dopD=sphereSDF(u_camPos+deth*direction-u_sphere[j].pos,u_sphere[j].r);
            if (dopD<dist){
                dist=dopD;
                color=u_sphere[j].color;
            }
        }
        deth+=dist;
        if (dist<MIN_DIST)break;
        color=vec4(0.47,0.47,0.47,1);
        if (deth>MAX_DIST)break;
    }

    if (dist<MIN_DIST){
        vec3 N=normalSphere(u_camPos+deth*direction-u_sphere[0].pos,u_sphere[0].r);
        vec3 L=normalize(u_camPos+deth*direction-u_sphere[0].pos-u_light.pos);
        float dotLN = dot(L, N);
        if (dotLN < 0.0) {
            // Light not visible from this point on the surface
            color+=color*dotLN;
        }else{
            color+=u_light.color*dotLN;
        }
       // return lightIntensity * (k_d * dotLN + k_s * pow(dotRV, alpha));
    }
    v_color=color;
    gl_Position = a_position;
}