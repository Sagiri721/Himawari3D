package com.himawari.HLA;

public class Vec3 implements Comparable<Vec3> {

    public static final Vec3 ZERO = new Vec3(0,0,0);
    public static final Vec3 ONE = new Vec3(1,1,1);

    public static final Vec3 UP = new Vec3(0, 1,0);
    public static final Vec3 DOWN = new Vec3(0, -1,0);
    public static final Vec3 RIGHT = new Vec3(1, 0,0);
    public static final Vec3 LEFT = new Vec3(-1, 0,0);

    public static final Vec3 FORWARD = new Vec3(0, 0,1);

    // 3D coordinates
    public float x, y, z;

    public static Vec3 from(float value){

        return new Vec3(value, value, value);
    }

    public static Vec3 from(float[] value){

        return new Vec3(value[0], value[1], value[2]);
    }

    public Vec3(){

        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vec3(float x, float y, float z) {

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static float DotProduct(Vec3 vec1, Vec3 vec2){

        return vec1.x * vec2.x + vec1.y * vec2.y + vec1.z * vec2.z;
    }

    public static Vec3 CrossProduct(Vec3 vec1, Vec3 vec2) {

        return new Vec3(
            vec1.y * vec2.z - vec1.z * vec2.y,
            vec1.z * vec2.x - vec1.x * vec2.z,
            vec1.x * vec2.y - vec1.y * vec2.x
        );
    }

    public Vec3 scale(float other) {

        this.x *= other;
        this.y *= other;
        this.z *= other;

        return this;
    }

    public Vec3 dot(Vec3 other) {

        this.x *= other.x;
        this.y *= other.y;
        this.z *= other.z;

        return this;
    }

    public Vec3 subtract(Vec3 other) {

        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;

        return this;
    }
    
    public Vec3 divide(Vec3 other) {

        this.x /= other.x;
        this.y /= other.y;
        this.z /= other.z;

        return this;
    }

    public Vec3 divide(float other) {

        this.x /= other;
        this.y /= other;
        this.z /= other;

        return this;
    }

    public Vec3 sum(Vec3 other) {

        this.x += other.x;
        this.y += other.y;
        this.z += other.z;

        return this;
    }

    public float sum() {return x + y + z;};

    public float magnitude() {

        double sum = Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2);
        return (float) Math.sqrt(sum);
    }

    public Vec3 normalized(){

        return this.divide(Vec3.from(this.magnitude()));
    }

    public float getVectorValue(int index){

        if(index == 0) return x;
        if(index == 1) return y;
        if(index == 2) return z;

        return 0;
    }

    public Vec3 flip(int index){

        if(index == 0) x = -x;
        if(index == 1) y = -y;
        if(index == 2) z = -z;

        return this;
    }

    public Vec3 invert(){

        return this.scale(-1);
    }
    
    public Vec3 copy(){

        return new Vec3(x, y, z);
    }

    @Override
    public String toString(){ return "("+x+", "+ y +", "+z+")"; }
    public String Format(){ return "("+x+", "+ y +", "+z+")"; }

    @Override
    public int compareTo(Vec3 o) {
        
        return (int) Math.signum(magnitude() - o.magnitude());
    }

    public static float getAngle(Vec3 from, Vec3 to){

        return (float) Math.acos(DotProduct(from, to) / (from.magnitude() * to.magnitude()));
    }
}