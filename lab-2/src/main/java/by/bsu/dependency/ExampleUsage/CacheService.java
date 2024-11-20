package by.bsu.dependency.ExampleUsage;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;

import java.util.HashMap;
import java.util.Map;

@Bean(name = "cacheService")
public class CacheService {
    private final Map<String, String> cache = new HashMap<>();

    @Inject
    private DataSource dataSource;

    public String getData(String key) {
        if (cache.containsKey(key)) {
            return "Cache hit: " + cache.get(key);
        } else {
            String data = dataSource.getData(key);
            cache.put(key, data);
            return "Cache miss: " + data;
        }
    }

    public void putData(String key, String value) {
        cache.put(key, value);
    }

    public void clearCache() {
        System.out.println("Cache cleared");
        cache.clear();
    }
}