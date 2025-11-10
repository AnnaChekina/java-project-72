package hexlet.code.controller;

import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import hexlet.code.model.UrlUtils;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import static hexlet.code.util.Constants.FLASH;
import static hexlet.code.util.Constants.FLASH_TYPE;
import static hexlet.code.util.Constants.DANGER;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class UrlController {

    public static void index(Context ctx) {
        var page = new UrlsPage(Collections.emptyList(), Collections.emptyList());
        page.setFlash(ctx.consumeSessionAttribute(FLASH));
        page.setFlashType(ctx.consumeSessionAttribute(FLASH_TYPE));

        ctx.render("index.jte", Map.of("page", page));
    }

    public static void create(Context ctx) {
        var inputUrl = ctx.formParam("url");

        if (inputUrl == null || inputUrl.trim().isEmpty()) {
            ctx.sessionAttribute(FLASH, "URL не может быть пустым");
            ctx.sessionAttribute(FLASH_TYPE, DANGER);
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }

        try {
            String normalizedUrl = UrlUtils.normalizeUrl(inputUrl);

            if (UrlRepository.exists(normalizedUrl)) {
                ctx.sessionAttribute(FLASH, "Страница уже существует");
                ctx.sessionAttribute(FLASH_TYPE, "info");
                ctx.redirect(NamedRoutes.urlsPath());
                return;
            }

            var url = new Url(normalizedUrl);
            UrlRepository.save(url);

            ctx.sessionAttribute(FLASH, "Страница успешно добавлена");
            ctx.sessionAttribute(FLASH_TYPE, "success");
            ctx.redirect(NamedRoutes.urlsPath());

        } catch (IllegalArgumentException e) {
            ctx.sessionAttribute(FLASH, "Некорректный URL");
            ctx.sessionAttribute(FLASH_TYPE, DANGER);
            ctx.redirect(NamedRoutes.rootPath());
        } catch (SQLException e) {
            ctx.sessionAttribute(FLASH, "Ошибка базы данных: " + e.getMessage());
            ctx.sessionAttribute(FLASH_TYPE, DANGER);
            ctx.redirect(NamedRoutes.rootPath());
        }
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("URL not found"));

        var checks = UrlCheckRepository.findByUrlId(id);
        var page = new UrlPage(url, checks);
        page.setFlash(ctx.consumeSessionAttribute(FLASH));
        page.setFlashType(ctx.consumeSessionAttribute(FLASH_TYPE));

        ctx.render("urls/show.jte", Map.of("page", page));
    }

    public static void list(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var urlWithChecks = new ArrayList<UrlsPage.UrlWithCheck>();

        for (var url : urls) {
            var lastCheck = UrlCheckRepository.findLatestCheck(url.getId());
            var lastStatusCode = lastCheck.map(UrlCheck::getStatusCode).orElse(null);
            var lastCheckDate = lastCheck.map(UrlCheck::getFormattedCreatedAt).orElse("");

            urlWithChecks.add(new UrlsPage.UrlWithCheck(url, lastStatusCode, lastCheckDate));
        }

        var page = new UrlsPage(urls, urlWithChecks);
        page.setFlash(ctx.consumeSessionAttribute(FLASH));
        page.setFlashType(ctx.consumeSessionAttribute(FLASH_TYPE));

        ctx.render("urls/index.jte", Map.of("page", page));
    }

    public static void check(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("URL not found"));

        try {
            var checkData = UrlUtils.checkUrl(url.getName());

            var urlCheck = new UrlCheck(
                    (Integer) checkData.get("statusCode"),
                    (String) checkData.get("title"),
                    (String) checkData.get("h1"),
                    (String) checkData.get("description"),
                    url.getId()
            );

            UrlCheckRepository.save(urlCheck);

            ctx.sessionAttribute(FLASH, "Страница успешно проверена");
            ctx.sessionAttribute(FLASH_TYPE, "success");
        } catch (Exception e) {
            ctx.sessionAttribute(FLASH, "Некорректный адрес");
            ctx.sessionAttribute(FLASH_TYPE, DANGER);
        }

        ctx.redirect(NamedRoutes.urlPath(id));
    }
}
