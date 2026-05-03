package com.cluster;

import com.cluster.manager.ClusterManager;
import com.cluster.threads.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    // Cantidad de hilos por etapa (según el enunciado)
    private static final int SCHEDULER_THREADS  = 3;
    private static final int VALIDATOR_THREADS  = 2;
    private static final int WORKER_THREADS     = 3;
    private static final int AUDITOR_THREADS    = 2;

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Iniciando sistema de procesamiento de jobs...");

        // Recurso compartido central
        ClusterManager clusterManager = new ClusterManager();

        // Contador atómico para generar IDs únicos de jobs entre los schedulers
        AtomicInteger jobIdCounter = new AtomicInteger(1);

        // Lista para guardar todos los hilos y poder hacer join al final
        List<Thread> allThreads = new ArrayList<>();

        // --- Etapa 1: Schedulers ---
        for (int i = 0; i < SCHEDULER_THREADS; i++) {
            Thread t = new Thread(new SchedulerThread(clusterManager, jobIdCounter), "Scheduler-" + i);
            allThreads.add(t);
        }

        // --- Etapa 2: Validators ---
        for (int i = 0; i < VALIDATOR_THREADS; i++) {
            Thread t = new Thread(new ValidatorThread(clusterManager), "Validator-" + i);
            allThreads.add(t);
        }

        // --- Etapa 3: Workers ---
        for (int i = 0; i < WORKER_THREADS; i++) {
            Thread t = new Thread(new WorkerThread(clusterManager), "Worker-" + i);
            allThreads.add(t);
        }

        // --- Etapa 4: Auditors ---
        for (int i = 0; i < AUDITOR_THREADS; i++) {
            Thread t = new Thread(new AuditorThread(clusterManager), "Auditor-" + i);
            allThreads.add(t);
        }

        // --- Logger (corre en paralelo a todo) ---
        Thread loggerThread = new Thread(new Logger(clusterManager), "Logger");

        // lanzar todos los hilos de allThreads
        for (Thread t : allThreads) t.start();
        loggerThread.start();

        // Monitoreo: si tarda mucho, recicla nodos out-of-service
        Thread nodeRecycler = new Thread(() -> {
            while (!clusterManager.isFinished()) {
                try {
                    Thread.sleep(1000);  // Cada segundo
                    clusterManager.resetOutOfServiceNodes();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "NodeRecycler");
        nodeRecycler.start();

        for (Thread t : allThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        //esperar a que el loggerThread termine (join)
        loggerThread.join();
        nodeRecycler.join();
        System.out.println("Sistema finalizado. Revisar logs/cluster_stats.log para resultados.");
    }
}