package name.zicat.cofiguration.schema.xml;

import name.zicat.cofiguration.schema.AbstractSchema;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.charset.Charset;

/**
 *
 * @param <T>
 */
public class JAXBSchema<T> extends AbstractSchema<T> {

    public JAXBSchema(Class<T> clazz, Charset charset) {

        super(clazz, charset);
    }


    @Override
    public T parse(InputStream source) throws Exception {
        return unmarshal(source, clazz, charset);
    }


    /**
     *
     * @param is
     * @param clazz
     * @param charset
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    public static <T> T unmarshal(InputStream is, Class<T> clazz, Charset charset) throws IOException, JAXBException {

        if (is == null)
            throw new NullPointerException("InputStream is null");

        Reader reader = new InputStreamReader(is, charset);
        return unmarshal(reader, clazz);
    }

    /**
     *
     * @param reader
     * @param clazz
     * @return
     * @throws IOException
     * @throws JAXBException
     */
    @SuppressWarnings("unchecked")
    public static <T> T unmarshal(Reader reader, Class<T> clazz) throws IOException, JAXBException {

        if (reader == null)
            throw new NullPointerException("reader is null");

        if (clazz == null)
            throw new NullPointerException("class is null");

        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller um = context.createUnmarshaller();
        return (T) um.unmarshal(reader);
    }

}
