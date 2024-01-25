package com.himawari.Gfx;

import com.himawari.Utils;
import com.himawari.HLA.Triangle;

import io.github.libsdl4j.api.render.SDL_Renderer;

public class Graphics {
    
    // Draw a triangle to the screen
    public static void DrawTriangle(SDL_Renderer renderer, Triangle triangle, Color color){

        BackBuffer.FillBufferLine(triangle.get(0).x, triangle.get(0).y, triangle.get(1).x, triangle.get(1).y, color);
        BackBuffer.FillBufferLine(triangle.get(1).x, triangle.get(1).y, triangle.get(2).x, triangle.get(2).y, color);
        BackBuffer.FillBufferLine(triangle.get(2).x, triangle.get(2).y, triangle.get(0).x, triangle.get(0).y, color);
    }

    // Fill the triangle's geometry
    public static void FillTriangle(SDL_Renderer renderer, Triangle triangle, Color color){

        BackBuffer.FillBufferTriangle(triangle.get(0), triangle.get(1), triangle.get(2), color);
    }
}
