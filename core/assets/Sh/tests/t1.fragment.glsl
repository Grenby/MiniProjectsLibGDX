#ifdef GL_ES
precision mediump float;
#endif

#define lightPointFlag 1


varying vec3 v_normal;
varying vec3 v_pos;

uniform struct{
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
} u_mat;


uniform struct{
    vec3 pos;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    vec3 fading;
} u_lightPoints[lightPointFlag];

void main() {
    vec3 color = vec3(0,0,0);

    vec3 n = normalize(v_normal);
    vec3 viewDir = normalize(-v_pos);

    for (int i=0;i<lightPointFlag;i++){
        vec3 lightDir = normalize(u_lightPoints[i].pos - v_pos);
        float diff = max(dot(n, lightDir), 0.0);

        vec3 reflectDir = reflect(-lightDir, n);
        float spec = pow(max(dot(viewDir, reflectDir), 0.0), u_mat.shininess);

        vec3 ambient =  u_lightPoints[i].ambient * u_mat.ambient;
        vec3 diffuse = u_lightPoints[i].diffuse * (diff * u_mat.diffuse);
        vec3 specular = spec * u_lightPoints[i].specular*u_mat.specular;

        float dist = length(u_lightPoints[i].pos - v_pos);
        float k = 1.0/(u_lightPoints[i].fading.x + dist * u_lightPoints[i].fading.y + dist*dist*u_lightPoints[i].fading.z);

        color+=k*ambient;
        color+=k*diffuse;
        color+=k*specular;
    }
    vec3 pp = v_pos;
    normalize(pp);
    pp+=vec3(1);
    normalize(pp);
    gl_FragColor =vec4(pp,1);

}
