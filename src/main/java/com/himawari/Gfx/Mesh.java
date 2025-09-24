package com.himawari.Gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.himawari.HLA.Vec2;
import com.himawari.HLA.Vec3;
import com.himawari.HLA.Vertex;
import com.himawari.Utils.Logger;
import com.himawari.Utils.Transform;
import com.himawari.Utils.Utils;

public class Mesh {

    public static final String VERTEX_CUE = "v";
    public static final String FACE_CUE = "f";

    public Transform transform = new Transform();

    public Color base = Color.WHITE;
    public boolean lit = true;
    
    public Vertex[] vertices;
    public short[][] faces;

    private int texture = -1;

    // Empty constructor
    public Mesh() {}

    public Mesh(List<Vertex> vertices, List<int[]> faces){

        this.vertices = new Vertex[vertices.size()];
        this.faces = new short[faces.size()][];

        for (int i = 0; i < vertices.size(); i++) {
            this.vertices[i] = new Vertex(
                vertices.get(i).position,
                vertices.get(i).texCoord
            );
        }

        for (int i = 0; i < faces.size(); i++) {
            this.faces[i] = new short[faces.get(i).length];
            for (int j = 0; j < faces.get(i).length; j++) {
                this.faces[i][j] = (short) faces.get(i)[j];
            }
        }
    }

    public Mesh(Vertex[] vertices, int[][] faces){

        this.vertices = new Vertex[vertices.length];
        this.faces = new short[faces.length][];

        for (int i = 0; i < vertices.length; i++) {
            this.vertices[i] = new Vertex(
                vertices[i].position,
                vertices[i].texCoord
            );
        }

        for (int i = 0; i < faces.length; i++) {
            this.faces[i] = new short[faces[i].length];
            for (int j = 0; j < faces[i].length; j++) {
                this.faces[i][j] = (short) faces[i][j];
            }
        }
    }

    private void fillArraysWithData(List<Vertex> vertices, List<int[]> faces){

        this.vertices = new Vertex[vertices.size()];
        this.faces = new short[faces.size()][];

        for (int i = 0; i < vertices.size(); i++) {
            this.vertices[i] = new Vertex(
                vertices.get(i).position,
                vertices.get(i).texCoord
            );
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

            List<Vertex> vertices = new ArrayList<Vertex>();
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

                        Vertex vertex = new Vertex();
                        vertex.position.x = Float.parseFloat(values[1]);
                        vertex.position.y = Float.parseFloat(values[2]);
                        vertex.position.z = Float.parseFloat(values[3]);

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
            mesh.vertices = new Vertex[vertices.size()];
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

    public void AddTexture(String texturePath) {

        BufferedImage textureImage = Utils.LoadImage(texturePath);
        this.texture = Utils.CreateTexture(textureImage, true);
    }

    public int getTexture() {
        return this.texture;
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
        clone.texture = this.texture;

        return clone;
    }

    public void dispose() {

        try {
            
            // Clean up the texture if it exists
            if (this.texture != -1) {
                Utils.DeleteTexture(this.texture);
                this.texture = -1;
            }
            
        } catch (Exception e) {
            Logger.LogWarning("Mesh cleanup failed: " + e.getMessage());
        }
    }

    public Vec2[] getTextureCoordinates(short[] s) {
        
        Vec2[] texCoords = new Vec2[s.length];
        for (int i = 0; i < s.length; i++) {
            if (s[i] < vertices.length) {
                texCoords[i] = vertices[s[i]].texCoord;
            }
        }
        return texCoords;
    }
}