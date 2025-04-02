package com.himawari.HLA;

import com.himawari.Gfx.Color;
import com.himawari.Gfx.ZBuffer;
import com.himawari.Utils.Utils;

public class Triangle {

    // Triangle vertices
    public Vec3[] vertices = new Vec3[3];
    public Color col = Color.WHITE;

    public Triangle(Vec3 vertex1, Vec3 vertex2, Vec3 vertex3){
        
        this.vertices[0] = vertex1;
        this.vertices[1] = vertex2;
        this.vertices[2] = vertex3;
    }

    public Triangle(){
        
        this.vertices[0] = new Vec3();
        this.vertices[1] = new Vec3();
        this.vertices[2] = new Vec3();
    }

    public Triangle(Vec3[] vertices){
        this.vertices = vertices;
    }

    public Triangle copy(){
        Triangle t = new Triangle(vertices);
        t.col = this.col;
        return t;
    }

    public Vec3 get(int index){
        return vertices[index];
    }

    public void set(int index, Vec3 value){
        vertices[index] = value;
    }

    public void set(Triangle other){
        vertices = other.vertices;
    }

    public float depth(int i) {

        Vec2 screenPos = new Vec2(vertices[i].x, vertices[i].y);
        float depth = ZBuffer.ProjectOntoToFace(vertices[0], vertices[1], vertices[2], new Vec3(screenPos)).z;
        return depth;
    }

    @Override
    public String toString(){
        return "("+vertices[0]+"),("+vertices[1]+"),("+vertices[2]+")";
    }
}
