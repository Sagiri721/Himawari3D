package com.himawari;

public class Main {
    public static void main(String[] args) {
        
        Window.SetDimensions(768, 768);
        Renderer.LoadMatrixInformation();

        Mesh cube = Mesh.LoadFrom("models/spaceship.obj");
        Renderer.renderQueue.add(cube);
        
        Window.InitWindow("testing");
    }
}