package escape.util;

public interface Factory<T> {
    /**
     * Make a new object of type T
     * @return
     */
    public T make();
}
