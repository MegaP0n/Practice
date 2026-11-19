package Service;

import Metrics.Metrics;
import Monitors.Monitor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MonitorService {

    private final MonitorDispatcher dispatcher;

    public MonitorService(MonitorDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void collectWithExecutorService() {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Monitor> monitors = dispatcher.getMonitors();
        Map<Monitor, Future<Metrics>> futures = new LinkedHashMap<>();

        try {
            for (Monitor monitor : monitors) {
                futures.put(monitor, executor.submit(monitor::collect));
            }

            System.out.println("========== ОТЧЁТ СИСТЕМЫ (ExecutorService) ==========\n");
            for (Map.Entry<Monitor, Future<Metrics>> entry : futures.entrySet()) {
                Monitor monitor = entry.getKey();
                System.out.println("[" + monitor.getName() + "]");
                try {
                    Metrics metrics = entry.getValue().get();
                    printMetrics(metrics);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("ОШИБКА: Сбор данных был прерван");
                } catch (ExecutionException e) {
                    System.out.println("ОШИБКА: Не удалось собрать информацию (" + e.getCause().getMessage() + ")");
                }
                System.out.println();
            }
            System.out.println("=====================================================\n");
        } finally {
            executor.shutdown();
        }
    }

    public void collectWithCompletableFuture() {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Monitor> monitors = dispatcher.getMonitors();

        try {
            Map<Monitor, CompletableFuture<String>> futures = new LinkedHashMap<>();

            for (Monitor monitor : monitors) {
                CompletableFuture<String> future = CompletableFuture
                        .supplyAsync(monitor::collect, executor)
                        .thenApply(this::formatMetrics)
                        .exceptionally(ex -> "ОШИБКА: Не удалось собрать информацию (" + ex.getCause().getMessage() + ")\n");

                futures.put(monitor, future);
            }

            CompletableFuture.allOf(futures.values().toArray(new CompletableFuture[0])).join();

            System.out.println("========== ОТЧЁТ СИСТЕМЫ (CompletableFuture) ==========\n");
            for (Map.Entry<Monitor, CompletableFuture<String>> entry : futures.entrySet()) {
                System.out.println("[" + entry.getKey().getName() + "]");
                System.out.println(entry.getValue().join());
            }
            System.out.println("=======================================================\n");
        } finally {
            executor.shutdown();
        }
    }

    private void printMetrics(Metrics metrics) {
        System.out.print(formatMetrics(metrics));
    }

    private String formatMetrics(Metrics metrics) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : metrics.getValues().entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}