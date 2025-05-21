package com.himawari.Recording;

import com.himawari.Gfx.Window;
import com.himawari.HLA.Vec2;

public class Resolution {

    public Vec2 resolution = new Vec2(0, 0);
    public boolean isNative = false;
    public float aspectRatio = 0;

    private Resolution(){
        isNative = true;
        resolution.x = Window.getInstance().config().width;
        resolution.y = Window.getInstance().config().height;

        aspectRatio = resolution.x / resolution.y;
    }

    public Resolution(int width, int height){
        resolution.x = width;
        resolution.y = height;

        aspectRatio = (float)height / (float)width;
    }

    public Resolution(float aspectRatio, int width){
        resolution.x = width;
        resolution.y = (int)(width * aspectRatio);

        this.aspectRatio = aspectRatio;
    }

    public static Resolution getNativeResolution(){
        return new Resolution();
    }
}
