package com.himawari.Gfx;

import java.io.IOException;

import org.lwjgl.opengl.GL30;

import com.himawari.Utils.Utils;

public class Shader {
    
    public int shaderProgram;

    private final String vertexShaderContent;
    private final String fragShaderContent;

    public Shader(String vertexShaderPath, String fragShaderPath) throws IOException {
        
        this.vertexShaderContent = Utils.GetFileContents(vertexShaderPath);
        this.fragShaderContent = Utils.GetFileContents(fragShaderPath);
    }

    public String getFragmentShaderContent() {
        return fragShaderContent;
    }

    public String getVertexShaderContent() {
        return vertexShaderContent;
    }

    public void use() {
        shaderProgram = Utils.CreateShader(this);
        GL30.glUseProgram(shaderProgram);
    }

    public void setUVec2(String string, float x, float y) {
        
        int uLoc = GL30.glGetUniformLocation(shaderProgram, string);
        GL30.glUniform2f(uLoc, x, y);
    }

    public void setUVec3(String string, float x, float y, float z) {
        
        int uLoc = GL30.glGetUniformLocation(shaderProgram, string);
        GL30.glUniform3f(uLoc, x, y, z);
    }

    public void setUMat4(String string, float[] matrix) {
        
        int uLoc = GL30.glGetUniformLocation(shaderProgram, string);
        GL30.glUniformMatrix4fv(uLoc, false, matrix);
    }

    public void setUFloat(String string, float value) {
        
        int uLoc = GL30.glGetUniformLocation(shaderProgram, string);
        GL30.glUniform1f(uLoc, value);
    }

    public void setUInt(String string, int value) {
        
        int uLoc = GL30.glGetUniformLocation(shaderProgram, string);
        GL30.glUniform1i(uLoc, value);
    }
}
