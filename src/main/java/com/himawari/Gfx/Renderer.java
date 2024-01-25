package com.himawari.Gfx;

import static io.github.libsdl4j.api.render.SdlRender.SDL_DestroyTexture;
import static io.github.libsdl4j.api.render.SdlRender.SDL_RenderClear;
import static io.github.libsdl4j.api.render.SdlRender.SDL_RenderCopy;
import static io.github.libsdl4j.api.render.SdlRender.SDL_RenderPresent;
import static io.github.libsdl4j.api.render.SdlRender.SDL_SetRenderDrawColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.himawari.Camera;
import com.himawari.Mesh;
import com.himawari.Utils;
import com.himawari.Window;
import com.himawari.HLA.Triangle;
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
    private static Triangle ProjectTriangleToScreen(Triangle triangle){

        for (int j = 0; j < 3; j++) {

            // Apply projection
            triangle.set(j, Utils.MultiplyMatrixVector(triangle.get(j), Projection.projectionMatrix));
            
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
            
            // Normal calculation
            // Visibility is dependant on normal calculations
            Vec3 normal = Utils.CalculateFaceNormal(triangle);
            
            // Invisible face
            // Skip projection and drawing
            Vec3 cameraRay = (triangle.get(0).copy().subtract(Camera.position.copy()));
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

            // Apply triangle clipping against near plane
            Triangle[] rasterQueue = ClipTriangleToFace(triangle, new Vec3(0,0, Projection.zNear), Vec3.FORWARD.copy());

            for (Triangle triangleToRaster : rasterQueue) {
                
                // Project the triangle points to screen space
                triangleToRaster = ProjectTriangleToScreen(triangleToRaster);

                // Draw all the properly spli troiangles yo fit the screen
                for (Triangle screenSplitTriangle : ClipTrianglesToScreen(triangleToRaster)) {
                    
                    // Buffer the face triangle
                    Graphics.FillTriangle(renderer, screenSplitTriangle, lightShade);
                }
    
            }
        }
    }

    private static List<Triangle> ClipTrianglesToScreen(Triangle triangleToRaster){

        // Final screen clip
        Triangle[] clippingOutput = new Triangle[2];

        List<Triangle> triangleSplits = new ArrayList<Triangle>();

        triangleSplits.add(triangleToRaster);

        int nNewTriangles = 1;
        int nTrianglesToAdd = 0;
        for (int j = 0; j < 4; j++) {
            
            while (nNewTriangles > 0) {

                // Take triangle from front of the queue
                Triangle testing = triangleSplits.getFirst();
                triangleSplits.removeFirst();
                nNewTriangles--;

                clippingOutput[0] = new Triangle();
                clippingOutput[1] = new Triangle();

                /*
                Clip againt a plane
                We only need to test new planes against new triangles, as all the previous one's will always be
                already inside the test plane
                */

                switch (j) {
                    case 0: nTrianglesToAdd = Utils.ClipTriangleAgainstPlane(Vec3.ZERO.copy(), Vec3.UP.copy(), testing, clippingOutput[0], clippingOutput[1]); break;
                    case 1: nTrianglesToAdd = Utils.ClipTriangleAgainstPlane(new Vec3(0, Window.height - 1, 0), Vec3.DOWN.copy(), testing, clippingOutput[0], clippingOutput[1]); break;
                    case 2: nTrianglesToAdd = Utils.ClipTriangleAgainstPlane(Vec3.ZERO, Vec3.RIGHT.copy(), testing, clippingOutput[0], clippingOutput[1]); break;
                    case 3: nTrianglesToAdd = Utils.ClipTriangleAgainstPlane(new Vec3(Window.width - 1, 0, 0), Vec3.LEFT.copy(), testing, clippingOutput[0], clippingOutput[1]); break;
                }

                // After clipping, a variable number of triangles is  yielded
                // Add these new ones to the back of the queue
                // Until all triangles are inside the screen
                for (int i = 0; i < nTrianglesToAdd; i++) {
                    triangleSplits.add(clippingOutput[i].copy());
                }
            }

            nNewTriangles = triangleSplits.size();
        }

        return triangleSplits;
    }

    // Givan a triangle to clip and a plane, clip it into the necessary triangles
    private static Triangle[] ClipTriangleToFace(Triangle triangle, Vec3 clipPlane, Vec3 clipNormal){
            
        // Number of triangles that need to be rastered pos-clipping
        int nClippedTriangles = 0;
        // Initialize empty triangle array with all the 2 possible outcomes
        Triangle[] clippingOutput = new Triangle[2];
        clippingOutput[0] = new Triangle();
        clippingOutput[1] = new Triangle();

        // Get the processed triangle output and store it as a reference on the triangle array
        nClippedTriangles = Utils.ClipTriangleAgainstPlane(
            /* Plane to clip againts, the zNear plane */
            clipPlane, 
            /* Clipping plane normal */
            clipNormal, 
            triangle,
            clippingOutput[0], 
            clippingOutput[1]
        );

        // Prepare output raster queue
        Triangle[] rasterQueue = new Triangle[nClippedTriangles];
        for (int j = 0; j < rasterQueue.length; j++) { rasterQueue[j] = clippingOutput[j]; }

        return rasterQueue;
    }

    // From the face vertex links, turn them to the cached vertex triangles
    private static Triangle UnlinkTriangleFaceVertices(int[] linkedface, Vec3[] vertices){

        return new Triangle(vertices[(int) linkedface[0]], vertices[(int) linkedface[1]], vertices[(int) linkedface[2]]);
    }
}
