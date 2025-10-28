<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Анализатор страниц</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card shadow">
                    <div class="card-body p-5 text-center">
                        <h1 class="card-title mb-4">Анализатор страниц</h1>
                        <p class="lead text-muted mb-4">
                            Бесплатно проверяйте сайты на SEO пригодность
                        </p>

                        <form action="/urls" method="POST" class="mb-4">
                            <div class="input-group">
                                <input type="url"
                                       name="url"
                                       class="form-control form-control-lg"
                                       placeholder="https://example.com"
                                       required>
                                <button type="submit" class="btn btn-primary btn-lg">
                                    Проверить
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
