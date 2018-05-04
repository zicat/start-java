package name.zicat.configuration.test.schema.xml;

import name.zicat.cofiguration.schema.AbstractSchema;
import name.zicat.cofiguration.schema.xml.JAXBSchema;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class JAXBSchemaTest {

    @Test
    public void test() throws Exception {

        String xml = "<configuration name=\"张\">a</configuration>";

        AbstractSchema<JavaBean> schema = new JAXBSchema<>(JavaBean.class, StandardCharsets.UTF_8);
        JavaBean javaBean = schema.parse(xml);
        Assert.assertEquals(javaBean.getName(), "张");
        Assert.assertEquals(javaBean.getValue(), "a");

    }
}
