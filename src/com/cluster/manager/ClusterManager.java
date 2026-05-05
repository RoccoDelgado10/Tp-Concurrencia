package com.cluster.manager;

import com.cluster.model.ComputeNode;
import com.cluster.model.Job;
import com.cluster.model.JobStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import java.util.concurrent.atomic.AtomicInteger;

public class ClusterManager {

    public static final int TOTAL_NODES = 200;
    public static final int TOTAL_JOBS  = 500;

    private final ComputeNode[] nodes;

    // Todas las listas protegidas con synchronized + wait/notify
    private final List<Job> jobsInQueue;
    private final List<Job> jobsInExecution;
    private final List<Job> finishedJobs;
    private final List<Job> failedJobs;
    private final List<Job> validatedJobs;

    private final AtomicInteger failedCount;
    private final AtomicInteger validatedCount;
    private final AtomicInteger processedJobsCount;

    public ClusterManager() {
        this.nodes = new ComputeNode[TOTAL_NODES];
        for (int i = 0; i < TOTAL_NODES; i++) {
            nodes[i] = new ComputeNode(i);
        }

        this.jobsInQueue       = new ArrayList<>();
        this.jobsInExecution   = new ArrayList<>();
        this.finishedJobs      = new ArrayList<>();
        this.failedJobs        = new ArrayList<>();
        this.validatedJobs     = new ArrayList<>();

        this.failedCount        = new AtomicInteger(0);
        this.validatedCount     = new AtomicInteger(0);
        this.processedJobsCount = new AtomicInteger(0);
    }

    // -------------------------------------------------------------------------
    // NODOS
    // -------------------------------------------------------------------------

    public ComputeNode getFreeNode() {
        int start = ThreadLocalRandom.current().nextInt(TOTAL_NODES);
        for (int i = 0; i <= TOTAL_NODES; i++) {
            int idx = (start + i) % TOTAL_NODES;
            synchronized (nodes[idx]) {
                if (nodes[idx].isFree()) {
                    nodes[idx].assignJob(); // marcar BUSY dentro del synchronized
                    return nodes[idx];
                }
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // ETAPA 1 → 2: jobsInQueue
    // -------------------------------------------------------------------------

    public void enqueueJob(Job job) {
        job.setStatus(JobStatus.QUEUED);
        synchronized (jobsInQueue) {
            jobsInQueue.add(job);
            jobsInQueue.notify(); // despierta a un ValidatorThread que esté esperando
        }
    }

    public Job pollFromQueue() throws InterruptedException {
        synchronized (jobsInQueue) {
            // while en lugar de if para protegerse de spurious wakeups
            while (jobsInQueue.isEmpty() && !isFinished()) {
                jobsInQueue.wait(500);
            }
            if (jobsInQueue.isEmpty()) return null;
            return jobsInQueue.remove(0);
        }
    }

    // -------------------------------------------------------------------------
    // ETAPA 2 → 3: jobsInExecution
    // -------------------------------------------------------------------------

    public void moveToExecution(Job job) {
        if (job == null) return;
        job.setStatus(JobStatus.IN_EXECUTION);
        synchronized (jobsInExecution) {
            jobsInExecution.add(job);
            jobsInExecution.notify(); // despierta a un WorkerThread que esté esperando
        }
    }

    public Job pollFromExecution() throws InterruptedException {
        synchronized (jobsInExecution) {
            while (jobsInExecution.isEmpty() && !isFinished()) {
                jobsInExecution.wait(500);
            }
            if (jobsInExecution.isEmpty()) return null;
            return jobsInExecution.remove(0);
        }
    }

    // -------------------------------------------------------------------------
    // ETAPA 3 → 4: finishedJobs
    // -------------------------------------------------------------------------

    public void moveToFinished(Job job) {
        if (job == null) return;
        job.setStatus(JobStatus.FINISHED);
        synchronized (finishedJobs) {
            finishedJobs.add(job);
        }
    }

    public Job pollFromFinished() {
        synchronized (finishedJobs) {
            if (finishedJobs.isEmpty()) return null;
            return finishedJobs.remove(0);
        }
    }

    // -------------------------------------------------------------------------
    // ESTADOS FINALES
    // -------------------------------------------------------------------------

    public void moveToFailed(Job job) {
        if (job == null) return;
        job.setStatus(JobStatus.FAILED);
        synchronized (failedJobs) {
            failedJobs.add(job);
        }
        failedCount.incrementAndGet();
        processedJobsCount.incrementAndGet();
    }

    public void moveToValidated(Job job) {
        if (job == null) return;
        job.setStatus(JobStatus.VALIDATED);
        synchronized (validatedJobs) {
            validatedJobs.add(job);
        }
        validatedCount.incrementAndGet();
        processedJobsCount.incrementAndGet();
    }

    // -------------------------------------------------------------------------
    // ESTADÍSTICAS
    // -------------------------------------------------------------------------

    public int[] getStats() {
        return new int[]{failedCount.get(), validatedCount.get(), processedJobsCount.get()};
    }

    public String getNodeStats() {
        StringBuilder estadoGeneral = new StringBuilder();
        for (int i = 0; i < TOTAL_NODES; i++) {
            estadoGeneral.append(nodes[i].toString()).append(System.lineSeparator());
        }
        return estadoGeneral.toString();
    }

    public boolean isFinished() {
        return processedJobsCount.get() >= TOTAL_JOBS;
    }

    public void incrementProcessed() {
        processedJobsCount.incrementAndGet();
    }

    // -------------------------------------------------------------------------
    // GETTERS
    // -------------------------------------------------------------------------

    public ComputeNode[] getNodes() { return nodes; }

}