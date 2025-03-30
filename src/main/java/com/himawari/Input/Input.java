package com.himawari.Input;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import java.nio.IntBuffer;

import com.himawari.Camera.Camera;
import com.himawari.HLA.Vec3;
import com.himawari.Utils.Window;

public class Input {

    static float moveSpeed = 12f;

    public static IntBuffer keyStates;

    public static final int WOffset = 87;
    public static final int AOffset = 65;
    public static final int SOffset = 83;
    public static final int DOffset = 68;
    public static final int UpOffset = 265;
    public static final int DownOffset = 264;
    public static final int RightOffset = 262;
    public static final int LeftOffset = 263;

    public static void Init() {
        keyStates = IntBuffer.allocate(512);
    }

    public static boolean isKeyDown(int key) {
        return keyStates.get(key) == 1;
    }

    public static void tick(){

        if (isKeyDown(UpOffset)) Camera.position.y += moveSpeed * Window.getInstance().frameDelta;
        if (isKeyDown(DownOffset)) Camera.position.y -= moveSpeed * Window.getInstance().frameDelta;

        if (isKeyDown(RightOffset)) Camera.position.x += moveSpeed * Window.getInstance().frameDelta;
        if (isKeyDown(LeftOffset)) Camera.position.x -= moveSpeed * Window.getInstance().frameDelta;

        Vec3 vforward = Camera.lookDirection.copy().scale(moveSpeed).scale((float) Window.getInstance().frameDelta);

        if (isKeyDown(WOffset)) Camera.position.sum(vforward);
        if (isKeyDown(SOffset)) Camera.position.subtract(vforward);

        if (isKeyDown(AOffset)) Camera.fYaw -= 2 * Window.getInstance().frameDelta;
        if (isKeyDown(DOffset)) Camera.fYaw += 2 * Window.getInstance().frameDelta;
    }

    public static void keyCallback(int key, int action) {
        
        if (action == GLFW_PRESS) {
            keyStates.put(key, 1);
        } else if (action == GLFW_RELEASE) {    
            keyStates.put(key, 0);
        }
    }
}
