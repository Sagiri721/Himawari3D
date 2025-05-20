package com.himawari.Simulation;

import java.io.File;
import java.util.HashMap;

import com.himawari.Gfx.Mesh;
import com.himawari.Gfx.Window;
import com.himawari.HLA.Vec3;
import com.himawari.Utils.CVSReader;
import com.himawari.Utils.Logger;
import com.himawari.Utils.YAMLReader;

/**
 * SimulationController is responsible for managing a visualization of a certain simulation.
 * Simulation data is defined from a specific format, a YAML header and a csv step indexed position matrix.
 */
public class SimulationController {
    
    /**
     * StepData is a class that holds the data for each step in the simulation.
     */
    private class StepData {
        
        public String droneId; // The drone ID we mean to read the data of at this step
        public Vec3 position; // The position of the drone at this step

        public StepData(String droneId, Vec3 position) {
            this.droneId = droneId;
            this.position = position;
        }

        @Override
        public String toString() {
            return "StepData [droneId=" + droneId + ", position=" + position + "]";
        }
    }

    private HashMap<String, String> simulationHead;
    private HashMap<String, Mesh> simulationObjects;
    private StepData[] steps;
    private int currentStep = 0;

    public SimulationController(File header, File stepData){

        if (!header.exists() || !stepData.exists()) {
            Logger.LogError("Simulation files do not exist");
            return;
        }

        Logger.LogInfo("Loading simulation data from " + header.getAbsolutePath() + " and " + stepData.getAbsolutePath());
        this.simulationHead = YAMLReader.readYAML(header);
        String[][] stepDataArray = CVSReader.readCSV(stepData);
        
        this.simulationObjects = new HashMap<>();
        this.steps = new StepData[stepDataArray.length - 1];
        for (int i = 0; i < stepDataArray.length - 1; i++) {

            String droneId = stepDataArray[i + 1][1];
            float x = Float.parseFloat(stepDataArray[i + 1][2]);
            float y = Float.parseFloat(stepDataArray[i + 1][3]);
            float z = Float.parseFloat(stepDataArray[i + 1][4]);

            Vec3 position = new Vec3(x, y, z);
            this.steps[i] = new StepData(droneId, position);
        }

        createObjects(Mesh.LoadFrom("models/cube.obj"));
    }

    int objectCount = 0;
    private String[] getIdList(){

        objectCount = Integer.parseInt(simulationHead.get("drone_count"));
        String[] idList = new String[objectCount];

        for (int i = 0; i < steps.length; i++) {
            
            StepData step = steps[i];

            if (i == objectCount) break;
            idList[i] = step.droneId;
        }

        return idList;
    }

    private void createObjects(Mesh model) {

        // Create objects based on the simulation head
        String[] idList = getIdList();

        for (int i = 0; i < idList.length; i++) {

            Mesh object = model.clone();
            object.transform.position = steps[i].position;
            object.transform.scale = new Vec3(0.5f, 0.5f, 0.5f);

            Window.getInstance().currentRenderEnvironment().AddMesh(object);

            simulationObjects.put(idList[i], object);
        }
    }

    private void renderStep(int stepIndex) {

        int headIndex = objectCount * stepIndex + 1;
        if (headIndex >= steps.length) {
            Logger.LogError("Step index out of bounds");
            return;
        }

        for (int i = 0; i < objectCount; i++) {

            if (headIndex + i >= steps.length) {
                Logger.LogError("Step index out of bounds");
                break;
            }

            StepData step = steps[headIndex + i];
            Mesh object = simulationObjects.get(step.droneId);

            if (object != null) {
                object.transform.position = step.position;
            } else {
                Logger.LogError("Object not found for drone ID: " + step.droneId);
            }
        }
    }

    public void step(int size) {

        if (currentStep + size >= steps.length || currentStep + size < 0) {
            Logger.LogError("Step index out of bounds");
            return;
        }

        currentStep += size;
        renderStep(currentStep);
    }
}
