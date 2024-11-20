package by.bsu.dependency.ExampleUsage;

import by.bsu.dependency.annotation.Bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * A simple data source that stores data in memory.
 * Used to simulate data fetching from a database.
 * Uses feature toggles to enable/disable certain features.
 */
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

    /**
     * Sets the feature toggle for the specified feature.
     *
     * @param feature   the feature to enable/disable
     * @param isEnabled true to enable the feature, false to disable it
     */
    public void setFeatureToggle(String feature, boolean isEnabled) {
        featureToggles.put(feature, isEnabled);
    }

    /**
     * Checks if the specified feature is enabled.
     *
     * @param feature the feature to check
     * @return true if the feature is enabled, false otherwise
     */
    public boolean isFeatureEnabled(String feature) {
        return featureToggles.getOrDefault(feature, false);
    }

    /**
     * Fetches data for the specified key.
     *
     * @param key the key to fetch data for
     * @return the data associated with the key, or a message indicating no data was found
     */
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