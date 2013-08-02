/**
 * Copyright 2013 John Crygier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.crygier.git.rest;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stores all the settings for the instance of this app.
 */
public enum Configuration {
    BaseUri                                                 ("http.baseUrl"),
    WebAppLocation                                          ("http.webAppLocation"),
    RepositoryDefaultDirectory                              ("repository.defaultDir"),
    RepositoryAutoCloneToDefault                            ("repository.autoCloneToDefault"),
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
        if (value != null)
            properties.setProperty(propertyName, value.toString());
        else
            properties.remove(propertyName);

        saveProperties();
    }

    public String getChildValue(String childName) {
        return properties.getProperty(propertyName + "." + childName);
    }

    public Collection<String> listAllChildren() {
        Collection<String> answer = new ArrayList<String>();

        Enumeration<?> allProperties = properties.propertyNames();
        while (allProperties.hasMoreElements()) {
            String aProperty = allProperties.nextElement().toString();
            if (aProperty.startsWith(propertyName + "."))
                answer.add(aProperty.substring(propertyName.length() + 1));
        }

        return answer;
    }

    public void setChildValue(String childName, Object value) {
        properties.setProperty(propertyName + "." + childName, value.toString());
        saveProperties();
    }

    public File getFileValue() {
        String strValue = getStringValue();
        if (strValue != null)
            return new File(strValue);
        else
            return null;
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

    public static File getPropertiesLocation() {
        return loadedPropertiesLocation;
    }
}
