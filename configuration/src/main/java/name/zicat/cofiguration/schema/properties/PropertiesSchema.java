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
