package com.himawari.Utils;

import java.util.ArrayList;
import java.util.List;

import com.himawari.Gfx.Color;
import com.himawari.Gfx.Mesh;
import com.himawari.Gfx.RenderMode;
import com.himawari.Gfx.RenderTarget;

public class RenderEnvironment {
    
    // List of meshes to display
    private List<Mesh> renderQueue = new ArrayList<Mesh>();
    
    // Definitions
    private RenderMode renderMode = RenderMode.SOLID;
    private RenderTarget renderTarget = RenderTarget.COLORBUFFER;
    private Color clearColor = Color.BLACK;

    public RenderMode getRenderMode(){
        return renderMode;
    }

    public RenderTarget getRenderTarget(){
        return renderTarget;
    }

    public Color getClearColor(){
        return clearColor;
    }

    public List<Mesh> getRenderQueue(){
        return renderQueue;
    }

    public void addMesh(Mesh mesh){
        renderQueue.add(mesh);
    }

    public void setRenderMode(RenderMode mode){
        renderMode = mode;
    }

    public void setRenderTarget(RenderTarget target){
        renderTarget = target;
    }

    public void setClearColor(Color color){
        clearColor = color;
    }

    public void clearRenderQueue(){
        renderQueue.clear();
    }

    public void removeMesh(Mesh mesh){
        renderQueue.remove(mesh);
    }

    public void removeMesh(Mesh[] meshes){
        for (Mesh mesh : meshes) {
            renderQueue.remove(mesh);
        }
    }
}
