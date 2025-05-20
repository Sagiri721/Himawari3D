package com.himawari.Input;

import java.nio.IntBuffer;

import com.himawari.Camera.Camera;
import com.himawari.Gfx.RenderMode;
import com.himawari.Gfx.RenderTarget;
import com.himawari.Gfx.Window;
import com.himawari.HLA.Vec2;
import com.himawari.HLA.Vec3;
import com.himawari.Utils.RenderEnvironment;

public class CameraInput implements InputListener {
    
    public static float moveSpeed = 12f;
    public static boolean debugMenu = false;

    @Override
    public void onKeyDown(int key) {

        if (key == KeyConstants.VKUP_OFFSET) Camera.position.y += moveSpeed * Window.getInstance().frameDelta;
        if (key == KeyConstants.VKDOWN_OFFSET) Camera.position.y -= moveSpeed * Window.getInstance().frameDelta;

        if (key == KeyConstants.VKRIGHT_OFFSET) Camera.position.x += moveSpeed * Window.getInstance().frameDelta;
        if (key == KeyConstants.VKLEFT_OFFSET) Camera.position.x -= moveSpeed * Window.getInstance().frameDelta;

        Vec3 vforward = Camera.lookDirection.copy().scale(moveSpeed).scale((float) Window.getInstance().frameDelta);

        if (key == KeyConstants.W_OFFSET) Camera.position.sum(vforward);
        if (key == KeyConstants.S_OFFSET) Camera.position.subtract(vforward);

        if (key == KeyConstants.A_OFFSET) Camera.fYaw -= 2 * Window.getInstance().frameDelta;
        if (key == KeyConstants.D_OFFSET) Camera.fYaw += 2 * Window.getInstance().frameDelta;
    }

    @Override
    public void onKeyUp(int key) {   
    }

    @Override
    public void onMousePress(int button) {
    }

    @Override
    public void onMouseRelease(int button) {
    }

    @Override
    public void onKeyPressed(int key) {
        
        if (key == KeyConstants.SPACE_OFFSET) debugMenu = !debugMenu;

        if (key == KeyConstants.ONE_OFFSET) {
            RenderEnvironment re = (RenderEnvironment) Window.getInstance().currentRenderer();
            re.setRenderMode(re.getRenderMode() == RenderMode.WIREFRAME ? RenderMode.SOLID : RenderMode.WIREFRAME);
        }
        if (key == KeyConstants.TWO_OFFSET) {
            
            RenderEnvironment re = (RenderEnvironment) Window.getInstance().currentRenderer();
            re.setRenderTarget(re.getRenderTarget() == RenderTarget.COLORBUFFER ? RenderTarget.NORMALMAP : RenderTarget.COLORBUFFER);                
        }
    }

    @Override
    public void onKeyReleased(int key) {
        
    }
}
