package com.himawari.Gfx;

import com.himawari.Window;
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
    private static float scaleOffseting = 1f;

    private static float scalingX = 0.5f * Window.width;
    private static float scalingY = 0.5f * Window.height;
    
    public static Vec3 normalizingTransformationPosition = new Vec3(scaleOffseting, scaleOffseting, 0);
    public static Vec3 normalizingTransformationScale = new Vec3(scalingX, scalingY, 1);

    // Projection modifiers
    private static float zNear = 0.1f, zFar = 1000;
    private static float fov = 90f;

    public static void LoadMatrixInformation(){

        float fovFunction = 1f / (float) Math.tan(fov * 0.5f / 180f * Math.PI);
     
        projectionMatrix.Set(0, 0, Window.aspectRatio * fovFunction);
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
        cameraView.Set(1, 0, up.x);
        cameraView.Set(1, 1, up.y);
        cameraView.Set(1, 2, up.z);
        cameraView.Set(2, 0, forward.x);
        cameraView.Set(2, 1, forward.y);
        cameraView.Set(2, 2, forward.z);
        cameraView.Set(3, 0, position.x);
        cameraView.Set(3, 1, position.y);
        cameraView.Set(3, 2, position.z);
        
        cameraView.Set(3, 3, 1f);
    }

    public static void ProjectCameraViewToAxisInvert(){

        Mat4 tempMatrix = new Mat4(cameraView.m); 

        tempMatrix.Set(0, 0, cameraView.Get(0, 0));
        tempMatrix.Set(0, 1, cameraView.Get(1, 0));
        tempMatrix.Set(0, 2, cameraView.Get(2, 0));
        tempMatrix.Set(0, 3, 0.0f);
    
        tempMatrix.Set(1, 0, cameraView.Get(0, 1));
        tempMatrix.Set(1, 1, cameraView.Get(1, 1));
        tempMatrix.Set(1, 2, cameraView.Get(2, 1));
        tempMatrix.Set(1, 3, 0.0f);
    
        tempMatrix.Set(2, 0, cameraView.Get(0, 2));
        tempMatrix.Set(2, 1, cameraView.Get(1, 2));
        tempMatrix.Set(2, 2, cameraView.Get(2, 2));
        tempMatrix.Set(2, 3, 0.0f);
    
        tempMatrix.Set(3, 0, -(
                cameraView.Get(3, 0) * tempMatrix.Get(0, 0) +
                cameraView.Get(3, 1) * tempMatrix.Get(1, 0) +
                cameraView.Get(3, 2) * tempMatrix.Get(2, 0)
        ));
    
        tempMatrix.Set(3, 1, -(
                cameraView.Get(3, 0) * tempMatrix.Get(0, 1) +
                cameraView.Get(3, 1) * tempMatrix.Get(1, 1) +
                cameraView.Get(3, 2) * tempMatrix.Get(2, 1)
        ));
    
        tempMatrix.Set(3, 2, -(
                cameraView.Get(3, 0) * tempMatrix.Get(0, 2) +
                cameraView.Get(3, 1) * tempMatrix.Get(1, 2) +
                cameraView.Get(3, 2) * tempMatrix.Get(2, 2)
        ));
    
        tempMatrix.Set(3, 3, 1.0f);
        cameraView.Set(tempMatrix.m);
    }
}
