package com.himawari.Gfx;

import com.himawari.Utils.*;
import com.himawari.HLA.Triangle;
import com.himawari.HLA.Vec3;

public final class Gizmos {
    
    public static void MakeArrow(Vec3 origin, Vec3 direction, float size){

        Mesh cube = Primitives.Cube();
        cube.transform.position = origin;
        
        // Make it point to direction
        cube.transform.setRotation(direction);

        // Make it narrow
        cube.transform.scale = new Vec3(0.3f, size, 0.3f);

        cube.base = Color.RED;

        Renderer.renderQueue.add(cube);
    }

    public static void DrawNormals(Mesh mesh){

        for (int[] face : mesh.faces){

            Vec3 a = mesh.vertices[face[0]];
            Vec3 b = mesh.vertices[face[1]];
            Vec3 c = mesh.vertices[face[2]];

            Vec3 center = new Vec3((a.x + b.x + c.x) / 3, (a.y + b.y + c.y) / 3, (a.z + b.z + c.z) / 3);

            Mesh cube = Primitives.Cube();
            cube.transform.position = mesh.transform.position.copy().sum(center);
            cube.transform.scale = new Vec3(0.05f, 0.05f, 0.05f);
            cube.transform.setRotation(Utils.CalculateFaceNormal(new Triangle(a,b,c)));
            cube.base = Color.WHITE;

            cube.lit = false;

            Renderer.renderQueue.add(cube);
        }
    }
}
