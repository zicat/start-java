package name.zicat.configuration.test;

import name.zicat.cofiguration.Configuration;
import name.zicat.cofiguration.LocalConfiguration;
import name.zicat.cofiguration.schema.AbstractSchema;
import name.zicat.cofiguration.schema.xml.JAXBSchema;
import name.zicat.configuration.test.schema.xml.JavaBean;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 *
 */
public class LocalConfigurationTest {

    @Test
    public void test() throws Exception {

        AbstractSchema<JavaBean> schema = new JAXBSchema<>(JavaBean.class, StandardCharsets.UTF_8);

        URL url = Thread.currentThread().getContextClassLoader().getResource("test.xml");
        Configuration<JavaBean> configuration = new LocalConfiguration<>(url, schema);
        JavaBean javaBean = configuration.createInstance();
        Assert.assertEquals(javaBean.getName(), "张");
        Assert.assertEquals(javaBean.getValue(), "a");
    }
}
