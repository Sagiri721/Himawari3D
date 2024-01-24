package com.himawari.HLA;

public class Mat4 {
    
    public float[][] m = new float[4][4];

    public Mat4(float f){
        for (int i = 0; i < m.length; i++) {
            m[i][i] = f;
        }
    }

    public Mat4(float[][] data){
        this.m = data;
    }

    public Mat4 Set(int row, int col, float value){
        m[row][col] = value;
        return this;
    }

    public void Set(float[][] matrix){
        this.m = matrix;
    }

    public float Get(int row, int col){
        return m[row][col];
    }

    public Mat4 copy(){
        return new Mat4(m);
    }
}
