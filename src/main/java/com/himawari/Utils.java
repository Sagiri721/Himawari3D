package com.himawari;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.himawari.Gfx.Color;
import com.himawari.Gfx.Projection;
import com.himawari.HLA.Mat4;
import com.himawari.HLA.Vec3;
import com.himawari.HLA.Vec4;

import io.github.libsdl4j.api.render.SDL_Vertex;

public class Utils {

    // Frequent colors
    public static final Color BLACK = new Color(0,0,0,255);
    public static final Color WHITE = new Color(255,255,255,255);
    
    public static final Color RED = new Color(255,0,0,255);
    public static final Color GREEN = new Color(0,255,0,255);
    public static final Color BLUE = new Color(0,0,255,255);
    
    // Return the contents of a text file
    public static String GetFileContents(String filename) throws IOException{

        File file = new File(filename);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line, fileContent = "";

        while((line = reader.readLine()) != null) fileContent += line + "\n";
        reader.close();

        return fileContent;
    }

    // Convert a point to an SDL vertex
    public static SDL_Vertex ToVertex(Vec3 point, Vec4 color){
        return new SDL_Vertex(point.x, point.y, (byte) color.x, (byte) color.y, (byte) color.z, (byte) color.w,  0f, 0f);
    }

    // Print matrix
    public static void PrintMatrix(Mat4 m){

        for (int i = 0; i < m.m.length; i++) {
            for (int j = 0; j < m.m[i].length; j++) {
                System.out.print(m.m[i][j] + " ");
            }
            System.err.println();
        }
    }

    // Project 3D vector
    public static Vec3 MultiplyMatrixVector(Vec3 input, Mat4 matrix){

        Vec3 output = new Vec3();
        output.x = input.x * matrix.m[0][0] + input.y * matrix.m[1][0] + input.z * matrix.m[2][0] + matrix.m[3][0];
        output.y = input.x * matrix.m[0][1] + input.y * matrix.m[1][1] + input.z * matrix.m[2][1] + matrix.m[3][1];
        output.z = input.x * matrix.m[0][2] + input.y * matrix.m[1][2] + input.z * matrix.m[2][2] + matrix.m[3][2];
        
        float w = input.x * matrix.m[0][3] + input.y * matrix.m[1][3] + input.z * matrix.m[2][3] + matrix.m[3][3];

        if(w == 0) {return output;}

        output.x /= w;
        output.y /= w;
        output.z /= w;

        return output;
    }

    public static void MatrixPointAt(Vec3 position, Vec3 target, Vec3 up){

        // Calculate new forward direction
        Vec3 newForward = target.copy().subtract(position).normalized();
        // Calculate new upwards direction
        Vec3 a = newForward.copy().scale(Vec3.DotProduct(up, newForward));
        Vec3 newUp = up.copy().subtract(a).normalized().invert();

        // New right direction is the cross product of the two previous vectors
        Vec3 newRight = Vec3.CrossProduct(newUp, newForward);

        // Apply matrix transformations of camera behaviour
        // look into wtf this does cuz i have no idea :3
        Projection.ProjectCameraViewToAxis(newForward, newUp, newRight, position);
        Projection.ProjectCameraViewToAxisInvert();
    }

    // Calculates the normal to a surface defined by 3 points
    public static Vec3 CalculateFaceNormal(Vec3[] triangle){
    
        Vec3 line1, line2, normal = new Vec3();
        line1 = triangle[1].copy().subtract(triangle[0].copy());
        line2 = triangle[2].copy().subtract(triangle[0].copy());

        normal = Vec3.CrossProduct(line1, line2).normalized();

        return normal;
    }
}
