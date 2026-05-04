package com.cluster.threads;

import com.cluster.manager.ClusterManager;
import com.cluster.model.Job;

import java.util.Random;

public class AuditorThread implements Runnable {

    // definino el tiempo de demora de esta etapa en milisegundos

    private static final int DELAY_MS = 10;

    // Probabilidad de que el resultado sea correcto (95%)
    private static final double CORRECT_PROBABILITY = 0.95;

    private final ClusterManager clusterManager;
    private final Random random;

    public AuditorThread(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
        this.random         = new Random();
    }

    @Override
    public void run() {
        // Repito hasta que el sistema haya terminado de procesar
        while (!clusterManager.isFinished()) {
            try {
                // toma un job de finalizados (clusterManager.pollFromFinished())
                Job job = clusterManager.pollFromFinished();

                if (job == null) {
                    // Si no hay jobs, esperar un poco antes de reintentar
                    Thread.sleep(10);
                    continue;
                }
                // Se simula validación con probabilidad VALID_PROBABILITY
                boolean isCorrect = random.nextDouble() < CORRECT_PROBABILITY;

                if (isCorrect) {
                    // muevo el job a validados (clusterManager.moveToValidated(job))
                    clusterManager.moveToValidated(job);
                } else {
                    // muevo el job a fallidos (clusterManager.moveToFailed(job))
                    clusterManager.moveToFailed(job);
                }

                // incrementar el contador de procesados en ambos casos
                // aplicar demora fija
                Thread.sleep(DELAY_MS);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
