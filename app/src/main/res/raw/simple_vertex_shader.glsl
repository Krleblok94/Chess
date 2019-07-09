#version 120

uniform mat4 u_Matrix;
attribute vec4 a_Position;
attribute vec3 a_Normals;
uniform vec4 u_Color;
varying vec4 v_Color;
uniform vec3 u_VectorToLight;
uniform float u_Ambient;

void main(){
    float diffuse = max(dot(a_Normals, u_VectorToLight), 0.0);
    v_Color = (u_Color * diffuse)*0.5 + u_Ambient;
    gl_Position = u_Matrix * a_Position;
}
