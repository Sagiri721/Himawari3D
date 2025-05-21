package com.himawari.Recording;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;

public class FrameSaverThread extends Thread {
    
    private final BlockingQueue<FrameData> frameQueue = new LinkedBlockingQueue<>();

    public void enqueueFrame(FrameData frame) {
        try {
            frameQueue.put(frame);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopThread() {
        this.interrupt();
    }
    
    private void saveFrame(FrameData frame){

        BufferedImage image = new BufferedImage(frame.width, frame.height, BufferedImage.TYPE_INT_RGB);

        ByteBuffer buf = frame.buffer;
        buf.rewind();

        for (int y = 0; y < frame.height; y++) {
            for (int x = 0; x < frame.width; x++) {
                int r = buf.get() & 0xFF;
                int g = buf.get() & 0xFF;
                int b = buf.get() & 0xFF;
                int rgb = (r << 16) | (g << 8) | b;
                image.setRGB(x, frame.height - y - 1, rgb);
            }
        }

        try {
            
            ImageIO.write(image, "png", new File("outputs/frame_" + String.format("%04d", frame.index) + ".png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        
        while (true) {

            if (Thread.interrupted()) {
                break;
            }

            // Wait for a frame to be available
            if (frameQueue.isEmpty()) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    break;
                }
            }else {
                    
                try {
                    
                    FrameData frame = frameQueue.take();
                    saveFrame(frame);

                } catch (InterruptedException ignored) {}
            }
        }

        while (!frameQueue.isEmpty()) {
            try {
                    
                FrameData frame = frameQueue.take();
                saveFrame(frame);

            } catch (InterruptedException ignored) {}
        }
    }
}
