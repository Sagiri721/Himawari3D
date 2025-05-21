package com.himawari.Utils;

public interface PipelineListener {
    
    /**
     * Called when the pipeline is started.
     */
    void init();

    /**
     * Called when the pipeline is finished.
     */
    void cleanUP();

    /**
     * Called when the pipeline is updated.
     */
    void update();
}
