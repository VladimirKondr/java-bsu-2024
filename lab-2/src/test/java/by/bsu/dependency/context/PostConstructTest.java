package by.bsu.dependency.context;

import by.bsu.dependency.example.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}