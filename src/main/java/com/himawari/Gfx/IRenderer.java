package com.himawari.Gfx;

import java.util.ArrayList;
import java.util.List;

public interface IRenderer {

    // Init rendering
    public abstract void Init();

    // Render loop
    public abstract void Render();

    // Clean up resources
    public abstract void Dispose();
}
