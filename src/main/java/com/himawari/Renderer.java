package com.himawari;

import static io.github.libsdl4j.api.render.SdlRender.SDL_RenderClear;
import static io.github.libsdl4j.api.render.SdlRender.SDL_RenderDrawLine;
import static io.github.libsdl4j.api.render.SdlRender.SDL_RenderGeometry;
import static io.github.libsdl4j.api.render.SdlRender.SDL_RenderPresent;
import static io.github.libsdl4j.api.render.SdlRender.SDL_SetRenderDrawColor;

import java.util.ArrayList;
import java.util.List;

import com.himawari.geom.Mat4;
import com.himawari.geom.Vec3;
import com.himawari.geom.Vec4;

import io.github.libsdl4j.api.render.SDL_Renderer;
import io.github.libsdl4j.api.render.SDL_Vertex;

public class Renderer {
    
    // Matrices
    public static Mat4 projectionMatrix = new Mat4(0f);
    
    public static Mat4 rotationX = new Mat4(0f);
    public static Mat4 rotationY = new Mat4(0f);
    public static Mat4 rotationZ = new Mat4(0f);

    // List of meshes to display
    public static List<Mesh> renderQueue = new ArrayList<Mesh>();

    public static float zNear = 0.1f, zFar = 1000;
    public static float fov = 90f;

    public static void LoadMatrixInformation(){

        float fovFunction = 1f / (float) Math.tan(fov * 0.5f / 180f * Math.PI);
     
        projectionMatrix.Set(0, 0, Window.aspectRatio * fovFunction);
        projectionMatrix.Set(1, 1, fovFunction);
        projectionMatrix.Set(2, 2, zFar / (zFar - zNear));
        projectionMatrix.Set(3, 2, -(zFar * zNear) / (zFar - zNear));
        projectionMatrix.Set(2, 3, 1);
    }

    // TODO: Optimize for each axis
    public static void ProjectRotationMatricesToAngle(Vec3 angle){

        // Rotation Z
		rotationZ.Set(0, 0, (float) Math.cos(angle.z));
        rotationZ.Set(0, 1, (float) Math.sin(angle.z));
        rotationZ.Set(1, 0, (float) -Math.sin(angle.z));
        rotationZ.Set(1, 1, (float) Math.cos(angle.z));
        rotationZ.Set(2, 2, 1);
        rotationZ.Set(3, 3, 1);

		// Rotation X
		rotationX.Set(0, 0, 1);
		rotationX.Set(1, 1, (float) Math.cos(angle.x));
		rotationX.Set(1, 2, (float) Math.sin(angle.x));
        rotationX.Set(2, 1, (float) -Math.sin(angle.x));
        rotationX.Set(2, 2, (float) Math.cos(angle.x));
        rotationX.Set(3, 3, 1);

        // Rotation Y
        rotationY.Set(0, 0, (float) Math.cos(angle.y));
        rotationY.Set(0, 2, (float) Math.sin(angle.y));
        rotationY.Set(2, 0, (float) -Math.sin(angle.y));
        rotationY.Set(1, 1, 1);
        rotationY.Set(2, 2, (float) Math.cos(angle.y));
        rotationY.Set(3, 3, 1);
    }

    public static void Render(SDL_Renderer renderer){

        // Draw the clear color
        SDL_SetRenderDrawColor(renderer, (byte)0, (byte)0, (byte)0, (byte)255);
        SDL_RenderClear(renderer);

        SDL_SetRenderDrawColor(renderer, (byte)255, (byte)255, (byte)255, (byte)255);

        for (Mesh mesh : renderQueue) {
            
            // Before buffering the renderer
            // Treat and cache the vertices after all projection operations

            Vec3[] trianglePool = new Vec3[mesh.vertices.length];
            int index = 0;

            for (Vec3 vertex : mesh.vertices) {

                // Copy the current value
                trianglePool[index] = vertex.copy();

                // Apply rotations
                ProjectRotationMatricesToAngle(mesh.rotation);
                trianglePool[index] = Utils.MultiplyMatrixVector(trianglePool[index], rotationZ);
                trianglePool[index] = Utils.MultiplyMatrixVector(trianglePool[index], rotationX);
                trianglePool[index] = Utils.MultiplyMatrixVector(trianglePool[index], rotationY);

                // Apply local transformations
                trianglePool[index].sum(mesh.position);
                trianglePool[index].dot(mesh.scale);

                // Apply projection
                trianglePool[index] = Utils.MultiplyMatrixVector(trianglePool[index], projectionMatrix);

                // Scale to normalized viewport
                float scaleOffseting = 1f;
                
                float scalingX = 0.5f * Window.width;
                float scalingY = 0.5f * Window.height;

                trianglePool[index].x += scaleOffseting;
                trianglePool[index].y += scaleOffseting;

                trianglePool[index].x *= scalingX;
                trianglePool[index].y *= scalingY;

                index++;
            }

            // Buffer rendering
            // For each face of the mesh draw it's triangles
            int[][] faces = mesh.faces;
            BufferFaceTriangles(faces, trianglePool, renderer);
        }

        // Render the current frame
        SDL_RenderPresent(renderer);
    }

    private static void BufferFaceTriangles(int[][] faces, Vec3[] vertices, SDL_Renderer renderer){

        for (int i = 0; i < faces.length; i++) {

            Vec3[] triangle = UnlinkTriangleFaceVertices(faces[i], vertices);

            DrawTriangle(
                renderer, 
                triangle[0],
                triangle[1],
                triangle[2]
            );
        }
    }

    // From the face vertex links, turn them to the cached vertex triangles
    private static Vec3[] UnlinkTriangleFaceVertices(int[] linkedface, Vec3[] vertices){

        return new Vec3[]{vertices[(int) linkedface[0]], vertices[(int) linkedface[1]], vertices[(int) linkedface[2]]};
    }

    // Draw a triangle to the screen
    private static void DrawTriangle(SDL_Renderer renderer, Vec3 point1, Vec3 point2, Vec3 point3){

        SDL_RenderDrawLine(renderer, (int) point1.x, (int) point1.y, (int) point2.x, (int) point2.y);
        SDL_RenderDrawLine(renderer, (int) point2.x, (int) point2.y, (int) point3.x, (int) point3.y);
        SDL_RenderDrawLine(renderer, (int) point3.x, (int) point3.y, (int) point1.x, (int) point1.y);
    }

    // Fill the triangle's geometry
    private static void FillTriangle(SDL_Renderer renderer, Vec3 point1, Vec3 point2, Vec3 point3, Vec4 color){

        List<SDL_Vertex> vertsGeometry = new ArrayList<SDL_Vertex>();
        vertsGeometry.add(Utils.ToVertex(point1, color));
        vertsGeometry.add(Utils.ToVertex(point2, color));
        vertsGeometry.add(Utils.ToVertex(point3, color));

        SDL_RenderGeometry(renderer, null, vertsGeometry, null);
    }
}
