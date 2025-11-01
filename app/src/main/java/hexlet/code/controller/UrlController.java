package hexlet.code.controller;

import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import hexlet.code.model.UrlUtils;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.sql.SQLException;
import java.util.Map;

public class UrlController {

    public static void index(Context ctx) {
        var page = new UrlsPage(null);
        ctx.render("index.jte", Map.of("page", page));
    }

    public static void create(Context ctx) {
        var inputUrl = ctx.formParam("url");

        if (inputUrl == null || inputUrl.trim().isEmpty()) {
            ctx.sessionAttribute("flash", "URL не может быть пустым");
            ctx.sessionAttribute("flashType", "danger");
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }

        try {
            // Нормализация URL
            String normalizedUrl = UrlUtils.normalizeUrl(inputUrl);

            // Проверка на существование URL
            if (UrlRepository.exists(normalizedUrl)) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.sessionAttribute("flashType", "info");
                ctx.redirect(NamedRoutes.urlsPath());
                return;
            }

            // Сохранение нового URL
            var url = new Url(normalizedUrl);
            UrlRepository.save(url);

            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.sessionAttribute("flashType", "success");
            ctx.redirect(NamedRoutes.urlsPath());

        } catch (IllegalArgumentException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flashType", "danger");
            ctx.redirect(NamedRoutes.rootPath());
        } catch (SQLException e) {
            ctx.sessionAttribute("flash", "Ошибка базы данных: " + e.getMessage());
            ctx.sessionAttribute("flashType", "danger");
            ctx.redirect(NamedRoutes.rootPath());
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Ошибка: " + e.getMessage());
            ctx.sessionAttribute("flashType", "danger");
            ctx.redirect(NamedRoutes.rootPath());
        }
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("URL not found"));

        var page = new UrlPage(url);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flashType"));

        ctx.render("urls/show.jte", Map.of("page", page));
    }

    public static void list(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var page = new UrlsPage(urls);
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flashType"));

        ctx.render("urls/index.jte", Map.of("page", page));
    }
}
