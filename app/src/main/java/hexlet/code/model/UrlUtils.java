package hexlet.code.model;

import java.net.URI;
import java.net.URL;

public class UrlUtils {

    public static String normalizeUrl(String inputUrl) {
        try {
            String trimmedUrl = inputUrl.trim();

            // Добавление протокола если отсутствует
            if (!trimmedUrl.startsWith("http://") && !trimmedUrl.startsWith("https://")) {
                trimmedUrl = "https://" + trimmedUrl;
            }

            // Парсинг URL через URI
            URI uri = new URI(trimmedUrl).normalize();
            URL url = uri.toURL();

            // Извлечение protocol, host и port
            String protocol = url.getProtocol();
            String host = url.getHost();
            int port = url.getPort();

            StringBuilder normalized = new StringBuilder();
            normalized.append(protocol).append("://").append(host);

            // Добавление порта если он указан и не стандартный
            if (port != -1 && port != 80 && port != 443) {
                normalized.append(":").append(port);
            }

            return normalized.toString();

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL format: " + e.getMessage());
        }
    }

    public static boolean isValidUrl(String url) {
        try {
            normalizeUrl(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
