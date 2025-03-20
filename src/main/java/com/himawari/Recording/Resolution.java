package com.himawari.Recording;

import com.himawari.HLA.Vec2;

public class Resolution {

    public static Resolution NATIVE_RES = new Resolution();

    public Vec2 resolution = new Vec2(0, 0);
    public boolean isNative = false;
    public float aspectRatio = 0;

    private Resolution(){
        isNative = true;
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
}
