package com.himawari.GUI;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

import com.himawari.Gfx.Window;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

public class GuiApp {
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    
    public void init(long window) {

        // Initialize ImGui
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.setConfigFlags(ImGuiConfigFlags.NavEnableKeyboard); // Enable Keyboard Controls
        io.setConfigFlags(ImGuiConfigFlags.DockingEnable); // Enable Docking
        io.setIniFilename(null);

        io.setDisplaySize(Window.getInstance().config().width, Window.getInstance().config().height);

        io.getFonts().build();
        
        imGuiGlfw.init(window, true);
        imGuiGl3.init("#version 120");
    }
    
    public void render() {
        ImGuiIO io = ImGui.getIO();
        io.setDisplaySize(Window.getInstance().config().width, Window.getInstance().config().height);

        // Start the frame
        imGuiGlfw.newFrame();
        ImGui.newFrame();
        
        // Render UI
        ImGui.begin("Hello, world!");
        ImGui.text("This is some useful text.");
        ImGui.end();
        
        // Render ImGui
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }
    
    public void dispose() {
        
        ImGui.destroyContext();
    }
}