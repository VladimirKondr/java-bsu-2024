package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;

@Bean(name = "megaBean")
public class MegaBean {
    private boolean isInitialized = false;
    private boolean multipleInitialized = false;
    private final List<String> initializationOrder = new ArrayList<>();

    @Inject
    private FirstBean firstBean;

    @PostConstruct
    public void init() {
        isInitialized = true;
        initializationOrder.add("init1");
    }

    @PostConstruct
    public void secondInit() {
        multipleInitialized = true;
        initializationOrder.add("init2");
    }

    public FirstBean getFirstBean() {
        if (!isInitialized || firstBean == null) {
            throw new IllegalStateException("Bean is not initialized yet.");
        }
        return firstBean;
    }

    public void doSomething() {
        if (!isInitialized || firstBean == null) {
            throw new IllegalStateException("Bean is not initialized yet.");
        }
        System.out.println("MegaBean is doing something.");
    }

    public boolean isMultipleInitialized() {
        return multipleInitialized;
    }

    public List<String> getInitializationOrder() {
        return initializationOrder;
    }

    public boolean isInitialized() {
        return isInitialized;
    }
}