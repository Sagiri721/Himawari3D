package com.himawari.Recording;

import static org.lwjgl.opengl.GL11.GL_BYTE;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.glReadPixels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import com.himawari.HLA.Vec2;

public class Recorder {
    
    private int framerate = 10, frameIndex = 0;
    private boolean recording = false;
    private String prefix;

    private Resolution recordResolution;

    // Reusable buffer for the screenshot
    private BufferedImage targetImage;
    ByteBuffer targetBuffer;

    // Framerate 
    private long lastCapture = 0;
    private long captureInterval = 1000 / framerate;

    public Recorder(int framerate, boolean clearRecorderFolder, String prefix, Resolution resolution) {

        recording = false;
        this.framerate = framerate;
        this.captureInterval = 1000 / framerate;

        if(clearRecorderFolder){
            
            File folder = new File("outputs");

            if (!folder.exists()) 
                folder.mkdir();

            File[] files = folder.listFiles();
            for(File file : files)
                file.delete();
        }

        recordResolution = resolution;
        this.prefix = prefix;

        // BufferedImage is used to store the screenshot
        targetImage = new BufferedImage((int) recordResolution.resolution.x, (int) recordResolution.resolution.y, BufferedImage.TYPE_INT_RGB);
        targetBuffer = BufferUtils.createByteBuffer((int) (recordResolution.resolution.x * recordResolution.resolution.y * 3));
    }

    public void startRecording() {
        recording = true;
    }

    public void stopRecording() {
        recording = false;
    }

    public void tickRecording() throws IOException{

        if(!recording) return;
        
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastCapture >= captureInterval) {
            
            saveFrame("outputs/" + prefix + "frame_" + String.format("%04d", frameIndex) + ".png");
            lastCapture = currentTime;

            frameIndex++;
        }
    }

    public File getRecordingFolder() {
        return new File("outputs/");
    }

    private void saveFrame(){

        // Reset the buffer head
        targetBuffer.rewind();

        int width = (int) recordResolution.resolution.x;
        int height = (int) recordResolution.resolution.y;

        glReadPixels(0, 0, width, height, GL_RGB, GL_BYTE, targetBuffer);

        for (int h = 0; h < (int) recordResolution.resolution.y; h++) {
            for (int w = 0; w < (int) recordResolution.resolution.x; w++) {
                // The color are the three consecutive bytes, it's like referencing
                // to the next consecutive array elements, so we got red, green, blue..
                // red, green, blue, and so on..
                int r = targetBuffer.get() & 0xFF;
                int g = targetBuffer.get() & 0xFF;
                int b = targetBuffer.get() & 0xFF;
                int rgb = (r << 16) | (g << 8) | b;
                targetImage.setRGB(w, height - h - 1, rgb); // h is for flipping the image
            }
        }
    }

    private void saveFrame(String filename) throws IOException{
        
        saveFrame();
        // Save the screenshot to a file
        ImageIO.write(targetImage, "png", new File(filename));
    }
}
