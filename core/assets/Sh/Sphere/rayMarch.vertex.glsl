#define MAX_DIST  100
#define MIN_DIST  0.1
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

struct s{
 vec3 v;
};

attribute vec4 a_position;//position (x,y,0,0) in display
attribute vec3 a_normal;//this is direction ray

uniform Sphere u_sphere [10];
uniform int u_num_sphere;
uniform Light u_light;
uniform vec3 u_camPos;

varying vec4 v_color;
varying s ss;

float distS(vec3 p,float r){
    return length(p)-r;
}
float distC(vec3 p,float r){
    return max(abs(p.x),max(abs(p.y),abs(p.z)))-r/2;
}

float dist(vec3 p,float r){
    return distS(p,r);
}

float sceneSFD(vec3 point){
    float res=MAX_DIST;
    for (int i=0;i<u_num_sphere;i++){
        float dist=dist(point-u_sphere[i].pos,u_sphere[i].r);
        if (res>dist)res=dist;
    }
    return res;
}

vec4 sceneColor(vec3 point){
    float res=MIN_DIST;
    //int n=0;
    vec4 color=vec4(0., 0., 0., 1);
    for (int i=0;i<u_num_sphere;i++){
        float dist=dist(point-u_sphere[i].pos,u_sphere[i].r);
        if (dist<res){
            res=dist;
            color=u_sphere[i].color;
        }
    }
    //if (n>0)color/=n;
    return color;
}

float lenWay(vec3 start,vec3 dir){
    float lenWay = 0;
    for (int i=0;i<MAX_STEP;i++){
        float dist = sceneSFD(start);
        lenWay+=dist;
        if (dist<MIN_DIST)return lenWay;
        if (lenWay>MAX_DIST)return lenWay;
        start+=dist*dir;
    }
    return lenWay;
}

vec3 getNormal(vec3 pos){
    float epsilon=MIN_DIST;
    float distX=sceneSFD(pos+vec3(epsilon,0,0))-sceneSFD(pos-vec3(epsilon,0,0));
    float distY=sceneSFD(pos+vec3(0,epsilon,0))-sceneSFD(pos-vec3(0,epsilon,0));
    float distZ=sceneSFD(pos+vec3(0,0,epsilon))-sceneSFD(pos-vec3(0,0,epsilon));
    return normalize(vec3(distX,distY,distZ));
}

//pos - позиция точки в пространстве, в цвет которой будет закрашен пиксель
//pos  - позиция камеры
vec4 getColor(vec3 pos,vec3  camPos){
    vec3 N = getNormal(pos);
    vec3 L = normalize(u_light.pos-pos);
    vec3 V = normalize(camPos-pos);
    vec3 R = normalize(reflect(-L,N));
    float dotLN = dot(L, N);
    float dotRV = dot(R, V);
    vec4 ambient =sceneColor(pos);
    vec4 diffuse = dotLN* ambient;
    vec4 spec = u_light.color * pow( max( dotRV, 0. ),1);
    vec3 T = normalize(-V+2.0*dot(V,N)*N);
    vec4 reflection=sceneColor(pos+(lenWay(pos+T,T)+1)*T);
    return ambient + diffuse + spec  + reflection/2;
}

void main(){
    vec3 nor=normalize(a_normal);
    vec3 pos = u_camPos;
    vec4 color=vec4(0,0,0,0);
    float len=lenWay(pos,nor);
    if (len>MAX_DIST)color=vec4(0.47,0.47,0.47,1);
    else color=getColor(pos+len*nor,u_camPos);
    color[3]=1;
    v_color=color;
    gl_Position = a_position;
    ss;
}