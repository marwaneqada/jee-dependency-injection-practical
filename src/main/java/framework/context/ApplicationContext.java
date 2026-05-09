package framework.context;

public interface ApplicationContext {
    Object getBean(String name);
    <T> T getBean(Class<T> type);
}
