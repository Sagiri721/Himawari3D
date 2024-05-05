package com.himawari.Gfx;

import com.himawari.HLA.Vec3;

/**
 * Class to store and create primitive 3D objects
 */
public class Primitives {

    private static final Vec3[] CUBE_VERTICES = new Vec3[]{
        new Vec3(-1, -1, -1),
        new Vec3(-1,  1, -1),
        new Vec3( 1,  1, -1),
        new Vec3( 1, -1, -1),
        new Vec3(-1, -1,  1),
        new Vec3(-1,  1,  1),
        new Vec3( 1,  1,  1),
        new Vec3( 1, -1,  1)
    };

    private static final int[][] CUBE_FACES = new int[][]{
        {0, 1, 2},
        {0, 2, 3},
        {4, 6, 5},
        {4, 7, 6},
        {0, 4, 1},
        {1, 4, 5},
        {1, 5, 2},
        {2, 5, 6},
        {2, 6, 3},
        {3, 6, 7},
        {3, 7, 0},
        {0, 7, 4}
    };
    
    // Return a basic cube mesh
    public static Mesh Cube(){
        return new Mesh(CUBE_VERTICES, CUBE_FACES);
    }
}
