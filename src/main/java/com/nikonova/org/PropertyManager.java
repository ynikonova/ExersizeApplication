package com.nikonova.org;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyManager {

    private static String propertyFilePath = System.getProperty("user.dir")+
            "\\src\\test\\resources\\configuration.properties";

    public String GetData(String key)
    {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(propertyFilePath));
        } catch (IOException e) {
            System.out.println("Configuration properties file cannot be found");
        }

        return prop.getProperty(key);
    }
}