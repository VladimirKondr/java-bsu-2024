package by.bsu.dependency.ExampleUsage;

import by.bsu.dependency.context.AutoScanApplicationContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ExampleUsage {
    /**
     * Demonstrates the usage of the implemented framework.
     * Tries to simulate work with a database using a data source and a repository.
     * Demonstrates the difference between sequential and parallel data fetching, utilizing prototype beans.
     * Demonstrates the usage of the cache service.
     * Demonstrates the usage of feature toggles made possible by utilizing singleton beans.
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        AutoScanApplicationContext context = new AutoScanApplicationContext("by.bsu.dependency.ExampleUsage");
        context.start();

        DataSource dataSource = context.getBean(DataSource.class);
        dataSource.setFeatureToggle("cacheEnabled", true);

        CacheService cacheService = context.getBean(CacheService.class);
        cacheService.clearCache();

        PrototypeService prototypeService1 = context.getBean(PrototypeService.class);
        PrototypeService prototypeService2 = context.getBean(PrototypeService.class);
        PrototypeService prototypeService3 = context.getBean(PrototypeService.class);
        System.out.print("\n\n");

        // Sequential data fetching
        System.out.println("Sequential data fetching:");
        long startSequential = System.currentTimeMillis();
        System.out.println(prototypeService1.processPrototypeDataAsync("key1").get());
        System.out.println(prototypeService2.processPrototypeDataAsync("key2").get());
        System.out.println(prototypeService3.processPrototypeDataAsync("key3").get());
        long endSequential = System.currentTimeMillis();
        System.out.println("Sequential fetching time: " + (endSequential - startSequential) + " ms");
        System.out.print("\n\n");

        // Parallel data fetching
        cacheService.clearCache();
        System.out.println("Parallel data fetching:");
        long startParallel = System.currentTimeMillis();
        CompletableFuture<String> future1 = prototypeService1.processPrototypeDataAsync("key1");
        CompletableFuture<String> future2 = prototypeService2.processPrototypeDataAsync("key2");
        CompletableFuture<String> future3 = prototypeService3.processPrototypeDataAsync("key3");

        System.out.println(future1.get());
        System.out.println(future2.get());
        System.out.println(future3.get());
        long endParallel = System.currentTimeMillis();
        System.out.println("Parallel fetching time: " + (endParallel - startParallel) + " ms");
        System.out.print("\n\n");

        System.out.println("Trying to get non-existent key:");
        System.out.println(prototypeService3.processPrototypeDataAsync("key37").get());
        System.out.print("\n\n");

        // Demonstrate with cacheEnabled = true
        cacheService.clearCache();
        dataSource.setFeatureToggle("cacheEnabled", true);
        System.out.println("Cache enabled:");
        Repository repository = context.getBean(Repository.class);
        System.out.println(repository.fetchData("key1"));
        System.out.println(repository.fetchData("key2"));
        System.out.println(repository.fetchData("key1"));
        System.out.print("\n\n");

        // Demonstrate with cacheEnabled = false
        dataSource.setFeatureToggle("cacheEnabled", false);
        System.out.println("Cache disabled:");
        System.out.println(repository.fetchData("key1"));
        System.out.println(repository.fetchData("key2"));
        System.out.println(repository.fetchData("key1"));
        System.out.print("\n\n");
    }
}