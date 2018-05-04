核心功能:解析在不同环境下的不同格式的文件。
为了保证代码的重用性和可扩展性，对需求进行简单的分析。
> 1. 支持多种格式,包括：xml，properties，json，avro等
> 2. 支持不同的数据源，包括：本地文件，zookeeper上的文件，redis中的文件，ftp上的文件等
> 3. 实现对文件的监听

首先先将文件格式和数据源进行隔离，将文件格式抽象成Schema，代码如下：

```java
package name.zicat.cofiguration.schema;


/**
 *
 * @param <S>
 * @param <T>
 */
public interface Schema<S, T> {

    /**
     *
     * @param s
     * @return
     * @throws Exception
     */
    T parse(S s) throws Exception;
}
```
S为输入的抽象，T为输出的抽象，T parse(S s)将输入转化为输出

```java
package name.zicat.cofiguration.schema;



/**
 *
 * @param <T>
 */
public interface StringSchema<T> extends Schema <String, T> {

}

```
StringSchema继承Schema，将String设置为输入类型

```java
package name.zicat.cofiguration.schema;

import java.io.InputStream;

/**
 *
 * @param <T>
 */
public interface InputStreamSchema<T> extends StringSchema <T> {

    T parse(InputStream str) throws Exception;
}
```
InputStreamSchema继承StringSchema，重载parse，将InputStream设置为输入类型

```java
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
```
AbstractSchema实现InputStreamSchema接口，文本需要制定编码和输出的类型，所以有两个私有成员，另外由于可以将str转化为输入流，可以实现 T parse(String str)

```java
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
```
JAXBSchema继承AbstractSchema，基于JAXB将xml数据转化成对象。

```java
package name.zicat.cofiguration.schema.properties;

import name.zicat.cofiguration.schema.AbstractSchema;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 *
 */
public class PropertiesSchema extends AbstractSchema<Properties> {


    public PropertiesSchema(Charset charset) {
        super(Properties.class, charset);
    }

    @Override
    public Properties parse(InputStream source) throws Exception {
        Properties prop = new Properties();
        prop.load(new InputStreamReader(source, charset));
        return prop;
    }
}
```
PropertiesSchema继承AbstractSchema，基于properties api将properties数据转化成对象。