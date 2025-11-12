package hexlet.code.model;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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

    public static UrlCheck checkUrl(String url) throws Exception {
        HttpResponse<String> response = Unirest.get(url)
                .connectTimeout(5000)
                .asString();

        int statusCode = response.getStatus();
        String body = response.getBody();

        Document doc = Jsoup.parse(body);

        String title = doc.title();
        String h1 = doc.selectFirst("h1") != null ? doc.selectFirst("h1").text() : "";
        String description = doc.selectFirst("meta[name=description]") != null
                ? doc.selectFirst("meta[name=description]").attr("content")
                : "";

        return new UrlCheck(statusCode, title, h1, description, null);
    }
}
