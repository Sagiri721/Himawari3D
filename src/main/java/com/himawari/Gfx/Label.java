package com.himawari.Gfx;

import java.awt.Font;
import java.awt.image.BufferedImage;

import com.himawari.HLA.Vec2;
import com.himawari.Utils.Utils;

public class Label {

    final String text;

    Font font;

    String style;
    int type, size;

    Vec2 imageDimensions = new Vec2();

    public Label(String text) {

        this.text = text;

        style = Font.MONOSPACED;
        type = Font.PLAIN;
        size = 12;

        font = new Font(style, type, size);

        UpdateImageDimensions();
    }

    public Label(String text, String style, int type, int size) {

        this.text = text;

        this.style = style;
        this.type = type;
        this.size = size;

        font = new Font(style, type, size);

        UpdateImageDimensions();
    }

    private void UpdateImageDimensions() {

        int width = 0, height = 0;

        for (int i = 0; i < text.length(); i++) {
            
            char c = text.charAt(i);
            BufferedImage charImage = Utils.CreateCharImage(font, c);

            width += charImage.getWidth();
            height = Math.max(height, charImage.getHeight());
        }

        imageDimensions = new Vec2(width, height);
    }
}
