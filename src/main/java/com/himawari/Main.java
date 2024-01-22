package com.himawari;

public class Main {
    public static void main(String[] args) {
        
        Renderer.LoadMatrixInformation();

        Mesh cube = Mesh.LoadFrom("cube.obj");
        Renderer.renderQueue.add(cube);

        Window.InitWindow(1024, 768, "testing");
    }
}