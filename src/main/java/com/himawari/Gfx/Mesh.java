package com.himawari.Gfx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.himawari.HLA.Vec3;
import com.himawari.Utils.Transform;
import com.himawari.Utils.Utils;

public class Mesh {

    public static final String VERTEX_CUE = "v";
    public static final String FACE_CUE = "f";

    public Transform transform = new Transform();

    public Color base = Color.WHITE;
    public boolean lit = true;
    
    public float[] vertices;
    public short[][] faces;

    // Empty constructor
    public Mesh() {}

    public Mesh(List<Vec3> vertices, List<int[]> faces){

        this.vertices = new float[vertices.size() * 3];
        this.faces = new short[faces.size()][];

        for (int i = 0; i < vertices.size(); i++) {
            this.vertices[i * 3] = vertices.get(i).x;
            this.vertices[i * 3 + 1] = vertices.get(i).y;
            this.vertices[i * 3 + 2] = vertices.get(i).z;
        }

        for (int i = 0; i < faces.size(); i++) {
            this.faces[i] = new short[faces.get(i).length];
            for (int j = 0; j < faces.get(i).length; j++) {
                this.faces[i][j] = (short) faces.get(i)[j];
            }
        }
    }

    public Mesh(Vec3[] vertices, int[][] faces){

        this.vertices = new float[vertices.length * 3];
        this.faces = new short[faces.length][];

        for (int i = 0; i < vertices.length; i++) {
            this.vertices[i * 3] = vertices[i].x;
            this.vertices[i * 3 + 1] = vertices[i].y;
            this.vertices[i * 3 + 2] = vertices[i].z;
        }

        for (int i = 0; i < faces.length; i++) {
            this.faces[i] = new short[faces[i].length];
            for (int j = 0; j < faces[i].length; j++) {
                this.faces[i][j] = (short) faces[i][j];
            }
        }
    }

    private void fillArraysWithData(List<Vec3> vertices, List<int[]> faces){

        this.vertices = new float[vertices.size() * 3];
        this.faces = new short[faces.size()][];

        for (int i = 0; i < vertices.size(); i++) {
            this.vertices[i * 3] = vertices.get(i).x;
            this.vertices[i * 3 + 1] = vertices.get(i).y;
            this.vertices[i * 3 + 2] = vertices.get(i).z;
        }

        for (int i = 0; i < faces.size(); i++) {
            this.faces[i] = new short[faces.get(i).length];
            for (int j = 0; j < faces.get(i).length; j++) {
                this.faces[i][j] = (short) faces.get(i)[j];
            }
        }
    }

    // public Vec3[] calculateNormals(){

    //     normals = new Vec3[faces.length];

    //     for (int i = 0; i < faces.length; i++) {
            
    //         Vec3 a = vertices[faces[i][0]];
    //         Vec3 b = vertices[faces[i][1]];
    //         Vec3 c = vertices[faces[i][2]];

    //         Triangle triangle = new Triangle(a, b, c);
    //         normals[i] = Utils.CalculateFaceNormal(triangle);
    //     }

    //     return normals;
    // }

    // Load mesh from .obj file
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
            mesh.vertices = new float[vertices.size() * 3];
            mesh.faces = new short[faces.size()][];
            
            // Fill the vertices array
            mesh.fillArraysWithData(vertices, faces);

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

    public Mesh clone() {
        
        Mesh clone = new Mesh();
        clone.vertices = Arrays.copyOf(this.vertices, this.vertices.length);
        clone.faces = new short[this.faces.length][];
        for (int i = 0; i < this.faces.length; i++) {
            clone.faces[i] = Arrays.copyOf(this.faces[i], this.faces[i].length);
        }

        clone.transform = this.transform.clone();
        clone.base = this.base;
        clone.lit = this.lit;
        return clone;
    }
}
