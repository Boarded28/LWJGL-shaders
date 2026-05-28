#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aColor;

out vec4 TriangleColor;
out vec4 AlternativeColor;
out vec4 VertexPostion;

out vec3 AccessibleColor;

uniform vec3 uPos;

void main() {
    gl_Position = vec4(aPos + uPos, 1.0f);
    VertexPostion = vec4(aPos, 1.0f);
    TriangleColor = vec4(0.780f, 0.173f, 0.255f, 1.0f); // crimson red
    AlternativeColor = vec4(1.000f, 0.200f, 0.267f, 1.0f); // bright scarlet    
    AccessibleColor = aColor;
}