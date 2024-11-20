package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

public class AutoScanApplicationContext extends AbstractApplicationContext {

    /**
     * Creates a context containing classes from the package {@code packageName} annotated with {@code @Bean}.
     * <br/>
     * If the bean name is not specified in the annotation ({@code name} is empty), it is taken from the class name.
     * <br/>
     * It is assumed that all classes passed in the list have a no-argument constructor.
     *
     * @param packageNames the name of the package to scan
     */
    public AutoScanApplicationContext(String ... packageNames) {
        super(getAnnotatedClasses(packageNames));
    }

    private static Class<?>[] getAnnotatedClasses(String... packageNames) {
        Reflections reflections = new Reflections(packageNames, Scanners.TypesAnnotated);
        return reflections.getTypesAnnotatedWith(Bean.class).toArray(Class[]::new);
    }
}