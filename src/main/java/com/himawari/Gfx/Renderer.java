package com.himawari.Gfx;

import static io.github.libsdl4j.api.render.SdlRender.SDL_DestroyTexture;
import static io.github.libsdl4j.api.render.SdlRender.SDL_RenderClear;
import static io.github.libsdl4j.api.render.SdlRender.SDL_RenderCopy;
import static io.github.libsdl4j.api.render.SdlRender.SDL_RenderPresent;
import static io.github.libsdl4j.api.render.SdlRender.SDL_SetRenderDrawColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.himawari.Camera.Camera;
import com.himawari.Camera.Clipping;
import com.himawari.HLA.Triangle;
import com.himawari.HLA.Vec3;
import com.himawari.Recording.Recorder;
import com.himawari.Utils.Utils;
import com.himawari.Utils.Window;

import io.github.libsdl4j.api.render.SDL_Renderer;
import io.github.libsdl4j.api.log.SdlLog;
import io.github.libsdl4j.api.render.SDL_Texture;

public class Renderer {

    // List of meshes to display
    public static List<Mesh> renderQueue = new ArrayList<Mesh>();
    public static RenderMode renderMode = RenderMode.WIREFRAME;

    public static void Render(SDL_Renderer renderer){

        // Draw the clear color
        SDL_SetRenderDrawColor(renderer, (byte)0, (byte)0, (byte)0, (byte)255);
        SDL_RenderClear(renderer);
        
        // Clear buffers
        BackBuffer.ClearBackBuffer();
        ZBuffer.ClearDepthBuffer();

        SDL_SetRenderDrawColor(renderer, (byte)255, (byte)255, (byte)255, (byte)255);

        // Set camera position for this frame
        DefineCameraProjection();

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

        // Save the frane to a file
        //Recorder.saveFrame("f" + Window.ticks + ".bmp", renderPixels, renderer);

        SDL_DestroyTexture(renderPixels);
    }

    // Efectuate the rotation of the points in 3D space
    // Normals are a reference to the normals array list that is used later
    // Normals need to be calculated from the raw vertex data
    private static Vec3[] ProjectVerticesFromMesh(Mesh mesh){

        Vec3[] trianglePool = new Vec3[mesh.vertices.length];
        int index = 0;

        for (Vec3 vertex : mesh.vertices) {

            // Copy the current value
            trianglePool[index] = vertex.copy();

            // Apply rotations
            // Apply rotations
            Projection.ProjectRotationMatricesToAngle(mesh.transform.getRotation());
            trianglePool[index] = Utils.MultiplyMatrixVector(trianglePool[index], Projection.rotationX, false);
            trianglePool[index] = Utils.MultiplyMatrixVector(trianglePool[index], Projection.rotationZ, false);
            trianglePool[index] = Utils.MultiplyMatrixVector(trianglePool[index], Projection.rotationY, false);
            
            // Apply local transformations
            trianglePool[index] = Utils.MultiplyMatrixVector(trianglePool[index], Projection.MakeScale(mesh.transform.scale), false);
            trianglePool[index] = Utils.MultiplyMatrixVector(trianglePool[index], Projection.MakeTranslation(mesh.transform.position), false);

            trianglePool[index] = Utils.MultiplyMatrixVector(trianglePool[index], Projection.cameraView, false);

            index++;
        }

        return trianglePool;
    }

    private static void DefineCameraProjection(){

        Vec3 target = Vec3.FORWARD.copy();
        Projection.ProjectRotationAlongYAxis(Camera.fYaw);

        Camera.lookDirection = Utils.MultiplyMatrixVector(target, Projection.rotationY, false);
        target = Camera.position.copy().sum(Camera.lookDirection);

        Utils.MatrixPointAt(Camera.position.copy(), target, Vec3.UP);
    }

    // Efectuate the projection of the points in 3D space
    private static Triangle ProjectTriangleToScreen(Triangle triangle){

        for (int j = 0; j < 3; j++) {

            // Apply projection
            triangle.set(j, Utils.MultiplyMatrixVector(triangle.get(j), Projection.projectionMatrix, true));
            
            // Scale to normalized viewport
            triangle.get(j).sum(Projection.normalizingTransformationPosition);
            triangle.get(j).dot(Projection.normalizingTransformationScale);
        }

        return triangle;
    }

    // Unlink the faces and buffer the rendering of the faces
    private static void BufferFaceTriangles(int[][] faces, Vec3[] vertices, Mesh mesh, SDL_Renderer renderer){

        for (int i = 0; i < faces.length; i++) {
            
            // 3D points that compose the trinagle face unlinked
            // Efectuate the projection
            Triangle triangle = UnlinkTriangleFaceVertices(faces[i], vertices);

            // Visibility is dependant on normal calculations
            // Get this face's current normal
            Vec3 normal = Utils.CalculateFaceNormal(triangle);
            
            // Backface culling
            // Skip projection and drawing  
            Vec3 cameraRay = (triangle.get(0).copy().sum(Camera.lookDirection.copy()));
            float visionAngleDifference = Vec3.getAngle(normal, cameraRay) * (180/(float)Math.PI);
            if (visionAngleDifference <= 90) {
                // The surface is facing away from the camera, so continue processing the next faces
                continue;
            }

            // Calculate lighting conditions from normal
            Vec3 lightDirection = new Vec3(0,0,-1);
            float lightProduct = Vec3.DotProduct(normal, lightDirection);

            Color lightShade = (mesh.lit && Renderer.renderMode == RenderMode.SOLID) ? 
                Color.getLuminanceVariation(mesh.base, lightProduct) : 
                mesh.base;

            // Apply triangle clipping against near plane
            Triangle[] rasterQueue = Clipping.ClipTriangleToFace(triangle, new Vec3(0,0, Projection.zNear), Vec3.FORWARD.copy());

            for (Triangle triangleToRaster : rasterQueue) {
                
                // Project the triangle points to screen space
                triangleToRaster = ProjectTriangleToScreen(triangleToRaster);

                // Draw all the properly spli troiangles yo fit the screen
                for (Triangle screenSplitTriangle : Clipping.ClipTrianglesToScreen(triangleToRaster)) {
                    
                    // Buffer the face triangle
                    switch (Renderer.renderMode) {
                        case WIREFRAME:
                            Graphics.DrawTriangle(renderer, screenSplitTriangle, lightShade);
                        break;
                        case SOLID:
                            Graphics.FillTriangle(renderer, screenSplitTriangle, lightShade);
                            break;
                    }
                }
    
            }
        }
    }

    // From the face vertex links, turn them to the cached vertex triangles
    private static Triangle UnlinkTriangleFaceVertices(int[] linkedface, Vec3[] vertices){

        return new Triangle(vertices[(int) linkedface[0]], vertices[(int) linkedface[1]], vertices[(int) linkedface[2]]);
    }
}
