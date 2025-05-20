package com.himawari.Simulation;

import com.himawari.Input.InputListener;
import com.himawari.Input.KeyConstants;

public class SimulationKeyListener implements InputListener {

    private SimulationController simulationController;

    public SimulationKeyListener(SimulationController simulationController) {
        this.simulationController = simulationController;
    }

    @Override
    public void onKeyDown(int key) {
    }

    @Override
    public void onKeyUp(int key) {
    }

    @Override
    public void onKeyPressed(int key) {

        if (key == KeyConstants.ENTER_OFFSET) {
            this.simulationController.step(1);
        }
    }

    @Override
    public void onKeyReleased(int key) {
    }

    @Override
    public void onMousePress(int button) {
    }

    @Override
    public void onMouseRelease(int button) {
    }

}
