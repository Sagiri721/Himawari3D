package com.himawari.Utils;

import com.himawari.Gfx.Mesh;
import com.himawari.HLA.Vec3;

public class Transform {

    public Vec3 scale = new Vec3(1, 1, 1);
    private Vec3 rotation = new Vec3(0,0,0);
    public Vec3 position = new Vec3(1, 1, 15);

    public Transform(){
    }

    public void Rotate(Vec3 rotation){
        this.rotation.sum(rotation.copy());
    }

    public void setRotation(Vec3 rotation){
        this.rotation = rotation.copy();
    }

    public Vec3 getRotation(){
        return rotation;
    }
}
