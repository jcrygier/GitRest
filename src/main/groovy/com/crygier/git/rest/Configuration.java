package com.crygier.git.rest;

import java.io.*;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stores all the settings for the instance of this app.
 */
public enum Configuration {
    BaseUri                                                 ("http.baseUrl"),
    StoredRepositories                                      ("repositories")
    ;

    public static final Logger logger = java.util.logging.Logger.getLogger(Configuration.class.getName());
    private static final Properties properties = new Properties();
    private static File loadedPropertiesLocation;

    private String propertyName;
    private Configuration(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getStringValue() {
        return properties.getProperty(propertyName);
    }

    public void setValue(Object value) {
        properties.setProperty(propertyName, value.toString());
        saveProperties();
    }

    public String getChildValue(String childName) {
        return properties.getProperty(propertyName + "." + childName);
    }

    public void setChildValue(String childName, Object value) {
        properties.setProperty(propertyName + "." + childName, value.toString());
        saveProperties();
    }

    public File getFileValue() {
        return new File(getStringValue());
    }

    public File getChildFileValue(String childName) {
        String fileName = getChildValue(childName);

        if (fileName != null)
            return new File(getChildValue(childName));
        else
            return null;
    }

    public void addFileValue(String childName, File value) {
        setChildValue(childName, value.getAbsolutePath());
    }

    public static void loadProperties(File propertiesFile) throws IOException {
        if (propertiesFile.exists()) {
            FileInputStream fIn = new FileInputStream(propertiesFile);
            properties.load(fIn);
            fIn.close();

            loadedPropertiesLocation = propertiesFile;
        } else {
            logger.warning("Unable to load properties from: " + propertiesFile.getAbsolutePath());
        }
    }

    public static void saveProperties() {
        try {
            FileOutputStream fOut = new FileOutputStream(loadedPropertiesLocation);
            properties.store(fOut, "Saved on: " + Calendar.getInstance());
            fOut.close();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to save properties to " + loadedPropertiesLocation, e);
        }
    }
}
