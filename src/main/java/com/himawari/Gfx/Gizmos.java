package com.himawari.Gfx;

import com.himawari.HLA.Vec3;

public final class Gizmos {
    
    public static void MakeArrow(Vec3 origin, Vec3 direction, float size){

        Mesh cube = Primitives.Cube();
        cube.transform.position = origin;
        
        // Make it point to direction
        cube.transform.rotation = direction;

        // Make it narrow
        cube.transform.scale = new Vec3(0.3f, size, 0.3f);

        cube.base = Color.RED;

        Renderer.renderQueue.add(cube);
    }

    public static void DrawNormals(Mesh mesh){

        for (int[] points : mesh.faces) {

            Vec3 a = mesh.vertices[points[0]].copy();
            Vec3 b = mesh.vertices[points[1]].copy();
            Vec3 c = mesh.vertices[points[2]].copy();

            Vec3 normal = Vec3.CrossProduct(b.subtract(a), c.subtract(a)).normalized();

            Vec3 center = a.sum(c.sum(a).divide(3));

            MakeArrow(center, normal, 1);
        }
    }
}
