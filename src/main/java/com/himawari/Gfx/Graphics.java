package com.himawari.Gfx;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_MODULATE;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_REPLACE;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV_MODE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor3ub;
import static org.lwjgl.opengl.GL11.glColor4ub;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexEnvi;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import com.himawari.HLA.Triangle;
import com.himawari.HLA.Vec2;
import com.himawari.HLA.Vec3;
import com.himawari.Utils.Utils;

public class Graphics {
    
    // Draw a triangle to the screen
    public static void DrawTriangle(Triangle triangle, Color color){

        // BackBuffer.FillBufferLine(triangle.get(0).x, triangle.get(0).y, triangle.get(1).x, triangle.get(1).y, color);
        // BackBuffer.FillBufferLine(triangle.get(1).x, triangle.get(1).y, triangle.get(2).x, triangle.get(2).y, color);
        // BackBuffer.FillBufferLine(triangle.get(2).x, triangle.get(2).y, triangle.get(0).x, triangle.get(0).y, color);

        glColor3ub(color.r, color.g, color.b);

        glBegin(GL_LINES);
        for (int i = 0; i < 3; i++) {

            Vec2 normalizeCoordinates = Utils.ScreenSpaceToNormalizedCoordinates(triangle.get(i));
            Vec2 normalizeCoordinatesNext = Utils.ScreenSpaceToNormalizedCoordinates(triangle.get((i + 1) % 3));

            glVertex2f(normalizeCoordinates.x, normalizeCoordinates.y);
            glVertex2f(normalizeCoordinatesNext.x, normalizeCoordinatesNext.y);
        }
        glEnd();
    }

    // Fill the triangle's geometry
    public static void FillTriangle(Triangle triangle, Color color){
        
        glColor3ub(color.r, color.g, color.b);

        glBegin(GL_TRIANGLES);
        
        for (int i = 0; i < 3; i++) {
            
            Vec2 normalizeCoordinates = Utils.ScreenSpaceToNormalizedCoordinates(triangle.get(i));

            float depth = Utils.ProjectOntoToFace(triangle.get(0), triangle.get(1), triangle.get(2), new Vec3(triangle.get(0).x, triangle.get(0).y, 0)).z;
            glVertex3f(normalizeCoordinates.x, normalizeCoordinates.y, depth);
        }

        glEnd();

        // BackBuffer.FillBufferTriangle(triangle.get(0), triangle.get(1), triangle.get(2), color);
    }
    public static void RenderTexture(int textureID, float x, float y, float width, float height, Optional<Color> color) {
        
        // Enable blending for transparency
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureID);
        
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        
        glBegin(GL_QUADS);
            // Set color (default to white if not provided)
            if (color.isPresent()) {
                Color c = color.get();
                glColor4ub(c.r, c.g, c.b, c.a);
            } else {
                glColor4ub((byte)255, (byte)255, (byte)255, (byte)255);
            }
    
            glTexCoord2f(0, 0); 
            glVertex2f(x, y);
    
            glTexCoord2f(1, 0); 
            glVertex2f(x + width, y);
    
            glTexCoord2f(1, 1); 
            glVertex2f(x + width, y + height);
    
            glTexCoord2f(0, 1); 
            glVertex2f(x, y + height);
        glEnd();
        
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }
}
