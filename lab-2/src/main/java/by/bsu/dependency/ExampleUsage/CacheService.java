package by.bsu.dependency.ExampleUsage;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple cache service that stores data in memory.
 */
@Bean(name = "cacheService")
public class CacheService {
    private final Map<String, String> cache = new HashMap<>();

    @Inject
    private DataSource dataSource;

    /**
     * Retrieves data from the cache if it exists, otherwise fetches it from the data source.
     *
     * @param key the key to retrieve data for
     * @return a message indicating whether the data was found in the cache or fetched from the data source
     */
    public String getData(String key) {
        if (cache.containsKey(key)) {
            return "Cache hit: " + cache.get(key);
        } else {
            String data = dataSource.getData(key);
            cache.put(key, data);
            return "Cache miss: " + data;
        }
    }

    /**
     * Stores data in the cache.
     *
     * @param key   the key to store data for
     * @param value the data to store
     */
    public void putData(String key, String value) {
        cache.put(key, value);
    }

    /**
     * Clears the cache.
     */
    public void clearCache() {
        System.out.println("Cache cleared");
        cache.clear();
    }
}