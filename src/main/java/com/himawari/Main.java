package com.himawari;

public class Main {
    public static void main(String[] args) {
        
        // loading essential information
        Window.SetDimensions(768, 768);
        Renderer.LoadMatrixInformation();
        
        BackBuffer.Init();
        ZBuffer.Init();

        // Rendering shit

        Mesh cube = Mesh.LoadFrom("models/teapot.obj");
        Renderer.renderQueue.add(cube);
        
        Window.InitWindow("testing");
    }
}