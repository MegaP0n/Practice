package Monitors;

import Metrics.Metrics;
import Metrics.MapMetrics;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;

import java.util.Map;

public class MemoryMonitor implements Monitor {

    private static final long BYTES_IN_MB = 1024L * 1024L;
    private final GlobalMemory memory;

    public MemoryMonitor() {
        SystemInfo systemInfo = new SystemInfo();
        this.memory = systemInfo.getHardware().getMemory();
    }

    @Override
    public String getName() {
        return "Монитор оперативной памяти";
    }

    @Override
    public Metrics collect() {
        long totalMb = memory.getTotal() / BYTES_IN_MB;
        long availableMb = memory.getAvailable() / BYTES_IN_MB;
        long usedMb = totalMb - availableMb;

        return new MapMetrics(Map.of(
                "memory.used", usedMb,
                "memory.total", totalMb,
                "memory.available", availableMb
        ));
    }
}
