package by.bsu.dependency.ExampleUsage;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;

/**
 * A repository that fetches data from a data source.
 * Uses a cache service to store data.
 */
@Bean(name = "repository")
public class Repository {
    @Inject
    private DataSource dataSource;

    @Inject
    private CacheService cacheService;

    /**
     * Fetches data for the specified key.
     *
     * @param key the key to fetch data for
     * @return the data associated with the key, or a message indicating no data was found
     */
    public String fetchData(String key) {
        String data;
        if (dataSource.isFeatureEnabled("cacheEnabled")) {
            data = cacheService.getData(key);
            if (data == null) {
                data = dataSource.getData(key);
                cacheService.putData(key, data);
            }
        } else {
            data = dataSource.getData(key);
        }
        return data + " fetched from repository";
    }
}