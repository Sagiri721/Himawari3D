package com.himawari;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.himawari.Gfx.Color;
import com.himawari.HLA.Vec3;

public class Mesh {

    public static final String VERTEX_CUE = "v";
    public static final String FACE_CUE = "f";

    public Vec3 scale = new Vec3(1, 1, 1);
    public Vec3 rotation = new Vec3(0,0,0);
    public Vec3 position = new Vec3(1, 1, 15);

    public Color base = Color.WHITE;
    
    public Vec3[] vertices;
    public int[][] faces;
    public Vec3[] normals;

    public static Mesh LoadFrom(String filename){

        try {

            List<Vec3> vertices = new ArrayList<Vec3>();
            List<int[]> faces = new ArrayList<int[]>();
            
            String[] text = Utils.GetFileContents(filename).split("\n");
            for (String line : text) {
                
                // Get the line indicator at the start of each line
                if(line.length()==0) continue;
                char indicator = line.charAt(0);

                // Ignore comments
                if(indicator == '#') continue;

                // Divide the data
                String[] values = line.split(" ");

                switch (values[0]) {
                    case VERTEX_CUE:

                        Vec3 vertex = new Vec3();
                        vertex.x = Float.parseFloat(values[1]);
                        vertex.y = Float.parseFloat(values[2]);
                        vertex.z = Float.parseFloat(values[3]);

                        vertices.add(vertex);

                        break;         
                    case FACE_CUE:

                        int[] faceStructure = new int[3];
                        faceStructure[0] = Integer.parseInt(values[1]) - 1;
                        faceStructure[1] = Integer.parseInt(values[2]) - 1;
                        faceStructure[2] = Integer.parseInt(values[3]) - 1;

                        faces.add(faceStructure);
                        break;
                }
            }

            // Initialize mesh
            Mesh mesh = new Mesh();
            mesh.vertices = vertices.toArray(new Vec3[vertices.size()]);
            
            // Transform list of arrays to matrix
            mesh.faces = new int[faces.size()][];
            mesh.faces = faces.toArray(mesh.faces);

            mesh.normals = new Vec3[mesh.faces.length];

            return mesh;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Vec3 ParseFace(String text) {

        Float[] map = Arrays.asList(text.split(" ")).stream().map(Float::valueOf).toArray(Float[]::new);
        return new Vec3(
                map[0].floatValue(),
                map[1].floatValue(),
                map[2].floatValue()
            );
    }
}
