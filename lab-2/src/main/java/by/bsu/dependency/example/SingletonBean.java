package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;

@Bean
public class SingletonBean {
    @Inject
    private MegaBean megaBean;

    public void useMegaBean() {
        if (!megaBean.isInitialized()) {
            throw new IllegalStateException("MegaBean is not initialized yet.");
        }
        megaBean.doSomething();
    }
}