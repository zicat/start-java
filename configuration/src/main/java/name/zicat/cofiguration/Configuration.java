package name.zicat.cofiguration;

import name.zicat.cofiguration.schema.AbstractSchema;

/**
 *
 * @param <T>
 */
public abstract class Configuration<T> {

    protected AbstractSchema<T> abstractSchema;
    protected T instance;

    public Configuration(AbstractSchema<T> abstractSchema) {

        if(abstractSchema == null)
            throw new NullPointerException("abstractSchema is null");

        this.abstractSchema = abstractSchema;
    }


    /**
     *
     * @return
     */
    protected abstract T load() throws Exception;

    public T createInstance() throws Exception {
        instance = load();
        return instance;
    }

    public T getInstance() {
        return instance;
    }
}
