package com.himawari.Gfx;

import static io.github.libsdl4j.api.render.SdlRender.SDL_DestroyTexture;
import static io.github.libsdl4j.api.render.SdlRender.SDL_RenderClear;
import static io.github.libsdl4j.api.render.SdlRender.SDL_RenderCopy;
import static io.github.libsdl4j.api.render.SdlRender.SDL_RenderPresent;
import static io.github.libsdl4j.api.render.SdlRender.SDL_SetRenderDrawColor;

import java.util.ArrayList;
import java.util.List;

import com.himawari.Camera;
import com.himawari.Mesh;
import com.himawari.Utils;
import com.himawari.HLA.Vec3;

import io.github.libsdl4j.api.render.SDL_Renderer;
import io.github.libsdl4j.api.render.SDL_Texture;

public class Renderer {

    // List of meshes to display
    public static List<Mesh> renderQueue = new ArrayList<Mesh>();

    public static void Render(SDL_Renderer renderer){

        // Draw the clear color
        SDL_SetRenderDrawColor(renderer, (byte)0, (byte)0, (byte)0, (byte)255);
        SDL_RenderClear(renderer);
        
        // Clear buffers
        BackBuffer.ClearBackBuffer();
        ZBuffer.ClearDepthBuffer();

        SDL_SetRenderDrawColor(renderer, (byte)255, (byte)255, (byte)255, (byte)255);

        // Set camera position for this frame
        ApplyCameraProjection();

        // Loop through stored meshes and draw each of them
        for (Mesh mesh : renderQueue) {
            
            // Before buffering the renderer
            // Treat and cache the vertices after all projection operations
            Vec3[] trianglePool = ProjectVerticesFromMesh(mesh);

            // Buffer rendering
            // For each face of the mesh draw it's triangles
            int[][] faces = mesh.faces;
            BufferFaceTriangles(faces, trianglePool, mesh, renderer);
        }

        // Render the current frame
        
        SDL_Texture renderPixels = BackBuffer.FetchTexture(renderer);
        SDL_RenderCopy(renderer, renderPixels, null, null);
        SDL_RenderPresent(renderer);

        SDL_DestroyTexture(renderPixels);
    }

    // Efectuate the rotation of the points in 3D space
    private static Vec3[] ProjectVerticesFromMesh(Mesh mesh){

        Vec3[] trianglePool = new Vec3[mesh.vertices.length];
        int index = 0;

        for (Vec3 vertex : mesh.vertices) {

            // Copy the current value
            trianglePool[index] = vertex.copy();

            // Apply rotations
            Projection.ProjectRotationMatricesToAngle(mesh.rotation);
            trianglePool[index] = Utils.MultiplyMatrixVector(trianglePool[index], Projection.rotationZ);
            trianglePool[index] = Utils.MultiplyMatrixVector(trianglePool[index], Projection.rotationX);
            trianglePool[index] = Utils.MultiplyMatrixVector(trianglePool[index], Projection.rotationY);

            // Apply local transformations
            trianglePool[index].sum(mesh.position);
            trianglePool[index].dot(mesh.scale);

            trianglePool[index] = Utils.MultiplyMatrixVector(trianglePool[index], Projection.cameraView);

            index++;
        }

        return trianglePool;
    }

    private static void ApplyCameraProjection(){

        Vec3 target = Vec3.FORWARD.copy();
        Projection.ProjectRotationAlongYAxis(Camera.fYaw);

        Camera.lookDirection = Utils.MultiplyMatrixVector(target, Projection.rotationY);
        target = Camera.position.copy().sum(Camera.lookDirection);

        Utils.MatrixPointAt(Camera.position.copy(), target, Vec3.UP);
    }

    // Efectuate the projection of the points in 3D space
    private static Vec3[] ProjectTriangleToScreen(Vec3[] triangle){
        
        for (int j = 0; j < triangle.length; j++) {

            // Apply projection
            triangle[j] = Utils.MultiplyMatrixVector(triangle[j], Projection.projectionMatrix);
            
            // Scale to normalized viewport
            triangle[j].sum(Projection.normalizingTransformationPosition);
            triangle[j].dot(Projection.normalizingTransformationScale);
        }

        return triangle;
    }

    // Unlink the faces and buffer the rendering of the faces
    private static void BufferFaceTriangles(int[][] faces, Vec3[] vertices, Mesh mesh, SDL_Renderer renderer){

        for (int i = 0; i < faces.length; i++) {

            // 3D points that compose the trinagle face unlinked
            // Efectuate the projection
            Vec3[] triangle = UnlinkTriangleFaceVertices(faces[i], vertices);
            
            // Normal calculation
            // Visibility is dependant on normal calculations
            Vec3 normal = Utils.CalculateFaceNormal(triangle);
            
            // Invisible face
            // Skip projection and drawing
            Vec3 cameraRay = (triangle[0].copy().subtract(Camera.position.copy()));
            // float visionAngleDifference = Vec3.DotProduct(normal, cameraRay);
            // if (visionAngleDifference < 1) {
            //     // The surface is facing away from the camera, so continue processing
            //     // (or you may want to skip rendering depending on your needs)
            //     continue;
            // }

            // Calculate lighting conditions from normal
            Vec3 lightDirection = cameraRay.invert().normalized();
            float lightProduct = Vec3.DotProduct(normal, lightDirection);

            Color lightShade = Color.getLuminanceVariation(mesh.base, lightProduct);
            
            // Project the triangle points to screen space
            triangle = ProjectTriangleToScreen(triangle);

            // Buffer the face triangle
            FillTriangle(
                renderer, 
                triangle[0],
                triangle[1],
                triangle[2],
                lightShade
            );
        }
    }

    // From the face vertex links, turn them to the cached vertex triangles
    private static Vec3[] UnlinkTriangleFaceVertices(int[] linkedface, Vec3[] vertices){

        return new Vec3[]{vertices[(int) linkedface[0]], vertices[(int) linkedface[1]], vertices[(int) linkedface[2]]};
    }

    // Draw a triangle to the screen
    private static void DrawTriangle(SDL_Renderer renderer, Vec3 point1, Vec3 point2, Vec3 point3){

        BackBuffer.FillBufferLine(point1.x, point1.y, point2.x, point2.y, Utils.WHITE);
        BackBuffer.FillBufferLine(point2.x, point2.y, point3.x, point3.y, Utils.WHITE);
        BackBuffer.FillBufferLine(point3.x, point3.y, point1.x, point1.y, Utils.WHITE);
    }

    // Fill the triangle's geometry
    private static void FillTriangle(SDL_Renderer renderer, Vec3 point1, Vec3 point2, Vec3 point3, Color color){

        BackBuffer.FillBufferTriangle(point1, point2, point3, color);
    }
}
