package framework.context;

import framework.annotations.Autowired;
import framework.annotations.Component;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
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
        for (Class<?> clazz : classes) {
            if (!clazz.isAnnotationPresent(Component.class)) {
                continue;
            }

            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                String beanName = getBeanName(clazz);

                if (beans.containsKey(beanName)) {
                    throw new RuntimeException("A bean already exists with name: " + beanName);
                }

                beans.put(beanName, instance);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create bean for class: " + clazz.getName(), e);
            }
        }
    }

    private void injectDependencies() {
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

    private String getBeanName(Class<?> clazz) {
        Component component = clazz.getAnnotation(Component.class);
        if (!component.value().isEmpty()) {
            return component.value();
        }

        String simpleName = clazz.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    private Object findBeanByType(Class<?> type) {
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

        if (foundBean == null) {
            throw new RuntimeException("No bean found for type: " + type.getName());
        }

        return foundBean;
    }
}
