package com.himawari.Recording;

import java.io.File;

import com.sun.jna.ptr.IntByReference;

public class Recorder {
    
    private int framerate = 10, frameCount = 0, frameIndex = 0;
    private boolean recording = false;

    private Resolution recordResolution = Resolution.NATIVE_RES;

    public Recorder(int framerate, boolean clearRecorderFolder){

        recording = false;
        this.framerate = framerate;

        if(clearRecorderFolder){
            
            File folder = new File("outputs");

            if (!folder.exists()) 
                folder.mkdir();

            File[] files = folder.listFiles();
            for(File file : files)
                file.delete();
        }

    }

    public void startRecording() {
        recording = true;
    }

    public void stopRecording() {
        recording = false;
    }

    // public void tickRecording(SDL_Texture texture, SDL_Renderer renderer){
        
    //     if(recording && frameCount > framerate){
            
    //         saveFrame("f" + frameIndex + ".bmp", texture, renderer);
    //         frameCount = 0;
    //         frameIndex++;
    //     }

    //     frameCount++;
    // }

    // private void saveFrame(String filename, SDL_Texture texture, SDL_Renderer renderer){
        
    //     // Get the current render target to redirect drawing to an image and then restore it
    //     SDL_Texture target = SDL_GetRenderTarget(renderer);
    //     SDL_SetRenderTarget(renderer, target);

    //     // Get the texture dimensions
    //     IntByReference width = new IntByReference(0), height = new IntByReference(0);
    //     SDL_QueryTexture(texture, null, null, width, height);

    //     // Create a surface to store the pixels
    //     SDL_Surface surface = SDL_CreateRGBSurface(0, width.getValue(), height.getValue(), 32, 0, 0, 0, 0);
    //     // Read pixels from the current rendering target to the surface
    //     SDL_RenderReadPixels(renderer, null, surface.getFormat().getFormat(), surface.getPixels(), surface.getPitch());

    //     // Save the surface to a file
    //     SDL_SaveBMP(surface, "outputs/" + filename);

    //     // Free the surface
    //     SDL_FreeSurface(surface);
    // }
}
