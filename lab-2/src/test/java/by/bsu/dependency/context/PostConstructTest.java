package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.example.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PostConstructTest {

    private AutoScanApplicationContext context;

    @BeforeAll
    public static void init() {
        System.out.println("\n\n\nRunning PostConstructTest");
    }

    @BeforeEach
    public void setUp() {
        context = new AutoScanApplicationContext("by.bsu.dependency.TestBeans", "by.bsu.dependency.example");
        context.start();
    }

    @Test
    public void testPostConstructCalled() {
        PCBean exampleBean = context.getBean(PCBean.class);
        assertTrue(exampleBean.isInitialized, "PostConstruct method should be called");
    }

    @Test
    public void testDependenciesInjectedBeforePostConstruct() {
        PCBean exampleBean = context.getBean(PCBean.class);
        assertNotNull(exampleBean.getFirstBean(), "Dependencies should be injected before PostConstruct method is called");
    }

    @Test
    public void testPostConstructWithDependency() {
        DependentBean dependentBean = context.getBean(DependentBean.class);
        assertTrue(dependentBean.isInitialized(), "PostConstruct method should be called");
        assertNotNull(dependentBean.getDependency(), "Dependency should be injected before PostConstruct method is called");
    }

    @Test
    public void testMultiplePostConstructMethods() {
        MegaBean exampleBean = context.getBean(MegaBean.class);
        assertTrue(exampleBean.isMultipleInitialized(), "All PostConstruct methods should be called");
    }

    @Test
    public void testPostConstructOrder() {
        MegaBean exampleBean = context.getBean(MegaBean.class);
        assertEquals(2, exampleBean.getInitializationOrder().size(), "Both PostConstruct methods should be called");
        assertEquals("init1", exampleBean.getInitializationOrder().get(0), "First PostConstruct method should be called first");
        assertEquals("init2", exampleBean.getInitializationOrder().get(1), "Second PostConstruct method should be called second");
    }

    @Test
    public void testMegaBeanInitialization() {
        SingletonBean singletonBean = context.getBean(SingletonBean.class);
        assertNotNull(singletonBean, "SingletonBean should be created");

        MegaBean megaBean = context.getBean(MegaBean.class);
        assertTrue(megaBean.isInitialized(), "MegaBean should be initialized when used");

        singletonBean.useMegaBean();
        assertTrue(megaBean.isInitialized(), "MegaBean should be initialized when used");
    }

    @Bean
    public static class DependentBean {
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

    @Bean
    public static class SingletonBean {
        @Inject
        private MegaBean megaBean;

        public void useMegaBean() {
            if (!megaBean.isInitialized()) {
                throw new IllegalStateException("MegaBean is not initialized yet.");
            }
            megaBean.doSomething();
        }
    }

    @Bean(name = "megaBean")
    public static class MegaBean {
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

    @Bean(name = "exampleBean")
    public static class PCBean {
        public boolean isInitialized = false;

        @Inject
        private FirstBean firstBean;

        @PostConstruct
        public void init() {
            isInitialized = true;
            System.out.println("PostConstruct method called. FirstBean is initialized: " + (firstBean != null));
        }

        public Object getFirstBean() {
            if (!isInitialized || firstBean == null) {
                throw new IllegalStateException("Bean is not initialized yet.");
            }
            return firstBean;
        }
    }
}