#version 330 core
out vec4 FragColor;

in vec4 TriangleColor;
in vec4 AlternativeColor;
in vec4 VertexPostion;

in vec3 AccessibleColor;

uniform vec4 VertexColor;

void main() {
    FragColor = VertexColor;
}