package name.zicat.configuration.test.schema.properties;

import name.zicat.cofiguration.schema.AbstractSchema;
import name.zicat.cofiguration.schema.properties.PropertiesSchema;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class PropertiestSchemaTest {

    @Test
    public void test() throws Exception {

        String value = "name=张\n" +
                       "value=a\n";
        AbstractSchema<Properties> schema = new PropertiesSchema(StandardCharsets.UTF_8);
        Properties properties = schema.parse(value);
        Assert.assertEquals(properties.getProperty("name"), "张");
        Assert.assertEquals(properties.getProperty("value"), "a");

    }
}
