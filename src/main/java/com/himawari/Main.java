package com.himawari;

import java.io.File;
import java.util.Random;

import com.himawari.GUI.GuiApp;
import com.himawari.Gfx.Color;
import com.himawari.Gfx.Mesh;
import com.himawari.Gfx.Primitives;
import com.himawari.Gfx.RendererGPU;
import com.himawari.Gfx.Window;
import com.himawari.HLA.Vec2;
import com.himawari.HLA.Vec3;
import com.himawari.Input.CameraInput;
import com.himawari.Input.Input;
import com.himawari.Simulation.SimulationController;
import com.himawari.Simulation.SimulationKeyListener;
import com.himawari.Utils.Utils;
import com.himawari.Utils.WindowConfig;

public class Main {
    
    public static void main(String[] args) {
        
        // // Start buffers
        // BackBuffer.Init();
        // ZBuffer.Init();

        //GuiApp.launch(new GuiApp());

        Window myWindow = new Window(new WindowConfig(true));
        File head = new File("D:\\TIAGO\\school\\iseppers\\pi4\\shodrone.simulator\\recordings\\recording_1521031101.yaml");
        File step = new File("D:\\TIAGO\\school\\iseppers\\pi4\\shodrone.simulator\\recordings\\recording_step_1521031101.csv");

        SimulationController sim = new SimulationController(head, step);

        Input.hookListener(new CameraInput());
        Input.hookListener(new SimulationKeyListener(sim));

        Mesh plane = Primitives.Plane(new Vec2(50, 50));
        plane.base = Color.GREEN;
        plane.transform.position = new Vec3(0, -1, 0);

        Window.getInstance().currentRenderEnvironment().AddMesh(plane);

        // final int bounds = 20;

        // // Rendering shit
        // for (int i = 0; i < 100; i++) {

        //     Mesh cube = Mesh.LoadFrom("models/cube.obj");
        //     cube.base = Color.BLUE;

        //     cube.transform.position = new Vec3(Utils.randomInt(-bounds, bounds, System.nanoTime()), Utils.randomInt(-bounds, bounds, System.nanoTime()), Utils.randomInt(-bounds, bounds, System.nanoTime()));
        //     cube.transform.setRotation(new Vec3(Utils.randomInt(-bounds, bounds, System.nanoTime()), Utils.randomInt(-bounds, bounds, System.nanoTime()), Utils.randomInt(-bounds, bounds, System.nanoTime())));

        //     cube.transform.position.sum(new Vec3(0, 0, 20));

        //     myWindow.currentRenderEnvironment().AddMesh(cube);
        // }

        // Mesh l = Primitives.Cube();
        // l.transform.scale = new Vec3(0.3f, 0.3f, 0.3f);

        // l.transform.position = new Vec3(0, 0, 15);
        // l.base = Color.YELLOW;

        // //Gizmos.DrawNormals(cube);

        // Window.getInstance().currentRenderEnvironment().AddMesh(l);
        //Renderer.renderQueue.add(rotationTest);

        myWindow.Loop();
        myWindow.close();
    }
}