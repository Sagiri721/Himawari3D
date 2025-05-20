#version 330 core
 
// Input data
in vec4 vertexColor;
in vec3 vertexNormal;
in vec3 vertexPos;

// A light that illuminates all triangles in facing a certain direction
struct DirectionalLight {
    vec3 direction;
    float intensity;
};

// A light tha starts on a point and attenuates radially
struct PointLight {
    vec3 position;
    float intensity;

    float constant; // Should be 1, safety net to make sure denominator doesnt fall lower than one
    float linear; // Controls rate of attenuation
    float quadratic; // Quadractic decrease of intensity
};

// Scene casters
DirectionalLight lightDir = DirectionalLight(vec3(0.2, 0.3, 1.0), 0.8);
PointLight lights[1] = PointLight[](PointLight(vec3(0,0,1), 1, 1.0, 1.0, 1.8));
float ambientLighting = 0.2;

float calculateAttenuation(PointLight light, float distance) {
    return 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
}

// 0 = colors
// 1 = normals
const int renderTarget = 0;

void main()
{
    // Important vectors
    vec3 normal = normalize(vertexNormal);
    vec3 lightDirection = normalize(-lightDir.direction);

    vec4 finalColor;
    
    if (renderTarget == 1) {
        // Visualize normals as colors
        finalColor = vec4((normal + 1.0) / 2.0, 1.0);
    }
    else {
        
        // ######################### Directional lighting
        float diffuse = max(dot(normal, lightDirection), 0.0);

        // ######################### Point lighting
        // PointLight pl = lights[0];
        // vec3 to_light = pl.position - vertexPos;
        // float distance = length(to_light);

        // float attenuation = calculateAttenuation(lights[0], distance);

        // ######################### Final light calculation
        // Add ambient lighting to prevent complete darkness
        float lighting = /*attenuation * */(ambientLighting + (diffuse * lightDir.intensity));

        finalColor = vertexColor * lighting;
            
        // Normalize final color
        finalColor.xyzw /= 255;
        finalColor.w = 1;
    }

    gl_FragColor = finalColor;
}