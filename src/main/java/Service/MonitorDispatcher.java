package Service;

import Monitors.Monitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MonitorDispatcher {

    private final List<Monitor> monitors = new ArrayList<>();

    public void register(Monitor monitor) {
        Objects.requireNonNull(monitor, "Монитор не может быть null");
        if (!monitors.contains(monitor)) {
            monitors.add(monitor);
        }
    }

    public List<Monitor> getMonitors() {
        return Collections.unmodifiableList(monitors);
    }
}
