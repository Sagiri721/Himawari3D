package com.himawari.Gfx;

import static org.lwjgl.opengl.GL11.GL_BYTE;
import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_HEIGHT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WIDTH;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glGetTexLevelParameteri;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import com.himawari.HLA.Vec2;
import com.himawari.Utils.Utils;

public class Text {

    // The text texture to render
    public int id;

    final String text;
    Font font;

    // The font style, type and size
    String style;
    int type, size;

    // The image dimensions for the text
    Vec2 imageDimensions = new Vec2();

    public Text(String text) {

        this.text = text;

        style = Font.MONOSPACED;
        type = Font.PLAIN;
        size = 12;

        font = new Font(style, type, size);

        UpdateImageDimensions();
    }

    public Text(String text, String style, int type, int size) {

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
        MakeImage();
    }

    private void MakeImage() {

        // Create ARGB image with white text on transparent BG
        BufferedImage texture = new BufferedImage((int)imageDimensions.x, (int)imageDimensions.y, BufferedImage.TYPE_INT_ARGB);    
        Graphics2D g = texture.createGraphics();

        // Draw text
        g.setFont(font);
        g.drawString(text, 0, font.getSize());

        g.dispose();

        // Register this texture
        id = Utils.createTexture(texture);
    }

    public void Render(Vec2 position, Color color) {

        Graphics.RenderTexture(id, position.x, position.y, imageDimensions.x, imageDimensions.y, Optional.of(color));
    }
}
