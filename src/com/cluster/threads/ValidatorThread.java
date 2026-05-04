package com.cluster.threads;

import com.cluster.manager.ClusterManager;
import com.cluster.model.ComputeNode;
import com.cluster.model.Job;

import java.util.Random;

public class ValidatorThread implements Runnable {

    // Se define el tiempo de demora de esta etapa en milisegundos
    private static final int DELAY_MS = 100;

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
        // Repite hasta que el sistema haya terminado de procesar
        while (!clusterManager.isFinished()) {

            try {
                // Se toma un job de la cola de validación
                Job job = clusterManager.pollFromQueue(); // Bloquea si queue vacia y retorna null si timeout

                if (job == null) continue;

                //Obtengo el nodo asignado al job usando 
                ComputeNode node = clusterManager.getNodes()[job.getAssignedNodeId()];

                // Se simula validación con probabilidad VALID_PROBABILITY
                boolean isValid = random.nextDouble() < VALID_PROBABILITY; // random.nextDouble() genera un número entre 0.0 y 1.0

                if (isValid) {
                    node.setFree(); //libera el nodo (node.setFree())
                    clusterManager.moveToExecution(job); //Se mueve el job a ejecución
                } else {
                    node.setOutOfService(); //Se pone el nodo fuera de servicio
                    clusterManager.moveToFailed(job); //Se mueve el job a fallidos
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
