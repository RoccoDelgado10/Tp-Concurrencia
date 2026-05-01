package com.cluster.threads;

import com.cluster.manager.ClusterManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger implements Runnable {

    private static final int LOG_INTERVAL_MS = 200;
    private static final String LOG_FILE_PATH = "logs/cluster_stats.log";

    private final ClusterManager clusterManager;
    private final long startTime;

    public Logger(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
        this.startTime      = System.currentTimeMillis();
    }

    @Override
    public void run() {
        // TODO: asegurarse de que exista la carpeta "logs/" antes de escribir
        // Sugerencia: new File("logs").mkdirs()

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {

            // TODO: escribir encabezado del log con fecha y hora de inicio

            // Loop periódico: registrar stats cada 200ms mientras el sistema corre
            while (!clusterManager.isFinished()) {
                try {
                    Thread.sleep(LOG_INTERVAL_MS);
                    writeStats(writer);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            // TODO: al terminar, escribir las estadísticas finales
            writeFinalStats(writer);

        } catch (IOException e) {
            System.err.println("Error escribiendo el log: " + e.getMessage());
        }
    }

    /**
     * Escribe una línea de estadísticas periódicas en el archivo.
     * Formato sugerido: [timestamp] Failed: X | Validated: Y
     */
    private void writeStats(BufferedWriter writer) throws IOException {
        // TODO: obtener stats con clusterManager.getStats()
        // TODO: escribir una línea con timestamp, failedCount y validatedCount
        int[] stats = clusterManager.getStats();

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        String line = "[" + timestamp + "] Failed: " + stats[0] + " | Validated: " + stats[1];

        // TODO: escribir `line` en el writer y hacer flush
    }

    /**
     * Escribe el resumen final al terminar el programa.
     * Debe incluir: estadísticas de nodos y tiempo total de ejecución.
     */
    private void writeFinalStats(BufferedWriter writer) throws IOException {
        long totalTimeMs = System.currentTimeMillis() - startTime;

        // TODO: escribir separador y título de estadísticas finales
        // TODO: escribir clusterManager.getNodeStats()
        // TODO: escribir el tiempo total en segundos (totalTimeMs / 1000.0)
    }
}