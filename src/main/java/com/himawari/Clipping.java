package com.himawari;

import java.util.ArrayList;
import java.util.List;

import com.himawari.HLA.Triangle;
import com.himawari.HLA.Vec3;

public class Clipping {

    // Given a triangle to clip and a plane, clip it into the necessary triangles
    public static Triangle[] ClipTriangleToFace(Triangle triangle, Vec3 clipPlane, Vec3 clipNormal){
            
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
 
    public static List<Triangle> ClipTrianglesToScreen(Triangle triangleToRaster){

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
}
