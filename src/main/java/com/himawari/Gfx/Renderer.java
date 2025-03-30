package com.himawari.Gfx;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import java.util.ArrayList;
import java.util.List;

import com.himawari.Camera.Camera;
import com.himawari.Camera.Clipping;
import com.himawari.HLA.Triangle;
import com.himawari.HLA.Vec3;
import com.himawari.Recording.Recorder;
import com.himawari.Utils.Utils;

public class Renderer {

    // Recorder for the current frame
    public static Recorder recorder = new Recorder(1, true);

    // List of meshes to display
    public static List<Mesh> renderQueue = new ArrayList<Mesh>();
    
    // Definitions
    public static RenderMode renderMode = RenderMode.SOLID;
    public static RenderTarget renderTarget = RenderTarget.COLORBUFFER;
    public static Color clearColor = Color.BLACK;


    public static void Render(){

        // At the start of each render loop
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Set camera position for this frame
        DefineCameraProjection();

        // Loop through stored meshes and draw each of them
        for (Mesh mesh : renderQueue) {

            // Before buffering the renderer
            // Treat and cache the vertices after all projection operations
            Vec3[] trianglePool = ProjectVerticesFromMesh(mesh);

            // Buffer rendering
            // For each face of the mesh draw it's triangles
            BufferFaceTriangles(trianglePool, mesh);
        }

        // Render the current frame
        // SDL_Texture renderPixels = BackBuffer.FetchTexture(renderer);
        // SDL_RenderCopy(renderer, renderPixels, null, null);
        // SDL_RenderPresent(renderer);

        // // Save the frane to a file
        // recorder.tickRecording(renderPixels, renderer);

        // SDL_DestroyTexture(renderPixels);
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
    private static void BufferFaceTriangles(Vec3[] vertices, Mesh mesh){

        int[][] faces = mesh.faces;
        for (int i = 0; i < faces.length; i++) {
            
            // 3D points that compose the trinagle face unlinked
            // Efectuate the projection
            Triangle projTri = UnlinkTriangleFaceVertices(faces[i], vertices);

            // Visibility is dependant on normal calculations
            // Get this face's current normal
            Vec3 realNormal = mesh.normals[i];
            Vec3 projNormal = Utils.CalculateFaceNormal(projTri);
            
            // Backface culling
            // Skip projection and drawing  
            Vec3 cameraRay = (projTri.get(0).copy().sum(Camera.lookDirection.copy()));
            float visionAngleDifference = Utils.RadiansToEuler(Vec3.getAngle(projNormal, cameraRay));
            if (visionAngleDifference <= 90) {
                // The surface is facing away from the camera, so continue processing the next faces
                continue;
            }

            // Calculate lighting conditions from normal
            Vec3 lightDirection = new Vec3(0, 0, -1).normalized(); 
            float lightProduct = Vec3.DotProduct(projNormal, lightDirection);
            lightProduct = Math.max(0, lightProduct); 

            Color lightShade;

            if (renderTarget == RenderTarget.NORMALMAP) {
                // Map normal to color
                lightShade = new Color(
                    (int) Math.abs(realNormal.x * 255),
                    (int) Math.abs(realNormal.y * 255),
                    (int) Math.abs(realNormal.z * 255),
                    255
                );
            }else {    
                lightShade = (mesh.lit && Renderer.renderMode == RenderMode.SOLID) ? 
                Color.getLuminanceVariation(mesh.base, lightProduct) : 
                mesh.base;
            }

            // Apply triangle clipping against near plane
            Triangle[] rasterQueue = Clipping.ClipTriangleToFace(projTri, new Vec3(0,0, Projection.zNear), Vec3.FORWARD.copy());

            for (Triangle triangleToRaster : rasterQueue) {
                
                // Project the triangle points to screen space
                triangleToRaster = ProjectTriangleToScreen(triangleToRaster);

                // Draw all the properly spli troiangles yo fit the screen
                for (Triangle screenSplitTriangle : Clipping.ClipTrianglesToScreen(triangleToRaster)) {
                    
                    // Buffer the face triangle
                    switch (Renderer.renderMode) {
                        case WIREFRAME:
                            Graphics.DrawTriangle(screenSplitTriangle, lightShade);
                        break;
                        case SOLID:
                            Graphics.FillTriangle(screenSplitTriangle, lightShade);
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
