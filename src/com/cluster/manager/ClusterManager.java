package com.cluster.manager;

import com.cluster.model.ComputeNode;
import com.cluster.model.Job;
import com.cluster.model.JobStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ClusterManager {

    // Configuración del sistema
    public static final int TOTAL_NODES = 200;
    public static final int TOTAL_JOBS   = 500;

    // Matriz de nodos (se puede tratar como array lineal)
    private final ComputeNode[] nodes;

    // Colas y listas de jobs (thread-safe)
    private final BlockingQueue<Job> jobsInQueue;
    private final BlockingQueue<Job> jobsInExecution;
    private final List<Job> finishedJobs;
    private final List<Job> failedJobs;
    private final List<Job> validatedJobs;

    // Contadores atómicos para el Logger
    private final AtomicInteger failedCount;
    private final AtomicInteger validatedCount;

    // Control de finalización
    private final AtomicInteger processedJobsCount;

    public ClusterManager() {
        this.nodes = new ComputeNode[TOTAL_NODES];
        for (int i = 0; i < TOTAL_NODES; i++) {
            nodes[i] = new ComputeNode(i);
        }

        this.jobsInQueue      = new LinkedBlockingQueue<>();
        this.jobsInExecution  = new LinkedBlockingQueue<>();
        this.finishedJobs     = new CopyOnWriteArrayList<>();
        this.failedJobs       = new CopyOnWriteArrayList<>();
        this.validatedJobs    = new CopyOnWriteArrayList<>();

        this.failedCount      = new AtomicInteger(0);
        this.validatedCount   = new AtomicInteger(0);
        this.processedJobsCount = new AtomicInteger(0);
    }

    /**
     * Busca y retorna un nodo libre de forma aleatoria.
     * Debe garantizar acceso exclusivo al nodo encontrado.
     * @return un ComputeNode libre, o null si no hay ninguno disponible
     */
    public ComputeNode getFreeNode() {
        // TODO: recorrer los nodos en orden aleatorio buscando uno con estado FREE
        // IMPORTANTE: el acceso al nodo debe ser exclusivo (evitar race condition)
        // Sugerencia: usar synchronized sobre el nodo candidato antes de verificar su estado
        return null;
    }

    /**
     * Agrega un job a la cola de jobs en espera.
     */
    public void enqueueJob(Job job) {
        // TODO: agregar el job a jobsInQueue y actualizar su estado a QUEUED
    }

    /**
     * Toma un job de la cola de jobs en espera (bloqueante).
     * @return el siguiente job disponible
     */
    public Job pollFromQueue() throws InterruptedException {
        // TODO: tomar un job de jobsInQueue (usar take() para bloquear si está vacía)
        return null;
    }

    /**
     * Mueve un job a la cola de ejecución.
     */
    public void moveToExecution(Job job) {
        // TODO: cambiar estado del job a IN_EXECUTION y agregarlo a jobsInExecution
    }

    /**
     * Toma un job de la cola de ejecución (bloqueante).
     * @return el siguiente job disponible
     */
    public Job pollFromExecution() throws InterruptedException {
        // TODO: tomar un job de jobsInExecution (usar take() para bloquear si está vacía)

        return null;
    }

    /**
     * Mueve un job a la lista de finalizados.
     */
    public void moveToFinished(Job job) {
        // TODO: cambiar estado del job a FINISHED y agregarlo a finishedJobs
    }

    /**
     * Toma un job de la lista de finalizados (bloqueante o polling).
     * @return el siguiente job disponible, o null si no hay
     */
    public Job pollFromFinished() {
        // TODO: obtener y remover un job de finishedJobs de forma thread-safe
        return null;
    }

    /**
     * Mueve un job a la lista de fallidos.
     */
    public void moveToFailed(Job job) {
        // TODO: cambiar estado del job a FAILED, agregar a failedJobs e incrementar failedCount
    }

    /**
     * Mueve un job a la lista de validados.
     */
    public void moveToValidated(Job job) {
        // TODO: cambiar estado del job a VALIDATED, agregar a validatedJobs e incrementar validatedCount
    }

    /**
     * Retorna las estadísticas actuales para el Logger periódico.
     */
    public int[] getStats() {
        // TODO: retornar [failedCount, validatedCount]
        return new int[]{0, 0};
    }

    /**
     * Retorna las estadísticas finales de todos los nodos.
     * Usado por el Logger al terminar el programa.
     */
    public String getNodeStats() {
        // TODO: construir un String con el estado y executionCount de cada nodo
        StringBuilder EstadoGeneral;
        for (int i = 0; i < TOTAL_NODES; i++) {
            EstadoGeneral += nodes[i].getState();
        }
        return EstadoGeneral;
    }

    /**
     * Indica si el sistema ya terminó de procesar todos los jobs.
     */
    public boolean isFinished() {
        // TODO: retornar true cuando processedJobsCount >= TOTAL_JOBS
        return processedJobsCount.get() >= TOTAL_JOBS;
    }

    public void incrementProcessed() {
        processedJobsCount.incrementAndGet();
    }

    // --- Getters de colecciones (para consultas o debug) ---

    public ComputeNode[] getNodes() {
        return nodes;
    }

    public int getQueueSize() {
        return jobsInQueue.size();
    }

    public int getExecutionSize() {
        return jobsInExecution.size();
    }

    public int getFinishedCount() {
        return finishedJobs.size();
    }

    public int getFailedCount() {
        return failedCount.get();
    }

    public int getValidatedCount() {
        return validatedCount.get();
    }
}