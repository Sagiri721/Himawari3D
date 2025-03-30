package com.himawari.Utils;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import com.himawari.Gfx.Projection;
import com.himawari.HLA.Mat4;
import com.himawari.HLA.Triangle;
import com.himawari.HLA.Vec3;

public class Utils {
    
    // Return the contents of a text file
    public static String GetFileContents(String filename) throws IOException{

        File file = new File(filename);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line, fileContent = "";

        while((line = reader.readLine()) != null) fileContent += line + "\n";
        reader.close();

        return fileContent;
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
    public static Vec3 MultiplyMatrixVector(Vec3 input, Mat4 matrix, boolean divide){

        Vec3 output = new Vec3();
        output.x = input.x * matrix.m[0][0] + input.y * matrix.m[1][0] + input.z * matrix.m[2][0] + matrix.m[3][0];
        output.y = input.x * matrix.m[0][1] + input.y * matrix.m[1][1] + input.z * matrix.m[2][1] + matrix.m[3][1];
        output.z = input.x * matrix.m[0][2] + input.y * matrix.m[1][2] + input.z * matrix.m[2][2] + matrix.m[3][2];
        
        float w = input.x * matrix.m[0][3] + input.y * matrix.m[1][3] + input.z * matrix.m[2][3] + matrix.m[3][3];

        if(divide){        
    
            if(w == 0) {return output;}
    
            output.x /= w;
            output.y /= w;
            output.z /= w;
        }

        return output;
    }

    public static void MatrixPointAt(Vec3 position, Vec3 target, Vec3 up){

        // Calculate new forward direction
        Vec3 newForward = target.copy().subtract(position).normalized();

        // Calculate new upwards direction
        Vec3 a = newForward.copy().scale(Vec3.DotProduct(up, newForward));
        Vec3 newUp = up.copy().flip(1).subtract(a).normalized();

        // New right direction is the cross product of the two previous vectors
        Vec3 newRight = Vec3.CrossProduct(newUp, newForward).normalized();

        // Apply matrix transformations of camera behaviour
        // look into wtf this does cuz i have no idea :3
        Projection.ProjectCameraViewToAxis(newForward, newUp, newRight, position);
        Projection.InvertCameraMatrix();
    }

    // Calculates the normal to a surface defined by 3 points
    public static Vec3 CalculateFaceNormal(Triangle triangle){
    
        Vec3 line1, line2, normal = new Vec3();
        line1 = triangle.get(1).copy().subtract(triangle.get(0).copy());
        line2 = triangle.get(2).copy().subtract(triangle.get(0).copy());

        normal = Vec3.CrossProduct(line1, line2).normalized();

        return normal;
    }

    private static Vec3 Vec3IntersectPlane(Vec3 plane, Vec3 normal, Vec3 lineStart, Vec3 lineEnd){

        normal = normal.normalized();
        float planeD = -Vec3.DotProduct(normal, plane);
        float ad = Vec3.DotProduct(lineStart, normal);
        float bd = Vec3.DotProduct(lineEnd, normal);
        float t = (-planeD - ad) / (bd - ad);

        Vec3 lineStartToEnd = lineEnd.copy().subtract(lineStart);
        Vec3 lineIntersec = lineStartToEnd.scale(t);

        return lineStart.copy().sum(lineIntersec);
    }

    private static float DistanceToShortestPlanePoint(Vec3 p, Vec3 normal, Vec3 plane){
        
        normal = normal.normalized();
        return (normal.x * p.x + normal.y * p.y + normal.z * p.z - Vec3.DotProduct(normal, plane));
    }

    public static int ClipTriangleAgainstPlane(Vec3 plane, Vec3 normal, Triangle face, Triangle return1, Triangle return2){

        // Normalize the original plane normal
        normal.normalized();

        // This variables will store the number of points inside and outside the plane space
        // As well as their positions
        Vec3[] insidePoints = new Vec3[3], outsidePoints = new Vec3[3];
        int nInsidePoints = 0, nOutsidePoints = 0;

        // The distance of every point in the original triangle to the given plane
        Float[] originDistance = Arrays.stream(face.vertices).map(v -> DistanceToShortestPlanePoint(v, normal, plane)).toArray(Float[]::new);

        for (int i = 0; i < originDistance.length; i++) {
            
            // Get the Float class value
            float value = originDistance[i].floatValue();

            // When distance is positive, the vertex is inside
            // When it is negative, it is outside
            if(value >= 0) insidePoints[nInsidePoints++] = face.vertices[i];
            else outsidePoints[nOutsidePoints++] = face.vertices[i];
        }

        if(nInsidePoints == 0){
            // All points exist on the outside of the plane
            // Skip rendering
            return 0;
        }

        if(nInsidePoints == 3){
            // All points points exist inside the the plane
            // Render the triangle without any modifications
            return1.set(face);
            return 1;
        }

        if(nInsidePoints == 1 && nOutsidePoints == 2){

            // One point is inside and teo outside
            // We can construct a new smaller triangle we 2 new points
            // That's out output
            return1.set(0, insidePoints[0]); 
            return1.set(1, Vec3IntersectPlane(plane, normal, insidePoints[0], outsidePoints[0]));
            return1.set(2, Vec3IntersectPlane(plane, normal, insidePoints[0], outsidePoints[1]));

            return 1;
        }

        if(nInsidePoints == 2 && nOutsidePoints == 1){

            // When two points are inside the plane and the other one's outside
            // We form a quad, and so, we need to break it up into two triangles
            return1.set(0, insidePoints[0]);
            return1.set(1, insidePoints[1]);
            return1.set(2, Vec3IntersectPlane(plane, normal, insidePoints[0], outsidePoints[0]));
            
            return2.set(0, insidePoints[1]);
            return2.set(1, return1.get(2));
            return2.set(2, Vec3IntersectPlane(plane, normal, insidePoints[1], outsidePoints[0]));

            return 2;
        }

        return 0;
    }

    public static float EulerToRadians(float degrees){
        return degrees * (float) Math.PI / 180;
    }

    public static float RadiansToEuler(float radians){
        return radians * 180 / (float) Math.PI;
    }

    public static int clamp(int g, int lim0, int lim1) {
        return Math.max(lim0, Math.min(g, lim1));
    }

    public static final byte[] IntToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    public static final byte[] FloatToByteArray(float value) {
        return IntToByteArray(Float.floatToIntBits(value));
    }

    public static BufferedImage CreateCharImage(Font font, char c) {
        
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();
        g.dispose();

        int charWidth = metrics.charWidth(c);
        int charHeight = metrics.getHeight();

        image = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
        g = image.createGraphics();

        g.setFont(font);
        g.drawString(String.valueOf(c), 0, metrics.getAscent());
        g.dispose();

        return image;
    }
}
