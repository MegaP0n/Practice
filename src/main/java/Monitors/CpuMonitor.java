package Monitors;

import Metrics.Metrics;
import Metrics.MapMetrics;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.util.Map;

public class CpuMonitor implements Monitor {

    private final CentralProcessor processor;

    public CpuMonitor() {
        SystemInfo systemInfo = new SystemInfo();
        this.processor = systemInfo.getHardware().getProcessor();
    }

    @Override
    public String getName() {
        return "Монитор процессора";
    }

    @Override
    public Metrics collect() {
        long[] prevTicks = processor.getSystemCpuLoadTicks();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Сбор данных о загрузке процессора был прерван", e);
        }

        double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100.0;
        double roundedLoad = Math.round(cpuLoad * 100.0) / 100.0;

        return new MapMetrics(Map.of(
                "cpu.usage", roundedLoad
        ));
    }
}
