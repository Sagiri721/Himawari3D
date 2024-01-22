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
    public static Mat4 projectionMatrix = new Mat4(1f);

    // List of meshes to display
    public static List<Mesh> renderQueue = new ArrayList<Mesh>();

    public static void LoadMatrixInformation(){
     
        projectionMatrix.Set(3, 3, 0);
        projectionMatrix.Set(0, 3, 1);
        projectionMatrix.Set(1, 3, 2);
        projectionMatrix.Set(3, 3, 3);
    }

    public static void Render(SDL_Renderer renderer){

        // Draw the clear color
        SDL_SetRenderDrawColor(renderer, (byte)0, (byte)0, (byte)0, (byte)255);
        SDL_RenderClear(renderer);

        SDL_SetRenderDrawColor(renderer, (byte)255, (byte)255, (byte)255, (byte)255);

        for (Mesh mesh : renderQueue) {
            
            // For each face of the mesh draw it's triangles
            Vec3[][] faces = mesh.faces;
            BufferFaceTriangles(faces, mesh, renderer);
        }

        // Render the current frame
        SDL_RenderPresent(renderer);
    }

    private static void BufferFaceTriangles(Vec3[][] faces, Mesh mesh, SDL_Renderer renderer){

        for (int i = 0; i < faces.length; i++) {

            Vec3[][] triangle = UnlinkTriangleFaceVertices(faces[i], mesh);

            for (int j = 0; j < triangle.length; j++) {
                
                DrawTriangle(
                    renderer, 
                    triangle[j][0],
                    triangle[j][1],
                    triangle[j][2]
                );
            }
        }
    }

    // From the face vertex links, turn them to the cached vertex triangles
    private static Vec3[][] UnlinkTriangleFaceVertices(Vec3[] linkedface, Mesh origin){

        return new Vec3[][] {
            new Vec3[] {origin.vertices[(int) linkedface[0].x - 1], origin.vertices[(int) linkedface[0].y - 1], origin.vertices[(int) linkedface[0].z - 1]},
            new Vec3[] {origin.vertices[(int) linkedface[1].x - 1], origin.vertices[(int) linkedface[1].y - 1], origin.vertices[(int) linkedface[1].z - 1]},
            new Vec3[] {origin.vertices[(int) linkedface[2].x - 1], origin.vertices[(int) linkedface[2].y - 1], origin.vertices[(int) linkedface[2].z - 1]}
        };
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
