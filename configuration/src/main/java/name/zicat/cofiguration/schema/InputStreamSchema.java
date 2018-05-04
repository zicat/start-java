package name.zicat.cofiguration.schema;

import java.io.InputStream;

/**
 *
 * @param <T>
 */
public interface InputStreamSchema<T> extends StringSchema <T> {

    T parse(InputStream str) throws Exception;
}
