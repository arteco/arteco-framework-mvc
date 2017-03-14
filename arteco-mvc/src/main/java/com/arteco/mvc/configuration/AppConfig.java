package com.arteco.mvc.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by amalagraba on 08/03/2017.
 * Arteco Consulting Sl
 * mailto: info@arteco-consulting.com
 */
public class AppConfig {

    private Properties applicationProperties;

    public AppConfig() {
        loadApplicationProperties();
    }

    private void loadApplicationProperties() {
        applicationProperties = new Properties();
        String propFileName = "/application.properties";
        InputStream is = AppConfig.class.getResourceAsStream(propFileName);
        if (is != null) {
            try {
                applicationProperties.load(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getProperty(String name) {
        return applicationProperties.getProperty(name);
    }
}
