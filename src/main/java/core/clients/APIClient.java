package core.clients;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class APIClient {

    private final String baseUlr;

    public APIClient() {
        this.baseUlr = determineBaseUlr();
    }

    //Определение базового URL на основе файла конфигурации
    private String determineBaseUlr() {
        String environment = System.getProperty("env", "test");
        String configFileName = "application-" + environment + ".properties";

        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            if (input == null) {
                throw new IllegalStateException("Configuration file not found: " + configFileName);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load configuration file: " + configFileName, e);
        }

        return properties.getProperty("baseUlr");
    }
}
