package name.zicat.cofiguration.schema;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 *
 * @param <T>
 */
public abstract class AbstractSchema<T> implements InputStreamSchema<T> {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    protected final Class<T> clazz;
    protected final Charset charset;

    public AbstractSchema(Class<T> clazz, Charset charset) {

        if (clazz == null)
            throw new NullPointerException("class is null");

        this.clazz = clazz;
        this.charset = charset == null ? DEFAULT_CHARSET : charset;
    }


    @Override
    public T parse(String str) throws Exception {

        InputStream in = null;
        try {
            in = new ByteArrayInputStream(str.getBytes(charset));
            return parse(in);
        } finally {
            if(in != null)
                in.close();
        }
    }
}
