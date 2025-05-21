package com.himawari.Simulation;

import com.himawari.Input.Input;
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

            if (Input.isKeyDown(KeyConstants.LEFT_SHIFT_OFFSET)){

                this.simulationController.playDir *= -1;
                return;
            }

            this.simulationController.playing = !this.simulationController.playing;

            if (this.simulationController.playing) {
                this.simulationController.recorder.startRecording();
            } else {
                this.simulationController.recorder.stopRecording();
            }
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
