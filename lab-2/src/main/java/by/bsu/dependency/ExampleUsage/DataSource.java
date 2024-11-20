package by.bsu.dependency.ExampleUsage;

import by.bsu.dependency.annotation.Bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Bean(name = "dataSource")
public class DataSource {
    private final Map<String, Boolean> featureToggles = new ConcurrentHashMap<>();
    private final Map<String, String> dataset = new ConcurrentHashMap<>();

    public DataSource() {
        dataset.put("key1", "value1");
        dataset.put("key2", "value2");
        dataset.put("key3", "value3");
        dataset.put("key4", "value4");
        dataset.put("key5", "value5");
    }

    public void setFeatureToggle(String feature, boolean isEnabled) {
        featureToggles.put(feature, isEnabled);
    }

    public boolean isFeatureEnabled(String feature) {
        return featureToggles.getOrDefault(feature, false);
    }

    public String getData(String key) {
        try {
            // Simulate data fetching
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return dataset.getOrDefault(key, "No data found for " + key);
    }
}