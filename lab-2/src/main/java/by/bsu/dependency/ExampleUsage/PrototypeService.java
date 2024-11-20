package by.bsu.dependency.ExampleUsage;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

import java.util.concurrent.CompletableFuture;

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

    public CompletableFuture<String> processPrototypeDataAsync(String key) {
        return CompletableFuture.supplyAsync(() -> repository.fetchData(key) + " and processed by prototype data in instance " + instanceId);
    }
}