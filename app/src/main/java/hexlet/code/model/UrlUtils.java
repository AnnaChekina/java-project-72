package hexlet.code.model;

import java.net.URI;
import java.net.URL;

public class UrlUtils {

    public static String normalizeUrl(String inputUrl) {
        try {
            String trimmedUrl = inputUrl.trim();

            // Проверяем, что URL содержит протокол
            if (!trimmedUrl.startsWith("http://") && !trimmedUrl.startsWith("https://")) {
                throw new IllegalArgumentException("URL должен содержать протокол (http:// или https://)");
            }

            // Парсинг URL через URI
            URI uri = new URI(trimmedUrl).normalize();
            URL url = uri.toURL();

            // Извлечение protocol, host и port
            String protocol = url.getProtocol();
            String host = url.getHost();
            int port = url.getPort();

            // Проверяем что host не пустой
            if (host == null || host.isEmpty()) {
                throw new IllegalArgumentException("URL должен содержать доменное имя");
            }

            StringBuilder normalized = new StringBuilder();
            normalized.append(protocol).append("://").append(host);

            // Добавление порта если он указан и не стандартный
            if (port != -1 && port != 80 && port != 443) {
                normalized.append(":").append(port);
            }

            return normalized.toString();

        } catch (Exception e) {
            throw new IllegalArgumentException("Некорректный URL: " + e.getMessage());
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
