package com.himawari.Utils;

import com.himawari.Gfx.Color;
import com.himawari.Gfx.IRenderer;
import com.himawari.Gfx.RendererGPU;

public class WindowConfig {

    public final int width, height;
    public final String name;

    public final boolean useVSync;
    public final boolean resizable;
    public final boolean startOnCenter;

    public final Class<? extends IRenderer> targetRenderer;

    public final Color clearColor;

    public WindowConfig() {

        this.width = 768;
        this.height = 768;

        this.name = "Himawari3D";
        this.useVSync = false;
        this.resizable = false;
        this.startOnCenter = true;

        this.clearColor = Color.BLACK;

        this.targetRenderer = RendererGPU.class;
    }

    public WindowConfig(int width, int height, String name) {
        
        this.width = width;
        this.height = height;
        this.name = name;

        this.useVSync = true;
        this.resizable = false;
        this.startOnCenter = true;

        this.clearColor = Color.BLACK;

        this.targetRenderer = RendererGPU.class;
    }

    public WindowConfig(
        int width, int height, String name, 
        boolean useVSync, boolean resizable, boolean startOnCenter, 
        Color clearColor, 
        Class<? extends IRenderer> targetRenderer
    ) {
        
        this.width = width;
        this.height = height;
        this.name = name;

        this.useVSync = useVSync;
        this.resizable = resizable;
        this.startOnCenter = startOnCenter;

        this.clearColor = clearColor;

        this.targetRenderer = targetRenderer;
    }
}
