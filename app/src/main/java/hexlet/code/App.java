package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.repository.BaseRepository;
import io.javalin.Javalin;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import io.javalin.rendering.template.JavalinJte;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class App {

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    public static Javalin getApp() throws Exception {
        var hikariConfig = new HikariConfig();

        String jdbcUrl = System.getenv().getOrDefault("JDBC_DATABASE_URL",
                "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");

        hikariConfig.setJdbcUrl(jdbcUrl);

        var dataSource = new HikariDataSource(hikariConfig);

        var sql = loadSchema();
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        BaseRepository.dataSource = dataSource;

        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        // Главная страница (только форма)
        app.get("/", ctx -> ctx.render("index.jte"));

        // Обработчик для добавления URL (заглушка)
        app.post("/urls", ctx -> {
            var url = ctx.formParam("url");
            ctx.contentType("text/html; charset=utf-8");
            ctx.result("URL добавлен: " + url + " (функциональность в разработке)");
        });

        return app;
    }

    private static String loadSchema() {
        var inputStream = App.class.getClassLoader().getResourceAsStream("schema.sql");
        if (inputStream == null) {
            throw new RuntimeException("Schema file not found");
        }
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }

    public static void main(String[] args) throws Exception {
        var app = getApp();
        app.start(getPort());
    }
}
