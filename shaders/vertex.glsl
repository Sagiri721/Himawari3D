#version 330 core

// Attribs
layout(location = 0) in vec3 position;
layout(location = 1) in vec4 color;

// Uniforms
uniform vec2 screenSize;

// Colour of vertex
out vec4 vertexColor;

void main() {

    vec4 normal_position = vec4((position.x / screenSize.x) * 2 - 1, (position.y / screenSize.y) * -2 + 1, position.z, 1.0);

    gl_Position = normal_position; 
    vertexColor = color; 
}