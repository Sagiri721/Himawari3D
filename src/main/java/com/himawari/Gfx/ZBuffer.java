package com.himawari.Gfx;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import com.himawari.HLA.Triangle;
import com.himawari.HLA.Vec3;
import com.himawari.Utils.Window;

public class ZBuffer {
    
    public static int bufferWidth, bufferHeight;
    public static FloatBuffer depthBuffer, referenceBuffer;

    // initialize the buffer with the respective window dimensions
    public static void Init(){

        ZBuffer.bufferWidth = Window.width;
        ZBuffer.bufferHeight = Window.height;

        ZBuffer.depthBuffer = ByteBuffer.allocateDirect(bufferWidth * bufferHeight * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        ZBuffer.referenceBuffer = ByteBuffer.allocateDirect(bufferWidth * bufferHeight * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        ClearDepthBuffer();
        SwapDepthBuffers();
    }

    // Overwrite (or not) current depth value
    public static boolean TestAndSet(int x, int y, float depth){
        
        if (x < 0 || y < 0 || x >= bufferWidth || y >= bufferHeight)
            return false;

        int index = x + y * bufferWidth;
        float cachedDepth = depthBuffer.get(index);

        if (depth < cachedDepth) {
            
            depthBuffer.put(index, depth);
            return true;
        }

        return false;
    }

    public static boolean TestAgainstReference(int x, int y, float depth) {
        if (x < 0 || y < 0 || x >= bufferWidth || y >= bufferHeight)
            return false;
        
        // Add a small bias to avoid z-fighting and other artifacts
        float depthBias = 0.01f; 
        int index = x + y * bufferWidth;
        float cachedDepth = referenceBuffer.get(index);
        
        return (depth - depthBias) < cachedDepth;
    }

    // Given a 3D plane, correctly project the point to the matching Z axis
    public static Vec3 ProjectOntoToFace(Vec3 vertex1, Vec3 vertex2, Vec3 vertex3, Vec3 point){

        Vec3 v1 = vertex2.copy().subtract(vertex1);
        Vec3 v2 = vertex3.copy().subtract(vertex1);

        Vec3 planeCoefficients = Vec3.CrossProduct(v1, v2);
        float planeConstant = planeCoefficients.copy().dot(vertex1).sum();

        float zComponent = (planeConstant - point.x*planeCoefficients.x - point.y * planeCoefficients.y) / planeCoefficients.z;
        point.z = zComponent;

        return point;
    }

    // Fill the depth buffer with infinity
    public static void ClearDepthBuffer(){
        
        depthBuffer.clear();

        for (int i = 0; i < bufferWidth * bufferHeight; i++)
            depthBuffer.put(Float.MAX_VALUE);
        
        depthBuffer.rewind();
    }

    public static void SwapDepthBuffers() {
        
        depthBuffer.rewind();
        referenceBuffer.rewind();
        referenceBuffer.put(depthBuffer);
        depthBuffer.rewind();
        referenceBuffer.rewind();
    }
}
