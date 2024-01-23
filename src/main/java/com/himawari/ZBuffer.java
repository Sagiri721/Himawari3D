package com.himawari;

import java.util.Arrays;

public class ZBuffer {
    
    public static int bufferWidth, bufferHeight;
    public static float[][] depthBuffer;

    // initialize the buffer with the respective window dimensions
    public static void Init(){

        ZBuffer.bufferWidth = Window.width;
        ZBuffer.bufferHeight = Window.height;

        ZBuffer.depthBuffer = new float[bufferWidth][bufferHeight];

        ClearDepthBuffer();
    }

    // Overwrite (or not) current depth value
    public static boolean TestAndSet(int x, int y, float depth){

        float cachedDepth = depthBuffer[x][y];
        if (depth < cachedDepth) {
            depthBuffer[x][y] = depth;
            return true;
        }

        return false;
    }

    // Fill the depth buffer with infinity
    public static void ClearDepthBuffer(){
        Arrays.stream(depthBuffer).forEach(x -> Arrays.fill(x, 9999));
    }
}
