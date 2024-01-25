package com.himawari.Input;

import static io.github.libsdl4j.api.keycode.SDL_Keycode.SDLK_A;
import static io.github.libsdl4j.api.keycode.SDL_Keycode.SDLK_D;
import static io.github.libsdl4j.api.keycode.SDL_Keycode.SDLK_DOWN;
import static io.github.libsdl4j.api.keycode.SDL_Keycode.SDLK_LEFT;
import static io.github.libsdl4j.api.keycode.SDL_Keycode.SDLK_RIGHT;
import static io.github.libsdl4j.api.keycode.SDL_Keycode.SDLK_S;
import static io.github.libsdl4j.api.keycode.SDL_Keycode.SDLK_UP;
import static io.github.libsdl4j.api.keycode.SDL_Keycode.SDLK_W;

import com.himawari.Camera;
import com.himawari.Window;
import com.himawari.HLA.Vec3;
import com.sun.jna.Pointer;

import io.github.libsdl4j.api.keyboard.SdlKeyboard;

public class Input {

    static float moveSpeed = 12f;

    private static final int UpOffset = SdlKeyboard.SDL_GetScancodeFromKey(SDLK_UP);
    private static final int DownOffset = SdlKeyboard.SDL_GetScancodeFromKey(SDLK_DOWN);
    private static final int RightOffset = SdlKeyboard.SDL_GetScancodeFromKey(SDLK_RIGHT);
    private static final int LeftOffset = SdlKeyboard.SDL_GetScancodeFromKey(SDLK_LEFT);

    private static final int WOffset = SdlKeyboard.SDL_GetScancodeFromKey(SDLK_W);
    private static final int SOffset = SdlKeyboard.SDL_GetScancodeFromKey(SDLK_S);
    private static final int AOffset = SdlKeyboard.SDL_GetScancodeFromKey(SDLK_A);
    private static final int DOffset = SdlKeyboard.SDL_GetScancodeFromKey(SDLK_D);

    public static void KeyDown(Pointer keyStates){

        if (keyStates.getByte(UpOffset) != 0) Camera.position.y += moveSpeed * Window.frameDelta;
        if (keyStates.getByte(DownOffset) != 0) Camera.position.y -= moveSpeed * Window.frameDelta;

        if (keyStates.getByte(RightOffset) != 0) Camera.position.x -= moveSpeed * Window.frameDelta;
        if (keyStates.getByte(LeftOffset) != 0) Camera.position.x += moveSpeed * Window.frameDelta;

        Vec3 vforward = Camera.lookDirection.copy().scale(moveSpeed).scale((float) Window.frameDelta);

        if (keyStates.getByte(WOffset) != 0) Camera.position.sum(vforward);
        if (keyStates.getByte(SOffset) != 0) Camera.position.subtract(vforward);

        if (keyStates.getByte(AOffset) != 0) Camera.fYaw -= 2 * Window.frameDelta;
        if (keyStates.getByte(DOffset) != 0) Camera.fYaw += 2 * Window.frameDelta;
    }
}
