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
import static org.lwjgl.opengl.GL11.glDeleteTextures;
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
import com.himawari.Utils.LabelSettings;
import com.himawari.Utils.Utils;

public class Text implements AutoCloseable {

    // The text texture to render
    public int id;

    private String text;
    private Font font;

    private LabelSettings settings = new LabelSettings();

    // The image dimensions for the text
    public Vec2 imageDimensions = new Vec2();

    public Text(String text) {

        this.text = text;
        font = new Font(settings.style, settings.type, settings.size);

        UpdateImageDimensions();
    }

    public Text(String text, LabelSettings set) {

        this.text = text;

        settings = set;
        font = new Font(settings.style, settings.type, settings.size);

        UpdateImageDimensions();
    }

    public void setText(String text) {

        // Remove the old texture
        close();

        this.text = text;
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

        // Set rendering hints
        if (settings.useAntiAliasing) {
            
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

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

    @Override
    public void close() {
        
        // Delete the texture
        glDeleteTextures(id);
    }
}
