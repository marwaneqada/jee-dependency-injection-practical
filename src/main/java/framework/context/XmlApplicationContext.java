package framework.context;

import framework.xml.BeanDefinition;
import framework.xml.BeansConfiguration;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class XmlApplicationContext implements ApplicationContext {
    private final Map<String, Object> beans = new LinkedHashMap<>();
    private final Map<String, BeanDefinition> beanDefinitions = new LinkedHashMap<>();

    public XmlApplicationContext(String configFile) {
        BeansConfiguration configuration = loadConfiguration(configFile);

        for (BeanDefinition beanDefinition : configuration.getBeans()) {
            beanDefinitions.put(beanDefinition.getId(), beanDefinition);
        }

        for (BeanDefinition beanDefinition : configuration.getBeans()) {
            createBean(beanDefinition.getId());
        }
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

        return type.cast(foundBean);
    }

    private BeansConfiguration loadConfiguration(String configFile) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (inputStream == null) {
                throw new RuntimeException("XML configuration file not found in resources: " + configFile);
            }

            JAXBContext context = JAXBContext.newInstance(BeansConfiguration.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (BeansConfiguration) unmarshaller.unmarshal(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load XML configuration file: " + configFile, e);
        }
    }

    private Object createBean(String beanId) {
        if (beans.containsKey(beanId)) {
            return beans.get(beanId);
        }

        BeanDefinition beanDefinition = beanDefinitions.get(beanId);
        if (beanDefinition == null) {
            throw new RuntimeException("No bean definition found with id: " + beanId);
        }

        try {
            Object bean;
            String injection = beanDefinition.getInjection();

            if ("constructor".equals(injection)) {
                bean = createBeanWithConstructor(beanDefinition);
            } else {
                bean = createBeanWithDefaultConstructor(beanDefinition);
                beans.put(beanDefinition.getId(), bean);

                if ("setter".equals(injection)) {
                    injectBySetter(bean, beanDefinition.getDependencies());
                } else if ("field".equals(injection)) {
                    injectByField(bean, beanDefinition.getDependencies());
                } else if (injection != null && !injection.isEmpty()) {
                    throw new RuntimeException("Unsupported injection type: " + injection);
                }

                return bean;
            }

            beans.put(beanDefinition.getId(), bean);
            return bean;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bean with id: " + beanDefinition.getId(), e);
        }
    }

    private Object createBeanWithDefaultConstructor(BeanDefinition beanDefinition) throws Exception {
        Class<?> beanClass = Class.forName(beanDefinition.getClassName());
        Constructor<?> constructor = beanClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private Object createBeanWithConstructor(BeanDefinition beanDefinition) throws Exception {
        Class<?> beanClass = Class.forName(beanDefinition.getClassName());
        Object[] dependencies = resolveDependencies(beanDefinition.getDependencies());
        Constructor<?> constructor = findConstructor(beanClass, dependencies);
        constructor.setAccessible(true);
        return constructor.newInstance(dependencies);
    }

    private void injectBySetter(Object bean, List<String> dependencyIds) throws Exception {
        for (Object dependency : resolveDependencies(dependencyIds)) {
            Method setter = findSetter(bean.getClass(), dependency);
            setter.setAccessible(true);
            setter.invoke(bean, dependency);
        }
    }

    private void injectByField(Object bean, List<String> dependencyIds) throws Exception {
        for (Object dependency : resolveDependencies(dependencyIds)) {
            Field field = findField(bean.getClass(), dependency);
            field.setAccessible(true);
            field.set(bean, dependency);
        }
    }

    private Object[] resolveDependencies(List<String> dependencyIds) {
        Object[] dependencies = new Object[dependencyIds.size()];

        for (int i = 0; i < dependencyIds.size(); i++) {
            dependencies[i] = createBean(dependencyIds.get(i));
        }

        return dependencies;
    }

    private Constructor<?> findConstructor(Class<?> beanClass, Object[] dependencies) {
        for (Constructor<?> constructor : beanClass.getDeclaredConstructors()) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length != dependencies.length) {
                continue;
            }

            if (matches(parameterTypes, dependencies)) {
                return constructor;
            }
        }

        throw new RuntimeException("No compatible constructor found for class: " + beanClass.getName());
    }

    private Method findSetter(Class<?> beanClass, Object dependency) {
        for (Method method : beanClass.getDeclaredMethods()) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (!method.getName().startsWith("set") || parameterTypes.length != 1) {
                continue;
            }

            if (parameterTypes[0].isAssignableFrom(dependency.getClass())) {
                return method;
            }
        }

        throw new RuntimeException("No compatible setter found in class: " + beanClass.getName());
    }

    private Field findField(Class<?> beanClass, Object dependency) {
        for (Field field : beanClass.getDeclaredFields()) {
            if (field.getType().isAssignableFrom(dependency.getClass())) {
                return field;
            }
        }

        throw new RuntimeException("No compatible field found in class: " + beanClass.getName());
    }

    private boolean matches(Class<?>[] parameterTypes, Object[] dependencies) {
        for (int i = 0; i < parameterTypes.length; i++) {
            if (!parameterTypes[i].isAssignableFrom(dependencies[i].getClass())) {
                return false;
            }
        }

        return true;
    }
}
