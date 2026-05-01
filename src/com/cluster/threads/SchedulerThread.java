package com.cluster.threads;

import com.cluster.manager.ClusterManager;
import com.cluster.model.ComputeNode;

import java.util.concurrent.atomic.AtomicInteger;

public class SchedulerThread implements Runnable {

    // TODO (equipo): definir el tiempo de demora de esta etapa en milisegundos
    private static final int DELAY_MS = 0;

    private final ClusterManager clusterManager;
    private final AtomicInteger jobIdCounter;

    public SchedulerThread(ClusterManager clusterManager, AtomicInteger jobIdCounter) {
        this.clusterManager = clusterManager;
        this.jobIdCounter   = jobIdCounter;
    }

    @Override
    public void run() {
        // TODO: repetir hasta que se hayan generado todos los jobs (TOTAL_JOBS)
        while (true) {

            // TODO: obtener el siguiente id de job de forma atómica
            // Si el id supera TOTAL_JOBS, salir del loop
            int jobId = 0; // reemplazar con jobIdCounter

            // TODO: crear un nuevo Job con ese id

            // TODO: buscar un nodo libre llamando a clusterManager.getFreeNode()
            // Si no hay nodo disponible, reintentar (o esperar brevemente)
            ComputeNode node = null;

            // TODO: asignar el job al nodo (node.assignJob())
            // TODO: setear el assignedNodeId en el job

            // TODO: encolar el job (clusterManager.enqueueJob(job))

            // TODO: aplicar la demora fija de esta etapa (Thread.sleep(DELAY_MS))
            try {
                Thread.sleep(DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}