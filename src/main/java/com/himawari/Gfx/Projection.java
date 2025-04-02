package com.himawari.Gfx;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;

import com.himawari.HLA.Mat4;
import com.himawari.HLA.Vec3;

public class Projection {
    
    // Matrices
    // Matrix for vertex projection
    public static Mat4 projectionMatrix = new Mat4(0f);
    
    // Matrices for vertex rotation
    public static Mat4 rotationX = new Mat4(0f);
    public static Mat4 rotationY = new Mat4(0f);
    public static Mat4 rotationZ = new Mat4(0f);

    // Matrix for camera directioning
    public static Mat4 cameraView = new Mat4(0f);

    // Viewport normalizing
    public static float scaleOffseting = 1f;
    
    public static Vec3 normalizingTransformationPosition = new Vec3(scaleOffseting, scaleOffseting, 0);
    public static Vec3 normalizingTransformationScale = new Vec3(0, 0, 1);

    // Projection modifiers
    public static float zNear = 0.2f, zFar = 1000;
    public static float fov = 90f;

    public static void LoadMatrixInformation(){

        float scalingX  = 0.5f * Window.getInstance().config().width;
        float scalingY = 0.5f * Window.getInstance().config().height;

        Projection.normalizingTransformationPosition = new Vec3(Projection.scaleOffseting, Projection.scaleOffseting, 0);
        Projection.normalizingTransformationScale = new Vec3(scalingX, scalingY, 1);

        float fovFunction = 1f / (float) Math.tan(fov * 0.5f / 180f * Math.PI);
     
        projectionMatrix.Set(0, 0, Window.getInstance().aspectRatio * fovFunction);
        projectionMatrix.Set(1, 1, fovFunction);
        projectionMatrix.Set(2, 2, zFar / (zFar - zNear));
        projectionMatrix.Set(3, 2, -(zFar * zNear) / (zFar - zNear));
        projectionMatrix.Set(2, 3, 1);
    }

    // Project along all axis
    public static void ProjectRotationMatricesToAngle(Vec3 angle){

        // Rotation Z
        ProjectRotationAlongXAxis(angle.z);
        // Rotation Y
		ProjectRotationAlongYAxis(angle.y);
		// Rotation X
		ProjectRotationAlongZAxis(angle.x);
    }

    public static void ProjectRotationAlongZAxis(float angle){

        angle = angle * 0.5f;

        rotationZ.Set(0, 0, (float) Math.cos(angle));
        rotationZ.Set(0, 1, (float) Math.sin(angle));
        rotationZ.Set(1, 0, (float) -Math.sin(angle));
        rotationZ.Set(1, 1, (float) Math.cos(angle));
        rotationZ.Set(2, 2, 1);
        rotationZ.Set(3, 3, 1);
    }

    public static void ProjectRotationAlongYAxis(float angle){

        rotationY.Set(0, 0, (float) Math.cos(angle));
        rotationY.Set(0, 2, (float) Math.sin(angle));
        rotationY.Set(2, 0, (float) -Math.sin(angle));
        rotationY.Set(1, 1, 1);
        rotationY.Set(2, 2, (float) Math.cos(angle));
        rotationY.Set(3, 3, 1);
    }

    public static void ProjectRotationAlongXAxis(float angle){
        
        rotationX.Set(0, 0, 1);
		rotationX.Set(1, 1, (float) Math.cos(angle));
		rotationX.Set(1, 2, (float) Math.sin(angle));
        rotationX.Set(2, 1, (float) -Math.sin(angle));
        rotationX.Set(2, 2, (float) Math.cos(angle));
        rotationX.Set(3, 3, 1);
    }

    public static void ProjectCameraViewToAxis(Vec3 forward, Vec3 up, Vec3 right, Vec3 position){

        cameraView.Set(0, 0, right.x);
        cameraView.Set(0, 1, right.y);
        cameraView.Set(0, 2, right.z);
        cameraView.Set(0, 3, -Vec3.DotProduct(right, position));
        cameraView.Set(1, 0, up.x);
        cameraView.Set(1, 1, up.y);
        cameraView.Set(1, 2, up.z);
        cameraView.Set(1, 3, -Vec3.DotProduct(up, position));
        cameraView.Set(2, 0, forward.x);
        cameraView.Set(2, 1, forward.y);
        cameraView.Set(2, 2, forward.z);
        cameraView.Set(2, 3, -Vec3.DotProduct(forward, position));
        cameraView.Set(3, 3, 1);
    }

    public static void InvertCameraMatrix(){

        Mat4 invertedMatrix = new Mat4(0f);
        invertedMatrix.Set(0, 0, cameraView.Get(0, 0));
        invertedMatrix.Set(0, 1, cameraView.Get(1, 0));
        invertedMatrix.Set(0, 2, cameraView.Get(2, 0));
        invertedMatrix.Set(1, 0, cameraView.Get(0, 1));
        invertedMatrix.Set(1, 1, cameraView.Get(1, 1));
        invertedMatrix.Set(1, 2, cameraView.Get(2, 1));
        invertedMatrix.Set(2, 0, cameraView.Get(0, 2));
        invertedMatrix.Set(2, 1, cameraView.Get(1, 2));
        invertedMatrix.Set(2, 2, cameraView.Get(2, 2));
        invertedMatrix.Set(3, 0, cameraView.Get(0, 3));
        invertedMatrix.Set(3, 1, cameraView.Get(1, 3));
        invertedMatrix.Set(3, 2, cameraView.Get(2, 3));
        invertedMatrix.Set(3, 3, 1);
        cameraView = invertedMatrix;
    }

    public static Mat4 MakeScale(Vec3 scale) {
        
        Mat4 scaleMatrix = new Mat4(1f);   
        scaleMatrix.Set(0, 0, scale.x);
        scaleMatrix.Set(1, 1, scale.y);
        scaleMatrix.Set(2, 2, scale.z);
        return scaleMatrix;
    }

    public static Mat4 MakeTranslation(Vec3 translation) {
        
        Mat4 translationMatrix = new Mat4(1f);
        translationMatrix.Set(3, 0, translation.x);
        translationMatrix.Set(3, 1, translation.y);
        translationMatrix.Set(3, 2, translation.z);
        return translationMatrix;
    }
}
