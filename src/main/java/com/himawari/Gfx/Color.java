package com.himawari.Gfx;

import com.himawari.HLA.Vec4;

public class Color {
    
    // Color rgb data
    public Vec4 colorData = new Vec4();
    
    public Color(int r, int g, int b, int a){

        colorData.x = r;
        colorData.y = g;
        colorData.z = b;
        colorData.w = a;
    }

    public static Color getLuminanceVariation(Color baseColor, float lum){

        return new Color(
            (int)(lum*baseColor.colorData.x),
            (int)(lum*baseColor.colorData.y),
            (int)(lum*baseColor.colorData.z),
            (int) baseColor.colorData.w
        ).clampColor(30, 225);
    }

    public Color copy(){

        return new Color(
            (int) colorData.x, 
            (int) colorData.y, 
            (int) colorData.z, 
            (int) colorData.w
        );
    }

    public Color clampColor(int lim0, int lim1){

        colorData.clamp(lim0, lim1);
        return this;
    }

    public byte[] toByte(){
        return colorData.forEach();
    }

    @Override
    public String toString() {
        return "(r: "+colorData.x+", g:"+colorData.y+", b:"+colorData.z+", a:"+colorData.w+")";
    }
}
