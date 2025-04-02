package com.himawari.Camera;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.himawari.Gfx.Window;
import com.himawari.HLA.Triangle;
import com.himawari.HLA.Vec3;
import com.himawari.Utils.Utils;

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
        nClippedTriangles = ClipTriangleAgainstPlane(
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

    public static int ClipTriangleAgainstPlane(Vec3 plane, Vec3 normal, Triangle face, Triangle return1, Triangle return2){

        // Normalize the original plane normal
        normal.normalized();

        // This variables will store the number of points inside and outside the plane space
        // As well as their positions
        Vec3[] insidePoints = new Vec3[3], outsidePoints = new Vec3[3];
        int nInsidePoints = 0, nOutsidePoints = 0;

        // The distance of every point in the original triangle to the given plane
        Float[] originDistance = Arrays.stream(face.vertices).map(v -> Utils.DistanceToShortestPlanePoint(v, normal, plane)).toArray(Float[]::new);

        for (int i = 0; i < originDistance.length; i++) {
            
            // Get the Float class value
            float value = originDistance[i].floatValue();

            // When distance is positive, the vertex is inside
            // When it is negative, it is outside
            if(value >= 0) insidePoints[nInsidePoints++] = face.vertices[i];
            else outsidePoints[nOutsidePoints++] = face.vertices[i];
        }

        if(nInsidePoints == 0){
            // All points exist on the outside of the plane
            // Skip rendering
            return 0;
        }

        if(nInsidePoints == 3){
            // All points points exist inside the the plane
            // Render the triangle without any modifications
            return1.set(face);
            return 1;
        }

        if(nInsidePoints == 1 && nOutsidePoints == 2){

            // One point is inside and teo outside
            // We can construct a new smaller triangle we 2 new points
            // That's out output
            return1.set(0, insidePoints[0]); 
            return1.set(1, Utils.Vec3IntersectPlane(plane, normal, insidePoints[0], outsidePoints[0]));
            return1.set(2, Utils.Vec3IntersectPlane(plane, normal, insidePoints[0], outsidePoints[1]));

            return 1;
        }

        if(nInsidePoints == 2 && nOutsidePoints == 1){

            // When two points are inside the plane and the other one's outside
            // We form a quad, and so, we need to break it up into two triangles
            return1.set(0, insidePoints[0]);
            return1.set(1, insidePoints[1]);
            return1.set(2, Utils.Vec3IntersectPlane(plane, normal, insidePoints[0], outsidePoints[0]));
            
            return2.set(0, insidePoints[1]);
            return2.set(1, return1.get(2));
            return2.set(2, Utils.Vec3IntersectPlane(plane, normal, insidePoints[1], outsidePoints[0]));

            return 2;
        }

        return 0;
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
                Triangle testing = triangleSplits.get(0);
                triangleSplits.remove(0);
                nNewTriangles--;

                clippingOutput[0] = new Triangle();
                clippingOutput[1] = new Triangle();

                /*
                Clip againt a plane
                We only need to test new planes against new triangles, as all the previous one's will always be
                already inside the test plane
                */

                switch (j) {
                    case 0: nTrianglesToAdd = ClipTriangleAgainstPlane(Vec3.ZERO.copy(), Vec3.UP.copy(), testing, clippingOutput[0], clippingOutput[1]); break;
                    case 1: nTrianglesToAdd = ClipTriangleAgainstPlane(new Vec3(0, Window.getInstance().config().height - 1, 0), Vec3.DOWN.copy(), testing, clippingOutput[0], clippingOutput[1]); break;
                    case 2: nTrianglesToAdd = ClipTriangleAgainstPlane(Vec3.ZERO, Vec3.RIGHT.copy(), testing, clippingOutput[0], clippingOutput[1]); break;
                    case 3: nTrianglesToAdd = ClipTriangleAgainstPlane(new Vec3(Window.getInstance().config().width - 1, 0, 0), Vec3.LEFT.copy(), testing, clippingOutput[0], clippingOutput[1]); break;
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
