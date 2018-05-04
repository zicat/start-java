package name.zicat.cofiguration;


import name.zicat.cofiguration.schema.AbstractSchema;

import java.io.InputStream;
import java.net.URL;

/**
 *
 * @param <T>
 */
public class LocalConfiguration<T> extends Configuration<T> {

    protected URL url;

    public LocalConfiguration(URL url, AbstractSchema<T> abstractSchema) {

        super(abstractSchema);
        if(url == null)
            throw new NullPointerException("url is null");
        this.url = url;
    }


    @Override
    protected T load() throws Exception{

        InputStream in = null;
        try {
            in = url.openStream();
            return abstractSchema.parse(in);
        } finally {
            if(in != null)
                in.close();
        }
    }
}
