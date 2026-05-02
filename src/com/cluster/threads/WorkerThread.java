package com.cluster.threads;

import com.cluster.manager.ClusterManager;
import com.cluster.model.Job;
import java.util.Random;

public class WorkerThread implements Runnable {

    private static final int DELAY_MS = 100;
    private static final double SUCCESS_PROBABILITY = 0.90;

    private final ClusterManager clusterManager;
    private final Random random;

    public WorkerThread(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
        this.random = new Random();
    }

    @Override
    public void run() {
        while (!clusterManager.isFinished()) {
            try {
                Job job = clusterManager.pollFromExecution();

                if (job == null) continue;

                boolean isSuccess = random.nextDouble() < SUCCESS_PROBABILITY;

                if (isSuccess) {
                    clusterManager.moveToFinished(job);
                } else {
                    clusterManager.moveToFailed(job);
                    clusterManager.incrementProcessed();
                }

                Thread.sleep(DELAY_MS);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}