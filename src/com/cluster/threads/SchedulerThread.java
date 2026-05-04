package com.cluster.threads;

import com.cluster.manager.ClusterManager;
import com.cluster.model.ComputeNode;
import com.cluster.model.Job;

import java.util.concurrent.atomic.AtomicInteger;

public class SchedulerThread implements Runnable {

    private static final int DELAY_MS = 5;

    private final ClusterManager clusterManager;
    private final AtomicInteger jobIdCounter;
    //usamos un AtomicInteger para evitar raceCondition en el jobIdCounter
    //AtomicInteger es thread safe y utiliza instrucciones a nivel de hardware (Más eficiente que Synchronized)
    // podria pasar que dos hilos modifiquen la variable una modificación se pierda

    public SchedulerThread(ClusterManager clusterManager, AtomicInteger jobIdCounter) {
        this.clusterManager = clusterManager;
        this.jobIdCounter   = jobIdCounter;
    }

    @Override
    public void run() {
        //  repetir hasta que se hayan generado todos los jobs (TOTAL_JOBS)
        while (true) {
            //  obtener el siguiente id de job de forma atómica
            // Si el id supera TOTAL_JOBS, salir del loop
            int jobId = jobIdCounter.getAndIncrement();
            // Hilo 1 obtiene 1, Hilo 2 obtiene 2, Hilo 3 obtiene 3
            // Siempre únicos, siempre en orden

            //si el el job id siguiente es mayor que el numero total que salga del loop
            if(jobId > ClusterManager.TOTAL_JOBS){break;}
            //  crear un nuevo Job con ese id
            Job job = new Job(jobId);
            //  buscar un nodo libre llamando a clusterManager.getFreeNode()
            ComputeNode node = null;
            while(node == null) {  // Máx 50 reintentos = 500ms
                node = clusterManager.getFreeNode();

                if (node == null) {                 // Si no hay nodo disponible, reintentar (o esperar brevemente)
                    try {
                        System.out.println("Scheduler: No se encontró nodo libre para job " + jobId);
                        Thread.sleep(10); // espera 10ms antes de reintentar

                    } catch (InterruptedException e){
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            // asignar el job al nodo (node.assignJob())
            node.assignJob();
            //  setear el assignedNodeId en el job
            job.setAssignedNodeId(node.getId());
            //  encolar el job (clusterManager.enqueueJob(job))
            clusterManager.enqueueJob(job);
            //  aplicar la demora fija de esta etapa (Thread.sleep(DELAY_MS))
            try {
                Thread.sleep(DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
