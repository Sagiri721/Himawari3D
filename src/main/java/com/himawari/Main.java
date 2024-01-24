package com.himawari;

import com.himawari.Gfx.BackBuffer;
import com.himawari.Gfx.Projection;
import com.himawari.Gfx.Renderer;
import com.himawari.Gfx.ZBuffer;

public class Main {
    public static void main(String[] args) {
        
        // Save essential information
        Window.SetDimensions(768, 768);
        Projection.LoadMatrixInformation();
        
        // Start buffers
        BackBuffer.Init();
        ZBuffer.Init();

        // Rendering shit
        Mesh cube = Mesh.LoadFrom("models/axis.obj");

        Renderer.renderQueue.add(cube);
        
        Window.InitWindow("testing");
    }
}