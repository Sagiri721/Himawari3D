package com.himawari.Gfx;

import java.awt.Font;

public class LabelSettings {

    // The font style, type and size
    public String style;
    public int type, size;

    public boolean useAntiAliasing;

    public LabelSettings(String style, int type, int size, boolean useAntiAliasing) {
        this.style = style;
        this.type = type;
        this.size = size;
        this.useAntiAliasing = useAntiAliasing;
    }

    public LabelSettings(String style, int type, int size) {
        this(style, type, size, false);
    }

    public LabelSettings() {
        this(Font.MONOSPACED, Font.PLAIN, 12, false);
    }
}