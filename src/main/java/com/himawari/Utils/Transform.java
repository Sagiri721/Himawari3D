package com.himawari.Utils;

import com.himawari.Gfx.Mesh;
import com.himawari.HLA.Vec3;

public class Transform {

    private Mesh reference;

    public Vec3 scale = new Vec3(1, 1, 1);
    private Vec3 rotation = new Vec3(0,0,0);
    public Vec3 position = new Vec3(1, 1, 15);

    public Transform(Mesh reference){
        this.reference = reference;
    }

    public void Rotate(Vec3 rotation){

        // Always recalculate normals when rotating
        this.rotation.sum(rotation.copy());
        reference.calculateNormals();
    }

    public void setRotation(Vec3 rotation){

        // Always recalculate normals when rotating
        this.rotation = rotation.copy();
        reference.calculateNormals();
    }

    public Vec3 getRotation(){
        return rotation;
    }
}
