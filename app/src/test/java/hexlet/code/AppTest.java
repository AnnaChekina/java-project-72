package hexlet.code;

import static org.assertj.core.api.Assertions.assertThat;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;

import java.sql.SQLException;

public class AppTest {

    private Javalin app;

    @BeforeEach
    public final void setUp() throws Exception {
        app = App.getApp();
        UrlRepository.deleteAll();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assert response.body() != null;
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }

    @Test
    public void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            assert response.body() != null;
            assertThat(response.body().string()).contains("Сайты");
        });
    }

    @Test
    public void testUrlPage() throws SQLException {
        var url = new Url("https://example.com");
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
            assert response.body() != null;
            assertThat(response.body().string()).contains("https://example.com");
        });
    }

    @Test
    public void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/999999");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    public void testCreateValidUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://example.com";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);

            var urls = UrlRepository.getEntities();
            assertThat(urls).hasSize(1);
            assertThat(urls.getFirst().getName()).isEqualTo("https://example.com");
        });
    }

    @Test
    public void testCreateUrlWithNormalization() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://example.com:443/path/to/page?query=string";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);

            var urls = UrlRepository.getEntities();
            assertThat(urls).hasSize(1);
            assertThat(urls.getFirst().getName()).isEqualTo("https://example.com");
        });
    }

    @Test
    public void testCreateUrlWithEmptyUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);

            assert response.body() != null;
            var body = response.body().string();
            assertThat(body).contains("Анализатор страниц");

            var urls = UrlRepository.getEntities();
            assertThat(urls).isEmpty();
        });
    }

    @Test
    public void testCreateDuplicateUrl() throws SQLException {
        var url = new Url("https://duplicate.com");
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://duplicate.com";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);

            var urls = UrlRepository.getEntities();
            assertThat(urls).hasSize(1); // Все еще одна запись, дубликат не добавился
        });
    }

    @Test
    public void testCreateUrlWithoutProtocol() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=example.com";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);

            assert response.body() != null;
            var body = response.body().string();
            assertThat(body).contains("Анализатор страниц");

            var urls = UrlRepository.getEntities();
            assertThat(urls).isEmpty();
        });
    }
}
