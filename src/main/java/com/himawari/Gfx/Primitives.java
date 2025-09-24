package com.himawari.Gfx;

import com.himawari.HLA.Vec2;
import com.himawari.HLA.Vec3;
import com.himawari.HLA.Vertex;

/**
 * Class to store and create primitive 3D objects
 */
public class Primitives {

    private static final Vertex[] CUBE_VERTICES = new Vertex[]{
        // Front face
        new Vertex(new Vec3(-1, -1, -1), new Vec2(0, 0)),
        new Vertex(new Vec3(-1,  1, -1), new Vec2(0, 1)),
        new Vertex(new Vec3( 1,  1, -1), new Vec2(1, 1)),
        new Vertex(new Vec3( 1, -1, -1), new Vec2(1, 0)),
    
        // Back face
        new Vertex(new Vec3( 1, -1,  1), new Vec2(0, 0)),
        new Vertex(new Vec3( 1,  1,  1), new Vec2(0, 1)),
        new Vertex(new Vec3(-1,  1,  1), new Vec2(1, 1)),
        new Vertex(new Vec3(-1, -1,  1), new Vec2(1, 0)),
    
        // Left face
        new Vertex(new Vec3(-1, -1,  1), new Vec2(0, 0)),
        new Vertex(new Vec3(-1,  1,  1), new Vec2(0, 1)),
        new Vertex(new Vec3(-1,  1, -1), new Vec2(1, 1)),
        new Vertex(new Vec3(-1, -1, -1), new Vec2(1, 0)),
    
        // Right face
        new Vertex(new Vec3( 1, -1, -1), new Vec2(0, 0)),
        new Vertex(new Vec3( 1,  1, -1), new Vec2(0, 1)),
        new Vertex(new Vec3( 1,  1,  1), new Vec2(1, 1)),
        new Vertex(new Vec3( 1, -1,  1), new Vec2(1, 0)),
    
        // Top face
        new Vertex(new Vec3(-1,  1, -1), new Vec2(0, 0)),
        new Vertex(new Vec3(-1,  1,  1), new Vec2(0, 1)),
        new Vertex(new Vec3( 1,  1,  1), new Vec2(1, 1)),
        new Vertex(new Vec3( 1,  1, -1), new Vec2(1, 0)),
    
        // Bottom face
        new Vertex(new Vec3(-1, -1,  1), new Vec2(0, 0)),
        new Vertex(new Vec3(-1, -1, -1), new Vec2(0, 1)),
        new Vertex(new Vec3( 1, -1, -1), new Vec2(1, 1)),
        new Vertex(new Vec3( 1, -1,  1), new Vec2(1, 0))
    };

    private static final int[][] CUBE_FACES = new int[][]{
        {0, 1, 2}, {0, 2, 3},       // Front face
        {4, 5, 6}, {4, 6, 7},       // Back face
        {8, 9,10}, {8,10,11},       // Left face
        {12,13,14}, {12,14,15},     // Right face
        {16,17,18}, {16,18,19},     // Top face
        {20,21,22}, {20,22,23}      // Bottom face
    };
    
    
    // Return a basic cube mesh
    public static Mesh Cube(){
        return new Mesh(CUBE_VERTICES, CUBE_FACES);
    }

    public static Mesh Plane(Vec2 dimensions){

        Vertex[] vertices = new Vertex[]{
            // new Vec3(-dimensions.x, 0, -dimensions.y),
            // new Vec3(-dimensions.x, 0, dimensions.y),
            // new Vec3(dimensions.x, 0, dimensions.y),
            // new Vec3(dimensions.x, 0, -dimensions.y)
            new Vertex(new Vec3(-dimensions.x, 0, -dimensions.y), new Vec2(0, 0)),
            new Vertex(new Vec3(-dimensions.x, 0, dimensions.y), new Vec2(0, 1)),
            new Vertex(new Vec3(dimensions.x, 0, dimensions.y), new Vec2(1, 1)),
            new Vertex(new Vec3(dimensions.x, 0, -dimensions.y), new Vec2(1, 0))
        };

        int[][] faces = new int[][]{
            {0, 1, 2},
            {0, 2, 3}
        };

        return new Mesh(vertices, faces);
    }
}
