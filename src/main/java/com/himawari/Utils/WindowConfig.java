package com.himawari.Utils;

public class WindowConfig {

    public final int width, height;
    public final String name;

    public final boolean useVSync;
    public final boolean resizable;
    public final boolean startOnCenter;

    public WindowConfig() {

        this.width = 768;
        this.height = 768;

        this.name = "Himawari3D";
        this.useVSync = false;
        this.resizable = false;
        this.startOnCenter = true;
    }

    public WindowConfig(int width, int height, String name) {
        
        this.width = width;
        this.height = height;
        this.name = name;

        this.useVSync = true;
        this.resizable = false;
        this.startOnCenter = true;
    }

    public WindowConfig(int width, int height, String name, boolean useVSync, boolean resizable, boolean startOnCenter) {
        
        this.width = width;
        this.height = height;
        this.name = name;

        this.useVSync = useVSync;
        this.resizable = resizable;
        this.startOnCenter = startOnCenter;
    }
}
