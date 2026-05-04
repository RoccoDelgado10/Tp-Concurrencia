package com.cluster.threads;

import com.cluster.manager.ClusterManager;
import com.cluster.model.Job;
import java.util.Random;

public class WorkerThread implements Runnable {
    // Se define el tiempo de demora de esta etapa en milisegundos
    private static final int DELAY_MS = 200;
    // Probabilidad de que un job sea válido (90%)
    private static final double SUCCESS_PROBABILITY = 0.90;

    private final ClusterManager clusterManager;
    private final Random random;

    public WorkerThread(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
        this.random = new Random();
    }

    @Override
    public void run() {
        // Repite hasta que el sistema haya terminado de procesar
        while (!clusterManager.isFinished()) {
            try {
                // Se toma un job de la cola de validación
                Job job = clusterManager.pollFromExecution();

                if (job == null) continue;
                // Se simula validación con probabilidad VALID_PROBABILITY
                boolean isSuccess = random.nextDouble() < SUCCESS_PROBABILITY;// random.nextDouble() genera un número entre 0.0 y 1.0

                if (isSuccess) {
                    clusterManager.moveToFinished(job);// Se mueve el job a finalizados
                } else {
                    clusterManager.moveToFailed(job); // Se mueve el job a fallidos
                }
                //Se aplica una demora fija
                Thread.sleep(DELAY_MS);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
