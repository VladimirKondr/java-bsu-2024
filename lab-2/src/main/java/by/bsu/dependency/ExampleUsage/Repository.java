package by.bsu.dependency.ExampleUsage;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;

@Bean(name = "repository")
public class Repository {
    @Inject
    private DataSource dataSource;

    @Inject
    private CacheService cacheService;

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