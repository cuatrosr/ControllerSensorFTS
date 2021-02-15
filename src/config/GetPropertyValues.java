package config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GetPropertyValues {

    static int ammountPlatforms;
    static int refreshControllerTime;
    static String HOST;
    static int PUERTO;
    int refreshPlatformsTime;
    String URL;

    private InputStream inputStream;

    public void setPropValues() throws IOException {
        try {
            Properties prop = new Properties();
            String propFileName = "configuration.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            ammountPlatforms = Integer.parseInt(prop.getProperty("ammountPlatforms"));

            HOST = prop.getProperty("HOST");

            PUERTO = Integer.parseInt(prop.getProperty("PUERTO"));

            refreshPlatformsTime = Integer.parseInt(prop.getProperty("refreshPlatformsTime"));
            refreshControllerTime = Integer.parseInt(prop.getProperty("refreshControllerTime"));
            
            URL = prop.getProperty("URL");
            
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
    }

    public static int getAmmountPlatforms() {
        return ammountPlatforms;
    }

    public static String getHOST() {
        return HOST;
    }

    public static int getPUERTO() {
        return PUERTO;
    }

    public int getRefreshPlatformsTime() {
        return refreshPlatformsTime;
    }

    public String getURL() {
        return URL;
    }
    
    public static int getRefreshControllerTime() {
        return refreshControllerTime;
    }
}
