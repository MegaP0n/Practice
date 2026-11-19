package Monitors;

import Metrics.Metrics;

public interface Monitor {
    String getName();
    Metrics collect();
}
