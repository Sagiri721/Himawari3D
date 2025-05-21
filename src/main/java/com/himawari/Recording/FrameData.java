package com.himawari.Recording;

import java.nio.ByteBuffer;

public class FrameData {
    
    public final ByteBuffer buffer;
    public final int width;
    public final int height;
    public final int index;

    public FrameData(ByteBuffer buffer, int width, int height, int index) {
        this.buffer = buffer;
        this.width = width;
        this.height = height;
        this.index = index;
    }
}
