package util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Files {

    public static Path resourceToPath(Class<?> clazz, String resourceName) {
        try {
            URL resourceUrl = clazz.getResource(resourceName);
            if (resourceUrl == null) {
                throw new IOException("Resource not found: " + resourceName);
            }
            Path resourcePath = Paths.get(resourceUrl.toURI());
            return resourcePath.toRealPath();
        }
        catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
