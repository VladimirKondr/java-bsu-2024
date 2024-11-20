package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

@Bean
public class DependentBean {
    @Inject
    private PCBean dependency;

    private boolean initialized = false;

    @PostConstruct
    public void init() {
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public PCBean getDependency() {
        if (!initialized || dependency == null) {
            throw new IllegalStateException("Bean is not initialized yet.");
        }
        return dependency;
    }
}