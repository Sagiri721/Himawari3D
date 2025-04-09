package com.himawari.Gfx;

import com.himawari.Utils.*;
import com.himawari.HLA.Triangle;
import com.himawari.HLA.Vec3;

public final class Gizmos {
    
    public static Mesh MakeArrow(Vec3 origin, Vec3 direction, float size){

        Mesh cube = Primitives.Cube();
        cube.transform.position = origin;
        
        // Make it point to direction
        cube.transform.setRotation(direction);

        // Make it narrow
        cube.transform.scale = new Vec3(0.3f, size, 0.3f);

        cube.base = Color.RED;

        return cube;
    }

    public static void DrawNormals(Mesh mesh){


    }
}
