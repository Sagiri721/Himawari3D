package com.himawari.Input;

import com.himawari.HLA.Vec2;

public interface InputListener {
    
    /**
     * Called when a key is pressed.
     *
     * @param key The key that was pressed.
     */
    void onKeyDown(int key);

    /**
     * Called when a key is released.
     *
     * @param key The key that was released.
     */
    void onKeyUp(int key);

    /**
     * Called when a key is just pressed (not held down).
     *
     * @param key The key that was just pressed.
     */
    void onKeyPressed(int key);

    /**
     * Called when a key is just released (not held down).
     *
     * @param key The key that was just released.
     */
    void onKeyReleased(int key);

    /**
     * Called when the mouse button is pressed.
     *
     * @param button The button that was pressed.
     */
    void onMousePress(int button);

    /**
     * Called when the mouse button is released.
     *
     * @param button The button that was released.
     */
    void onMouseRelease(int button);
}
