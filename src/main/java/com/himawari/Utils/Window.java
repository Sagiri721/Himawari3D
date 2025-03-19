package com.himawari.Utils;

import static io.github.libsdl4j.api.Sdl.SDL_Init;
import static io.github.libsdl4j.api.Sdl.SDL_Quit;
import static io.github.libsdl4j.api.SdlSubSystemConst.SDL_INIT_EVERYTHING;
import static io.github.libsdl4j.api.event.SDL_EventType.*;
import static io.github.libsdl4j.api.event.SdlEvents.SDL_PollEvent;
import static io.github.libsdl4j.api.render.SDL_RendererFlags.SDL_RENDERER_ACCELERATED;
import static io.github.libsdl4j.api.render.SdlRender.*;
import static io.github.libsdl4j.api.video.SDL_WindowFlags.SDL_WINDOW_RESIZABLE;
import static io.github.libsdl4j.api.video.SDL_WindowFlags.SDL_WINDOW_SHOWN;
import static io.github.libsdl4j.api.video.SdlVideo.SDL_CreateWindow;
import static io.github.libsdl4j.api.video.SdlVideoConst.SDL_WINDOWPOS_CENTERED;

import com.himawari.Gfx.Renderer;
import com.himawari.HLA.Vec3;
import com.himawari.Input.Input;
import com.sun.jna.Pointer;

import io.github.libsdl4j.api.event.SDL_Event;
import io.github.libsdl4j.api.keyboard.SdlKeyboard;
import io.github.libsdl4j.api.render.SDL_Renderer;
import io.github.libsdl4j.api.video.SDL_Window;

public class Window {

    // Window size
    public static int width = 0, height = 0;
    public static float aspectRatio;

    // Tick tracking
    public static long ticks, lastFrame, elapsedTime;
    public static double fps, frameDelta;
    
    public static void SetDimensions(int width, int height){
        
        Window.width = width;
        Window.height = height;

        Window.aspectRatio = ((float)height) / ((float)width);
    }

    public static void InitWindow(String name){

        // Initialize sdl
        SDL_Init(SDL_INIT_EVERYTHING);
        // Create and initialize the window
        SDL_Window window = SDL_CreateWindow(name, SDL_WINDOWPOS_CENTERED, SDL_WINDOWPOS_CENTERED, width, height, SDL_WINDOW_SHOWN | SDL_WINDOW_RESIZABLE);

        // Create and initialize the renderer
        SDL_Renderer renderer = SDL_CreateRenderer(window, -1, SDL_RENDERER_ACCELERATED);

        SDL_SetRenderDrawColor(renderer, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
        SDL_RenderClear(renderer);
        // Render present buffer
        SDL_RenderPresent(renderer);

        Window.ticks = 0;
        Window.fps = 0;
        Window.frameDelta = 0;

        Window.elapsedTime = 0;
        Window.lastFrame = System.currentTimeMillis();

        // Start an event loop
        SDL_Event evt = new SDL_Event();
        boolean running = true;
        while (running){

            while(SDL_PollEvent(evt) != 0) {

                switch (evt.type) {
                    case SDL_QUIT:
                        running = false;
                        break;
                }
            }

            Pointer keyStates = SdlKeyboard.SDL_GetKeyboardState(null);
            Input.KeyDown(keyStates);

            // Realize tick operations
            Window.frameDelta = (System.currentTimeMillis() - Window.lastFrame) / 1000f;
            Window.lastFrame = System.currentTimeMillis();

            if (frameDelta > 0) Window.fps = 1 / (frameDelta / 1000);

            Window.ticks++;
            Window.elapsedTime += Window.frameDelta;

            Renderer.renderQueue.get(0).transform.Rotate(new Vec3(0.01f,0.01f, 0));

            Renderer.Render(renderer);
        }

        SDL_Quit();
    }
}
