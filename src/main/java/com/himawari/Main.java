package com.himawari;

import com.himawari.Gfx.Color;
import com.himawari.Gfx.Mesh;
import com.himawari.Gfx.Primitives;
import com.himawari.Gfx.RendererGPU;
import com.himawari.Gfx.Window;
import com.himawari.HLA.Vec3;
import com.himawari.Utils.WindowConfig;

public class Main {
    
    public static void main(String[] args) {
        
        // // Start buffers
        // BackBuffer.Init();
        // ZBuffer.Init();

        Window myWindow = new Window(new WindowConfig());

        // Rendering shit
        Mesh cube = Mesh.LoadFrom("models/mountains.obj");
        cube.base = Color.BLUE;

        Mesh rotationTest = Primitives.Cube();
        rotationTest.transform.scale = new Vec3(2f, 2f, 2);

        //Gizmos.DrawNormals(cube);

        Window.getInstance().currentRenderEnvironment().AddMesh(cube);
        //Renderer.renderQueue.add(rotationTest);

        myWindow.Loop();
        myWindow.close();
    }
}