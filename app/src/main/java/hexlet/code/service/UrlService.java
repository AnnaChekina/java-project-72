package hexlet.code.service;

import hexlet.code.model.UrlCheck;
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URI;
import java.net.URL;

public class UrlService {

    public static String normalizeUrlWithPort(URI uri) {
        try {
            if (!uri.isAbsolute()) {
                throw new IllegalArgumentException("URL должен содержать протокол (http:// или https://)");
            }

            URL url = uri.toURL();

            String protocol = url.getProtocol();
            String host = url.getHost();
            int port = url.getPort();

            if (host == null || host.isEmpty()) {
                throw new IllegalArgumentException("URL должен содержать доменное имя");
            }

            if (!"http".equals(protocol) && !"https".equals(protocol)) {
                throw new IllegalArgumentException("Поддерживаются только HTTP и HTTPS протоколы");
            }

            if (port == 80 && "http".equals(protocol)) {
                return protocol + "://" + host;
            } else if (port == 443 && "https".equals(protocol)) {
                return protocol + "://" + host;
            }

            return String.format(
                    "%s://%s%s",
                    protocol,
                    host,
                    port == -1 ? "" : ":" + port
            ).toLowerCase();

        } catch (Exception e) {
            throw new IllegalArgumentException("Некорректный URL: " + e.getMessage());
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
