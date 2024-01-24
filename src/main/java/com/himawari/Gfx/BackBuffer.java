package com.himawari.Gfx;

import static io.github.libsdl4j.api.render.SdlRender.SDL_CreateTexture;
import static io.github.libsdl4j.api.render.SdlRender.SDL_UpdateTexture;

import com.himawari.Utils;
import com.himawari.Window;
import com.himawari.HLA.Vec3;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;

import java.util.Arrays;

import io.github.libsdl4j.api.pixels.SDL_PixelFormatEnum;
import io.github.libsdl4j.api.render.SDL_Renderer;
import io.github.libsdl4j.api.render.SDL_Texture;
import io.github.libsdl4j.api.render.SDL_TextureAccess;

public class BackBuffer {

    // Buffer data
    public static int bufferWidth, bufferHeight;
    public static Color[] colorBuffer;

    // initialize the buffer with the respective window dimensions
    public static void Init(){

        BackBuffer.bufferWidth = Window.width;
        BackBuffer.bufferHeight = Window.height;

        BackBuffer.colorBuffer = new Color[bufferWidth*bufferHeight];

        ClearBackBuffer();
    }

    public static void PutPixel(int x, int y, Color color, Vec3 v1, Vec3 v2, Vec3 v3){

        if(x < 0 || y < 0 || x >= bufferWidth || y >= bufferHeight) return;

        // When no surface is given skip Depth buffering
        if(v1 == null || v2 == null || v3 == null)
            BackBuffer.colorBuffer[x + y * bufferWidth] = color;
        else{

            // Calculate the pixel projection onto surface
            float depth = ZBuffer.ProjectOntoToFace(v1, v2, v3, new Vec3(x, y, 0)).z;
            boolean result = ZBuffer.TestAndSet(x, y, depth);

            if(result) BackBuffer.colorBuffer[x + y * bufferWidth] = color;
        }
    }

    public static void ClearBackBuffer(){
        Arrays.fill(colorBuffer, Utils.BLACK);
    }
    
    public static void FillBufferLine(float x1, float y1, float x2, float y2, Color fillValue){

        float dx = x2 - x1;
        float dy = y2 - y1;

        int step = (int) (dx > dy ? dx : dy);

        if (step == 0) {
            PutPixel((int) x1, (int) y1, fillValue, null, null, null);
            return;
        }

        float xIncrement = dx / step;
        float yIncrement = dy / step;

        float x = x1;
        float y = y1;

        for (int i = 0; i <= step; i++) {
            if (y >= bufferHeight || x >= bufferWidth || x < 0 || y < 0) break;
            
            PutPixel((int) x, (int) y, fillValue, null, null, null);

            x += xIncrement;
            y += yIncrement;
        }
    }

    // Simple algorithm for filling triangles
    public static void FillBufferTriangle(Vec3 v1, Vec3 v2, Vec3 v3, Color color) {
       
        int maxX = (int) Math.max(v1.x, Math.max(v2.x, v3.x));
        int minX = (int) Math.min(v1.x, Math.min(v2.x, v3.x));
        int maxY = (int) Math.max(v1.y, Math.max(v2.y, v3.y));
        int minY = (int) Math.min(v1.y, Math.min(v2.y, v3.y));

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                
                if (PointInTriangle(new Vec3(x,y,0), v1, v2, v3)) {
                    PutPixel(x, y, color, v1, v2, v3);
                }
            }
        }
    }

    // Check if point inside triangle
    private static boolean PointInTriangle (Vec3 pt, Vec3 v1, Vec3 v2, Vec3 v3) {
        
        float d1, d2, d3;
        boolean has_neg, has_pos;

        d1 = sign(pt, v1, v2);
        d2 = sign(pt, v2, v3);
        d3 = sign(pt, v3, v1);

        has_neg = (d1 < 0) || (d2 < 0) || (d3 < 0);
        has_pos = (d1 > 0) || (d2 > 0) || (d3 > 0);

        return !(has_neg && has_pos);
    }

    private static float sign (Vec3 p1, Vec3 p2, Vec3 p3) {
        return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
    }

    // Turn current backbuffer into SDL_Texture
    public static SDL_Texture FetchTexture(SDL_Renderer renderer) {

        // Allocate the buffer of correct size
        int bufferSize = bufferWidth * bufferHeight * 4;
        Pointer pointer = new Memory(bufferSize);
        pointer.write(0, FlattenBuffer(), 0, bufferSize);

        SDL_Texture output = SDL_CreateTexture(renderer, SDL_PixelFormatEnum.SDL_PIXELFORMAT_RGBA32, SDL_TextureAccess.SDL_TEXTUREACCESS_STREAMING, bufferWidth, bufferHeight);
        SDL_UpdateTexture(output, null, pointer, bufferWidth * 4);

        pointer.clear(bufferSize);
        pointer = null;

        return output;
    }

    public static byte[] FlattenBuffer(){

        byte[] flatBuffer = new byte[bufferWidth * bufferHeight * 4];

        for (int i = 0; i < flatBuffer.length; i+=4) {
            flatBuffer[i] = (byte) colorBuffer[i / 4].colorData.x;
            flatBuffer[i+1] = (byte) colorBuffer[i / 4].colorData.y;
            flatBuffer[i+2] = (byte) colorBuffer[i / 4].colorData.z;
            flatBuffer[i+3] = (byte) colorBuffer[i / 4].colorData.w;
        }

        return flatBuffer;
    }
}
