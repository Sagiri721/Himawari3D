package com.himawari.geom;

import io.github.libsdl4j.api.pixels.SDL_Color;
import io.github.libsdl4j.api.rect.SDL_FPoint;
import io.github.libsdl4j.api.render.SDL_Vertex;

public class Vec2 implements Comparable<Vec2> {

    public static final Vec2 ZERO = new Vec2(0,0);
    public static final Vec2 ONE = new Vec2(1,1);

    // 2D coordinates
    public float x, y;

    public static Vec2 from(float value){

        return new Vec2(value, value);
    }

    public static Vec2 from(float[] value){

        return new Vec2(value[0], value[1]);
    }

    public Vec2(){

        this.x = 0;
        this.y = 0;
    }

    public Vec2(float x, float y) {

        this.x = x;
        this.y = y;
    }

    public static float DotProduct(Vec2 vec1, Vec2 vec2){

        return vec1.x * vec2.x + vec1.y * vec2.y;
    }

    public static float CrossProduct(Vec2 vec1, Vec2 vec2) {

        return vec1.x * vec2.y - vec1.y * vec2.x;
    }

    public Vec2 scale(float other) {

        this.x *= other;
        this.y *= other;

        return this;
    }

    public Vec2 subtract(Vec2 other) {

        this.x -= other.x;
        this.y -= other.y;

        return this;
    }
    
    public Vec2 divide(Vec2 other) {

        this.x /= other.x;
        this.y /= other.y;

        return this;
    }

    public Vec2 sum(Vec2 other) {

        this.x += other.x;
        this.y += other.y;

        return this;
    }

    public float magnitude() {

        double sum = Math.pow(this.x, 2) + Math.pow(this.y, 2);
        return (float) Math.sqrt(sum);
    }

    public Vec2 normalized(){

        return this.divide(Vec2.from(this.magnitude()));
    }

    public float getVectorValue(int index){

        if(index == 0) return x;
        if(index == 1) return y;

        return 0;
    }

    public Vec2 flip(int index){

        if(index == 0) x = -x;
        if(index == 1) y = -y;

        return this;
    }

    public Vec2 invert(){

        return this.scale(-1);
    }
    
    public Vec2 copy(){

        return new Vec2(x, y);
    }

    @Override
    public String toString(){ return "("+x+", "+ y +")"; }
    public String Format(){ return "("+x+", "+ y +")"; }

    @Override
    public int compareTo(Vec2 o) {
        
        return (int) Math.signum(magnitude() - o.magnitude());
    }
}