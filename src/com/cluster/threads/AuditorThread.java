package com.cluster.threads;

import com.cluster.manager.ClusterManager;
import com.cluster.model.Job;

import java.util.Random;

public class AuditorThread implements Runnable {

    // TODO (equipo): definir el tiempo de demora de esta etapa en milisegundos
    private static final int DELAY_MS = 0;

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
        // TODO: repetir hasta que el sistema haya terminado de procesar
        while (!clusterManager.isFinished()) {

            try {
                // TODO: tomar un job de finalizados (clusterManager.pollFromFinished())
                Job job = null;

                if (job == null) {
                    // Si no hay jobs, esperar un poco antes de reintentar
                    Thread.sleep(10);
                    continue;
                }

                // TODO: simular verificación con probabilidad CORRECT_PROBABILITY
                boolean isCorrect = false; // random.nextDouble() < CORRECT_PROBABILITY

                if (isCorrect) {
                    // TODO: mover el job a validados (clusterManager.moveToValidated(job))
                } else {
                    // TODO: mover el job a fallidos (clusterManager.moveToFailed(job))
                }

                // TODO: incrementar el contador de procesados en ambos casos

                // TODO: aplicar demora fija
                Thread.sleep(DELAY_MS);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}