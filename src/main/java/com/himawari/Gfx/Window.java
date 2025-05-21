package com.himawari.Gfx;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import com.himawari.GUI.GuiApp;
import com.himawari.Input.Input;
import com.himawari.Input.InputListener;
import com.himawari.Recording.Recorder;
import com.himawari.Utils.LabelSettings;
import com.himawari.Utils.Logger;
import com.himawari.Utils.PipelineListener;
import com.himawari.Utils.RenderEnvironment;
import com.himawari.Utils.WindowConfig;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.awt.Font;
public class Window implements AutoCloseable {

    private static Window instance;
    private IRenderer renderer;

    public static Window getInstance(){
        return instance;
    }

    // Window handle
    public long window;

    // Window ar
    public float aspectRatio;

    // Listeners to window events
    private static final CopyOnWriteArrayList<PipelineListener> listeners = new CopyOnWriteArrayList<PipelineListener>();

    // Tick tracking
    public long ticks, lastFrame, elapsedTime, startTime;
    public double fps, frameDelta;

    private final WindowConfig config;

    public static void hookListener(PipelineListener listener) {
        listeners.add(listener);
    }

    public static void unhookListener(PipelineListener listener) {
        listeners.remove(listener);
    }

    public Window(WindowConfig config) {

        if (instance != null) throw new IllegalStateException("Window already created");
        instance = this;

        this.config = config;
        this.aspectRatio = (float)config.width / (float)config.height;

        // Load projection information
        Projection.LoadMatrixInformation();

        // Init input buffer
        Input.Init();

        InitWindow();
    }

    @Override
    public void close(){

        long simulationTime = System.currentTimeMillis() - startTime;

        Logger.LogInfo("#### Execution finished ####");
        Logger.LogInfo("Ticks: " + ticks);
        Logger.LogInfo("Simulation time: " + simulationTime + "ms");
        Logger.LogInfo("- Cleaning window");

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void InitWindow(){

        Logger.LogInfo("#### Initializing window ####");

        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay visible after creation
        glfwWindowHint(GLFW_RESIZABLE, config.resizable ? GLFW_TRUE : GLFW_FALSE);

        // Create the window
        window = glfwCreateWindow(config.width, config.height, config.name, NULL, NULL);

        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        if (config.startOnCenter) {

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window, (vidmode.width() - config.width) / 2, (vidmode.height() - config.height) / 2);
        }

        // Setup a key callback
        // This will be called everytime a key is pressed
                // Register the key callback
        GLFW.glfwSetKeyCallback(Window.getInstance().window, (window, key, scancode, action, mods) -> {
            
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                GLFW.glfwSetWindowShouldClose(window, true);

            Input.keyCallback(key, action);
        });

        // Register the mouse button callback
        GLFW.glfwSetMouseButtonCallback(Window.getInstance().window, (window, button, action, mods) -> {
            Input.mouseCallback(button, action);
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

        Logger.LogInfo("OpenGL version: " + glGetString(GL_VERSION));

        // // Project window space to normalized device coordinates
        // glViewport(0, 0, config.width, config.height);

        // glMatrixMode(GL_PROJECTION);
        // glLoadIdentity();
        // glOrtho(0, config.width, config.height, 0, -1, 1);
        // glMatrixMode(GL_MODELVIEW);
        // glLoadIdentity();

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
        glClearColor(config.clearColor.r, config.clearColor.g, config.clearColor.b, config.clearColor.a);
        
        Logger.LogInfo("OpenGL flags:");
        Logger.LogInfo("GL_DEPTH_TEST: " + glIsEnabled(GL_DEPTH_TEST));
        Logger.LogInfo("GL_CULL_FACE: " + glIsEnabled(GL_CULL_FACE));
        Logger.LogInfo("GL_BLEND: " + glIsEnabled(GL_BLEND));

        // Find graphics card
        Logger.LogInfo("Graphics card: " + glGetString(GL_RENDERER));

        // Set up graphics
        try {
            
            Class<? extends IRenderer> targetRenderer = config.targetRenderer;
            Constructor<?> ctor = targetRenderer.getConstructor();

            this.renderer = (IRenderer) ctor.newInstance();

            Logger.LogInfo("Renderer loaded: " + this.renderer.getClass());

        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Renderer must have a constructor that takes void");
        } catch (SecurityException e) {
            throw new IllegalStateException("Renderer constructor must be accessible");
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        
        if (this.renderer instanceof RenderEnvironment)
            this.renderer.Init();
        else throw new IllegalStateException("Renderer doesnt have an environment");

        ticks = 0;
        fps = 0;
        frameDelta = 0;
        elapsedTime = 0;
        lastFrame = System.currentTimeMillis();

        startTime = System.currentTimeMillis();

        for (PipelineListener listener : listeners) 
            listener.init();

        LabelSettings settings = new LabelSettings();
        settings.size = 20;
        settings.style = Font.MONOSPACED;
        settings.type = Font.PLAIN;
        settings.useAntiAliasing = false;
    }

    public void Loop() {

        // BufferedImage image = Utils.loadImage("debug.png");
        // int textureId = Utils.createTexture(image);

        while (!glfwWindowShouldClose(window)) {

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Realize tick operations
            frameDelta = (System.currentTimeMillis() - lastFrame) / 1000f;
            lastFrame = System.currentTimeMillis();

            if (frameDelta > 0) fps = 1 / (frameDelta);

            ticks++;
            elapsedTime += frameDelta;

            //Renderer.renderQueue.get(0).transform.Rotate(new Vec3(0.01f,0.01f, 0));

            // Render the scene
            renderer.Render();

            for ( PipelineListener listener : listeners) 
                listener.update();

            // Debug menu
            // if (Input.debugMenu) {

            //     // Show fps
            //     if (ticks % 60 == 0) 
            //         text.setText("FPS: " + (int)fps);

            //     for (int i = 0; i < debugMenu.length; i++) {

            //         Text t = debugMenu[i];

            //         // Draw text
            //     }
            // }
            
            
            // Tick input
            Input.tick();

            glfwSwapBuffers(window); 
            glfwPollEvents();
        }

        for (PipelineListener listener : listeners) 
            listener.cleanUP();
    }

    public WindowConfig config() {
        return config;
    }

    public IRenderer currentRenderer() {
        return renderer;
    }

    public RenderEnvironment currentRenderEnvironment() {
        return (RenderEnvironment)renderer;
    }
}
