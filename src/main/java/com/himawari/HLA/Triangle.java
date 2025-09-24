package com.himawari.HLA;

import com.himawari.Gfx.Color;
import com.himawari.Utils.Utils;

public class Triangle {
 
    // Triangle vertices
    public Vertex[] vertices = new Vertex[3];
    public Color col = Color.WHITE;

    public Triangle(Vertex vertex1, Vertex vertex2, Vertex vertex3){
        
        this.vertices[0] = vertex1;
        this.vertices[1] = vertex2;
        this.vertices[2] = vertex3;
    }

    public Triangle(){
        
        this.vertices[0] = new Vertex();
        this.vertices[1] = new Vertex();
        this.vertices[2] = new Vertex();
    }

    public Triangle(Vertex[] vertices){
        this.vertices = vertices;
    }

    public Triangle copy(){
        Triangle t = new Triangle(vertices);
        t.col = this.col;
        return t;
    }

    public Vertex get(int index){
        return vertices[index];
    }

    public void set(int index, Vertex value){
        vertices[index] = value;
    }

    public void set(Triangle other){
        vertices = other.vertices;
    }

    public float depth(int i) {

        Vec2 screenPos = new Vec2(vertices[i].position.x, vertices[i].position.y);
        float depth = Utils.ProjectOntoToFace(vertices[0], vertices[1], vertices[2], new Vec3(screenPos)).z;
        return depth;
    }

    @Override
    public String toString(){
        return "("+vertices[0]+"),("+vertices[1]+"),("+vertices[2]+")";
    }
}
