package com.himawari.Gfx;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_ARRAY;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.GL_VIEWPORT;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColorPointer;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGetIntegerv;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glVertexPointer;

import java.io.FileWriter;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import com.himawari.HLA.Triangle;
import com.himawari.HLA.Vec3;
import com.himawari.Utils.Utils;

public class Batch {

    private final byte VERTEX_SIZE = 3;
    private final byte COLOR_SIZE = 4;
    private final byte NORMAL_SIZE = 3;

    // Identifiers of the opengl gpu buffers
    int vertexBuffer, indexBuffer;
    int vertexCount = 0, bufferSize = 0, vertexObject = 0;

    FloatBuffer vertexData; // Java side buffer to store vertices
    IntBuffer indexData; // java side buffer to store indices
    
    /**
     * Begin a batch of draw calls
     * That means to store all vertices and their indices in in GPU memory
     * Buffer size in number of vertices
     */
    public void begin(int bufferSize) {

        // Oops memory leak
        if (vertexBuffer != 0) 
            GL30.glDeleteVertexArrays(vertexBuffer);
        if (indexBuffer != 0)
            GL30.glDeleteBuffers(indexBuffer);
        if (vertexObject != 0)
            GL30.glDeleteBuffers(vertexObject);

        this.bufferSize = bufferSize;
        
        // Batch rendering
        // Store vertex information in a buffer and then draw everything in one draw call
        // Store all vertices in GPU memory
        // VAO
        vertexBuffer = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vertexBuffer);

        // Store vertex indices
        // VBO
        vertexObject = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vertexObject);

        // Allocate memory for the buffers
        vertexData = BufferUtils.createFloatBuffer(this.bufferSize * 3 * (VERTEX_SIZE + COLOR_SIZE + NORMAL_SIZE));
        // Dynamic draw is an optimization hint means optimized for frequent updates
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertexData.capacity() * Float.BYTES, GL30.GL_DYNAMIC_DRAW); 

        // IBO
        indexBuffer = GL30.glGenBuffers();
        GL30.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);

        indexData = BufferUtils.createIntBuffer(this.bufferSize * 3);
        // For index arrays we use GL_ELEMENT_ARRAY_BUFFER
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indexData.capacity() * Integer.BYTES, GL30.GL_DYNAMIC_DRAW);
        
        // Set vertex attributes
        // 0 is the offset, the first attribute
        // false means the data is not normalized
        // (VERTEX_SIZE + COLOR_SIZE) * 4 is the stride, the total amount of data as its the size of each data bucket
        // 0 offset
        GL30.glVertexAttribPointer(0, VERTEX_SIZE, GL_FLOAT, false, (VERTEX_SIZE + COLOR_SIZE + NORMAL_SIZE) * Float.BYTES, 0);
        GL30.glEnableVertexAttribArray(0);
        
        // Set color attributes
        // 1 is the offset, the second attribute
        GL30.glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, (VERTEX_SIZE + COLOR_SIZE + NORMAL_SIZE) * Float.BYTES, VERTEX_SIZE * Float.BYTES);
        GL30.glEnableVertexAttribArray(1); 

        // Set normal attributes
        // 2 is the offset, the third attribute
        GL30.glVertexAttribPointer(2, NORMAL_SIZE, GL_FLOAT, false, (VERTEX_SIZE + COLOR_SIZE + NORMAL_SIZE) * Float.BYTES, (VERTEX_SIZE + COLOR_SIZE) * Float.BYTES);
        GL30.glEnableVertexAttribArray(2);
    }

    public void end() {
    
        // Unbind the buffers
        GL30.glBindVertexArray(0);
        GL30.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void batch(Triangle triangle, Color color, Vec3 normal) {

        // Check if adding this triangle would exceed buffer capacity
        if (vertexCount + 3 > bufferSize)
            flush(); 

        float r = Byte.toUnsignedInt(color.r);
        float g = Byte.toUnsignedInt(color.g);
        float b = Byte.toUnsignedInt(color.b);
        float a = Byte.toUnsignedInt(color.a);

        for (int i = 0; i < 3; i++) {
            
            // Put vertex data
            vertexData.put(triangle.get(i).x)
                .put(triangle.get(i).y)
                .put(triangle.depth(i))
                .put(r)  
                .put(g)  
                .put(b)
                .put(a)
                .put(normal.x)
                .put(normal.y)
                .put(normal.z);

            // Add index
            indexData.put(vertexCount++);
        }

        // Flush batch if full
        if (vertexCount >= bufferSize) flush();
    }

    public void flush() {

        // Flip the buffers idk why you always gotta do this
        vertexData.flip();
        indexData.flip();

        // Bind and fill the buffers with the data
        GL30.glBindVertexArray(vertexBuffer);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexObject);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vertexData);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
        GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, indexData);

        GL30.glDrawElements(GL_TRIANGLES, vertexCount, GL15.GL_UNSIGNED_INT, 0);

        // try {

        //     String data = "";
        //     for (int i = 0; i < 6 * 7 * 6; i++) {
                
        //         data += vertexData.get(i) + " ";
        //     }

        //     FileWriter writer = new FileWriter("vertexData.txt");
        //     writer.write(data);
        //     writer.close();

        // } catch (Exception e) {
        //     e.printStackTrace();
        // }

        // Rest the counters
        vertexCount = 0;

        vertexData.clear();
        indexData.clear();
    }

    public void dispose() {
        
        GL30.glDeleteVertexArrays(vertexBuffer);
        GL30.glDeleteBuffers(indexBuffer);

        vertexData.clear();
        indexData.clear();

        vertexData = null;
        indexData = null;
    }
}

