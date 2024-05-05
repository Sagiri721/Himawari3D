package com.himawari;

import com.himawari.Gfx.BackBuffer;
import com.himawari.Gfx.Color;
import com.himawari.Gfx.Gizmos;
import com.himawari.Gfx.Mesh;
import com.himawari.Gfx.Primitives;
import com.himawari.Gfx.Projection;
import com.himawari.Gfx.Renderer;
import com.himawari.Gfx.ZBuffer;
import com.himawari.HLA.Vec3;
import com.himawari.Utils.Window;

public class Main {
    
    public static void main(String[] args) {

        // Save essential information
        Window.SetDimensions(768, 768);
        Projection.LoadMatrixInformation();
        
        // Start buffers
        BackBuffer.Init();
        ZBuffer.Init();

        // Rendering shit
        Mesh cube = Mesh.LoadFrom("models/mountains.obj");

        //Gizmos.DrawNormals(cube);
        //Mesh rotationTest = Primitives.Cube();
        //rotationTest.transform.scale = new Vec3(0.1f, 2f, 2);

        Renderer.renderQueue.add(cube);
        //Renderer.renderQueue.add(rotationTest);

        Window.InitWindow("testing");
    }
}