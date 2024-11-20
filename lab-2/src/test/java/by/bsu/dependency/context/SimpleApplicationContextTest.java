package by.bsu.dependency.context;

import by.bsu.dependency.ExampleUsage.*;
import by.bsu.dependency.TestBeans.NonBeanBean;
import by.bsu.dependency.TestBeans.TestBeanA;
import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.example.FirstBean;
import by.bsu.dependency.example.OtherBean;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class SimpleApplicationContextTest {

    private SimpleApplicationContext context;

    @BeforeAll
    public static void init() {
        System.out.println("\n\n\nRunning SimpleApplicationContextTest");
    }

    /**
     * Sets up the test context before each test.
     */
    @BeforeEach
    public void setUp() {
        context = new SimpleApplicationContext(FirstBean.class, OtherBean.class, PrototypeBean.class);
    }

    /**
     * Tests if the context starts correctly.
     */
    @Test
    public void testContextStart() {
        context.start();
        assertTrue(context.isRunning());
    }

    /**
     * Tests if the context contains the specified beans.
     */
    @Test
    public void testContainsBean() {
        context.start();
        assertTrue(context.containsBean("firstBean"));
        assertTrue(context.containsBean("otherBean"));
        assertFalse(context.containsBean("nonExistentBean"));
    }

    /**
     * Tests if the context can retrieve a bean by its name.
     */
    @Test
    public void testGetBeanByName() {
        context.start();
        FirstBean firstBean = (FirstBean) context.getBean("firstBean");
        assertNotNull(firstBean);
        assertEquals("Hello, I'm first bean", firstBean.printSomething());
    }

    /**
     * Tests if the context can retrieve a bean by its class.
     */
    @Test
    public void testGetBeanByClass() {
        context.start();
        FirstBean firstBean = context.getBean(FirstBean.class);
        assertNotNull(firstBean);
        assertEquals("Hello, I'm first bean", firstBean.printSomething());
    }

    /**
     * Tests if the context creates prototype beans correctly.
     */
    @Test
    public void testPrototypeBeanCreation() {
        context.start();
        PrototypeBean prototypeBean1 = (PrototypeBean) context.getBean("prototypeBean");
        PrototypeBean prototypeBean2 = (PrototypeBean) context.getBean("prototypeBean");
        assertNotSame(prototypeBean1, prototypeBean2);
    }

    /**
     * Tests if the context creates singleton beans correctly.
     */
    @Test
    public void testSingletonBeanCreation() {
        context.start();
        FirstBean firstBean1 = (FirstBean) context.getBean("firstBean");
        FirstBean firstBean2 = (FirstBean) context.getBean("firstBean");
        assertSame(firstBean1, firstBean2);
    }

    /**
     * Tests if the context injects dependencies correctly.
     */
    @Test
    public void testInjectDependencies() {
        context.start();
        OtherBean otherBean = (OtherBean) context.getBean("otherBean");
        assertNotNull(otherBean.getFirstBean());
        assertEquals("Hello, I'm first bean", otherBean.getFirstBean().printSomething());
    }

    /**
     * Tests if the context correctly identifies prototype beans.
     */
    @Test
    public void testIsPrototype() {
        context.start();
        assertTrue(context.isPrototype("prototypeBean"));
        assertFalse(context.isPrototype("firstBean"));
    }

    /**
     * Tests if the context correctly identifies singleton beans.
     */
    @Test
    public void testIsSingleton() {
        context.start();
        assertTrue(context.isSingleton("firstBean"));
        assertFalse(context.isSingleton("prototypeBean"));
    }

    /**
     * Tests if the context throws an exception when trying to get a bean before the context is started.
     */
    @Test
    public void testGetBeanBeforeStart() {
        assertThrows(ApplicationContextNotStartedException.class, () -> context.getBean("firstBean"));
    }

    /**
     * Tests if the context throws an exception when trying to check if a bean exists before the context is started.
     */
    @Test
    public void testContainsBeanBeforeStart() {
        assertThrows(ApplicationContextNotStartedException.class, () -> context.containsBean("firstBean"));
    }

    /**
     * Tests if the context throws an exception when trying to get a non-existent bean.
     */
    @Test
    public void testNoSuchBeanDefinitionException() {
        context.start();
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean("nonExistentBean"));
    }

    /**
     * Tests if the context throws an exception when trying to get a non-existent bean by class.
     */
    @Test
    public void testGetBeanByClassNoSuchBeanDefinitionException() {
        context.start();
        assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(NonExistentBean.class));
    }

    /**
     * Tests if the context can create a bean with multiple dependencies.
     * Additionally, checks if we are searching for dependencies in a directed graph
     */
    @Test
    public void testBeanWithMultipleDependencies() {
        context = new SimpleApplicationContext(BeanWithMultipleDependencies.class, FirstBean.class, OtherBean.class);
        context.start();
        BeanWithMultipleDependencies bean = context.getBean(BeanWithMultipleDependencies.class);
        assertNotNull(bean);
        assertNotNull(bean.getFirstBean());
        assertNotNull(bean.getOtherBean());
    }

    @Test
    public void testCircularDependencyWithoutInject() {
        context = new SimpleApplicationContext(CircularBeanC.class, CircularBeanD.class);
        assertDoesNotThrow(context::start);
    }

    /**
     * Tests if the context can create a bean with nested dependencies.
     */
    @Test
    public void testBeanWithNestedDependencies() {
        context = new SimpleApplicationContext(BeanWithNestedDependencies.class, FirstBean.class, OtherBean.class);
        context.start();
        BeanWithNestedDependencies bean = context.getBean(BeanWithNestedDependencies.class);
        assertNotNull(bean);
        assertNotNull(bean.getOtherBean());
        assertNotNull(bean.getOtherBean().getFirstBean());
    }

    /**
     * Tests if the context throws an exception when there is a circular dependency.
     */
    @Test
    public void testCircularDependency() {
        context = new SimpleApplicationContext(CircularBeanA.class, CircularBeanB.class);
        assertDoesNotThrow(context::start);
    }

    @Test
    public void testCircularDependencyPrototype() {
        context = new SimpleApplicationContext(CircularBeanProtA.class, CircularBeanProtB.class);
        assertThrows(ApplicationContextNotStartedException.class, context::start);
    }

    @Bean(name = "firstBean")
    public static class FirstBean {
        public String printSomething() {
            return "Hello, I'm first bean";
        }
    }

    @Bean(name = "otherBean")
    public static class OtherBean {
        @Inject
        private FirstBean firstBean;

        public FirstBean getFirstBean() {
            return firstBean;
        }
    }


    /**
     * Bean class representing a prototype bean.
     */
    @Bean(name = "prototypeBean", scope = BeanScope.PROTOTYPE)
    public static class PrototypeBean {
    }

    /**
     * Bean class representing a non-existent bean.
     */
    public static class NonExistentBean {
    }

    /**
     * Bean class representing circular bean A.
     */
    @Bean(name = "circularBeanA")
    public static class CircularBeanA {
        @Inject
        private CircularBeanB circularBeanB;
    }

    /**
     * Bean class representing circular bean B.
     */
    @Bean(name = "circularBeanB")
    public static class CircularBeanB {
        @Inject
        private CircularBeanA circularBeanA;
    }

    @Bean(name = "circularBeanProtA", scope = BeanScope.PROTOTYPE)
    public static class CircularBeanProtA {
        @Inject
        private CircularBeanB circularBeanB;
    }

    /**
     * Bean class representing circular bean B.
     */
    @Bean(name = "circularBeanProtB", scope = BeanScope.PROTOTYPE)
    public static class CircularBeanProtB {
        @Inject
        private CircularBeanA circularBeanA;
    }

    @Bean(name = "beanWithMultipleDependencies")
    public static class BeanWithMultipleDependencies {
        @Inject
        private FirstBean firstBean;
        @Inject
        private OtherBean otherBean;

        public FirstBean getFirstBean() {
            return firstBean;
        }

        public OtherBean getOtherBean() {
            return otherBean;
        }
    }

    @Bean(name = "circularBeanC")
    public static class CircularBeanC {
        private final CircularBeanD circularBeanD;

        public CircularBeanC() {
            this.circularBeanD = null;
        }

        public CircularBeanC(CircularBeanD circularBeanD) {
            this.circularBeanD = circularBeanD;
        }
    }

    @Bean(name = "circularBeanD")
    public static class CircularBeanD {
        private final CircularBeanC circularBeanC;

        public CircularBeanD() {
            this.circularBeanC = null;
        }

        public CircularBeanD(CircularBeanC circularBeanC) {
            this.circularBeanC = circularBeanC;
        }
    }

    @Bean(name = "beanWithNestedDependencies")

    public static class BeanWithNestedDependencies {
        @Inject
        private OtherBean otherBean;

        public OtherBean getOtherBean() {
            return otherBean;
        }
    }
}