package com.himawari.Utils;

import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

import com.himawari.Gfx.Color;
import com.himawari.Gfx.Projection;
import com.himawari.Gfx.Window;
import com.himawari.HLA.Mat4;
import com.himawari.HLA.Triangle;
import com.himawari.HLA.Vec2;
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

    public static void TransformVectorInPlace(float[] vector, Mat4 matrix) {
        float x = vector[0];
        float y = vector[1];
        float z = vector[2];
        float w = 1.0f;
        
        vector[0] = x * matrix.m[0][0] + y * matrix.m[1][0] + z * matrix.m[2][0] + w * matrix.m[3][0];
        vector[1] = x * matrix.m[0][1] + y * matrix.m[1][1] + z * matrix.m[2][1] + w * matrix.m[3][1];
        vector[2] = x * matrix.m[0][2] + y * matrix.m[1][2] + z * matrix.m[2][2] + w * matrix.m[3][2];
        
        // Auto perpective divide
        if (matrix.m.length > 3 && matrix.m[0].length > 3) {
            float w2 = x * matrix.m[0][3] + y * matrix.m[1][3] + z * matrix.m[2][3] + w * matrix.m[3][3];
            if (w2 != 0) {
                vector[0] /= w2;
                vector[1] /= w2;
                vector[2] /= w2;
            }
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
    public static Vec3 CalculateFaceNormal(Triangle triangle) {

        Vec3 normal = new Vec3(0, 0, 0);
        
        // Calculate vectors directly without creating copies
        float line1x = triangle.get(1).x - triangle.get(0).x;
        float line1y = triangle.get(1).y - triangle.get(0).y;
        float line1z = triangle.get(1).z - triangle.get(0).z;
        
        float line2x = triangle.get(2).x - triangle.get(0).x;
        float line2y = triangle.get(2).y - triangle.get(0).y;
        float line2z = triangle.get(2).z - triangle.get(0).z;
        
        normal.x = line1y * line2z - line1z * line2y;
        normal.y = line1z * line2x - line1x * line2z;
        normal.z = line1x * line2y - line1y * line2x;
        
        normal.normalized();
        
        return normal;
    }

    public static Vec3 Vec3IntersectPlane(Vec3 plane, Vec3 normal, Vec3 lineStart, Vec3 lineEnd){

        normal = normal.normalized();
        float planeD = -Vec3.DotProduct(normal, plane);
        float ad = Vec3.DotProduct(lineStart, normal);
        float bd = Vec3.DotProduct(lineEnd, normal);
        float t = (-planeD - ad) / (bd - ad);

        Vec3 lineStartToEnd = lineEnd.copy().subtract(lineStart);
        Vec3 lineIntersec = lineStartToEnd.scale(t);

        return lineStart.copy().sum(lineIntersec);
    }

    public static float DistanceToShortestPlanePoint(Vec3 p, Vec3 normal, Vec3 plane){
        
        normal = normal.normalized();
        return (normal.x * p.x + normal.y * p.y + normal.z * p.z - Vec3.DotProduct(normal, plane));
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

    public static BufferedImage loadImage(String filePath) {
        try {
            return ImageIO.read(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load image: " + filePath);
        }
    }

    public static int createTexture(BufferedImage image) {

        int width = image.getWidth();
        int height = image.getHeight();

        // Convert BufferedImage to ByteBuffer 
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        // A swap from ARGB to RGBA has to be made
        // Java buffered image uses ARGB format
        // OpenGL uses RGBA format
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
        
        for (int y = 0; y < height; y++) { 
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red
                buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green
                buffer.put((byte) (pixel & 0xFF));         // Blue
                buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha
            }
        }
        buffer.flip();

        // Create OpenGL texture
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        
        // Set texture parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
        // Upload texture data to memory
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        
        return textureID;
    }

    private static int CompileShader(String source, int type) {

        int id = GL30.glCreateShader(type);
        GL30.glShaderSource(id, source);

        GL30.glCompileShader(id);

        // Check for compilation errors
        int result = GL30.glGetShaderi(id, GL30.GL_COMPILE_STATUS);
        if(result == GL_FALSE) {
            Logger.LogError(GL30.glGetShaderInfoLog(id));
            return 0;
        }

        return id;
    }

    /**
     * @param vertexShader source code for the vertex shader
     * @param fragmentShader source code for the fragment shader
     * @return
     */
    public static int CreateShader(String vertexShader, String fragmentShader) {

        int program = GL30.glCreateProgram();

        // Load shader source code
        int vs = CompileShader(vertexShader, GL30.GL_VERTEX_SHADER);
        int fs = CompileShader(fragmentShader, GL30.GL_FRAGMENT_SHADER);

        // Attach shaders to program
        GL30.glAttachShader(program, vs);
        GL30.glAttachShader(program, fs);

        GL30.glLinkProgram(program);
        GL30.glValidateProgram(program); // TODO: learn about this https://docs.gl/gl3/glValidateProgram

        // Delete shaders after linking
        // They no longer need to be stored in memory
        GL30.glDeleteShader(vs);
        GL30.glDeleteShader(fs);

        return program;
    }

    public static Vec2 ScreenSpaceToNormalizedCoordinates(Vec3 screnSpace) {

        float xx = (screnSpace.x / Window.getInstance().config().width) * 2 - 1;
        float yy = (screnSpace.y / Window.getInstance().config().height) * -2 + 1;

        return new Vec2(xx, yy);
    }

    public static int randomInt(int i, int j, long seed) {
        
        Random rand = new Random(seed);
        return rand.nextInt(j - i) + i;
    }
    
    public static Color RandomColor(long seed) {

        return new Color(
            Utils.randomInt(0, 255, seed),
            Utils.randomInt(0, 255, seed + 1),
            Utils.randomInt(0, 255, seed + 2),
            255
        );
    }

    // Given a 3D plane, correctly project the point to the matching Z axis
    public static Vec3 ProjectOntoToFace(Vec3 vertex1, Vec3 vertex2, Vec3 vertex3, Vec3 point){

        Vec3 v1 = vertex2.copy().subtract(vertex1);
        Vec3 v2 = vertex3.copy().subtract(vertex1);

        Vec3 planeCoefficients = Vec3.CrossProduct(v1, v2);
        float planeConstant = planeCoefficients.copy().dot(vertex1).sum();

        float zComponent = (planeConstant - point.x*planeCoefficients.x - point.y * planeCoefficients.y) / planeCoefficients.z;
        point.z = zComponent;

        return point;
    }

    public static float ProjectOntoFaceScalar(float[] triangle, float pointX, float pointY) {
        
        float v1x = triangle[3] - triangle[0]; // v2.x - v1.x
        float v1y = triangle[4] - triangle[1]; // v2.y - v1.y
        float v1z = triangle[5] - triangle[2]; // v2.z - v1.z
        
        float v2x = triangle[6] - triangle[3]; // v3.x - v2.x
        float v2y = triangle[7] - triangle[4]; // v3.y - v2.y
        float v2z = triangle[8] - triangle[5]; // v3.z - v2.z
        
        // Cross product to get the normal vector of the plane
        float normalX = v1y * v2z - v1z * v2y;
        float normalY = v1z * v2x - v1x * v2z;
        float normalZ = v1x * v2y - v1y * v2x;
        
        float planeConstant = -(normalX * triangle[0] + normalY * triangle[1] + normalZ * triangle[2]);
        
        if (normalZ == 0)
            return 0;
        
        return (-planeConstant - normalX * pointX - normalY * pointY) / normalZ;
    }
    
    // Keep the original method but make it use the scalar version
    public static Vec3 ProjectOntoFace(float[] triangle, Vec3 point) {
        point.z = ProjectOntoFaceScalar(triangle, point.x, point.y);
        return point;
    }

    public static double Lerp(float a, float b, float t) {
        System.out.println(t);
        return a + t * (b - a);
    }
}
