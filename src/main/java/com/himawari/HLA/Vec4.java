package com.himawari.HLA;

import io.github.libsdl4j.api.pixels.SDL_Color;
import io.github.libsdl4j.api.rect.SDL_FPoint;
import io.github.libsdl4j.api.render.SDL_Vertex;

public class Vec4 implements Comparable<Vec4> {

    public static final Vec4 ZERO = new Vec4(0,0,0, 0);
    public static final Vec4 ONE = new Vec4(1,1,1, 1);

    // 3D coordinates
    public float x, y, z, w;

    public static Vec4 from(float value){

        return new Vec4(value, value, value, value);
    }

    public static Vec4 from(float[] value){

        return new Vec4(value[0], value[1], value[2], value[3]);
    }

    public Vec4(){

        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
    }

    public Vec4(float x, float y, float z, float w) {

        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public static float DotProduct(Vec4 vec1, Vec4 vec2){

        return vec1.x * vec2.x + vec1.y * vec2.y + vec1.z * vec2.z + vec1.w * vec2.w;
    }

    public Vec4 scale(float other) {

        this.x *= other;
        this.y *= other;
        this.z *= other;
        this.w *= other;

        return this;
    }

    public Vec4 subtract(Vec4 other) {

        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        this.w -= other.w;

        return this;
    }
    
    public Vec4 divide(Vec4 other) {

        this.x /= other.x;
        this.y /= other.y;
        this.z /= other.z;
        this.w /= other.w;

        return this;
    }

    public Vec4 sum(Vec4 other) {

        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        this.w += other.w;

        return this;
    }

    public float magnitude() {

        double sum = Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2) + Math.pow(this.w, 2);
        return (float) Math.sqrt(sum);
    }

    public Vec4 normalized(){

        return this.divide(Vec4.from(this.magnitude()));
    }

    public float getVectorValue(int index){

        if(index == 0) return x;
        if(index == 1) return y;
        if(index == 2) return z;
        if(index == 3) return w;

        return 0;
    }

    public Vec4 flip(int index){

        if(index == 0) x = -x;
        if(index == 1) y = -y;
        if(index == 2) z = -z;
        if(index == 3) z = -w;

        return this;
    }

    public Vec4 invert(){

        return this.scale(-1);
    }
    
    public Vec4 copy(){

        return new Vec4(x, y, z, w);
    }

    @Override
    public String toString(){ return "("+x+", "+ y +", "+z+", "+w+")"; }
    public String Format(){ return "("+x+", "+ y +", "+z+", "+w+")"; }

    @Override
    public int compareTo(Vec4 o) {
        
        return (int) Math.signum(magnitude() - o.magnitude());
    }
}