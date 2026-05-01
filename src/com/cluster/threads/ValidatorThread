package com.cluster.threads;

import com.cluster.manager.ClusterManager;
import com.cluster.model.ComputeNode;
import com.cluster.model.Job;

import java.util.Random;

public class ValidatorThread implements Runnable {

    // TODO (equipo): definir el tiempo de demora de esta etapa en milisegundos
    private static final int DELAY_MS = 0;

    // Probabilidad de que un job sea válido (85%)
    private static final double VALID_PROBABILITY = 0.85;

    private final ClusterManager clusterManager;
    private final Random random;

    public ValidatorThread(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
        this.random         = new Random();
    }

    @Override
    public void run() {
        // TODO: repetir hasta que el sistema haya terminado de procesar
        while (!clusterManager.isFinished()) {

            try {
                // TODO: tomar un job de la cola (clusterManager.pollFromQueue())
                Job job = null;

                if (job == null) continue;

                // TODO: obtener el nodo asignado al job usando job.getAssignedNodeId()
                ComputeNode node = null; // clusterManager.getNodes()[job.getAssignedNodeId()]

                // TODO: simular validación con probabilidad VALID_PROBABILITY
                boolean isValid = false; // random.nextDouble() < VALID_PROBABILITY

                if (isValid) {
                    // TODO: liberar el nodo (node.setFree())
                    // TODO: mover el job a ejecución (clusterManager.moveToExecution(job))
                } else {
                    // TODO: poner el nodo fuera de servicio (node.setOutOfService())
                    // TODO: mover el job a fallidos (clusterManager.moveToFailed(job))
                    // TODO: incrementar el contador de procesados
                }

                // TODO: aplicar demora fija
                Thread.sleep(DELAY_MS);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}