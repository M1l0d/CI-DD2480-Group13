import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigData {

    /**
     * Return the public access token for Github
     * 
     * @return public access token or null if non-existent
     */
    public static String getAccessToken() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
            return properties.getProperty("access.token");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}