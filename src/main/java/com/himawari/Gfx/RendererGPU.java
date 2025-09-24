package com.himawari.Gfx;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import java.io.IOException;

import org.lwjgl.opengl.GL30;

import com.himawari.Camera.Camera;
import com.himawari.Camera.Clipping;
import com.himawari.HLA.Triangle;
import com.himawari.HLA.Vec2;
import com.himawari.HLA.Vec3;
import com.himawari.HLA.Vertex;
import com.himawari.Utils.Logger;
import com.himawari.Utils.RenderEnvironment;
import com.himawari.Utils.Utils;

public class RendererGPU extends RenderEnvironment implements IRenderer {

    public static String VERTEX_PATH = "shaders/vertex.vert";
    public static String FRAGMENT_PATH = "shaders/fragment.frag"; 

    private final Batch batchRenderer = new Batch();
    private int shaderProgram;

    public RendererGPU() {}

    @Override
    public void Init() {
        
        // Load shaders
        try {
            
            Shader shader = new Shader(VERTEX_PATH, FRAGMENT_PATH);

            Logger.LogInfo("Vertex shader: " + VERTEX_PATH);
            Logger.LogInfo("Fragment shader: " + FRAGMENT_PATH);

            shader.use();

            // Uniforms
            Vec2 screenSize = new Vec2(Window.getInstance().config().width, Window.getInstance().config().height);
            shader.setUVec2("screenSize", screenSize.x, screenSize.y);

            // Begin batch renderer
            batchRenderer.begin(1000);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Render(){

        // At the start of each render loop
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Set camera position for this frame
        DefineCameraProjection();

        // Loop through stored meshes and draw each of them
        for (Mesh mesh : getRenderQueue()) {

            // Bach render each mesh
            batchRenderer.textureID = mesh.getTexture();

            // Before buffering the renderer
            // Treat and cache the vertices after all projection operations
            Vertex[] trianglePool = ProjectVerticesFromMesh(mesh);

            // Buffer rendering
            // For each face of the mesh draw it's triangles
            BufferFaceTriangles(trianglePool, mesh);

            // Flush any remaining vertices before rendering mesh
            batchRenderer.flush();
        }
    }

    // Efectuate the rotation of the points in 3D space
    // Normals are a reference to the normals array list that is used later
    // Normals need to be calculated from the raw vertex data
    private static Vertex[] ProjectVerticesFromMesh(Mesh mesh){

        Vertex[] trianglePool = new Vertex[mesh.vertices.length];
        Vertex tempVertex = new Vertex();

        for (int i = 0; i < mesh.vertices.length; i++) {

            // Copy the current value
            tempVertex = mesh.vertices[i].clone();

            // Apply rotations
            Projection.ProjectRotationMatricesToAngle(mesh.transform.getRotation());

            Utils.TransformVectorInPlace(tempVertex, Projection.rotationX);
            Utils.TransformVectorInPlace(tempVertex, Projection.rotationZ);
            Utils.TransformVectorInPlace(tempVertex, Projection.rotationY);
            
            // Apply local transformations
            Utils.TransformVectorInPlace(tempVertex, Projection.MakeScale(mesh.transform.scale));
            Utils.TransformVectorInPlace(tempVertex, Projection.MakeTranslation(mesh.transform.position));

            // Update mesh normals
            Utils.TransformVectorInPlace(tempVertex, Projection.cameraView);

            // Copy the transformed vertex to the triangle pool
            trianglePool[i] = tempVertex.clone();
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
            triangle.set(j, new Vertex(Utils.MultiplyMatrixVector(triangle.get(j).position, Projection.projectionMatrix, true), triangle.get(j).texCoord));
            
            // Scale to normalized viewport
            triangle.get(j).position.sum(Projection.normalizingTransformationPosition);
            triangle.get(j).position.dot(Projection.normalizingTransformationScale);
        }

        return triangle;
    }

    // Unlink the faces and buffer the rendering of the faces
    private void BufferFaceTriangles(Vertex[] vertices, Mesh mesh){

        Triangle projTri = null;
        Vec3 projNormal = null, cameraRay = null;

        for (int i = 0; i < mesh.faces.length; i++) {
            
            // 3D points that compose the trinagle face unlinked
            // Efectuate the projection
            projTri = UnlinkTriangleFaceVertices(mesh.faces[i], vertices);

            // Visibility is dependant on normal calculations
            // Get this face's current normal
            projNormal = Utils.CalculateFaceNormal(projTri);
            
            // Backface culling
            // Skip projection and drawing  
            cameraRay = (projTri.get(0).position.copy().sum(Camera.lookDirection.copy()));
            if (Utils.RadiansToEuler(Vec3.getAngle(projNormal, cameraRay)) <= 90) {
                // The surface is facing away from the camera, so continue processing the next faces
                continue;
            }

            Color base = mesh.base;

            if (getRenderTarget() == RenderTarget.NORMALMAP) {
                
                Vec3 realNormal = projNormal;
                base = new Color(
                    (int) Math.abs(realNormal.x * 255),
                    (int) Math.abs(realNormal.y * 255),
                    (int) Math.abs(realNormal.z * 255),
                    255
                );
            }

            // Apply triangle clipping against near plane
            Triangle[] rasterQueue = Clipping.ClipTriangleToFace(projTri, new Vec3(0,0, Projection.zNear), Vec3.FORWARD.copy());

            for (Triangle triangleToRaster : rasterQueue) {
                
                // Project the triangle points to screen space
                triangleToRaster = ProjectTriangleToScreen(triangleToRaster);

                // Draw all the properly spli troiangles yo fit the screen
                for (Triangle screenSplitTriangle : Clipping.ClipTrianglesToScreen(triangleToRaster)) {

                    // Buffer the face triangle
                    switch (getRenderMode()) {
                        case WIREFRAME:
                            //Graphics.DrawTriangle(screenSplitTriangle, base);
                        break;
                        case SOLID:

                            batchRenderer.batch(screenSplitTriangle, base, projNormal);
                            break;
                    }
                }
    
            }
        }
    }

    // From the face vertex links, turn them to the cached vertex triangles
    private static Triangle UnlinkTriangleFaceVertices(short[] linkedFace, Vertex[] vertices) {
        Vertex v1 = vertices[0];
        v1.position = new Vec3(
            vertices[linkedFace[0]].position.x, 
            vertices[linkedFace[0]].position.y,
            vertices[linkedFace[0]].position.z
        );
        
        Vertex v2 = vertices[1];
        v2.position = new Vec3(
            vertices[linkedFace[1]].position.x, 
            vertices[linkedFace[1]].position.y,
            vertices[linkedFace[1]].position.z
        );

        Vertex v3 = vertices[2];
        v3.position = new Vec3(
            vertices[linkedFace[2]].position.x, 
            vertices[linkedFace[2]].position.y,
            vertices[linkedFace[2]].position.z
        );
        
        return new Triangle(v1, v2, v3);
    }

    @Override
    public void Dispose() {

        // Clean render quweue
        getRenderQueue().clear();

        batchRenderer.end();
        batchRenderer.dispose();
        GL30.glDeleteShader(shaderProgram);
    }
}
