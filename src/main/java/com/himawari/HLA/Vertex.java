package com.himawari.HLA;

public class Vertex {

    public Vec3 position;
    public Vec2 texCoord;

    public Vertex(Vec3 position, Vec2 texCoord) {
        this.position = position;
        this.texCoord = texCoord;
    }

    public Vertex() {
        this.position = new Vec3();
        this.texCoord = new Vec2();
    }

    public Vertex(Vec3 position) {
        this.position = position;
        this.texCoord = new Vec2();
    }

    public Vertex clone() {
        return new Vertex(new Vec3(this.position.x, this.position.y, this.position.z), new Vec2(this.texCoord.x, this.texCoord.y));
    }
}
