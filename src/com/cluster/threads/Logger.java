package com.cluster.threads;

import com.cluster.manager.ClusterManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;

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
        new File("logs").mkdirs(); // Nos aseguramos de que el directorio de logs exista

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {

            String inicio = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // Formateamos la fecha de inicio
            writer.write("=== LOG INICIADO: " + inicio + " ===");
            writer.newLine();
            writer.flush();

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

            writeFinalStats(writer); // Al terminar, se escribe las estadísticas finales

        } catch (IOException e) {
            System.err.println("Error escribiendo el log: " + e.getMessage());
        }
    }

    /**
     * Escribe una línea de estadísticas periódicas en el archivo.
     * Formato sugerido: [timestamp] Failed: X | Validated: Y
     */
    private void writeStats(BufferedWriter writer) throws IOException {
        // Se escribe una línea con timestamp, failedCount y validatedCount
        int[] stats = clusterManager.getStats();

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        String line = "[" + timestamp + "] Failed: " + stats[0] + " | Validated: " + stats[1] + "| Procesados" + stats[2];

        writer.write(line);
        writer.newLine();
        writer.flush();
    }

    /**
     * Escribe el resumen final al terminar el programa.
     * Debe incluir: estadísticas de nodos y tiempo total de ejecución.
     */
    private void writeFinalStats(BufferedWriter writer) throws IOException {
        long totalTimeMs = System.currentTimeMillis() - startTime;

        // Separador y título
        writer.write("==========================================");
        writer.newLine();
        writer.write("=== ESTADÍSTICAS FINALES ===");
        writer.newLine();

        // Stats de nodos
        writer.write(clusterManager.getNodeStats());
        writer.newLine();

        // Tiempo total
        writer.write("Tiempo total de ejecución: " + (totalTimeMs / 1000.0) + " segundos");
        writer.newLine();
        writer.flush();
    }
}