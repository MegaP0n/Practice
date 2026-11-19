package Metrics;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MapMetrics implements Metrics {

    private final Map<String, Object> values;

    public MapMetrics(Map<String, Object> values) {
        Objects.requireNonNull(values, "Карта показателей не может быть null");
        this.values = Map.copyOf(values);
    }

    @Override
    public Object get(String key) {
        return values.get(key);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        Objects.requireNonNull(type, "Целевой тип не может быть null");
        Object value = values.get(key);

        if (value == null) {
            return null;
        }

        if (!type.isInstance(value)) {
            throw new IllegalArgumentException(
                    String.format("Показатель '%s' имеет тип %s, невозможно привести к %s",
                            key, value.getClass().getSimpleName(), type.getSimpleName())
            );
        }

        return type.cast(value);
    }

    @Override
    public boolean contains(String key) {
        return values.containsKey(key);
    }

    @Override
    public Set<String> keys() {
        return Collections.unmodifiableSet(values.keySet());
    }

    @Override
    public Map<String, Object> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "MapMetrics" + values;
    }
}


