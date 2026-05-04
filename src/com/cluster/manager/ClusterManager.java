package com.cluster.manager;

import com.cluster.model.ComputeNode;
import com.cluster.model.Job;
import com.cluster.model.JobStatus;
import com.cluster.model.NodeState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ClusterManager {

    // Configuración del sistema
    public static final int TOTAL_NODES = 200;
    public static final int TOTAL_JOBS   = 499;

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
    //(inicio aleatorio + escaneo circular). Ventajas: rápido y distribuye carga
    public ComputeNode getFreeNode() {
        int start = ThreadLocalRandom.current().nextInt(TOTAL_NODES);
        for(int i=0; i <= TOTAL_NODES;i++) {
            int idx = (start + i) % TOTAL_NODES;
            synchronized (nodes[idx]) {
                if (nodes[idx].isFree()) {
                    return nodes[idx];
                }
            }
        }
        return null;
    }

    /**
     * Agrega un job a la cola de jobs en espera.
     */
    public void enqueueJob(Job job) {
        jobsInQueue.add(job);
        job.setStatus(JobStatus.QUEUED);
    }

    /**
     * Toma un job de la cola de jobs en espera (bloqueante).
     * @return el siguiente job disponible
     */
    public Job pollFromQueue() throws InterruptedException {

        return jobsInQueue.poll(500, TimeUnit.MILLISECONDS );
    }

    /**
     * Mueve un job a la cola de ejecución.
     */
    public void moveToExecution(Job job) {
        if(job == null)return;
        jobsInExecution.add(job);
        job.setStatus(JobStatus.IN_EXECUTION);

    }

    /**
     * Toma un job de la cola de ejecución (bloqueante).
     * @return el siguiente job disponible
     */
    public Job pollFromExecution() throws InterruptedException {
        return jobsInExecution.poll(500, TimeUnit.MILLISECONDS);
    }

    /**
     * Mueve un job a la lista de finalizados.
     */
    public void moveToFinished(Job job) {
        if(job == null)return;
        synchronized (finishedJobs) {
            jobsInExecution.remove(job);
            finishedJobs.add(job);
            job.setStatus(JobStatus.FINISHED);
        }
    }

    /**
     * Toma un job de la lista de finalizados (bloqueante o polling).
     * @return el siguiente job disponible, o null si no hay
     */
    public Job pollFromFinished() {
        //lockeamos con la lista para que ningun hilo entre a cambiar
        synchronized (finishedJobs) {
            if(finishedJobs.isEmpty()) return null;
            return finishedJobs.remove(0);
        }
    }

    /**
     * Mueve un job a la lista de fallidos.
     */
    public void moveToFailed(Job job) {
        // cambiar estado del job a FAILED, agregar a failedJobs e incrementar failedCount
        job.setStatus(JobStatus.FAILED);
        failedJobs.add(job);
        failedCount.incrementAndGet();
        processedJobsCount.incrementAndGet();
    }

    /**
     * Mueve un job a la lista de validados.
     */
    public void moveToValidated(Job job) {
        //  cambiar estado del job a VALIDATED, agregar a validatedJobs e incrementar validatedCount
        job.setStatus(JobStatus.VALIDATED);
        validatedJobs.add(job);
        validatedCount.incrementAndGet();
        processedJobsCount.incrementAndGet();

    }

    /**
     * Retorna las estadísticas actuales para el Logger periódico.
     */
    public int[] getStats() {
        // retornar [failedCount, validatedCount]
        return new int[]{failedCount.get(), validatedCount.get(), processedJobsCount.get()};
    }

    /**
     * Retorna las estadísticas finales de todos los nodos.
     * Usado por el Logger al terminar el programa.
     */
    public String getNodeStats() {
        //construir un String con el estado y executionCount de cada nodo
        StringBuilder EstadoGeneral = new StringBuilder();
        for (int i = 0; i < TOTAL_NODES; i++) {
            EstadoGeneral.append(nodes[i].toString()).append(System.lineSeparator());
        }
        return EstadoGeneral.toString();
    }

    /**
     * Indica si el sistema ya terminó de procesar todos los jobs.
     */
    public boolean isFinished() {
        // retornar true cuando processedJobsCount >= TOTAL_JOBS
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
