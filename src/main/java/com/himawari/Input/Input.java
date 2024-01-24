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

public class Input {

    static float moveSpeed = 10f;

    public static void KeyDown(int key){

        if (key == SDLK_UP) Camera.position.y += 10 * Window.frameDelta;
        if (key == SDLK_DOWN) Camera.position.y -= 10 * Window.frameDelta;

        if (key == SDLK_RIGHT) Camera.position.x += 10 * Window.frameDelta;
        if (key == SDLK_LEFT) Camera.position.x -= 10 * Window.frameDelta;

        Vec3 vforward = Camera.lookDirection.copy().scale(moveSpeed).scale((float) Window.frameDelta);

        if(key == SDLK_W) Camera.position.sum(vforward);
        if(key == SDLK_S) Camera.position.subtract(vforward);

        if(key == SDLK_A) Camera.fYaw += 3 * Window.frameDelta;
        if(key == SDLK_D) Camera.fYaw -= 3 * Window.frameDelta;
    }
}
