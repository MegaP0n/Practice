package Monitors;

import Metrics.MapMetrics;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class DiskMonitor implements Monitor {

    private static final long BYTES_IN_GB = 1024L * 1024L * 1024L;

    @Override
    public String getName() {
        return "Монитор дисков";
    }

    @Override
    public MapMetrics collect() {
        File[] roots = File.listRoots();
        if (roots == null || roots.length == 0) {
            throw new IllegalStateException("Не удалось получить список доступных дисков");
        }

        Map<String, Object> values = new LinkedHashMap<>();

        long totalAll = 0;
        long freeAll = 0;
        long usableAll = 0;

        for (File root : roots) {
            long total = root.getTotalSpace() / BYTES_IN_GB;
            long free = root.getFreeSpace() / BYTES_IN_GB;
            long usable = root.getUsableSpace() / BYTES_IN_GB;

            totalAll += total;
            freeAll += free;
            usableAll += usable;

            String diskName = root.getAbsolutePath();
            values.put("disk.total (" + diskName + ")", total);
            values.put("disk.free (" + diskName + ")", free);
            values.put("disk.usable (" + diskName + ")", usable);
        }

        // Обязательные общие ключи из задания (суммарно по всем дискам в ГБ)
        values.put("disk.total", totalAll);
        values.put("disk.free", freeAll);
        values.put("disk.usable", usableAll);

        return new MapMetrics(values);
    }
}
