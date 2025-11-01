@import gg.jte.Content
@import hexlet.code.dto.BasePage
@import hexlet.code.util.NamedRoutes
@param Content content
@param BasePage page

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Анализатор страниц</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
    <header class="navbar navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="${NamedRoutes.rootPath()}">Анализатор страниц</a>
            <nav class="navbar-nav flex-row">
                <a class="nav-link me-3" href="${NamedRoutes.rootPath()}">Главная</a>
                <a class="nav-link" href="${NamedRoutes.urlsPath()}">Сайты</a>
            </nav>
        </div>
    </header>

    <main class="container py-5">
        @if(page != null && page.getFlash() != null)
            <div class="alert alert-${page.getFlashType()} alert-dismissible fade show" role="alert">
                ${page.getFlash()}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        @endif

        ${content}
    </main>

    <footer class="bg-dark text-white py-4 mt-5">
        <div class="container text-center">
            <a href="https://github.com/AnnaChekina" class="text-white">Мой профиль на GitHub</a>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
