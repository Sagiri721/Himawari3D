package com.himawari;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.himawari.geom.Vec2;
import com.himawari.geom.Vec3;
import com.himawari.geom.Vec4;

import io.github.libsdl4j.api.render.SDL_Vertex;

public class Utils {

    // Frequent colors
    public static final Vec4 BLACK = new Vec4(0,0,0,255);
    public static final Vec4 WHITE = new Vec4(255,255,255,255);
    
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
}
