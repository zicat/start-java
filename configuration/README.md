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

单元测试：https://github.com/zicat/start-java/tree/master/configuration/src/test/java/name/zicat/configuration/test/schema

有了文件格式Schema对象抽象后，再对数据源抽象成Configuration，代码如下：
```java
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
```
Configuration核心成员为AbstractSchema<T>，定义抽象方法T load(),createInstance()内部调用load()构建instance。


```java
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
```
LocalConfiguration继承Configuration，本地文件通过URL进行资源定位，T load()将url构造inputstream，通过schema代理构造T对象。

```java
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
```
ZookeeperConfiguration继承Configuration，zookeeper文件通过zkHost和path进行资源定位，T load()可以通过zookeeper api实现。

可以看到无论是ZookeeperConfiguration还是LocalConfiguration，都不关心文件格式，只关心如何将各自的数据源转化成Schema的输入即可。实现了数据源和格式的隔离。
添加新的数据源后，该数据源即可支持Schema实现的所有格式。添加一个新的Schema格式后，所有数据源不需要改动代码即可直接支持。
测试用例：https://github.com/zicat/start-java/blob/master/configuration/src/test/java/name/zicat/configuration/test/LocalConfigurationTest.java