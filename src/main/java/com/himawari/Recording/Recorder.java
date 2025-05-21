package com.himawari.Recording;

import static org.lwjgl.opengl.GL11.GL_BYTE;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.glReadPixels;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
public class Recorder {
    
    private int framerate = 10, frameIndex = 0;
    private boolean recording = false;

    private Resolution recordResolution;
    
    // Reusable buffer for the screenshot
    private ByteBuffer targetBuffer;
    private FrameSaverThread frameSaverThread = new FrameSaverThread();

    // Framerate 
    private long lastCapture = 0;
    private long captureInterval = 1000 / framerate;

    public Recorder(int framerate, boolean clearRecorderFolder, Resolution resolution) {

        recording = false;
        this.framerate = framerate;
        this.captureInterval = 1000 / framerate;

        if (!getRecordingFolder().exists())
            getRecordingFolder().mkdir();

        if(clearRecorderFolder){
            
            File folder = getRecordingFolder();

            if (!folder.exists()) 
                folder.mkdir();

            File[] files = folder.listFiles();
            for(File file : files)
                file.delete();
        }

        recordResolution = resolution;
        targetBuffer = BufferUtils.createByteBuffer((int) (recordResolution.resolution.x * recordResolution.resolution.y * 3));
    }

    public void startRecording() {
        recording = true;
        frameSaverThread.start();
    }

    public void stopRecording() {
        recording = false;
        frameSaverThread.stopThread();
    }

    public void tickRecording() throws IOException{

        if(!recording) return;
        
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastCapture >= captureInterval) {
            
            //saveFrame("outputs/" + prefix + "frame_" + String.format("%04d", frameIndex) + ".png");
            capture();
            lastCapture = currentTime;

            frameIndex++;
        }
    }

    public File getRecordingFolder() {
        return new File("outputs/");
    }

    private void capture(){

        int width = (int) recordResolution.resolution.x;
        int height = (int) recordResolution.resolution.y;
        
        glReadPixels(0, 0, width, height, GL_RGB, GL_BYTE, targetBuffer);

        // Clone for thread safety
        ByteBuffer clone = BufferUtils.createByteBuffer(targetBuffer.capacity());
        clone.put(targetBuffer);
        clone.flip();

        frameSaverThread.enqueueFrame(new FrameData(clone, width, height, frameIndex));
        
        targetBuffer.rewind();
    }
}
