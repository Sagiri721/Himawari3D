package com.himawari.Gfx;

import com.himawari.HLA.Vec4;
import com.himawari.Utils.Utils;

public class Color {
    
    // Frequent colors
    public static final Color BLACK = new Color(0,0,0,255);
    public static final Color WHITE = new Color(255,255,255,255);

    public static final Color RED = new Color(255,0,0,255);
    public static final Color GREEN = new Color(0,255,0,255);
    public static final Color BLUE = new Color(0,0,255,255);

    // Color rgb data
    public byte r, g, b, a;
    
    public Color(int r, int g, int b, int a){

        this.r = (byte) r;
        this.g = (byte) g;
        this.b = (byte) b;
        this.a = (byte) a;
    }

    public Color(int rgba){

        this.a = (byte) (rgba & 0xFF);
        this.b = (byte) ((rgba >> 8) & 0xFF);
        this.g = (byte) ((rgba >> 16) & 0xFF);
        this.r = (byte) ((rgba >> 24) & 0xFF);
    }

    public Color(Vec4 vec4){

        this.r = (byte) vec4.x;
        this.g = (byte) vec4.y;
        this.b = (byte) vec4.z;
        this.a = (byte) vec4.w;
    }

    public Color(byte[] color){

        this.r = color[0];
        this.g = color[1];
        this.b = color[2];
        this.a = color[3];
    }

    public static Color getLuminanceVariation(Color baseColor, float lum){

        return new Color(
            (int)(lum * (baseColor.r & 0xFF)),
            (int)(lum * (baseColor.g & 0xFF)),
            (int)(lum * (baseColor.b & 0xFF)),
            (int)(baseColor.a & 0xFF)
        ).clampColor(30, 225);
    }

    public Color copy(){

        return new Color(
            r & 0xFF,
            g & 0xFF,
            b & 0xFF,
            a & 0xFF
        );
    }

    public Color clampColor(int lim0, int lim1) {
        r = (byte) Utils.clamp(r & 0xFF, lim0, lim1);
        g = (byte) Utils.clamp(g & 0xFF, lim0, lim1);
        b = (byte) Utils.clamp(b & 0xFF, lim0, lim1);
        a = (byte) Utils.clamp(a & 0xFF, lim0, lim1);
        return this;
    }

    public byte[] toByte(){    
        return new byte[]{r, g, b, a};
    }
    
    public int toInt() {
        return ((r & 0xFF) << 24) | ((g & 0xFF) << 16) | ((b & 0xFF) << 8) | (a & 0xFF);
    }

    @Override
    public String toString() {
        return "Color [a=" + a + ", r=" + r + ", g=" + g + ", b=" + b + "]";
    }
}
