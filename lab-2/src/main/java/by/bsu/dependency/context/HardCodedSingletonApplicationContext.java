package by.bsu.dependency.context;

import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;


public class HardCodedSingletonApplicationContext extends AbstractApplicationContext {

    public HardCodedSingletonApplicationContext(Class<?>... beanClasses) {
        super(beanClasses);
    }
    @Override
    public <T> T getBean(Class<T> clazz) {
        ensureRunning();
        return clazz.cast(beanDefinitions.values().stream()
                .filter(clazz::isInstance)
                .findFirst()
                .orElseThrow(() -> new NoSuchBeanDefinitionException("No bean of type '" + clazz.getName() + "' is defined"))
        );
    }

    @Override
    public boolean isPrototype(String name) {
        if (!beanDefinitions.containsKey(name)) {
            throw new NoSuchBeanDefinitionException("No bean named '" + name + "' is defined");
        }
        return false;
    }

    @Override
    public boolean isSingleton(String name) {
        if (!beanDefinitions.containsKey(name)) {
            throw new NoSuchBeanDefinitionException("No bean named '" + name + "' is defined");
        }
        return true;
    }

    @Override
    protected void injectDependencies() throws IllegalAccessException {
        return;
    }

    @Override
    protected void checkCircularDependencies() {
        return;
    }

    @Override
    protected void invokePostConstructs() {
        return;
    }


}
