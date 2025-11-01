@import hexlet.code.dto.UrlsPage
@import hexlet.code.util.NamedRoutes
@param UrlsPage page

@template.layout.page(
    content = @`
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card shadow">
                    <div class="card-body p-5 text-center">
                        <h1 class="card-title mb-4">Анализатор страниц</h1>
                        <p class="lead text-muted mb-4">
                            Бесплатно проверяйте сайты на SEO пригодность
                        </p>

                        <form action="${NamedRoutes.urlsPath()}" method="POST" class="mb-4">
                            <div class="input-group">
                                <input type="text"
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
    `,
    page = page
)
