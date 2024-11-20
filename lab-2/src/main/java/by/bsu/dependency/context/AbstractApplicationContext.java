package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractApplicationContext implements ApplicationContext {
    protected enum ContextStatus {
        NOT_STARTED,
        STARTED
    }

    protected Map<String, Object> singletonBeans = new HashMap<>();
    protected Map<String, Class<?>> beanDefinitions = new HashMap<>();
    protected Map<String, BeanScope> beanScopes = new HashMap<>();
    protected ContextStatus state = ContextStatus.NOT_STARTED;
    // private static final Logger logger = Logger.getLogger(AbstractApplicationContext.class.getName());


    public AbstractApplicationContext(Class<?>... beanClasses) {
        for (Class<?> beanClass : beanClasses) {
            String beanName = getBeanName(beanClass);
            Bean beanAnnotation = beanClass.getAnnotation(Bean.class);
            BeanScope scope = (beanAnnotation != null) ? beanAnnotation.scope() : BeanScope.SINGLETON;
            beanDefinitions.put(beanName, beanClass);
            beanScopes.put(beanName, scope);
        }
    }


    /** Помимо прочего, метод должен заниматься внедрением зависимостей в создаваемые объекты */
    @Override
    public void start() {
        if (state != ContextStatus.NOT_STARTED) {
            throw new IllegalStateException("Context can only be started from CREATED state");
        }
        try {
            // logger.info("Starting application context");
            state = ContextStatus.STARTED;
            checkCircularDependencies();
            createSingletonBeans();
            injectDependencies();
            invokePostConstructs();
            // logger.info("Application context started successfully");
        } catch (RuntimeException e) {
            state = ContextStatus.NOT_STARTED;
            // logger.log(Level.SEVERE, "Failed to start application context", e);
            throw new ApplicationContextNotStartedException(
                    "Failed to start application context " + e.getMessage());
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            state = ContextStatus.NOT_STARTED;
            // logger.log(Level.SEVERE, "Failed to start application context", e);
            throw new ApplicationContextNotStartedException(e.getMessage());
        }
    }

    @Override
    public boolean isRunning() {
        return state == ContextStatus.STARTED;
    }

    @Override
    public boolean containsBean(String name) {
        ensureRunning();
        return singletonBeans.containsKey(name) || beanDefinitions.containsKey(name);
    }


    @Override
    public Object getBean(String name) {
        ensureRunning();
        if (!containsBean(name)) {
            throw new NoSuchBeanDefinitionException("No bean named '" + name + "' is defined");
        }
        return (beanScopes.get(name) == BeanScope.SINGLETON)
                ? singletonBeans.get(name)
                : createPrototypeBean(name);
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        ensureRunning();
        for (Object bean : singletonBeans.values()) {
            if (clazz.isInstance(bean)) {
                return clazz.cast(bean);
            }
        }
        for (String beanName : beanDefinitions.keySet()) {
            if (clazz.isAssignableFrom(beanDefinitions.get(beanName))) {
                return clazz.cast(getBean(beanName));
            }
        }
        throw new NoSuchBeanDefinitionException("No bean of type '" + clazz.getName() + "' is defined");
    }

    @Override
    public boolean isPrototype(String name) {
        ensureRunning();
        return beanScopes.get(name) == BeanScope.PROTOTYPE;
    }

    @Override
    public boolean isSingleton(String name) {
        ensureRunning();
        return beanScopes.get(name) == BeanScope.SINGLETON;
    }

    protected void ensureRunning() {
        if (state != ContextStatus.STARTED) {
            throw new ApplicationContextNotStartedException("Application context is not started");
        }
    }

    protected String getBeanName(Class<?> beanClass) {
        Bean beanAnnotation = beanClass.getAnnotation(Bean.class);
        if (beanAnnotation != null && !beanAnnotation.name().isEmpty()) {
            return beanAnnotation.name();
        }
        return Character.toLowerCase(beanClass.getSimpleName().charAt(0)) + beanClass.getSimpleName().substring(1);
    }

    protected Object instantiateBean(Class<?> beanClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        // logger.info("Instantiating bean: " + beanClass.getName());
        return beanClass.getDeclaredConstructor().newInstance();
    }

    protected void createSingletonBeans() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (Map.Entry<String, Class<?>> entry : beanDefinitions.entrySet()) {
            String beanName = entry.getKey();
            if (beanScopes.get(beanName) == BeanScope.SINGLETON && !singletonBeans.containsKey(beanName)) {
                // logger.info("Creating singleton bean: " + beanName);
                Object bean = instantiateBean(entry.getValue());
                singletonBeans.put(beanName, bean);
            }
        }
    }

    protected Object createPrototypeBean(String name) {
        try {
            // logger.info("Creating prototype bean: " + name);
            Object prototypeBean = instantiateBean(beanDefinitions.get(name));
            injectDependencies(prototypeBean);
            invokePostConstruct(prototypeBean);
            return prototypeBean;
        } catch (Exception e) {
            // logger.log(Level.SEVERE, "Failed to create prototype bean", e);
            throw new RuntimeException("Failed to create prototype bean", e);
        }
    }

    protected void injectDependencies() throws IllegalAccessException {
        for (Object bean : singletonBeans.values()) {
            injectDependencies(bean);
        }
    }

    protected void injectDependencies(Object bean) throws IllegalAccessException {
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                boolean accessible = field.canAccess(bean);
                field.setAccessible(true);
                Object dependency = getBean(field.getType());
                field.set(bean, dependency);
                field.setAccessible(accessible);
                // logger.info("Injected dependency: " + field.getName() + " into bean: " + bean.getClass().getName());
            }
        }
    }
    /**
     * The method should check for circular dependencies consisting only of PROTOTYPE in the context.
     * The cycles are searched in the directed graph of bean dependencies.
     */
    protected void checkCircularDependencies() {
        for (String beanName : beanDefinitions.keySet()) {
            if (beanScopes.get(beanName) == BeanScope.PROTOTYPE) {
                checkCircularDependencies(beanName, new HashSet<>(), new HashSet<>());
            }
        }
    }

    protected void checkCircularDependencies(String beanName, Set<String> visitedBeans, Set<String> currentPath) {
        if (beanScopes.get(beanName) == BeanScope.SINGLETON) {
            return;
        }
        if (beanScopes.get(beanName) == BeanScope.PROTOTYPE && currentPath.contains(beanName)) {
            throw new ApplicationContextNotStartedException("Circular dependency detected: " + beanName);
        }
        if (visitedBeans.contains(beanName)) {
            return;
        }

        currentPath.add(beanName);
        Class<?> beanClass = beanDefinitions.get(beanName);
        for (Field field : beanClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                String dependencyName = getBeanName(field.getType());
                checkCircularDependencies(dependencyName, visitedBeans, currentPath);
            }
        }
        currentPath.remove(beanName);
        visitedBeans.add(beanName);
    }

    protected void invokePostConstructs() throws InvocationTargetException, IllegalAccessException {
        for (Object bean : singletonBeans.values()) {
            invokePostConstruct(bean);
        }
    }

    private void invokePostConstruct(Object bean) throws InvocationTargetException, IllegalAccessException {
        for (Method method : bean.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                boolean accessible = method.canAccess(bean);
                method.setAccessible(true);
                method.invoke(bean);
                method.setAccessible(accessible);
                // logger.info("Invoked @PostConstruct method: " + method.getName() + " on bean: " + bean.getClass().getName());
            }
        }
    }
}
