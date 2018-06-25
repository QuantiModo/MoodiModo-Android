package io.swagger.client;

/**
 * Singleton that stores the the base path for the API requests
 */
public class SwaggerClient {
    private static SwaggerClient ourInstance = new SwaggerClient();
    private static String APP_BASE_PATH = null;

    public static SwaggerClient getInstance() {
        return ourInstance;
    }

    private SwaggerClient() {
    }

    /**
     * Sets the base path for the requests
     * @param path the base path in the form http://url/some_endpoint/
     */
    public void setAppBasePath(final String path){
        APP_BASE_PATH = path;
    }

    /**
     * Retrieves the saved base path to make API requests
     * @return the base path in the form http://url/some_endpoint/
     */
    public String getAppBasePath() {
        return APP_BASE_PATH;
    }
}
