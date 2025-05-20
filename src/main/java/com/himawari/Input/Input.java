package com.himawari.Input;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import java.nio.DoubleBuffer;
import java.util.BitSet;
import java.util.concurrent.CopyOnWriteArrayList;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import com.himawari.Gfx.Window;
import com.himawari.HLA.Vec2;

/** 
 * Input class is responsible for handling input events and storing the state of keys.
 */
public class Input {

    // Input listeners
    // All listeners are called when an event occurs
    private static final CopyOnWriteArrayList<InputListener> listeners = new CopyOnWriteArrayList<>();

    // Key state buffer
    public static final BitSet keyStates = new BitSet(GLFW.GLFW_KEY_LAST + 1);

    public static void Init() {

        // Initialize the key state buffer
        keyStates.clear();
    }

    public static void mouseCallback(int button, int action) {

        // Check if button is valid
        if (button < 0 || button > GLFW.GLFW_MOUSE_BUTTON_LAST) {
            return;
        }

        if (action == GLFW_PRESS) {
            for (InputListener listener : listeners) 
                listener.onMousePress(button);
        } else if (action == GLFW_RELEASE) {
            for (InputListener listener : listeners) 
                listener.onMouseRelease(button);
        }
    }

    public static void hookListener(InputListener listener) {
        listeners.add(listener);
    }

    public static void unhookListener(InputListener listener) {
        listeners.remove(listener);
    }

    public static void keyCallback(int key, int action) {

        // Check if key is valid
        if (key < 0 || key > GLFW.GLFW_KEY_LAST) {
            return;
        }
            
        if (action == GLFW_PRESS) {
            keyStates.set(key);
            for (InputListener listener : listeners) 
                listener.onKeyPressed(key);
        }
        else if (action == GLFW_RELEASE) {
            keyStates.clear(key);
            for (InputListener listener : listeners) 
                listener.onKeyReleased(key);
        
        }

    }

    public static void tick() {

        // Notify all listeners of the current key state
        for (InputListener listener : listeners) {

            for (int i = 0; i <= GLFW.GLFW_KEY_LAST; i++) {

                if (keyStates.get(i)) {
                    listener.onKeyDown(i);
                } else {
                    listener.onKeyUp(i);
                }
            }
        }
    }
    
    /**
     * Helper function to check if a key is pressed
     * @param key the key to check
     * @return true if the key is pressed, false otherwise
     */
    public static boolean isKeyDown(int key) {
        return keyStates.get(key);
    }

    /**
     * Helper function to check if a key is released
     * @param key the key to check
     * @return true if the key is released, false otherwise
     */
    public static boolean isKeyUp(int key) {
        return !keyStates.get(key);
    }

    /**
     * Helper function to get the mouse position
     * @return
     */
    public static Vec2 getMousePosition() {
        
        DoubleBuffer xPos  = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer yPos  = BufferUtils.createDoubleBuffer(1);

        GLFW.glfwGetCursorPos(Window.getInstance().window, xPos, yPos);

        return new Vec2((float)xPos.get(0), (float)yPos.get(0));
    }
}
