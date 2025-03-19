package com.himawari.Recording;

import static io.github.libsdl4j.api.render.SdlRender.SDL_GetRenderTarget;
import static io.github.libsdl4j.api.render.SdlRender.SDL_QueryTexture;
import static io.github.libsdl4j.api.render.SdlRender.SDL_RenderReadPixels;
import static io.github.libsdl4j.api.render.SdlRender.SDL_SetRenderTarget;
import static io.github.libsdl4j.api.surface.SdlSurface.SDL_CreateRGBSurface;
import static io.github.libsdl4j.api.surface.SdlSurface.SDL_FreeSurface;
import static io.github.libsdl4j.api.surface.SdlSurface.SDL_SaveBMP;

import com.sun.jna.ptr.IntByReference;

import io.github.libsdl4j.api.render.SDL_Renderer;
import io.github.libsdl4j.api.render.SDL_Texture;
import io.github.libsdl4j.api.surface.SDL_Surface;

public class Recorder {
    
    public static void saveFrame(String filename, SDL_Texture texture, SDL_Renderer renderer){
        
        // Get the current render target to redirect drawing to an image and then restore it
        SDL_Texture target = SDL_GetRenderTarget(renderer);
        SDL_SetRenderTarget(renderer, target);

        // Get the texture dimensions
        IntByReference width = new IntByReference(0), height = new IntByReference(0);
        SDL_QueryTexture(texture, null, null, width, height);

        // Create a surface to store the pixels
        SDL_Surface surface = SDL_CreateRGBSurface(0, width.getValue(), height.getValue(), 32, 0, 0, 0, 0);
        // Read pixels from the current rendering target to the surface
        SDL_RenderReadPixels(renderer, null, surface.getFormat().getFormat(), surface.getPixels(), surface.getPitch());

        // Save the surface to a file
        SDL_SaveBMP(surface, "outputs/" + filename);

        // Free the surface
        SDL_FreeSurface(surface);
    }
}
