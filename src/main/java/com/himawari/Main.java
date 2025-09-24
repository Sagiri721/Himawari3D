package com.himawari;

import java.io.File;
import java.util.Random;

import com.himawari.Gfx.Color;
import com.himawari.Gfx.Mesh;
import com.himawari.Gfx.Primitives;
import com.himawari.Gfx.RendererGPU;
import com.himawari.Gfx.Window;
import com.himawari.HLA.Vec2;
import com.himawari.HLA.Vec3;
import com.himawari.Input.CameraInput;
import com.himawari.Input.Input;
import com.himawari.Input.InputListener;
import com.himawari.Utils.Utils;
import com.himawari.Utils.WindowConfig;

public class Main {
    
    public static void main(String[] args) {
        
        // // Start buffers
        // BackBuffer.Init();
        // ZBuffer.Init();

        //GuiApp.launch(new GuiApp());

        Window myWindow = new Window(new WindowConfig(true));
        Input.hookListener(new CameraInput());

        final int bounds = 20;

        Mesh cube = Primitives.Cube();
        cube.AddTexture("textures/wall.jpg");
        cube.base = Color.BLUE;

        // Rendering shit
        for (int i = 0; i < 100; i++) {

            cube.transform.position = new Vec3(Utils.randomInt(-bounds, bounds, System.nanoTime()), Utils.randomInt(-bounds, bounds, System.nanoTime()), Utils.randomInt(-bounds, bounds, System.nanoTime()));
            cube.transform.setRotation(new Vec3(Utils.randomInt(-bounds, bounds, System.nanoTime()), Utils.randomInt(-bounds, bounds, System.nanoTime()), Utils.randomInt(-bounds, bounds, System.nanoTime())));

            cube.transform.position.sum(new Vec3(0, 0, 20));

            myWindow.currentRenderEnvironment().addMesh(cube.clone());
        }

        Mesh l = Primitives.Cube();
        l.transform.scale = new Vec3(0.3f, 0.3f, 0.3f);

        l.transform.position = new Vec3(0, 0, 15);
        l.base = Color.YELLOW;

        //Gizmos.DrawNormals(cube);

        Window.getInstance().currentRenderEnvironment().addMesh(l);
        //Renderer.renderQueue.add(rotationTest);

        myWindow.Loop();
        myWindow.close();

        // Cleanup
        for (Mesh m : myWindow.currentRenderEnvironment().getRenderQueue())
            m.dispose();
    }
}