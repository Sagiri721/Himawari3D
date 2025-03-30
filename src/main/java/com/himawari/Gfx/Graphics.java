package com.himawari.Gfx;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3ub;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3f;

import com.himawari.HLA.Triangle;
import com.himawari.HLA.Vec2;
import com.himawari.HLA.Vec3;

public class Graphics {
    
    // Draw a triangle to the screen
    public static void DrawTriangle(Triangle triangle, Color color){

        // BackBuffer.FillBufferLine(triangle.get(0).x, triangle.get(0).y, triangle.get(1).x, triangle.get(1).y, color);
        // BackBuffer.FillBufferLine(triangle.get(1).x, triangle.get(1).y, triangle.get(2).x, triangle.get(2).y, color);
        // BackBuffer.FillBufferLine(triangle.get(2).x, triangle.get(2).y, triangle.get(0).x, triangle.get(0).y, color);
    }

    // Fill the triangle's geometry
    public static void FillTriangle(Triangle triangle, Color color){
        
        glColor3ub(color.r, color.g, color.b);
        glBegin(GL_TRIANGLES);
        
        for (int i = 0; i < 3; i++) {
            
            Vec2 screenPos = new Vec2(triangle.vertices[i].x, triangle.vertices[i].y);

            float depth = ZBuffer.ProjectOntoToFace(triangle.get(0), triangle.get(1), triangle.get(2), new Vec3(screenPos)).z;
            glVertex3f(screenPos.x, screenPos.y, -depth);
        }

        glEnd();

        // BackBuffer.FillBufferTriangle(triangle.get(0), triangle.get(1), triangle.get(2), color);
    }
}
