package com.himawari.Utils;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import com.himawari.Gfx.Color;
import com.himawari.Gfx.Graphics;
import com.himawari.Gfx.Projection;
import com.himawari.Gfx.Renderer;
import com.himawari.Gfx.Text;
import com.himawari.Gfx.LabelSettings;
import com.himawari.HLA.Vec2;
import com.himawari.Input.Input;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.Font;
import java.awt.image.BufferedImage;

public class Window implements AutoCloseable {

    private static Window instance;

    public static Window getInstance(){
        return instance;
    }

    // Window handle
    private long window;

    // Window ar
    public float aspectRatio;

    // Tick tracking
    public long ticks, lastFrame, elapsedTime;
    public double fps, frameDelta;

    private final WindowConfig config;

    public Window(WindowConfig config) {

        if (instance != null) throw new IllegalStateException("Window already created");
        instance = this;

        this.config = config;
        this.aspectRatio = (float)config.width / (float)config.height;

        // Load projection information
        Projection.LoadMatrixInformation();

        // Init input buffer
        Input.Init();

        InitWindow(config.name);
        Loop();

        // Clean up
        close();
    }

    @Override
    public void close(){

        System.out.println("#### Execution finished ####");
        System.out.println("Ticks: " + ticks);
        System.out.println("- Cleaning window");

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void InitWindow(String name){

        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay visible after creation
        
        if (config.resizable)
            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // The window will be resizable

        // Create the window
        window = glfwCreateWindow(config.width, config.height, name, NULL, NULL);

        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        if (config.startOnCenter) {

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window, (vidmode.width() - config.width) / 2, (vidmode.height() - config.height) / 2);
        }

        // Setup a key callback
        // This will be called everytime a key is pressed
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true);

            Input.keyCallback(key, action);
        });
        
        // Set up GL context
        glfwMakeContextCurrent(window);
        glfwShowWindow(window);
        glfwWindowHint(GLFW_DEPTH_BITS, 24);

        if (config.useVSync)
            glfwSwapInterval(1); // Enable v-sync

        // Set lwjgl to interface with an OpenGL context
        // This creates the GLCapabilities instance and makes the OpenGL bindings available for use
        GL.createCapabilities();

        // Project window space to normalized device coordinates
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, config.width, config.height, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        // Disable open gl culling
        glDisable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

        // texture envoronment
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

        // Alpha blending
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Set up OpenGL
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        ticks = 0;
        fps = 0;
        frameDelta = 0;
        elapsedTime = 0;
        lastFrame = System.currentTimeMillis();
    }

    private void Loop() {

        BufferedImage image = Utils.loadImage("debug.png");
        int textureId = Utils.createTexture(image);

        LabelSettings settings = new LabelSettings();
        settings.size = 20;
        settings.style = Font.MONOSPACED;
        settings.type = Font.PLAIN;
        settings.useAntiAliasing = false;

        Text text = new Text("FPS: " + fps, settings);
        
        glClearColor(Renderer.clearColor.r, Renderer.clearColor.g, Renderer.clearColor.b, Renderer.clearColor.a);

        while (!glfwWindowShouldClose(window)) {

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Realize tick operations
            frameDelta = (System.currentTimeMillis() - lastFrame) / 1000f;
            lastFrame = System.currentTimeMillis();

            if (frameDelta > 0) fps = 1 / (frameDelta);

            ticks++;
            elapsedTime += frameDelta;

            //Renderer.renderQueue.get(0).transform.Rotate(new Vec3(0.01f,0.01f, 0));

            Renderer.Render();

            //Graphics.RenderTexture(textureId, 0, 0, 84, 17);
            if (ticks % 60 == 0) 
                text.setText("FPS: " + (int)fps);
            text.Render(new Vec2(0, 0), Color.RED);

            // Tick input
            Input.tick();

            glfwSwapBuffers(window); 
            glfwPollEvents();
        }
    }

    public WindowConfig config() {
        return config;
    }
}
