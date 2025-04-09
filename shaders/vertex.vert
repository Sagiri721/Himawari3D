#version 330 core

// Attribs
layout(location = 0) in vec3 position;
layout(location = 1) in vec4 color;
layout(location = 2) in vec3 normal;

// Uniforms
uniform vec2 screenSize;

// Outputs to fragment
out vec4 vertexColor;
out vec3 vertexNormal; // face for now
out vec3 vertexPos;

void main() {

    // Normalize screen space coords to open gl normal coords
    vec4 normalPosition = vec4((position.x / screenSize.x) * 2 - 1, (position.y / screenSize.y) * -2 + 1, position.z, 1.0);

    gl_Position = normalPosition;
    vertexColor = color; 
    vertexNormal = normal;
    vertexPos = gl_Position.xyz;
}