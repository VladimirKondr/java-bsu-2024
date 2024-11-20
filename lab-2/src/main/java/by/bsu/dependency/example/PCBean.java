package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

@Bean(name = "exampleBean")
public class PCBean {
    public boolean isInitialized = false;

    @Inject
    private FirstBean firstBean;

    @PostConstruct
    public void init() {
        isInitialized = true;
        System.out.println("PostConstruct method called. FirstBean is initialized: " + (firstBean != null));
    }

    public void doSomething() {
        if (!isInitialized || firstBean == null) {
            throw new IllegalStateException("Bean is not initialized yet.");
        }
        System.out.println("ExampleBean is doing something.");
    }

    public Object getFirstBean() {
        if (!isInitialized || firstBean == null) {
            throw new IllegalStateException("Bean is not initialized yet.");
        }
        return firstBean;
    }
}