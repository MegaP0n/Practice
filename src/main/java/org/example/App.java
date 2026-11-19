package org.example;

import Monitors.CpuMonitor;
import Monitors.DiskMonitor;
import Monitors.MemoryMonitor;
import Service.MonitorDispatcher;
import Service.MonitorService;

public class App {
    public static void main(String[] args) {
        MonitorDispatcher dispatcher = new MonitorDispatcher();
        dispatcher.register(new CpuMonitor());
        dispatcher.register(new MemoryMonitor());
        dispatcher.register(new DiskMonitor());

        MonitorService monitorService = new MonitorService(dispatcher);

        monitorService.collectWithExecutorService();

        monitorService.collectWithCompletableFuture();
    }
}
