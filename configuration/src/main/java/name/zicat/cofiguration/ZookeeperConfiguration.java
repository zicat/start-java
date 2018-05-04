package name.zicat.cofiguration;

import name.zicat.cofiguration.schema.AbstractSchema;

/**
 *
 * @param <T>
 */
public class ZookeeperConfiguration<T> extends Configuration<T> {

    private String zkHost;
    private String path;

    public ZookeeperConfiguration(String zkHost, String path, AbstractSchema<T> abstractSchema) {

        super(abstractSchema);
        if(zkHost == null)
            throw new NullPointerException("zkHost is null");

        if(path == null)
            throw new NullPointerException("path is null");
        this.zkHost = zkHost;
        this.path = path;
    }

    @Override
    protected T load() throws Exception {
        return null; //todo
    }
}
