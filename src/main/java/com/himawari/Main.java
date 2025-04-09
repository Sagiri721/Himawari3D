package com.himawari;

import java.util.Random;

import com.himawari.Gfx.Color;
import com.himawari.Gfx.Mesh;
import com.himawari.Gfx.Primitives;
import com.himawari.Gfx.RendererGPU;
import com.himawari.Gfx.Window;
import com.himawari.HLA.Vec3;
import com.himawari.Utils.Utils;
import com.himawari.Utils.WindowConfig;

public class Main {
    
    public static void main(String[] args) {
        
        // // Start buffers
        // BackBuffer.Init();
        // ZBuffer.Init();

        Window myWindow = new Window(new WindowConfig(true));

        final int bounds = 20;

        // Rendering shit
        for (int i = 0; i < 100; i++) {

            Mesh cube = Mesh.LoadFrom("models/cube.obj");
            cube.base = Color.BLUE;

            cube.transform.position = new Vec3(Utils.randomInt(-bounds, bounds, System.nanoTime()), Utils.randomInt(-bounds, bounds, System.nanoTime()), Utils.randomInt(-bounds, bounds, System.nanoTime()));
            cube.transform.setRotation(new Vec3(Utils.randomInt(-bounds, bounds, System.nanoTime()), Utils.randomInt(-bounds, bounds, System.nanoTime()), Utils.randomInt(-bounds, bounds, System.nanoTime())));

            cube.transform.position.sum(new Vec3(0, 0, 20));

            myWindow.currentRenderEnvironment().AddMesh(cube);
        }

        Mesh l = Primitives.Cube();
        l.transform.scale = new Vec3(0.3f, 0.3f, 0.3f);

        l.transform.position = new Vec3(0, 0, 15);
        l.base = Color.YELLOW;

        //Gizmos.DrawNormals(cube);

        Window.getInstance().currentRenderEnvironment().AddMesh(l);
        //Renderer.renderQueue.add(rotationTest);

        myWindow.Loop();
        myWindow.close();
    }
}