package by.bsu.dependency.ExampleUsage;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

import java.util.concurrent.CompletableFuture;

/**
 * A prototype service that processes data asynchronously.
 * Uses a repository to fetch data.
 */
@Bean(name = "prototypeService", scope = BeanScope.PROTOTYPE)
public class PrototypeService {
    private static int instanceCounter = 0;
    private final int instanceId;

    @Inject
    private Repository repository;

    public PrototypeService() {
        instanceId = ++instanceCounter;
    }

    @PostConstruct
    public void init() {
        System.out.println("PrototypeService bean initialized " + instanceId);
    }

    public static int getInstanceCounter() {
        return instanceCounter;
    }

    public int getInstanceId() {
        return instanceId;
    }

    /**
     * Processes the data for the specified key asynchronously.
     *
     * @param key the key to process data for
     * @return a future that will contain the processed data
     */
    public CompletableFuture<String> processPrototypeDataAsync(String key) {
        return CompletableFuture.supplyAsync(() -> repository.fetchData(key) + " and processed by prototype data in instance " + instanceId);
    }
}