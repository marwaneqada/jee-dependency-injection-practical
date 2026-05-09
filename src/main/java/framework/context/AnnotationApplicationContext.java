package framework.context;

import framework.annotations.Autowired;
import framework.annotations.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnnotationApplicationContext implements ApplicationContext {
    private final Map<String, Object> beans = new LinkedHashMap<>();

    public AnnotationApplicationContext(Class<?>... classes) {
        createBeans(classes);
        injectDependencies();
    }

    @Override
    public Object getBean(String name) {
        Object bean = beans.get(name);
        if (bean == null) {
            throw new RuntimeException("No bean found with name: " + name);
        }
        return bean;
    }

    @Override
    public <T> T getBean(Class<T> type) {
        return type.cast(findBeanByType(type));
    }

    private void createBeans(Class<?>... classes) {
        List<Class<?>> classesWithAutowiredConstructor = new ArrayList<>();

        for (Class<?> clazz : classes) {
            if (!clazz.isAnnotationPresent(Component.class)) {
                continue;
            }

            Constructor<?> autowiredConstructor = getAutowiredConstructor(clazz);
            if (autowiredConstructor != null) {
                classesWithAutowiredConstructor.add(clazz);
                continue;
            }

            createBeanWithDefaultConstructor(clazz);
        }

        createBeansWithAutowiredConstructors(classesWithAutowiredConstructor);
    }

    private void createBeanWithDefaultConstructor(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();
            addBean(clazz, instance);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bean with default constructor for class: " + clazz.getName(), e);
        }
    }

    private void createBeansWithAutowiredConstructors(List<Class<?>> classes) {
        List<Class<?>> pendingClasses = new ArrayList<>(classes);

        while (!pendingClasses.isEmpty()) {
            boolean beanCreated = false;
            Iterator<Class<?>> iterator = pendingClasses.iterator();

            while (iterator.hasNext()) {
                Class<?> clazz = iterator.next();
                Constructor<?> constructor = getAutowiredConstructor(clazz);

                if (!canResolveParameters(constructor.getParameterTypes())) {
                    continue;
                }

                try {
                    constructor.setAccessible(true);
                    Object instance = constructor.newInstance(resolveParameters(constructor.getParameterTypes()));
                    addBean(clazz, instance);
                    iterator.remove();
                    beanCreated = true;
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create bean with @Autowired constructor for class: " + clazz.getName(), e);
                }
            }

            if (!beanCreated) {
                throw new RuntimeException("Could not resolve constructor dependencies for classes: " + pendingClasses);
            }
        }
    }

    private void injectDependencies() {
        injectFields();
        injectMethods();
    }

    private void injectFields() {
        for (Object bean : beans.values()) {
            for (Field field : bean.getClass().getDeclaredFields()) {
                if (!field.isAnnotationPresent(Autowired.class)) {
                    continue;
                }

                try {
                    Object dependency = findBeanByType(field.getType());
                    field.setAccessible(true);
                    field.set(bean, dependency);
                } catch (Exception e) {
                    throw new RuntimeException(
                            "Failed to inject dependency into field: "
                                    + field.getName()
                                    + " of class: "
                                    + bean.getClass().getName(),
                            e
                    );
                }
            }
        }
    }

    private void injectMethods() {
        for (Object bean : beans.values()) {
            for (Method method : bean.getClass().getDeclaredMethods()) {
                if (!method.isAnnotationPresent(Autowired.class)) {
                    continue;
                }

                try {
                    method.setAccessible(true);
                    method.invoke(bean, resolveParameters(method.getParameterTypes()));
                } catch (Exception e) {
                    throw new RuntimeException(
                            "Failed to inject dependency with method: "
                                    + method.getName()
                                    + " of class: "
                                    + bean.getClass().getName(),
                            e
                    );
                }
            }
        }
    }

    private void addBean(Class<?> clazz, Object instance) {
        String beanName = getBeanName(clazz);

        if (beans.containsKey(beanName)) {
            throw new RuntimeException("A bean already exists with name: " + beanName);
        }

        beans.put(beanName, instance);
    }

    private Constructor<?> getAutowiredConstructor(Class<?> clazz) {
        Constructor<?> autowiredConstructor = null;

        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (!constructor.isAnnotationPresent(Autowired.class)) {
                continue;
            }

            if (autowiredConstructor != null) {
                throw new RuntimeException("More than one @Autowired constructor found in class: " + clazz.getName());
            }

            autowiredConstructor = constructor;
        }

        return autowiredConstructor;
    }

    private boolean canResolveParameters(Class<?>[] parameterTypes) {
        for (Class<?> parameterType : parameterTypes) {
            if (findBeanByTypeIfAvailable(parameterType) == null) {
                return false;
            }
        }

        return true;
    }

    private Object[] resolveParameters(Class<?>[] parameterTypes) {
        Object[] parameters = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            parameters[i] = findBeanByType(parameterTypes[i]);
        }

        return parameters;
    }

    private String getBeanName(Class<?> clazz) {
        Component component = clazz.getAnnotation(Component.class);
        if (!component.value().isEmpty()) {
            return component.value();
        }

        String simpleName = clazz.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    private Object findBeanByType(Class<?> type) {
        Object foundBean = findBeanByTypeIfAvailable(type);

        if (foundBean == null) {
            throw new RuntimeException("No bean found for type: " + type.getName());
        }

        return foundBean;
    }

    private Object findBeanByTypeIfAvailable(Class<?> type) {
        Object foundBean = null;

        for (Object bean : beans.values()) {
            if (!type.isAssignableFrom(bean.getClass())) {
                continue;
            }

            if (foundBean != null) {
                throw new RuntimeException("More than one bean found for type: " + type.getName());
            }

            foundBean = bean;
        }

        return foundBean;
    }
}
