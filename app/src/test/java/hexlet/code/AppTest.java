package hexlet.code;

import static org.assertj.core.api.Assertions.assertThat;

import hexlet.code.model.Url;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.repository.UrlCheckRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;

import java.io.IOException;
import java.sql.SQLException;

class AppTest {

    private Javalin app;
    private static MockWebServer mockWebServer;

    @BeforeAll
    static void setUpAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDownAll() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    final void setUp() throws Exception {
        app = App.getApp();
        UrlCheckRepository.deleteAll();
        UrlRepository.deleteAll();
    }

    @AfterEach
    final void tearDown() {
        if (app != null) {
            app.stop();
        }
        // Сбрасываем dataSource чтобы следующее создание приложения работало корректно
        BaseRepository.dataSource = null;
    }

    @Test
    void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assert response.body() != null;
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }

    @Test
    void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            assert response.body() != null;
            assertThat(response.body().string()).contains("Сайты");
        });
    }

    @Test
    void testUrlPage() throws SQLException {
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
    void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/999999");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    void testCreateValidUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://example.com";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);

            try {
                var urls = UrlRepository.getEntities();
                assertThat(urls).hasSize(1);
                assertThat(urls.getFirst().getName()).isEqualTo("https://example.com");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testCreateUrlWithNormalization() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://example.com:443/path/to/page?query=string";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);

            try {
                var urls = UrlRepository.getEntities();
                assertThat(urls).hasSize(1);
                assertThat(urls.getFirst().getName()).isEqualTo("https://example.com");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testCreateUrlWithEmptyUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);

            assert response.body() != null;
            var body = response.body().string();
            assertThat(body).contains("Анализатор страниц");

            try {
                var urls = UrlRepository.getEntities();
                assertThat(urls).isEmpty();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testCreateDuplicateUrl() throws SQLException {
        var url = new Url("https://duplicate.com");
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://duplicate.com";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);

            try {
                var urls = UrlRepository.getEntities();
                assertThat(urls).hasSize(1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testCreateUrlWithoutProtocol() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=example.com";
            var response = client.post("/urls", requestBody);
            assertThat(response.code()).isEqualTo(200);

            assert response.body() != null;
            var body = response.body().string();
            assertThat(body).contains("Анализатор страниц");

            try {
                var urls = UrlRepository.getEntities();
                assertThat(urls).isEmpty();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testCheckUrlSuccess() throws SQLException {
        String testUrl = mockWebServer.url("/").toString();
        var url = new Url(testUrl);
        UrlRepository.save(url);

        mockWebServer.enqueue(new MockResponse()
                .setBody("<title>Test Page</title><h1>Header</h1><meta name=\"description\" content=\"Test desc\">")
                .setResponseCode(200));

        JavalinTest.test(app, (server, client) -> {
            client.post("/urls/" + url.getId() + "/checks");

            try {
                var checks = UrlCheckRepository.findByUrlId(url.getId());
                assertThat(checks).hasSize(1);

                var check = checks.getFirst();
                assertThat(check.getStatusCode()).isEqualTo(200);
                assertThat(check.getTitle()).isEqualTo("Test Page");
                assertThat(check.getH1()).isEqualTo("Header");
                assertThat(check.getDescription()).isEqualTo("Test desc");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testCheckUrlWithErrorStatus() throws SQLException {
        String testUrl = mockWebServer.url("/").toString();
        var url = new Url(testUrl);
        UrlRepository.save(url);

        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        JavalinTest.test(app, (server, client) -> {
            client.post("/urls/" + url.getId() + "/checks");

            try {
                var checks = UrlCheckRepository.findByUrlId(url.getId());
                assertThat(checks).hasSize(1);
                assertThat(checks.getFirst().getStatusCode()).isEqualTo(404);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void testMultipleUrlChecks() throws SQLException {
        String testUrl = mockWebServer.url("/").toString();
        var url = new Url(testUrl);
        UrlRepository.save(url);

        mockWebServer.enqueue(new MockResponse().setResponseCode(200));
        mockWebServer.enqueue(new MockResponse().setResponseCode(201));

        JavalinTest.test(app, (server, client) -> {
            client.post("/urls/" + url.getId() + "/checks");
            client.post("/urls/" + url.getId() + "/checks");

            try {
                var checks = UrlCheckRepository.findByUrlId(url.getId());
                assertThat(checks).hasSize(2);
                assertThat(checks.get(0).getStatusCode()).isEqualTo(201);
                assertThat(checks.get(1).getStatusCode()).isEqualTo(200);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
